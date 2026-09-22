package com.b2cmall.employee.service;
import com.b2cmall.common.EmployeeInitRequest;
import com.b2cmall.employee.web.request.LoginRequestVO;
public interface EmployeeService {
    Long initialize(EmployeeInitRequest request);
    String login(LoginRequestVO request, String ip, String device);
}
