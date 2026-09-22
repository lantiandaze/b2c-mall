package com.b2cmall.employee.service.event.handler;
import com.b2cmall.employee.dao.EmployeeMapper;
import com.b2cmall.employee.service.event.LoginEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
@Component
public class LoginAuditObserver {
    private final EmployeeMapper mapper;
    public LoginAuditObserver(EmployeeMapper mapper){this.mapper=mapper;}
    @EventListener public void onLogin(LoginEvent event){mapper.saveLoginLog(event);}
}

