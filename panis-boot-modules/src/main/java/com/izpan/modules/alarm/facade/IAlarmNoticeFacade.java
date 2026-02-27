package com.izpan.modules.alarm.facade;

import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.dto.alarmnotice.AlarmNoticeReadDTO;
import com.izpan.modules.alarm.domain.dto.alarmnotice.AlarmNoticeSearchDTO;
import com.izpan.modules.alarm.domain.vo.AlarmNoticeVO;

public interface IAlarmNoticeFacade {

    RPage<AlarmNoticeVO> listAlarmNoticePage(PageQuery pageQuery, AlarmNoticeSearchDTO alarmNoticeSearchDTO);

    AlarmNoticeVO get(Long id);

    boolean markAsRead(AlarmNoticeReadDTO alarmNoticeReadDTO);

    long countUnread(Long notifyUserId);
}
