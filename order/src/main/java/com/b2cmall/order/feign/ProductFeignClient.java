package com.b2cmall.order.feign;
import com.b2cmall.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="product-service",url="${product.service-url:}")
public interface ProductFeignClient {
    @GetMapping("/product/{id}")
    Result<ProductSnapshot> find(@PathVariable("id") Long id,@RequestHeader("Authorization") String token);
    class ProductSnapshot {
        public Long id,shopId,price;
        public Integer type,stock,status;
        public String name;
    }
}

