package com.b2cmall.order.web.controller;
import com.b2cmall.common.Result;
import com.b2cmall.order.service.PaymentService;
import com.b2cmall.order.service.PaymentFlowService;
import com.b2cmall.order.web.request.*;
import com.b2cmall.order.web.response.PayResponseVO;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
@RestController
@RequestMapping("/order")
public class PayController {
    private final PaymentService payment;
    private final PaymentFlowService flow;
    public PayController(PaymentService payment,PaymentFlowService flow){this.payment=payment;this.flow=flow;}
    @PostMapping("/pay")
    public Result<PayResponseVO> pay(@Valid @RequestBody PayRequestVO request,@RequestHeader("Authorization")String token){return Result.success(flow.pay(request,token));}
    @PostMapping("/mock/callback")
    public Result<String> callback(@Valid @RequestBody MockCallbackRequestVO request){return Result.success(payment.callback(request));}
}

