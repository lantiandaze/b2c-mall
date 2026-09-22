package com.b2cmall.employee.service.impl;
import com.b2cmall.common.*;
import com.b2cmall.employee.dao.EmployeeMapper;
import com.b2cmall.employee.dao.po.EmployeePO;
import com.b2cmall.employee.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import com.b2cmall.employee.service.TokenService;
import com.b2cmall.employee.web.request.LoginRequestVO;
import com.b2cmall.employee.service.event.LoginEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeMapper mapper;
    private final TokenService tokens;
    private final ApplicationEventPublisher events;
    private final TransactionTemplate transactions;
    private final BCryptPasswordEncoder passwords=new BCryptPasswordEncoder();
    public EmployeeServiceImpl(EmployeeMapper mapper,TokenService tokens,ApplicationEventPublisher events,TransactionTemplate transactions) {
        this.mapper=mapper;this.tokens=tokens;this.events=events;this.transactions=transactions;
    }
    @Transactional
    public Long initialize(EmployeeInitRequest request) {
        EmployeePO employee=new EmployeePO();employee.shopId=request.shopId;
        employee.username=request.account;employee.password=request.passwordHash;
        employee.avatarUrl="/api/employee/avatar";
        mapper.save(employee);
        EmployeePO saved=mapper.find(employee);
        if (!request.passwordHash.equals(saved.password)) throw new BizException(409,"员工账号已经存在且凭据不一致");
        return saved.id;
    }
    public String login(LoginRequestVO request,String ip,String device) {
        // Commit credential-check audit before replacing a session in Redis.
        // A Redis failure afterwards is reported as 503; the committed audit remains.
        EmployeePO employee=transactions.execute(status -> {
            EmployeePO query=new EmployeePO();query.shopId=request.shopId;query.username=request.username;
            EmployeePO found=mapper.find(query);
            if(found==null)return null;
            boolean valid=passwords.matches(request.password,found.password) && Integer.valueOf(1).equals(found.status);
            String location=("127.0.0.1".equals(ip)||"0:0:0:0:0:0:0:1".equals(ip)) ? "本机" : "未知（未接入IP定位）";
            events.publishEvent(new LoginEvent(found.id,ip,device,location,valid));
            return valid ? found : null;
        });
        if(employee==null)throw new BizException(401,"账号或密码错误，或账号已禁用");
        return tokens.issue(employee);
    }
}
