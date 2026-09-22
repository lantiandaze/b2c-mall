package com.b2cmall.employee.service.event.handler;
import com.b2cmall.employee.dao.EmployeeMapper;
import com.b2cmall.employee.service.event.LoginEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
@Component
public class LoginStatusObserver {
    private final EmployeeMapper mapper;
    public LoginStatusObserver(EmployeeMapper mapper){this.mapper=mapper;}
    @EventListener public void onLogin(LoginEvent event) {
        if(event.success)mapper.updateLoginStatus(event.userId);
    }
}

