package com.izpan.modules.alarm.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.common.util.CglibUtil;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.bo.AlarmNoticeBO;
import com.izpan.modules.alarm.domain.dto.alarmnotice.AlarmNoticeReadDTO;
import com.izpan.modules.alarm.domain.dto.alarmnotice.AlarmNoticeSearchDTO;
import com.izpan.modules.alarm.domain.entity.AlarmNotice;
import com.izpan.modules.alarm.domain.vo.AlarmNoticeVO;
import com.izpan.modules.alarm.facade.IAlarmNoticeFacade;
import com.izpan.modules.alarm.service.IAlarmNoticeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmNoticeFacadeImpl implements IAlarmNoticeFacade {

    @NonNull
    private IAlarmNoticeService alarmNoticeService;

    @Override
    public RPage<AlarmNoticeVO> listAlarmNoticePage(PageQuery pageQuery, AlarmNoticeSearchDTO alarmNoticeSearchDTO) {
        AlarmNoticeBO alarmNoticeBO = CglibUtil.convertObj(alarmNoticeSearchDTO, AlarmNoticeBO::new);
        IPage<AlarmNotice> alarmNoticeIPage = alarmNoticeService.listAlarmNoticePage(pageQuery, alarmNoticeBO);
        return RPage.build(alarmNoticeIPage, AlarmNoticeVO::new);
    }

    @Override
    public AlarmNoticeVO get(Long id) {
        AlarmNotice alarmNotice = alarmNoticeService.getById(id);
        return CglibUtil.convertObj(alarmNotice, AlarmNoticeVO::new);
    }

    @Override
    @Transactional
    public boolean markAsRead(AlarmNoticeReadDTO alarmNoticeReadDTO) {
        return alarmNoticeService.markAsRead(alarmNoticeReadDTO.getIds(), alarmNoticeReadDTO.getNotifyUserId());
    }

    @Override
    public long countUnread(Long notifyUserId) {
        return alarmNoticeService.countUnread(notifyUserId);
    }
}
