package com.b2cmall.employee.web;
import com.b2cmall.common.*;
import com.b2cmall.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import com.b2cmall.employee.service.TokenService;
import com.b2cmall.employee.web.request.LoginRequestVO;
import com.b2cmall.employee.dao.po.EmployeePO;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
@RestController
public class EmployeeController {
    private final EmployeeService service;
    private final String token;
    private final TokenService tokens;
    public EmployeeController(EmployeeService service,TokenService tokens,@Value("${internal.token}") String token) {
        this.service=service;this.token=token;this.tokens=tokens;
    }
    @PostMapping({"/employee/save", "/internal/employees/initialize"})
    public Result<Long> initialize(@RequestHeader(value="X-Internal-Token",defaultValue="") String supplied,
                                  @Valid @RequestBody EmployeeInitRequest request) {
        if (token.isEmpty() || !MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8)))
            throw new BizException(403,"仅允许内部服务调用");
        return Result.success(service.initialize(request));
    }
    @GetMapping("/employee/health")
    public Result<String> health() { return Result.success("employee-service"); }
    @PostMapping("/employee/login")
    public Result<String> login(@Valid @RequestBody LoginRequestVO request,HttpServletRequest http) {
        String device=Optional.ofNullable(http.getHeader("User-Agent")).orElse("未知设备");
        String ip=http.getRemoteAddr();
        String gatewayKey=Optional.ofNullable(http.getHeader("X-Gateway-Token")).orElse("");
        String forwardedIp=http.getHeader("X-Login-IP");
        if(!token.isEmpty() && MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),gatewayKey.getBytes(StandardCharsets.UTF_8))
            && forwardedIp!=null && forwardedIp.length()<=50) ip=forwardedIp;
        return Result.success(service.login(request,ip,device.substring(0,Math.min(255,device.length()))));
    }
    @PostMapping("/employee/checkToken")
    public Result<Boolean> checkToken(@RequestHeader(value="Authorization",defaultValue="") String header) {
        return Result.success(tokens.check(header));
    }
    @PostMapping("/employee/logout")
    public Result<Boolean> logout(@RequestHeader(value="Authorization",defaultValue="") String header) {
        tokens.logout(header);return Result.success(true);
    }
    @GetMapping("/employee/me")
    public Result<Map<String,Object>> me(@RequestHeader(value="Authorization",defaultValue="") String header) {
        EmployeePO employee=tokens.authenticate(header);
        Map<String,Object> data=new LinkedHashMap<>();
        data.put("id",employee.id);data.put("shopId",employee.shopId);data.put("username",employee.username);
        data.put("avatarUrl","/api/employee/avatar");data.put("lastLoginTime",employee.lastLoginTime);data.put("loginCount",employee.loginCount);
        return Result.success(data);
    }
    @GetMapping(value="/employee/avatar",produces="image/svg+xml")
    public String avatar() {
        return "<svg xmlns='http://www.w3.org/2000/svg' width='96' height='96' viewBox='0 0 96 96'><rect width='96' height='96' rx='48' fill='#e6efff'/><circle cx='48' cy='34' r='17' fill='#3978d8'/><path d='M17 86a31 31 0 0 1 62 0' fill='#3978d8'/></svg>";
    }
}
