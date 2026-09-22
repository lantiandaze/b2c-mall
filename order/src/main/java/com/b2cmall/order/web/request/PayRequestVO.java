package com.b2cmall.order.web.request;
import javax.validation.constraints.*;
public class PayRequestVO {
    @NotBlank @Size(max=64) public String orderId;
    @NotBlank @Size(max=20) public String payType;
    @NotBlank @Pattern(regexp="[a-zA-Z0-9_-]{1,64}",message="requestId格式不正确") public String requestId;
    // Optional classroom compatibility field: expected TOTAL amount in cents, never a pricing source.
    @Positive public Long skuPrice;
}

