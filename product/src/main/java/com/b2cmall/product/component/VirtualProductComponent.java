package com.b2cmall.product.component;
import com.b2cmall.common.BizException;
import com.b2cmall.product.dao.mapper.ProductMapper;
import com.b2cmall.product.web.request.CreateProductRequestVO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import javax.validation.Validator;
@Component("createProductComponent2")
public class VirtualProductComponent extends CreateProductTemplate {
    public VirtualProductComponent(ProductMapper mapper,Validator validator,TransactionTemplate transactions) {super(mapper,validator,transactions);}
    protected int productType(){return 2;}
    protected void checkStock(CreateProductRequestVO request) {
        // Classroom virtual goods are not backed by a physical inventory.
        if(request.stock!=0)throw new BizException(400,"课堂虚拟商品库存请填0");
    }
    protected void checkIllegal(CreateProductRequestVO request) {
        if(request.description.trim().length()<10)throw new BizException(400,"虚拟商品需提供至少10字符的交付说明");
        if(request.name.contains("违规示例") || request.description.contains("违规示例"))
            throw new BizException(400,"虚拟商品未通过课堂示例内容检查");
    }
}

