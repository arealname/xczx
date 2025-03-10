package com.xuecheng.orders.service.ServiceImpl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwj.xccommon.exception.ParamException;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.config.IdWorkerUtils;
import com.xuecheng.orders.config.PayMessageConfig;
import com.xuecheng.orders.config.QRCodeUtil;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j

@Service
public class OrderImpl implements OrderService {

    /**
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付记录(包括二维码)
     * @description 创建商品订单
     * @date 2022/10/4 11:02
     */


    @Value("${pay.alipay.Proxy}")
    String web;

    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) throws Exception{

        //根据用户id和商品信息   生成订单

        XcOrders xcOrders = saveXcOrders(userId, addOrderDto);

        //生成支付记录
//        try{
        XcPayRecord payRecord = createPayRecord(xcOrders);

        //生成二维码
        String qrCode = null;

        try {
            String url = String.format(web + "/orders/requestpay?payNo=%s", payRecord.getPayNo());
            qrCode = QRCodeUtil.createQRCode(url, 200, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayOrder(String payNo) {
        XcPayRecord xcPayRecord = payRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
        return xcPayRecord;
    }


    @Autowired
    MqMessageService mqMessageService;


    @Override
    public void saveAliPayStatus(PayStatusDto payStatusDto) {     //支付宝支付成功后的回调
        //支付成功后，更新支付记录状态
        //支付流水号
        String payNo = payStatusDto.getOut_trade_no();
        XcPayRecord payRecord = getPayOrder(payNo);
        if (payRecord == null) {
            throw new ParamException("支付记录找不到");
        }
        //支付结果
        String trade_status = payStatusDto.getTrade_status();
        log.debug("收到支付结果:{},支付记录:{}}", payStatusDto.toString(), payRecord.toString());
        if (trade_status.equals("TRADE_SUCCESS")) {

            //支付金额变为分
            Float totalPrice = payRecord.getTotalPrice() * 100;
            Float total_amount = Float.parseFloat(payStatusDto.getTotal_amount()) * 100;
            //校验是否一致
            if (totalPrice.intValue() != total_amount.intValue()) {
                //校验失败
                log.info("校验支付结果失败,支付记录:{},APP_ID:{},totalPrice:{}", payRecord.toString(), payStatusDto.getApp_id(), total_amount.intValue());
                throw new ParamException("校验支付结果失败");
            }
            log.debug("更新支付结果,支付交易流水号:{},支付结果:{}", payNo, trade_status);
            XcPayRecord payRecord_u = new XcPayRecord();
            payRecord_u.setStatus("601002");//支付成功
            payRecord_u.setOutPayChannel("Alipay");
            payRecord_u.setOutPayNo(payStatusDto.getTrade_no());//支付宝交易号
            payRecord_u.setPaySuccessTime(LocalDateTime.now());//通知时间
            int update1 = payRecordMapper.update(payRecord_u, new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
            if (update1 > 0) {
                log.info("更新支付记录状态成功:{}", payRecord_u.toString());
            } else {
                log.info("更新支付记录状态失败:{}", payRecord_u.toString());
                throw new ParamException("更新支付记录状态失败");
            }


            //关联的订单号
            Long orderId = payRecord.getOrderId();
            XcOrders orders = ordersMapper.selectById(orderId);
            if (orders == null) {
                log.info("根据支付记录[{}}]找不到订单", payRecord_u.toString());
                throw new ParamException("根据支付记录找不到订单");
            }
            XcOrders order_u = new XcOrders();
            order_u.setStatus("600002");//支付成功
            int update = ordersMapper.update(order_u, new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getId, orderId));
            if (update > 0) {
                log.info("更新订单表状态成功,订单号:{}", orderId);

                //保存消息记录,参数1：支付结果通知类型，2: 业务id，3:业务类型
                MqMessage mqMessage = mqMessageService.addMessage("payresult_notify", orders.getOutBusinessId(), orders.getOrderType(), null);
                //通知消息
                addIntoQueue(mqMessage);

            } else {
                log.info("更新订单表状态失败,订单号:{}", orderId);
                throw new ParamException("更新订单表状态失败");
            }
        }
    }


    @Autowired
    RabbitTemplate rabbitTemplate;


    private void addIntoQueue(MqMessage mqMessage) {//将消息放入消息队列
        String json = JSON.toJSONString(mqMessage);

        Message build = MessageBuilder.withBody(json.getBytes(StandardCharsets.UTF_8)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();

        CorrelationData cor = new CorrelationData(mqMessage.getId().toString());

        cor.getFuture().addCallback(res -> {
            log.info("消息发送成功:{}", mqMessage.toString());
            //更新消息状态
            mqMessageService.completed(mqMessage.getId());
        }, err -> {
            log.error("消息发送失败:{}", mqMessage.toString());
        });

        rabbitTemplate.convertAndSend(PayMessageConfig.PAYNOTIFY_EXCHANGE_FANOUT, "", build, cor);

    }


    @Autowired
    XcOrdersMapper ordersMapper;
    @Autowired
    XcOrdersGoodsMapper ordersGoodsMapper;


    public XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto) {   //根据商品信息生成order
        //幂等性处理
        XcOrders order = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if (order != null) {
            return order;
        }
        order = new XcOrders();
        //生成订单号
        long orderId = IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        order.setTotalPrice(addOrderDto.getTotalPrice());
        order.setCreateDate(LocalDateTime.now());
        order.setStatus("600001");//未支付
        order.setUserId(userId);
        order.setOrderType(addOrderDto.getOrderType());
        order.setOrderName(addOrderDto.getOrderName());
        order.setOrderDetail(addOrderDto.getOrderDetail());
        order.setOrderDescrip(addOrderDto.getOrderDescrip());
        order.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id
        ordersMapper.insert(order);

        String orderDetailJson = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        xcOrdersGoodsList.forEach(goods -> {
            XcOrdersGoods xcOrdersGoods = new XcOrdersGoods();
            BeanUtils.copyProperties(goods, xcOrdersGoods);
            xcOrdersGoods.setOrderId(orderId);//订单号
            ordersGoodsMapper.insert(xcOrdersGoods);
        });
        return order;
    }


    @Autowired
    XcPayRecordMapper payRecordMapper;


    public XcPayRecord createPayRecord(XcOrders orders) throws ParamException {
        if (orders == null) {
            throw new ParamException("订单不存在");
        }
        if (orders.getStatus().equals("600002")) {
            System.out.println("订单已支付");

            ParamException.cast("订单已支付");
        }
        XcPayRecord payRecord = new XcPayRecord();
        //生成支付交易流水号
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);
        payRecord.setOrderId(orders.getId());//商品订单号
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("601001");//未支付
        payRecord.setUserId(orders.getUserId());
        payRecordMapper.insert(payRecord);
        return payRecord;
    }

    //根据业务id查询订单
    public XcOrders getOrderByBusinessId(String businessId) {
        XcOrders orders = ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
        return orders;
    }
}
