package com.b2cmall.shop.service.event;
/** Carries an identifier only; never log passwords or credential hashes. */
public class ShopRegisterEvent {
    public final Long shopId;
    public final String account;
    public ShopRegisterEvent(Long shopId,String account) {this.shopId=shopId;this.account=account;}
}
