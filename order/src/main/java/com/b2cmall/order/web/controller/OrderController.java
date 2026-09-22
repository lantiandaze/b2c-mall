package com.b2cmall.order.web.controller;
import com.b2cmall.common.Result;
import com.b2cmall.order.dao.po.OrderPO;
import com.b2cmall.order.service.OrderService;
import com.b2cmall.order.web.request.CreateOrderRequestVO;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orders;
    public OrderController(OrderService orders){this.orders=orders;}
    @GetMapping("/health") public Result<String> health(){return Result.success("order-service");}
    @PostMapping("/create")
    public Result<OrderPO> create(@Valid @RequestBody CreateOrderRequestVO request,@RequestHeader("Authorization")String token) {
        return Result.success(orders.create(request,token));
    }
    @GetMapping("/{id}") public Result<OrderPO> find(@PathVariable String id){return Result.success(orders.owned(id));}
    @PostMapping("/sent")
    public Result<OrderPO> sent(@Valid @RequestBody com.b2cmall.order.web.request.OrderActionRequest request){
        return Result.success(orders.advance(request.orderId,false));
    }
    @PostMapping("/complete")
    public Result<OrderPO> complete(@Valid @RequestBody com.b2cmall.order.web.request.OrderActionRequest request){
        return Result.success(orders.advance(request.orderId,true));
    }
}
