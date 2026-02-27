package com.izpan.modules.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.alarm.domain.bo.AlarmNoticeBO;
import com.izpan.modules.alarm.domain.entity.AlarmNotice;
import com.izpan.modules.alarm.repository.mapper.AlarmNoticeMapper;
import com.izpan.modules.alarm.service.IAlarmNoticeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlarmNoticeServiceImpl extends ServiceImpl<AlarmNoticeMapper, AlarmNotice> implements IAlarmNoticeService {

    @Override
    public IPage<AlarmNotice> listAlarmNoticePage(PageQuery pageQuery, AlarmNoticeBO alarmNoticeBO) {
        LambdaQueryWrapper<AlarmNotice> queryWrapper = new LambdaQueryWrapper<AlarmNotice>()
                .like(StringUtils.isNotBlank(alarmNoticeBO.getDeviceName()), AlarmNotice::getDeviceName, alarmNoticeBO.getDeviceName())
                .eq(alarmNoticeBO.getAlarmLevel() != null, AlarmNotice::getAlarmLevel, alarmNoticeBO.getAlarmLevel())
                .eq(alarmNoticeBO.getReadStatus() != null, AlarmNotice::getReadStatus, alarmNoticeBO.getReadStatus())
                .eq(alarmNoticeBO.getNotifyUserId() != null, AlarmNotice::getNotifyUserId, alarmNoticeBO.getNotifyUserId())
                .orderByDesc(AlarmNotice::getCreateTime);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public long countUnread(Long notifyUserId) {
        LambdaQueryWrapper<AlarmNotice> queryWrapper = new LambdaQueryWrapper<AlarmNotice>()
                .eq(AlarmNotice::getNotifyUserId, notifyUserId)
                .eq(AlarmNotice::getReadStatus, 0);
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean markAsRead(List<Long> ids, Long notifyUserId) {
        LambdaUpdateWrapper<AlarmNotice> updateWrapper = new LambdaUpdateWrapper<AlarmNotice>()
                .in(AlarmNotice::getId, ids)
                .eq(AlarmNotice::getNotifyUserId, notifyUserId)
                .set(AlarmNotice::getReadStatus, 1)
                .set(AlarmNotice::getReadTime, LocalDateTime.now());
        return baseMapper.update(null, updateWrapper) > 0;
    }
}
