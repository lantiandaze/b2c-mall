package com.b2cmall.order.enums;
import com.b2cmall.common.BizException;
public enum PayTypeEnum {
    ALIPAY, WECHAT;
    public static PayTypeEnum parse(String value) {
        try {return valueOf(value);}
        catch(Exception e) {throw new BizException(400,"payType只能是ALIPAY或WECHAT");}
    }
}

