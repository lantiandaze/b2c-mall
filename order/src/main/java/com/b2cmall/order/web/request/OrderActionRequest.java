package com.b2cmall.order.web.request;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
public class OrderActionRequest {
    @NotBlank @Size(max=64) public String orderId;
}
