package com.b2cmall.order.service;
import com.b2cmall.common.BizException;
import com.b2cmall.order.component.RequestHolderComponent;
import com.b2cmall.order.flow.PaymentContext;
import com.b2cmall.order.web.request.PayRequestVO;
import com.b2cmall.order.web.response.PayResponseVO;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.dao.DuplicateKeyException;
import javax.validation.Validator;

@Service
public class PaymentFlowService {
    private final FlowExecutor executor;
    private final TransactionTemplate transactions;
    private final Validator validator;
    public PaymentFlowService(FlowExecutor executor,TransactionTemplate transactions,Validator validator) {
        this.executor=executor;this.transactions=transactions;this.validator=validator;
    }
    public synchronized PayResponseVO pay(PayRequestVO request,String authorization) {
        if(request==null || !validator.validate(request).isEmpty())throw new BizException(400,"支付请求参数不正确");
        PaymentContext context=new PaymentContext();
        context.request=request;context.employee=RequestHolderComponent.current();context.authorization=authorization;
        try {
            return transactions.execute(status -> {
                // THEN and SWITCH execute synchronously, preserving the local transaction.
                LiteflowResponse result=executor.execute2Resp("payChain",request,context);
                if(!result.isSuccess()) {
                    Throwable cause=result.getCause();
                    if(cause instanceof RuntimeException)throw (RuntimeException)cause;
                    throw new IllegalStateException("支付流程执行失败",cause);
                }
                if(context.response==null)throw new IllegalStateException("支付流程未生成结果");
                return context.response;
            });
        }catch(DuplicateKeyException e){throw new BizException(409,"支付请求已存在，请重试原requestId");}
        finally {context.authorization=null;}
    }
}

