package com.b2cmall.shop.service;
import com.b2cmall.shop.dto.ShopRegisterDTO;
import com.b2cmall.shop.web.response.ShopRegisterResponse;
public interface ShopService { ShopRegisterResponse register(ShopRegisterDTO request); ShopRegisterResponse status(ShopRegisterDTO request); }
