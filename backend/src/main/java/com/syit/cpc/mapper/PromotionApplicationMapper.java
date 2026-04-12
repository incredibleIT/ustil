package com.syit.cpc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syit.cpc.entity.PromotionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 转正申请 Mapper 接口
 */
@Mapper
public interface PromotionApplicationMapper extends BaseMapper<PromotionApplication> {
    
    /**
     * 查询用户的最新申请（带行级锁，用于并发控制）
     * 
     * @param userId 用户ID
     * @return 最新的转正申请记录
     */
    @Select("SELECT * FROM promotion_applications WHERE user_id = #{userId} AND deleted = 0 ORDER BY created_at DESC LIMIT 1 FOR UPDATE")
    PromotionApplication selectLatestWithLock(@Param("userId") Long userId);
}
