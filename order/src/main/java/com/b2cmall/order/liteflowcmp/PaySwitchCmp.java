package com.b2cmall.order.liteflowcmp;
import com.b2cmall.order.flow.PaymentContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeSwitchComponent;
import java.util.Locale;
@LiteflowComponent("payswitch")
public class PaySwitchCmp extends NodeSwitchComponent {
    @Override public String processSwitch() {
        PaymentContext context=getContextBean(PaymentContext.class);context.steps.add("payswitch");
        return context.payType.name().toLowerCase(Locale.ROOT);
    }
}

