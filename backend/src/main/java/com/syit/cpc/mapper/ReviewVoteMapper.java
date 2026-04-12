package com.syit.cpc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syit.cpc.entity.ReviewVote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审核投票 Mapper 接口
 */
@Mapper
public interface ReviewVoteMapper extends BaseMapper<ReviewVote> {
}
