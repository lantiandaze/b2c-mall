package com.b2cmall.order.flow;
import com.b2cmall.order.bean.CurrentEmployeeBean;
import com.b2cmall.order.dao.po.*;
import com.b2cmall.order.enums.PayTypeEnum;
import com.b2cmall.order.web.request.PayRequestVO;
import com.b2cmall.order.web.response.PayResponseVO;
import java.util.*;
/** One instance per synchronous flow; never stored in singleton component fields. */
public class PaymentContext {
    public PayRequestVO request;
    public CurrentEmployeeBean employee;
    public String authorization;
    public OrderPO order;
    public PaymentPO payment;
    public PayTypeEnum payType;
    public boolean reused;
    public PayResponseVO response;
    public final List<String> steps=new ArrayList<>();
}

