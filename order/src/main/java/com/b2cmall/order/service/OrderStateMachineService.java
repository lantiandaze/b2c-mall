package com.b2cmall.order.service;
import com.b2cmall.common.BizException;
import com.b2cmall.order.enums.*;
import org.springframework.stereotype.Service;
import org.springframework.statemachine.*;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.List;

@Service
public class OrderStateMachineService {
    private static final Duration TIMEOUT=Duration.ofSeconds(5);
    private final StateMachineFactory<OrderStatus,OrderEvent> factory;
    public OrderStateMachineService(StateMachineFactory<OrderStatus,OrderEvent> factory){this.factory=factory;}

    public OrderStatus transition(String orderId,String persisted,OrderEvent event){
        if(orderId==null || orderId.trim().isEmpty() || event==null)throw new BizException(400,"订单或事件不能为空");
        final OrderStatus source;
        try {source=OrderStatus.valueOf(persisted);}
        catch(Exception e){throw new IllegalStateException("未知订单状态",e);}
        // Idempotent operations are acknowledged without replaying transitions.
        if(event==OrderEvent.PAY && source!=OrderStatus.WAIT_PAY)return source;
        if(event==OrderEvent.SENT && (source==OrderStatus.SENT||source==OrderStatus.COMPLETED))return source;
        if(event==OrderEvent.COMPLETED && source==OrderStatus.COMPLETED)return source;
        StateMachine<OrderStatus,OrderEvent> machine=factory.getStateMachine(orderId);
        try {
            machine.stopReactively().block(TIMEOUT);
            machine.getStateMachineAccessor().doWithAllRegions(access ->
                access.resetStateMachineReactively(new DefaultStateMachineContext<OrderStatus,OrderEvent>(
                    source,null,null,null)).block(TIMEOUT));
            machine.startReactively().block(TIMEOUT);
            List<StateMachineEventResult<OrderStatus,OrderEvent>> results=machine
                .sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).collectList().block(TIMEOUT);
            if(results==null||results.isEmpty())throw new IllegalStateException("状态机未返回事件结果");
            for(StateMachineEventResult<OrderStatus,OrderEvent> result:results){
                result.complete().block(TIMEOUT);
                if(result.getResultType()!=StateMachineEventResult.ResultType.ACCEPTED)
                    throw new BizException(409,"当前订单状态不允许此操作");
            }
            OrderStatus target=event==OrderEvent.PAY?OrderStatus.PAID:
                event==OrderEvent.SENT?OrderStatus.SENT:OrderStatus.COMPLETED;
            if(machine.hasStateMachineError()||machine.getState()==null||machine.getState().getId()!=target)
                throw new IllegalStateException("状态机未完成预期转换");
            return target;
        } finally {
            machine.stopReactively().block(TIMEOUT);
        }
    }
}
