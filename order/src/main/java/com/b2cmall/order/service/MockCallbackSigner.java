package com.b2cmall.order.service;
import com.b2cmall.order.dao.po.PaymentPO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
@Component
public class MockCallbackSigner {
    private final byte[] key;
    public MockCallbackSigner(@Value("${payment.mock-callback-key}")String key) {
        if(key.length()<32)throw new IllegalArgumentException("模拟回调密钥长度不足");
        this.key=key.getBytes(StandardCharsets.UTF_8);
    }
    public String sign(PaymentPO payment,String result) {
        try {
            Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(key,"HmacSHA256"));
            String value=payment.paymentNo+"|"+payment.orderId+"|"+payment.payType+"|"+payment.amount+"|"+result;
            StringBuilder hex=new StringBuilder();
            for(byte b:mac.doFinal(value.getBytes(StandardCharsets.UTF_8)))hex.append(String.format("%02x",b&255));
            return hex.toString();
        }catch(Exception e){throw new IllegalStateException("模拟回调签名失败",e);}
    }
    public boolean valid(PaymentPO payment,String result,String supplied) {
        return supplied!=null && MessageDigest.isEqual(sign(payment,result).getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8));
    }
}

