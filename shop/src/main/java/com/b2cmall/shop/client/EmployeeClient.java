package com.b2cmall.shop.client;
import com.b2cmall.common.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="employee-service",url="${employee.service-url:}")
public interface EmployeeClient {
    @PostMapping("/employee/save")
    Result<Long> initialize(@RequestHeader("X-Internal-Token") String token,@RequestBody EmployeeInitRequest request);
}
