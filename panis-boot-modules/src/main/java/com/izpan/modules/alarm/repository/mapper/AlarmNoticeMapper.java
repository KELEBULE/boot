package com.izpan.modules.alarm.repository.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.alarm.domain.entity.AlarmNotice;

@Mapper
public interface AlarmNoticeMapper extends BaseMapper<AlarmNotice> {
}
