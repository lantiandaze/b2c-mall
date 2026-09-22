package com.b2cmall.order.liteflowcmp;
import com.b2cmall.order.flow.PaymentContext;
import com.b2cmall.order.service.PaymentService;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
@LiteflowComponent("priceCheck")
public class SkuPriceCheckCmp extends NodeComponent {
    private final PaymentService payment;
    public SkuPriceCheckCmp(PaymentService payment){this.payment=payment;}
    @Override public void process(){
        PaymentContext context=getContextBean(PaymentContext.class);
        context.steps.add("priceCheck");payment.prepare(context);
    }
}

