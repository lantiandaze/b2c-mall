package com.b2cmall.order.liteflowcmp;
import com.b2cmall.order.flow.PaymentContext;
import com.b2cmall.order.service.PaymentService;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
@LiteflowComponent("orderStatus")
public class OrderStatusCmp extends NodeComponent {
    private final PaymentService payment;
    public OrderStatusCmp(PaymentService payment){this.payment=payment;}
    @Override public void process(){
        PaymentContext context=getContextBean(PaymentContext.class);context.steps.add("orderStatus");
        payment.finishInitiation(context);
    }
}

