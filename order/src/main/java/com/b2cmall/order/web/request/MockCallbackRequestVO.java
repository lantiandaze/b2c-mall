package com.b2cmall.order.web.request;
import javax.validation.constraints.*;
public class MockCallbackRequestVO {
    @NotBlank @Size(max=64) public String paymentNo;
    @NotBlank @Size(max=64) public String orderId;
    @NotBlank @Size(max=20) public String payType;
    @NotNull @Positive public Long amount;
    @NotBlank @Pattern(regexp="SUCCESS|FAILED") public String result;
    @NotBlank @Pattern(regexp="[0-9a-f]{64}",message="模拟回调签名格式不正确") public String signature;
}

