package com.izpan.modules.alarm.facade;

import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleAddDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleDeleteDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleSearchDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleUpdateDTO;
import com.izpan.modules.alarm.domain.vo.AlarmRuleVO;
import com.izpan.modules.alarm.domain.vo.DeviceTreeVO;
import com.izpan.modules.alarm.domain.vo.OrgUserTreeVO;

import java.util.List;

public interface IAlarmRuleFacade {

    RPage<AlarmRuleVO> listAlarmRulePage(PageQuery pageQuery, AlarmRuleSearchDTO alarmRuleSearchDTO);

    AlarmRuleVO get(Long id);

    boolean add(AlarmRuleAddDTO alarmRuleAddDTO);

    boolean update(AlarmRuleUpdateDTO alarmRuleUpdateDTO);

    boolean batchDelete(AlarmRuleDeleteDTO alarmRuleDeleteDTO);

    List<DeviceTreeVO> queryDeviceTree();

    List<OrgUserTreeVO> queryOrgUserTree();
}
