package com.b2cmall.shop.service.event.handler;
import com.b2cmall.shop.service.MessageService;
import com.b2cmall.shop.service.event.ShopRegisterEvent;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;
@Component
public class InitShopMessageEventHandle {
    private final MessageService store;
    private final com.b2cmall.shop.dao.ShopMapper mapper;
    public InitShopMessageEventHandle(MessageService store,com.b2cmall.shop.dao.ShopMapper mapper) {this.store=store;this.mapper=mapper;}
    @Subscribe public void handle(ShopRegisterEvent event) {try {store.saveWelcomeMessage(event.shopId);} catch (Exception e) {mapper.messageFailed(event.shopId);throw e;} }
}
