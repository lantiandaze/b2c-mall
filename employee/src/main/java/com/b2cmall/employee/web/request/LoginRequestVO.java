package com.b2cmall.employee.web.request;
import javax.validation.constraints.*;
public class LoginRequestVO {
    @NotNull(message="店铺ID不能为空") @Positive(message="店铺ID必须为正数")
    public Long shopId;
    @NotBlank(message="账号不能为空") @Size(max=50,message="账号过长")
    public String username;
    @NotBlank(message="密码不能为空") @Size(max=72,message="密码过长")
    public String password;
}

