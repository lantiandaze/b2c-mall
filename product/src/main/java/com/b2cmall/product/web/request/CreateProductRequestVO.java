package com.b2cmall.product.web.request;
import javax.validation.constraints.*;
public class CreateProductRequestVO {
    @NotBlank(message="商品名称不能为空") @Size(max=100,message="商品名称不能超过100字符")
    public String name;
    @NotNull(message="类目不能为空") @Positive(message="类目ID必须为正数")
    public Long categoryId;
    @NotNull(message="商品类型不能为空") @Min(value=1,message="商品类型只能为1或2") @Max(value=2,message="商品类型只能为1或2")
    public Integer type;
    @NotNull(message="价格不能为空") @Positive(message="价格必须为正整数分")
    public Long price;
    @NotNull(message="库存不能为空") @Min(value=0,message="库存不能为负数")
    public Integer stock;
    @NotBlank(message="商品描述不能为空") @Size(max=2000,message="描述不能超过2000字符")
    public String description;
}

