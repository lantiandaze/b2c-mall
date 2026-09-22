package com.b2cmall.order.service;
import com.b2cmall.common.*;
import com.b2cmall.order.component.RequestHolderComponent;
import com.b2cmall.order.bean.CurrentEmployeeBean;
import com.b2cmall.order.dao.OrderMapper;
import com.b2cmall.order.dao.po.OrderPO;
import com.b2cmall.order.feign.ProductFeignClient;
import com.b2cmall.order.feign.ProductFeignClient.ProductSnapshot;
import com.b2cmall.order.web.request.CreateOrderRequestVO;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class OrderService {
    private final OrderMapper mapper;
    private final ProductFeignClient product;
    private final TransactionTemplate transactions;
    private final OrderStateMachineService states;
    public OrderService(OrderMapper mapper,ProductFeignClient product,TransactionTemplate transactions,OrderStateMachineService states) {
        this.mapper=mapper;this.product=product;this.transactions=transactions;this.states=states;
    }
    public void confirmPaid(OrderPO order) {
        com.b2cmall.order.enums.OrderStatus next=states.transition(order.orderId,order.status,com.b2cmall.order.enums.OrderEvent.PAY);
        if(next!=com.b2cmall.order.enums.OrderStatus.PAID || mapper.markPaid(order.orderId)!=1)
            throw new BizException(409,"订单状态已改变");
    }
    public synchronized OrderPO advance(String id,boolean complete) {
        return transactions.execute(tx -> {
            OrderPO order=owned(id);
            com.b2cmall.order.enums.OrderEvent event=complete?com.b2cmall.order.enums.OrderEvent.COMPLETED:com.b2cmall.order.enums.OrderEvent.SENT;
            String next=states.transition(id,order.status,event).name();
            if(next.equals(order.status))return order;
            if(mapper.transition(id,order.status,next)!=1) {
                OrderPO latest=owned(id);
                if(states.transition(id,latest.status,event).name().equals(latest.status))return latest;
                throw new BizException(409,"订单状态已改变，请重试");
            }
            if(mapper.log(id,id+":"+next,order.status,next,complete?"课堂确认完成":"课堂模拟发货")!=1)
                throw new IllegalStateException("状态日志写入失败");
            return mapper.find(id);
        });
    }
    public OrderPO owned(String id) {
        OrderPO order=mapper.find(id);
        CurrentEmployeeBean user=RequestHolderComponent.current();
        if(order==null || !user.shopId.equals(order.shopId) || !user.id.equals(order.userId))
            throw new BizException(404,"订单不存在");
        return order;
    }
    public synchronized OrderPO create(CreateOrderRequestVO request,String token) {
        CurrentEmployeeBean user=RequestHolderComponent.current();
        OrderPO previous=mapper.findRequest(user.shopId,user.id,request.requestId);
        if(previous!=null)return same(previous,request);
        ProductSnapshot sku;
        try {
            Result<ProductSnapshot> response=product.find(request.skuId,token);
            if(response==null||response.status!=200||response.data==null)throw new BizException(503,"商品查询失败");
            sku=response.data;
        } catch(FeignException e) {throw new BizException(e.status()==404?404:503,"商品不可用或服务暂不可用");}
        if(!user.shopId.equals(sku.shopId)||!Integer.valueOf(1).equals(sku.status))throw new BizException(409,"商品未上架或不属于当前店铺");
        if(Integer.valueOf(1).equals(sku.type) && (sku.stock==null || sku.stock<request.quantity))
            throw new BizException(409,"实物商品库存不足");
        if(sku.price==null||sku.price<=0)throw new BizException(409,"商品价格无效");
        long total;
        try {total=Math.multiplyExact(sku.price,(long)request.quantity);}
        catch(ArithmeticException e) {throw new BizException(400,"订单金额超出范围");}
        OrderPO order=new OrderPO();order.orderId=UUID.randomUUID().toString();order.shopId=user.shopId;order.userId=user.id;
        order.requestId=request.requestId;order.skuId=sku.id;order.skuName=sku.name;order.skuType=sku.type;
        order.quantity=request.quantity;order.unitPrice=sku.price;order.totalAmount=total;
        try {
            return transactions.execute(status -> {
                mapper.save(order);
                mapper.log(order.orderId,order.orderId+":CREATED",null,"WAIT_PAY","课堂订单创建");
                return mapper.find(order.orderId);
            });
        } catch(DuplicateKeyException e) {
            OrderPO existing=mapper.findRequest(user.shopId,user.id,request.requestId);
            if(existing==null)throw e;
            return same(existing,request);
        }
    }
    private OrderPO same(OrderPO existing,CreateOrderRequestVO request) {
        if(!existing.skuId.equals(request.skuId)||!existing.quantity.equals(request.quantity))
            throw new BizException(409,"相同requestId不能用于不同订单内容");
        return existing;
    }
}
