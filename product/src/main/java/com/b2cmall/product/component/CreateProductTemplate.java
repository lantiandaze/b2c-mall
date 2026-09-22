package com.b2cmall.product.component;

import com.b2cmall.common.BizException;
import com.b2cmall.product.bean.CurrentEmployeeBean;
import com.b2cmall.product.dao.mapper.ProductMapper;
import com.b2cmall.product.dao.po.*;
import com.b2cmall.product.web.request.CreateProductRequestVO;
import org.springframework.transaction.support.TransactionTemplate;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import java.util.Set;

// Singleton components hold dependencies only; all request state stays in local variables.
public abstract class CreateProductTemplate {
    protected final ProductMapper mapper;
    private final Validator validator;
    private final TransactionTemplate transactions;
    protected CreateProductTemplate(ProductMapper mapper,Validator validator,TransactionTemplate transactions) {
        this.mapper=mapper;this.validator=validator;this.transactions=transactions;
    }
    // Programmatic transaction avoids the final-method @Transactional proxy trap.
    public final Long create(CreateProductRequestVO request) {
        checkParam(request);
        CurrentEmployeeBean employee=RequestHolderComponent.current();
        return transactions.execute(status -> {
            checkCategory(request,employee.shopId);
            checkStock(request);
            checkPrice(request);
            checkIllegal(request);
            SkuPO sku=saveProduct(request,employee);
            publishProduct(sku);
            afterCreate(sku);
            return sku.id;
        });
    }
    protected void checkParam(CreateProductRequestVO request) {
        if(request==null)throw new BizException(400,"商品信息不能为空");
        Set<ConstraintViolation<CreateProductRequestVO>> errors=validator.validate(request);
        if(!errors.isEmpty())throw new BizException(400,errors.iterator().next().getMessage());
        if(request.type!=productType())throw new BizException(400,"商品类型与模板不匹配");
    }
    protected void checkCategory(CreateProductRequestVO request,Long shopId) {
        if(mapper.categoryAllowed(request.categoryId,request.type,shopId)!=1)
            throw new BizException(400,"类目不存在、已禁用、类型不匹配或不属于当前店铺");
    }
    protected abstract int productType();
    protected abstract void checkStock(CreateProductRequestVO request);
    protected void checkPrice(CreateProductRequestVO request) {
        if(request.price<=0)throw new BizException(400,"价格必须为正整数分");
    }
    protected abstract void checkIllegal(CreateProductRequestVO request);
    protected SkuPO saveProduct(CreateProductRequestVO request,CurrentEmployeeBean employee) {
        SkuPO sku=new SkuPO();
        sku.shopId=employee.shopId;sku.createdUserId=employee.id;sku.updateUserId=employee.id;
        sku.name=request.name.trim();sku.description=request.description;sku.categoryId=request.categoryId;
        sku.type=request.type;sku.stock=request.stock;sku.price=request.price;sku.status=0;
        if(mapper.save(sku)!=1 || sku.id==null)throw new BizException(500,"保存商品失败");
        return sku;
    }
    protected void publishProduct(SkuPO sku) {
        if(mapper.publish(sku)!=1)throw new BizException(500,"上架失败");
        sku.status=1;
    }
    protected void afterCreate(SkuPO sku) {
        SkuLogPO log=new SkuLogPO();log.skuId=sku.id;log.stock=sku.stock;log.price=sku.price;
        log.status=sku.status;log.createdUserId=sku.createdUserId;log.updateUserId=sku.updateUserId;
        if(mapper.saveLog(log)!=1)throw new BizException(500,"保存商品日志失败");
    }
}

