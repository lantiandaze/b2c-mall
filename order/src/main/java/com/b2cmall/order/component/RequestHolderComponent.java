package com.b2cmall.order.component;
import com.b2cmall.common.BizException;
import com.b2cmall.order.bean.CurrentEmployeeBean;
public final class RequestHolderComponent {
    private static final ThreadLocal<CurrentEmployeeBean> CURRENT=new ThreadLocal<>();
    private RequestHolderComponent() {}
    public static void set(CurrentEmployeeBean employee) {CURRENT.set(employee);}
    public static CurrentEmployeeBean current() {
        CurrentEmployeeBean employee=CURRENT.get();
        if(employee==null)throw new BizException(401,"请先登录");
        return employee;
    }
    public static void clear() {CURRENT.remove();}
}

