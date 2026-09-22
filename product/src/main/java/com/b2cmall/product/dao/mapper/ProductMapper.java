package com.b2cmall.product.dao.mapper;
import com.b2cmall.product.dao.po.*;
import org.apache.ibatis.annotations.*;
@Mapper
public interface ProductMapper {
    @Select("SELECT count(*) FROM tb_category WHERE id=#{id} AND type=#{type} AND status=1 AND (shop_id IS NULL OR shop_id=#{shopId})")
    int categoryAllowed(@Param("id")Long id,@Param("type")Integer type,@Param("shopId")Long shopId);
    int save(SkuPO sku);
    @Update("UPDATE tb_sku SET status=1,updated_at=CURRENT_TIMESTAMP WHERE id=#{id} AND shop_id=#{shopId} AND status=0")
    int publish(SkuPO sku);
    int saveLog(SkuLogPO log);
    SkuPO find(@Param("id")Long id,@Param("shopId")Long shopId);
}

