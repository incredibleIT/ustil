package com.syit.cpc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syit.cpc.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内信通知 Mapper 接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
