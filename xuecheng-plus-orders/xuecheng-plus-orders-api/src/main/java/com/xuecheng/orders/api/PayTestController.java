package com.xuecheng.orders.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.cwj.xccommon.exception.ParamException;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import com.xuecheng.orders.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description 测试支付宝接口
 * @date 2022/10/20 22:19
 */
@RestController
public class PayTestController {

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;



    @Autowired
    OrderService orderService;

    @PostMapping("/generatepaycode")               //生成订单并生成二维码
    public PayRecordDto generateCode(@RequestBody AddOrderDto addOrderDto) throws Exception {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            throw new ParamException("请登录后继续选课");
        }
        return orderService.createOrder(user.getId(),addOrderDto);
    }

    @Value("${pay.alipay.Proxy}")
    String web;


    //根据二维码生成支付信息，跳转到支付宝支付页面
    @ApiOperation("扫码下单接口")
    @RequestMapping("/requestpay")
    public void requestpay(String payNo,HttpServletResponse httpResponse) throws IOException, AlipayApiException {


        XcPayRecord payOrder = orderService.getPayOrder(payNo);

        //订单不存在
        if (payOrder == null) {
            throw new ParamException("订单不存在");
        }

        //不要重复支付

        if (payOrder.getStatus() == "601002") {
            throw new ParamException("订单已支付,请勿重复支付");
        }

        System.out.println("支付宝支付");
        System.out.println("已经到达支付宝支付接口");

        synchronized (payNo) {  //锁住订单号，防止朋友和你同时支付，导致重复支付，这里只是简单的处理，实际中可以使用redis分布式锁，或者数据库乐观锁

            //获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
            AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
            alipayRequest.setReturnUrl(web + "/orders/getnotice");
            alipayRequest.setNotifyUrl(web + "/orders/getnotice");//在公共参数中设置回跳和通知地址

            System.out.println(payNo);
            alipayRequest.setBizContent("{" +
                    "    \"out_trade_no\":\" " + payOrder.getPayNo() + "\"," +
                    "    \"total_amount\":" + payOrder.getTotalPrice() + "," +
                    "    \"subject\":\"" + payOrder.getOrderName() + "\"," +
                    "    \"product_code\":\"QUICK_WAP_WAY\"" +
                    "  }");//填充业务参数
            String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
            httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
            httpResponse.getWriter().flush();
        }
    }

    @PostMapping("/getnotice")
    public void getnotice(HttpServletRequest request) throws IOException, AlipayApiException {
        System.out.println("回调啦，。。。。。。。。。。。。。。。。。。。");

        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //验签
        boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE);

        if (signVerified) {//验证成功
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            //appid
            String app_id = new String(request.getParameter("app_id").getBytes("ISO-8859-1"), "UTF-8");
            //total_amount
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            //交易成功处理
            //交易成功处理
            if (trade_status.equals("TRADE_SUCCESS")) {

                PayStatusDto payStatusDto = new PayStatusDto();
                payStatusDto.setOut_trade_no(out_trade_no);
                payStatusDto.setTrade_status(trade_status);
                payStatusDto.setApp_id(app_id);
                payStatusDto.setTrade_no(trade_no);
                payStatusDto.setTotal_amount(total_amount);
                System.out.println(payStatusDto);
                //处理逻辑。。。
                orderService.saveAliPayStatus(payStatusDto);
            }
        } else {
            System.out.println("支付宝支付验签失败了或者支付失败了");
        }


    }

}