package com.b2cmall.shop.dto;
/** Internal command; never serialize credentials into responses or event payloads. */
public class ShopRegisterDTO {
    public String shopName,adminAccount,adminPassword,source,invaliCode;
}
