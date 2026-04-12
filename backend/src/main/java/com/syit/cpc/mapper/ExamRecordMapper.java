package com.syit.cpc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syit.cpc.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试记录 Mapper 接口
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
}
