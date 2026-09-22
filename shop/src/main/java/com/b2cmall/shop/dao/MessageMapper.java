package com.b2cmall.shop.dao;
import com.b2cmall.shop.dao.po.MessagePO;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface MessageMapper { int saveWelcome(MessagePO message); }
