package com.b2cmall.order.feign;
import com.b2cmall.common.Result;
import com.b2cmall.order.bean.CurrentEmployeeBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="employee-service",url="${employee.service-url:}")
public interface EmployeeFeignClient {
    @GetMapping("/employee/me")
    Result<CurrentEmployeeBean> current(@RequestHeader("Authorization") String token);
}

