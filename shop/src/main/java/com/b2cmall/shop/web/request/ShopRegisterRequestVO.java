package com.b2cmall.shop.web.request;
import com.fasterxml.jackson.annotation.JsonAlias;
import javax.validation.constraints.*;
public class ShopRegisterRequestVO {
    @JsonAlias("name") @NotBlank(message="店铺名不能为空") @Size(max=6,message="店铺名最多6个字符")
    public String shopName;
    @JsonAlias("account") @NotBlank(message="账户不能为空") @Pattern(regexp="[A-Za-z0-9_]{3,50}",message="账户须为3至50位字母、数字或下划线")
    public String adminAccount;
    @JsonAlias("password") @NotBlank(message="密码不能为空") @Size(min=8,max=20,message="密码须为8至20个字符")
    public String adminPassword;
}
