package com.b2cmall.employee.service.event;
public class LoginEvent {
    public final Long userId;
    public final String ip,device,location;
    public final boolean success;
    public LoginEvent(Long userId,String ip,String device,String location,boolean success) {
        this.userId=userId;this.ip=ip;this.device=device;this.location=location;this.success=success;
    }
}

