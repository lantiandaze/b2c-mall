package com.b2cmall.order.service;
import com.b2cmall.common.*;
import com.b2cmall.order.dao.OrderMapper;
import com.b2cmall.order.dao.po.*;
import com.b2cmall.order.enums.PayTypeEnum;
import com.b2cmall.order.web.request.*;
import com.b2cmall.order.web.response.PayResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;

@Service
public class PaymentService {
    private final Map<PayTypeEnum,PayStrategyService> strategies=new EnumMap<>(PayTypeEnum.class);
    private final OrderMapper mapper;
    private final OrderService orders;
    private final MockCallbackSigner signer;
    private final TransactionTemplate transactions;
    private final boolean mockEnabled;
    public PaymentService(List<PayStrategyService> strategies,OrderMapper mapper,OrderService orders,
                          MockCallbackSigner signer,TransactionTemplate transactions,
                          @Value("${payment.mock-enabled:true}") boolean mockEnabled) {
        for(PayStrategyService strategy:strategies)
            if(this.strategies.put(strategy.payType(),strategy)!=null)
                throw new IllegalStateException("重复支付策略："+strategy.payType());
        this.mapper=mapper;this.orders=orders;this.signer=signer;this.transactions=transactions;this.mockEnabled=mockEnabled;
    }
    public void prepare(com.b2cmall.order.flow.PaymentContext context) {
        requireMock();
        PayRequestVO request=context.request;
        context.payType=PayTypeEnum.parse(request.payType);
        if(!strategies.containsKey(context.payType))throw new BizException(400,"支付方式尚未实现");
        OrderPO order=mapper.find(request.orderId);
        if(order==null || !context.employee.shopId.equals(order.shopId) || !context.employee.id.equals(order.userId))
            throw new BizException(404,"订单不存在");
        context.order=order;
        if(request.skuPrice!=null && !order.totalAmount.equals(request.skuPrice))
            throw new BizException(409,"请求金额与订单金额不一致");
        PaymentPO previous=mapper.paymentRequest(order.orderId,request.requestId);
        if(previous!=null) {
            if(!previous.payType.equals(context.payType.name()))throw new BizException(409,"相同requestId不能更换支付方式");
            context.payment=previous;context.reused=true;return;
        }
        if(!"WAIT_PAY".equals(order.status))throw new BizException(409,"订单已支付，不能再次发起支付");
        if(mapper.pending(order.orderId)!=null)throw new BizException(409,"该订单已有待确认支付，请复用原requestId");
    }
    public void invokeStrategy(com.b2cmall.order.flow.PaymentContext context,PayTypeEnum branch) {
        if(branch!=context.payType)throw new IllegalStateException("支付分支不匹配");
        if(context.reused) {
            context.response=new PayResponseVO();
            context.response.message="返回已有模拟支付记录，未再次调用支付策略";
            return;
        }
        PaymentPO created=new PaymentPO();created.paymentNo=UUID.randomUUID().toString();
        created.orderId=context.order.orderId;created.requestId=context.request.requestId;
        created.payType=branch.name();created.amount=context.order.totalAmount;created.mode="MOCK";created.status="PENDING";
        if(mapper.savePayment(created)!=1)throw new IllegalStateException("保存支付记录失败");
        context.payment=created;context.response=strategies.get(branch).pay(created);
        if(context.response==null)throw new IllegalStateException("支付策略未返回结果");
    }
    public void finishInitiation(com.b2cmall.order.flow.PaymentContext context) {
        PaymentPO payment=context.payment;
        if(!context.reused) {
            // Initiation is not confirmation: WAIT_PAY is retained until a verified callback.
            if(mapper.log(payment.orderId,payment.paymentNo+":STARTED","WAIT_PAY","WAIT_PAY",
                "MOCK "+payment.payType+" PENDING")!=1)throw new IllegalStateException("保存支付发起日志失败");
        }
        PayResponseVO response=context.response;
        response.orderId=payment.orderId;response.paymentNo=payment.paymentNo;response.payType=payment.payType;
        response.amount=payment.amount;response.status=payment.status;response.mode="MOCK";
        response.callbackUrl="/api/order/mock/callback";
        if("PENDING".equals(payment.status)) {
            response.successSignature=signer.sign(payment,"SUCCESS");
            response.failureSignature=signer.sign(payment,"FAILED");
        }
    }
    // MOCK signatures are teaching credentials, not Alipay/Wechat signatures.
    public synchronized String callback(MockCallbackRequestVO request) {
        requireMock();
        return transactions.execute(status -> {
            PaymentPO payment=mapper.payment(request.paymentNo);
            if(payment==null || !signer.valid(payment,request.result,request.signature))
                throw new BizException(401,"模拟回调签名无效");
            if(!payment.orderId.equals(request.orderId)||!payment.payType.equals(request.payType)||!payment.amount.equals(request.amount))
                throw new BizException(400,"模拟回调订单、渠道或金额不匹配");
            if(!"PENDING".equals(payment.status)) {
                if(payment.status.equals(request.result))return "重复回调已确认";
                throw new BizException(409,"支付结果已确定，不能反向修改");
            }
            OrderPO order=mapper.find(payment.orderId);
            if(order==null||!"WAIT_PAY".equals(order.status))throw new BizException(409,"订单状态不允许处理此支付结果");
            if(mapper.finishPayment(payment.paymentNo,request.result)!=1)throw new BizException(409,"支付状态已改变");
            if("SUCCESS".equals(request.result))orders.confirmPaid(order);
            mapper.log(order.orderId,payment.paymentNo+":RESULT","WAIT_PAY",
                "SUCCESS".equals(request.result)?"PAID":"WAIT_PAY","MOCK "+payment.payType+" "+request.result);
            return "模拟支付结果已处理";
        });
    }
    private void requireMock() {
        if(!mockEnabled)throw new BizException(503,"模拟支付已关闭；真实支付渠道尚未配置");
    }
}

