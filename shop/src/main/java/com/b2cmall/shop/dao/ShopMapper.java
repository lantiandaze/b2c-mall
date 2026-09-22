package com.b2cmall.shop.dao;
import com.b2cmall.shop.dao.po.ShopPO;
import org.apache.ibatis.annotations.*;
@Mapper
public interface ShopMapper {
    ShopPO findByName(String name);
    @Select("SELECT id,shop_name AS shopName,admin_account AS adminAccount,admin_password AS adminPassword FROM tb_shop WHERE id=#{id}")
    ShopPO findById(Long id);
    int insert(ShopPO shop);
    @Insert("INSERT INTO tb_registration_tasks(shop_id) VALUES(#{id})") int createTask(Long id);
    @Select("SELECT employee_done FROM tb_registration_tasks WHERE shop_id=#{id}") boolean employeeDone(Long id);
    @Select("SELECT message_done FROM tb_registration_tasks WHERE shop_id=#{id}") boolean messageDone(Long id);
    @Select("SELECT employee_failed OR message_failed FROM tb_registration_tasks WHERE shop_id=#{id}") boolean failed(Long id);
    @Update("UPDATE tb_registration_tasks SET employee_done=1,employee_failed=0 WHERE shop_id=#{id}") int employeeDoneUpdate(Long id);
    @Update("UPDATE tb_registration_tasks SET message_done=1,message_failed=0 WHERE shop_id=#{id}") int messageDoneUpdate(Long id);
    @Update("UPDATE tb_registration_tasks SET employee_failed=1 WHERE shop_id=#{id} AND employee_done=0") int employeeFailed(Long id);
    @Update("UPDATE tb_registration_tasks SET message_failed=1 WHERE shop_id=#{id} AND message_done=0") int messageFailed(Long id);
    @Update("UPDATE tb_registration_tasks SET employee_failed=0,message_failed=0 WHERE shop_id=#{id}") int retry(Long id);
}
