package com.b2cmall.order.service.impl;
import com.b2cmall.order.service.PayStrategyService;
import com.b2cmall.order.enums.PayTypeEnum;
import com.b2cmall.order.dao.po.PaymentPO;
import com.b2cmall.order.web.response.PayResponseVO;
import org.springframework.stereotype.Component;
@Component
public class AlipayStrategyServiceImpl implements PayStrategyService {
    public PayTypeEnum payType(){return PayTypeEnum.ALIPAY;}
    public PayResponseVO pay(PaymentPO payment){
        PayResponseVO response=new PayResponseVO();
        response.message="支付宝策略模拟：未调用支付宝，未产生真实扣款";
        return response;
    }
}

