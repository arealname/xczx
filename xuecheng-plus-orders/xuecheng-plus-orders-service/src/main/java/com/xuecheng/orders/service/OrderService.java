package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;


public interface OrderService {


   /**
    * @param addOrderDto 订单信息
    * @return PayRecordDto 支付记录(包括二维码)
    * @description 创建商品订单
    * @author Mr.M
    * @date 2022/10/4 11:02
    */
   public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) throws Exception;

   XcPayRecord getPayOrder(String payNo);

    void saveAliPayStatus(PayStatusDto payStatusDto);
}