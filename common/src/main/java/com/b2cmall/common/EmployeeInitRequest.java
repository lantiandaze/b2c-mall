package com.b2cmall.common;
import javax.validation.constraints.*;
public class EmployeeInitRequest {
    @NotNull @Positive public Long shopId;
    @com.fasterxml.jackson.annotation.JsonAlias("username") @NotBlank @Size(max=50) public String account;
    @NotBlank @Pattern(regexp="^\\$2[aby]\\$[0-9]{2}\\$[./A-Za-z0-9]{53}$") public String passwordHash;
    public EmployeeInitRequest() {}
    public EmployeeInitRequest(Long shopId,String account,String passwordHash) {
        this.shopId=shopId; this.account=account; this.passwordHash=passwordHash;
    }
}
