package com.izpan.modules.alarm.facade.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.quartz.SchedulerException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izpan.common.util.CglibUtil;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.bo.AlarmRuleBO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleAddDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleDeleteDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleSearchDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleUpdateDTO;
import com.izpan.modules.alarm.domain.entity.AlarmRule;
import com.izpan.modules.alarm.domain.vo.AlarmRuleVO;
import com.izpan.modules.alarm.domain.vo.DeviceTreeVO;
import com.izpan.modules.alarm.domain.vo.OrgUserTreeVO;
import com.izpan.modules.alarm.facade.IAlarmRuleFacade;
import com.izpan.modules.alarm.service.IAlarmPushSchedulerService;
import com.izpan.modules.alarm.service.IAlarmRuleService;
import com.izpan.modules.equipment.domain.entity.FactoryArea;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.entity.FactoryInfo;
import com.izpan.modules.equipment.service.IFactoryAreaService;
import com.izpan.modules.equipment.service.IFactoryDeviceService;
import com.izpan.modules.equipment.service.IFactoryInfoService;
import com.izpan.modules.system.domain.entity.SysOrgUnits;
import com.izpan.modules.system.domain.entity.SysUser;
import com.izpan.modules.system.domain.entity.SysUserOrg;
import com.izpan.modules.system.service.ISysOrgUnitsService;
import com.izpan.modules.system.service.ISysUserOrgService;
import com.izpan.modules.system.service.ISysUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AlarmRuleFacadeImpl implements IAlarmRuleFacade {

    private final IAlarmRuleService alarmRuleService;
    private final IFactoryInfoService factoryInfoService;
    private final IFactoryAreaService factoryAreaService;
    private final IFactoryDeviceService factoryDeviceService;
    private final ISysOrgUnitsService sysOrgUnitsService;
    private final ISysUserService sysUserService;
    private final ISysUserOrgService sysUserOrgService;
    private final ObjectMapper objectMapper;
    private final IAlarmPushSchedulerService alarmPushSchedulerService;

    public AlarmRuleFacadeImpl(
            IAlarmRuleService alarmRuleService,
            IFactoryInfoService factoryInfoService,
            IFactoryAreaService factoryAreaService,
            IFactoryDeviceService factoryDeviceService,
            ISysOrgUnitsService sysOrgUnitsService,
            @Lazy ISysUserService sysUserService,
            ISysUserOrgService sysUserOrgService,
            ObjectMapper objectMapper,
            IAlarmPushSchedulerService alarmPushSchedulerService) {
        this.alarmRuleService = alarmRuleService;
        this.factoryInfoService = factoryInfoService;
        this.factoryAreaService = factoryAreaService;
        this.factoryDeviceService = factoryDeviceService;
        this.sysOrgUnitsService = sysOrgUnitsService;
        this.sysUserService = sysUserService;
        this.sysUserOrgService = sysUserOrgService;
        this.objectMapper = objectMapper;
        this.alarmPushSchedulerService = alarmPushSchedulerService;
    }

    @Override
    public RPage<AlarmRuleVO> listAlarmRulePage(PageQuery pageQuery, AlarmRuleSearchDTO alarmRuleSearchDTO) {
        AlarmRuleBO alarmRuleBO = CglibUtil.convertObj(alarmRuleSearchDTO, AlarmRuleBO::new);
        IPage<AlarmRule> alarmRuleIPage = alarmRuleService.listAlarmRulePage(pageQuery, alarmRuleBO);
        List<AlarmRuleVO> voList = alarmRuleIPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new RPage<>(alarmRuleIPage.getCurrent(), alarmRuleIPage.getSize(), voList, alarmRuleIPage.getPages(), alarmRuleIPage.getTotal());
    }

    @Override
    public AlarmRuleVO get(Long id) {
        AlarmRule alarmRule = alarmRuleService.getById(id);
        return convertToVO(alarmRule);
    }

    @Override
    @Transactional
    public boolean add(AlarmRuleAddDTO alarmRuleAddDTO) {
        AlarmRule alarmRule = convertToEntity(alarmRuleAddDTO);
        boolean saved = alarmRuleService.save(alarmRule);
        if (saved && alarmRule.getRuleStatus() == 1) {
            createOrUpdateQuartzJob(alarmRule);
        }
        return saved;
    }

    @Override
    @Transactional
    public boolean update(AlarmRuleUpdateDTO alarmRuleUpdateDTO) {
        AlarmRule alarmRule = convertToEntity(alarmRuleUpdateDTO);
        boolean updated = alarmRuleService.updateById(alarmRule);
        if (updated) {
            manageQuartzJob(alarmRule);
        }
        return updated;
    }

    @Override
    public boolean batchDelete(AlarmRuleDeleteDTO alarmRuleDeleteDTO) {
        AlarmRuleBO alarmRuleBO = CglibUtil.convertObj(alarmRuleDeleteDTO, AlarmRuleBO::new);
        List<Long> ids = alarmRuleBO.getIds();
        for (Long id : ids) {
            deleteQuartzJob(id);
        }
        return alarmRuleService.removeBatchByIds(ids, true);
    }

    private void createOrUpdateQuartzJob(AlarmRule alarmRule) {
        try {
            if (alarmPushSchedulerService.checkExists(alarmRule.getRuleId())) {
                alarmPushSchedulerService.updateJob(alarmRule.getRuleId(), alarmRule.getPushInterval());
            } else {
                alarmPushSchedulerService.createJob(alarmRule.getRuleId(), alarmRule.getPushInterval());
            }
            log.info("Created/Updated Quartz job for rule: ruleId={}", alarmRule.getRuleId());
        } catch (SchedulerException e) {
            log.error("Failed to create/update Quartz job for rule: ruleId={}", alarmRule.getRuleId(), e);
        }
    }

    private void manageQuartzJob(AlarmRule alarmRule) {
        try {
            if (alarmRule.getRuleStatus() == 1) {
                createOrUpdateQuartzJob(alarmRule);
            } else {
                deleteQuartzJob(alarmRule.getRuleId());
            }
        } catch (Exception e) {
            log.error("Failed to manage Quartz job for rule: ruleId={}", alarmRule.getRuleId(), e);
        }
    }

    private void deleteQuartzJob(Long ruleId) {
        try {
            alarmPushSchedulerService.deleteJob(ruleId);
            log.info("Deleted Quartz job for rule: ruleId={}", ruleId);
        } catch (SchedulerException e) {
            log.error("Failed to delete Quartz job for rule: ruleId={}", ruleId, e);
        }
    }

    private AlarmRuleVO convertToVO(AlarmRule alarmRule) {
        if (alarmRule == null) {
            return null;
        }
        AlarmRuleVO vo = CglibUtil.convertObj(alarmRule, AlarmRuleVO::new);
        vo.setDeviceIds(parseJsonToList(alarmRule.getDeviceIds(), new TypeReference<List<Long>>() {}));
        vo.setAlarmLevels(parseJsonToList(alarmRule.getAlarmLevels(), new TypeReference<List<Integer>>() {}));
        vo.setNotifyTargetIds(parseJsonToList(alarmRule.getNotifyTargetIds(), new TypeReference<List<String>>() {}));
        return vo;
    }

    private AlarmRule convertToEntity(Object dto) {
        AlarmRule entity = CglibUtil.convertObj(dto, AlarmRule::new);
        if (dto instanceof AlarmRuleAddDTO addDTO) {
            entity.setDeviceIds(listToJson(addDTO.getDeviceIds()));
            entity.setAlarmLevels(listToJson(addDTO.getAlarmLevels()));
            entity.setNotifyTargetIds(listToJson(addDTO.getNotifyTargetIds()));
        } else if (dto instanceof AlarmRuleUpdateDTO updateDTO) {
            entity.setDeviceIds(listToJson(updateDTO.getDeviceIds()));
            entity.setAlarmLevels(listToJson(updateDTO.getAlarmLevels()));
            entity.setNotifyTargetIds(listToJson(updateDTO.getNotifyTargetIds()));
        }
        return entity;
    }

    private <T> List<T> parseJsonToList(String json, TypeReference<List<T>> typeReference) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON: {}", json, e);
            return Collections.emptyList();
        }
    }

    private <T> String listToJson(List<T> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert list to JSON: {}", list, e);
            return "[]";
        }
    }

    @Override
    public List<DeviceTreeVO> queryDeviceTree() {
        List<FactoryInfo> factories = factoryInfoService.list();
        List<FactoryArea> areas = factoryAreaService.list();
        List<FactoryDevice> devices = factoryDeviceService.list();

        Map<Long, List<FactoryArea>> areasByFactory = areas.stream()
                .collect(Collectors.groupingBy(FactoryArea::getFactoryId));
        Map<Long, List<FactoryDevice>> devicesByArea = devices.stream()
                .collect(Collectors.groupingBy(FactoryDevice::getLocationId));

        List<DeviceTreeVO> result = new ArrayList<>();
        for (FactoryInfo factory : factories) {
            DeviceTreeVO factoryVO = DeviceTreeVO.builder()
                    .id(factory.getFactoryId())
                    .name(factory.getFactoryName())
                    .code(factory.getFactoryCode())
                    .type("factory")
                    .children(new ArrayList<>())
                    .build();

            List<FactoryArea> factoryAreas = areasByFactory.getOrDefault(factory.getFactoryId(), Collections.emptyList());
            for (FactoryArea area : factoryAreas) {
                DeviceTreeVO areaVO = DeviceTreeVO.builder()
                        .id(area.getAreaId())
                        .name(area.getAreaName())
                        .code(area.getLocationCode())
                        .type("area")
                        .children(new ArrayList<>())
                        .build();

                List<FactoryDevice> areaDevices = devicesByArea.getOrDefault(area.getAreaId(), Collections.emptyList());
                for (FactoryDevice device : areaDevices) {
                    DeviceTreeVO deviceVO = DeviceTreeVO.builder()
                            .id(device.getDeviceId())
                            .name(device.getDeviceName())
                            .code(device.getDeviceCode())
                            .type("device")
                            .build();
                    areaVO.getChildren().add(deviceVO);
                }
                factoryVO.getChildren().add(areaVO);
            }
            result.add(factoryVO);
        }
        return result;
    }

    @Override
    public List<OrgUserTreeVO> queryOrgUserTree() {
        List<SysOrgUnits> allOrgs = sysOrgUnitsService.list(
                new LambdaQueryWrapper<SysOrgUnits>()
                        .eq(SysOrgUnits::getStatus, 1)
                        .orderByAsc(SysOrgUnits::getSort)
        );
        List<SysUser> allUsers = sysUserService.list(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, "1")
        );
        List<SysUserOrg> allUserOrgs = sysUserOrgService.list();

        Map<Long, List<SysOrgUnits>> orgsByParent = allOrgs.stream()
                .collect(Collectors.groupingBy(SysOrgUnits::getParentId));
        Map<Long, List<Long>> userIdsByOrg = allUserOrgs.stream()
                .collect(Collectors.groupingBy(SysUserOrg::getOrgId,
                        Collectors.mapping(SysUserOrg::getUserId, Collectors.toList())));
        Map<Long, SysUser> userMap = allUsers.stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        return buildOrgUserTree(0L, orgsByParent, userIdsByOrg, userMap);
    }

    private List<OrgUserTreeVO> buildOrgUserTree(Long parentId,
            Map<Long, List<SysOrgUnits>> orgsByParent,
            Map<Long, List<Long>> userIdsByOrg,
            Map<Long, SysUser> userMap) {

        List<SysOrgUnits> childOrgs = orgsByParent.getOrDefault(parentId, Collections.emptyList());
        List<OrgUserTreeVO> result = new ArrayList<>();

        for (SysOrgUnits org : childOrgs) {
            OrgUserTreeVO orgVO = OrgUserTreeVO.builder()
                    .id(String.valueOf(org.getId()))
                    .name(org.getName())
                    .type("org")
                    .children(new ArrayList<>())
                    .build();

            List<OrgUserTreeVO> children = buildOrgUserTree(org.getId(), orgsByParent, userIdsByOrg, userMap);
            orgVO.getChildren().addAll(children);

            List<Long> orgUserIds = userIdsByOrg.getOrDefault(org.getId(), Collections.emptyList());
            for (Long userId : orgUserIds) {
                SysUser user = userMap.get(userId);
                if (user != null) {
                    OrgUserTreeVO userVO = OrgUserTreeVO.builder()
                            .id(String.valueOf(user.getId()))
                            .name(user.getRealName() != null ? user.getRealName() : user.getUserName())
                            .type("user")
                            .build();
                    orgVO.getChildren().add(userVO);
                }
            }

            result.add(orgVO);
        }

        return result;
    }
}
