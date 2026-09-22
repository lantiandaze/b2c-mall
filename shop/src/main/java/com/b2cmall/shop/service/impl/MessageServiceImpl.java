package com.b2cmall.shop.service.impl;
import com.b2cmall.shop.service.MessageService;
import com.b2cmall.shop.dao.*;
import com.b2cmall.shop.dao.po.MessagePO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class MessageServiceImpl implements MessageService {
    private final MessageMapper messages;
    private final ShopMapper shops;
    public MessageServiceImpl(MessageMapper messages,ShopMapper shops) {this.messages=messages;this.shops=shops;}
    @Transactional
    public void saveWelcomeMessage(Long shopId) {
        MessagePO message=new MessagePO();message.shopId=shopId;
        message.title="欢迎入驻";message.content="店铺注册成功，欢迎使用商城";
        messages.saveWelcome(message);shops.messageDoneUpdate(shopId);
    }
}
