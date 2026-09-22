package com.b2cmall.shop.service.impl;
import com.b2cmall.common.BizException;
import com.b2cmall.shop.dao.*;
import com.b2cmall.shop.dao.po.ShopPO;
import com.b2cmall.shop.service.*;
import com.b2cmall.shop.service.event.ShopRegisterEvent;
import com.b2cmall.shop.dto.ShopRegisterDTO;
import com.b2cmall.shop.web.response.ShopRegisterResponse;
import com.google.common.eventbus.EventBus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
@Service
public class ShopServiceImpl implements ShopService {
    private final ShopMapper mapper;
    private final RegistrationStore store;
    private final EventBus bus;
    private final BCryptPasswordEncoder passwords=new BCryptPasswordEncoder();
    public ShopServiceImpl(ShopMapper mapper,RegistrationStore store,EventBus bus) {
        this.mapper=mapper;this.store=store;this.bus=bus;
    }
    // Only local registration is serialized; HTTP initialization runs in the event pool.
    public synchronized ShopRegisterResponse register(ShopRegisterDTO request) {
        ShopPO shop=mapper.findByName(request.shopName);
        if (shop==null) {
            shop=new ShopPO();shop.shopName=request.shopName;shop.adminAccount=request.adminAccount;
            shop.adminPassword=passwords.encode(request.adminPassword);
            try {store.create(shop);} catch (DuplicateKeyException e) {throw new BizException(409,"店铺名已存在，请重试");}
        } else { authenticate(shop,request); }
        if (!mapper.employeeDone(shop.id) || !mapper.messageDone(shop.id)) {
            mapper.retry(shop.id);
            try { bus.post(new ShopRegisterEvent(shop.id,shop.adminAccount)); }
            catch (java.util.concurrent.RejectedExecutionException e) {
                mapper.employeeFailed(shop.id);mapper.messageFailed(shop.id);
            }
        }
        return response(shop);
    }
    public ShopRegisterResponse status(ShopRegisterDTO request) {
        ShopPO shop=mapper.findByName(request.shopName);
        if (shop==null) throw new BizException(404,"店铺不存在");
        authenticate(shop,request);return response(shop);
    }
    private void authenticate(ShopPO shop,ShopRegisterDTO request) {
        if (!shop.adminAccount.equals(request.adminAccount) || !passwords.matches(request.adminPassword,shop.adminPassword))
            throw new BizException(409,"店铺名已存在且注册信息不一致");
    }
    private ShopRegisterResponse response(ShopPO shop) {
        ShopRegisterResponse result=new ShopRegisterResponse();result.shopId=shop.id;result.shopName=shop.shopName;
        result.employeeInitialized=mapper.employeeDone(shop.id);result.welcomeMessageCreated=mapper.messageDone(shop.id);
        result.state=result.employeeInitialized && result.welcomeMessageCreated ? "COMPLETED" : mapper.failed(shop.id) ? "FAILED" : "PENDING";
        return result;
    }
}
