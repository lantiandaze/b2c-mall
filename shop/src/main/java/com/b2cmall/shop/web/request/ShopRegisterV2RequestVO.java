package com.b2cmall.shop.web.request;
import javax.validation.constraints.*;
public class ShopRegisterV2RequestVO extends ShopRegisterRequestVO {
    @Size(max=32) @Pattern(regexp="[A-Za-z0-9_-]+") public String source;
    @Size(max=64) @Pattern(regexp="[A-Za-z0-9_-]+") public String invaliCode;
}
