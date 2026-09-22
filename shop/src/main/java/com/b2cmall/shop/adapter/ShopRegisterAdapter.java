package com.b2cmall.shop.adapter;
import com.b2cmall.common.BizException;
import com.b2cmall.shop.dto.ShopRegisterDTO;
import com.b2cmall.shop.web.request.*;
import org.springframework.stereotype.Component;
@Component
public class ShopRegisterAdapter {
    public ShopRegisterDTO convert(ShopRegisterRequestVO request){
        if(request==null)throw new BizException(400,"注册请求不能为空");
        ShopRegisterDTO dto=new ShopRegisterDTO();
        dto.shopName=request.shopName;dto.adminAccount=request.adminAccount;dto.adminPassword=request.adminPassword;
        dto.source="OLD_VER";dto.invaliCode="-1";
        return dto;
    }
    public ShopRegisterDTO convert(ShopRegisterV2RequestVO request){
        ShopRegisterDTO dto=convert((ShopRegisterRequestVO)request);
        dto.source=request.source==null?"NEW_VER":request.source;
        dto.invaliCode=request.invaliCode==null?"-1":request.invaliCode;
        return dto;
    }
}
