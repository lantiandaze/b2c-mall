package com.b2cmall.order.dao;
import com.b2cmall.order.dao.po.*;
import org.apache.ibatis.annotations.*;
@Mapper
public interface OrderMapper {
    @Select("SELECT * FROM tb_order WHERE order_id=#{id}")
    OrderPO find(String id);
    @Select("SELECT * FROM tb_order WHERE shop_id=#{shopId} AND user_id=#{userId} AND request_id=#{requestId}")
    OrderPO findRequest(@Param("shopId")Long shopId,@Param("userId")Long userId,@Param("requestId")String requestId);
    @Insert("INSERT INTO tb_order(order_id,shop_id,user_id,request_id,sku_id,sku_name,sku_type,quantity,unit_price,total_amount,status) VALUES(#{orderId},#{shopId},#{userId},#{requestId},#{skuId},#{skuName},#{skuType},#{quantity},#{unitPrice},#{totalAmount},'WAIT_PAY')")
    int save(OrderPO order);
    @Update("UPDATE tb_order SET status='PAID',paid_at=CURRENT_TIMESTAMP WHERE order_id=#{id} AND status='WAIT_PAY'")
    int markPaid(String id);
    @Update("UPDATE tb_order SET status=#{next},sent_at=CASE WHEN #{next}='SENT' THEN CURRENT_TIMESTAMP ELSE sent_at END,completed_at=CASE WHEN #{next}='COMPLETED' THEN CURRENT_TIMESTAMP ELSE completed_at END WHERE order_id=#{id} AND status=#{previous}")
    int transition(@Param("id")String id,@Param("previous")String previous,@Param("next")String next);
    @Select("SELECT * FROM tb_payment WHERE payment_no=#{id}")
    PaymentPO payment(String id);
    @Select("SELECT * FROM tb_payment WHERE order_id=#{orderId} AND request_id=#{requestId}")
    PaymentPO paymentRequest(@Param("orderId")String orderId,@Param("requestId")String requestId);
    @Select("SELECT * FROM tb_payment WHERE order_id=#{orderId} AND status='PENDING'")
    PaymentPO pending(String orderId);
    @Insert("INSERT INTO tb_payment(payment_no,order_id,request_id,pay_type,amount,mode,status) VALUES(#{paymentNo},#{orderId},#{requestId},#{payType},#{amount},'MOCK','PENDING')")
    int savePayment(PaymentPO payment);
    @Update("UPDATE tb_payment SET status=#{status},updated_at=CURRENT_TIMESTAMP WHERE payment_no=#{paymentNo} AND status='PENDING'")
    int finishPayment(@Param("paymentNo")String paymentNo,@Param("status")String status);
    @Insert("INSERT INTO tb_order_log(order_id,event_key,from_status,to_status,detail) VALUES(#{orderId},#{eventKey},#{fromStatus},#{toStatus},#{detail})")
    int log(@Param("orderId")String orderId,@Param("eventKey")String eventKey,@Param("fromStatus")String fromStatus,
            @Param("toStatus")String toStatus,@Param("detail")String detail);
}
