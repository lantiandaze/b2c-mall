package com.b2cmall.order.liteflowcmp;
import com.b2cmall.common.*;
import com.b2cmall.order.flow.PaymentContext;
import com.b2cmall.order.feign.ProductFeignClient;
import com.b2cmall.order.feign.ProductFeignClient.ProductSnapshot;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import feign.FeignException;
@LiteflowComponent("stockCheck")
public class SkuStockCheckCmp extends NodeComponent {
    private final ProductFeignClient products;
    public SkuStockCheckCmp(ProductFeignClient products){this.products=products;}
    @Override public void process(){
        PaymentContext context=getContextBean(PaymentContext.class);context.steps.add("stockCheck");
        // An existing payment must remain retryable after the product changes or goes offline.
        if(context.reused)return;
        ProductSnapshot sku;
        try {
            Result<ProductSnapshot> result=products.find(context.order.skuId,context.authorization);
            if(result==null||result.status!=200||result.data==null)throw new BizException(503,"商品查询失败");
            sku=result.data;
        }catch(FeignException e) {throw new BizException(e.status()==404?409:503,"商品不可用或服务暂不可用");}
        if(!context.order.shopId.equals(sku.shopId)||!Integer.valueOf(1).equals(sku.status)||
           !context.order.skuType.equals(sku.type))throw new BizException(409,"商品已下架、类型改变或不属于当前店铺");
        if(Integer.valueOf(1).equals(sku.type) && (sku.stock==null||sku.stock<context.order.quantity))
            throw new BizException(409,"支付前检查发现实物库存不足");
        // Current catalog price never overwrites the order's agreed price snapshot.
    }
}

