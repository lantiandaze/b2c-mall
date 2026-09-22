package com.b2cmall.shop.service;
import com.b2cmall.shop.dao.ShopMapper;
import com.b2cmall.shop.dao.po.ShopPO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class RegistrationStore {
    private final ShopMapper mapper;
    public RegistrationStore(ShopMapper mapper) {this.mapper=mapper;}
    @Transactional
    public void create(ShopPO shop) { mapper.insert(shop);mapper.createTask(shop.id); }

}
