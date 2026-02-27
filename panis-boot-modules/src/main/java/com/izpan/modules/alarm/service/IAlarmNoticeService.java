package com.izpan.modules.alarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.alarm.domain.bo.AlarmNoticeBO;
import com.izpan.modules.alarm.domain.entity.AlarmNotice;

public interface IAlarmNoticeService extends IService<AlarmNotice> {

    IPage<AlarmNotice> listAlarmNoticePage(PageQuery pageQuery, AlarmNoticeBO alarmNoticeBO);

    long countUnread(Long notifyUserId);

    boolean markAsRead(java.util.List<Long> ids, Long notifyUserId);
}
