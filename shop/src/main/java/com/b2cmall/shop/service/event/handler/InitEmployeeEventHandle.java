package com.b2cmall.shop.service.event.handler;
import com.b2cmall.common.*;
import com.b2cmall.shop.client.EmployeeClient;
import com.b2cmall.shop.dao.ShopMapper;
import com.b2cmall.shop.dao.po.ShopPO;
import com.b2cmall.shop.service.event.ShopRegisterEvent;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class InitEmployeeEventHandle {
    private final ShopMapper mapper;
    private final EmployeeClient client;
    private final String token;
    public InitEmployeeEventHandle(ShopMapper mapper,EmployeeClient client,@Value("${internal.token}") String token) {
        this.mapper=mapper;this.client=client;this.token=token;
    }
    @Subscribe
    public void handle(ShopRegisterEvent event) {
        try {
        if (mapper.employeeDone(event.shopId)) return;
        ShopPO shop=mapper.findById(event.shopId);
        Result<Long> response=client.initialize(token,new EmployeeInitRequest(shop.id,shop.adminAccount,shop.adminPassword));
        if (response==null || response.status!=200 || response.data==null) throw new IllegalStateException("Employee initialization failed");
        mapper.employeeDoneUpdate(event.shopId);
        org.slf4j.LoggerFactory.getLogger(getClass()).info("Employee initialized shopId={} employeeId={}",event.shopId,response.data);
        } catch (Exception e) { mapper.employeeFailed(event.shopId);throw e; }
    }
}
