package com.b2cmall.order.config;
import com.b2cmall.order.enums.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.*;
import org.springframework.statemachine.config.builders.*;
import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class OrderStateConfig extends EnumStateMachineConfigurerAdapter<OrderStatus,OrderEvent> {
    @Override public void configure(StateMachineStateConfigurer<OrderStatus,OrderEvent> states) throws Exception {
        states.withStates().initial(OrderStatus.WAIT_PAY).states(EnumSet.allOf(OrderStatus.class));
    }
    @Override public void configure(StateMachineTransitionConfigurer<OrderStatus,OrderEvent> transitions) throws Exception {
        transitions.withExternal().source(OrderStatus.WAIT_PAY).target(OrderStatus.PAID).event(OrderEvent.PAY)
            .and().withExternal().source(OrderStatus.PAID).target(OrderStatus.SENT).event(OrderEvent.SENT)
            .and().withExternal().source(OrderStatus.SENT).target(OrderStatus.COMPLETED).event(OrderEvent.COMPLETED);
    }
}
