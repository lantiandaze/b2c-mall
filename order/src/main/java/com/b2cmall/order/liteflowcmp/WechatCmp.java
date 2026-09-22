package com.b2cmall.order.liteflowcmp;
import com.b2cmall.order.flow.PaymentContext;
import com.b2cmall.order.service.PaymentService;
import com.b2cmall.order.enums.PayTypeEnum;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
@LiteflowComponent("wechat")
public class WechatCmp extends NodeComponent {
    private final PaymentService payment;
    public WechatCmp(PaymentService payment){this.payment=payment;}
    @Override public void process(){
        PaymentContext context=getContextBean(PaymentContext.class);context.steps.add("wechat");
        payment.invokeStrategy(context,PayTypeEnum.WECHAT);
    }
}

