package com.b2cmall.order.web.request;
import javax.validation.constraints.*;
public class CreateOrderRequestVO {
    @NotNull @Positive public Long skuId;
    @NotNull @Min(1) @Max(999) public Integer quantity;
    @NotBlank @Pattern(regexp="[a-zA-Z0-9_-]{1,64}",message="requestId需为1至64位字母数字下划线或短横线")
    public String requestId;
}

