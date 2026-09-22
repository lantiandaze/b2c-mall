package com.b2cmall.product.component;
import com.b2cmall.common.BizException;
import com.b2cmall.product.dao.mapper.ProductMapper;
import com.b2cmall.product.web.request.CreateProductRequestVO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import javax.validation.Validator;
@Component("createProductComponent1")
public class PhysicalProductComponent extends CreateProductTemplate {
    public PhysicalProductComponent(ProductMapper mapper,Validator validator,TransactionTemplate transactions) {super(mapper,validator,transactions);}
    protected int productType(){return 1;}
    protected void checkStock(CreateProductRequestVO request) {
        if(request.stock<=0)throw new BizException(400,"实物商品上架库存必须大于0");
    }
    protected void checkIllegal(CreateProductRequestVO request) {
        // Classroom only: no external moderation service is available.
        if(request.name.contains("违规示例") || request.description.contains("违规示例"))
            throw new BizException(400,"商品未通过课堂示例内容检查");
    }
}

