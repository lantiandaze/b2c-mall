package com.b2cmall.product.web.controller;
import com.b2cmall.common.*;
import com.b2cmall.product.component.*;
import com.b2cmall.product.dao.mapper.ProductMapper;
import com.b2cmall.product.dao.po.SkuPO;
import com.b2cmall.product.web.request.CreateProductRequestVO;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;
@RestController
@RequestMapping("/product")
public class ProductController {
    private final Map<String,CreateProductTemplate> templates;
    private final ProductMapper mapper;
    public ProductController(Map<String,CreateProductTemplate> templates,ProductMapper mapper) {this.templates=templates;this.mapper=mapper;}
    @GetMapping("/health")
    public Result<String> health(){return Result.success("product-service");}
    @PostMapping("/create")
    public Result<Long> create(@Valid @RequestBody CreateProductRequestVO request) {
        CreateProductTemplate template=templates.get("createProductComponent"+request.type);
        if(template==null)throw new BizException(400,"不支持的商品类型");
        return Result.success(template.create(request));
    }
    @GetMapping("/{id}")
    public Result<SkuPO> find(@PathVariable Long id) {
        SkuPO sku=mapper.find(id,RequestHolderComponent.current().shopId);
        if(sku==null)throw new BizException(404,"商品不存在");
        return Result.success(sku);
    }
}

