package com.izpan.modules.alarm.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.alarm.domain.entity.AlarmRule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlarmRuleMapper extends BaseMapper<AlarmRule> {

}
