package com.b2cmall.order.service;
import com.b2cmall.order.enums.PayTypeEnum;
import com.b2cmall.order.dao.po.PaymentPO;
import com.b2cmall.order.web.response.PayResponseVO;
public interface PayStrategyService {
    PayTypeEnum payType();
    PayResponseVO pay(PaymentPO payment);
}

