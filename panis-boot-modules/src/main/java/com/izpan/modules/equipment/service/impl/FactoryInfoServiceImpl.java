package com.izpan.modules.equipment.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.equipment.domain.dto.FactoryInfoAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoUpdateDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryArea;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.entity.FactoryInfo;
import com.izpan.modules.equipment.domain.vo.FactoryAreaTreeVO;
import com.izpan.modules.equipment.domain.vo.LatestAlarmDeviceVO;
import com.izpan.modules.equipment.domain.vo.MonitorDeviceTreeVO;
import com.izpan.modules.equipment.repository.mapper.DevicePartMapper;
import com.izpan.modules.equipment.repository.mapper.FactoryAreaMapper;
import com.izpan.modules.equipment.repository.mapper.FactoryDeviceMapper;
import com.izpan.modules.equipment.repository.mapper.FactoryInfoMapper;
import com.izpan.modules.equipment.service.IFactoryInfoService;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import com.izpan.modules.alarm.repository.mapper.DeviceAlarmMapper;

import lombok.RequiredArgsConstructor;
import com.izpan.starter.oss.manage.OssManager;

@Service
@RequiredArgsConstructor
public class FactoryInfoServiceImpl extends ServiceImpl<FactoryInfoMapper, FactoryInfo> implements IFactoryInfoService {

    private final FactoryAreaMapper factoryAreaMapper;
    private final FactoryDeviceMapper factoryDeviceMapper;
    private final DevicePartMapper devicePartMapper;
    private final DeviceAlarmMapper deviceAlarmMapper;
    private final OssManager ossManager;

    @Override
    public IPage<FactoryInfo> listFactoryInfoPage(PageQuery pageQuery, FactoryInfoSearchDTO searchDTO) {
        LambdaQueryWrapper<FactoryInfo> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getFactoryCode()), FactoryInfo::getFactoryCode, searchDTO.getFactoryCode())
                .like(StringUtils.isNotBlank(searchDTO.getFactoryName()), FactoryInfo::getFactoryName, searchDTO.getFactoryName())
                .eq(searchDTO.getStatus() != null, FactoryInfo::getStatus, searchDTO.getStatus())
                .orderByDesc(FactoryInfo::getCreateTime);

        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public FactoryInfo getFactoryInfoById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<FactoryInfo> listAllFactoryInfo() {
        LambdaQueryWrapper<FactoryInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FactoryInfo::getStatus, 1);
        return list(queryWrapper);
    }

    @Override
    public List<FactoryAreaTreeVO> getFactoryAreaTree() {
        List<FactoryAreaTreeVO> rawData = baseMapper.selectFactoryAreaTree();

        Map<Long, List<FactoryAreaTreeVO>> childrenMap = rawData.stream()
                .filter(item -> item.getChildren() != null && !item.getChildren().isEmpty())
                .flatMap(item -> item.getChildren().stream()
                .map(child -> Map.entry(item.getId(), child)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        return rawData.stream()
                .collect(Collectors.toMap(
                        FactoryAreaTreeVO::getId,
                        factory -> factory,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .map(factory -> FactoryAreaTreeVO.builder()
                .uniqueKey(factory.getUniqueKey())
                .id(factory.getId())
                .name(factory.getName())
                .code(factory.getCode())
                .type(factory.getType())
                .children(childrenMap.getOrDefault(factory.getId(), new ArrayList<>()))
                .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean addFactoryInfo(FactoryInfoAddDTO addDTO) {
        FactoryInfo factoryInfo = FactoryInfo.builder()
                .factoryCode(addDTO.getFactoryCode())
                .factoryName(addDTO.getFactoryName())
                .factoryAddress(addDTO.getFactoryAddress())
                .contactPerson(addDTO.getContactPerson())
                .contactPhone(addDTO.getContactPhone())
                .status(addDTO.getStatus())
                .build();
        return save(factoryInfo);
    }

    @Override
    public boolean updateFactoryInfo(FactoryInfoUpdateDTO updateDTO) {
        FactoryInfo factoryInfo = FactoryInfo.builder()
                .factoryId(updateDTO.getFactoryId())
                .factoryCode(updateDTO.getFactoryCode())
                .factoryName(updateDTO.getFactoryName())
                .factoryAddress(updateDTO.getFactoryAddress())
                .contactPerson(updateDTO.getContactPerson())
                .contactPhone(updateDTO.getContactPhone())
                .status(updateDTO.getStatus())
                .build();
        return updateById(factoryInfo);
    }

    @Override
    public boolean deleteFactoryInfo(FactoryInfoDeleteDTO deleteDTO) {
        return removeByIds(deleteDTO.getIds());
    }

    @Override
    public boolean deleteFactoryInfoByIds(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public List<MonitorDeviceTreeVO> getMonitorDeviceTree(Long factoryId, Long areaId, Long deviceId) {
        List<MonitorDeviceTreeVO> result = new ArrayList<>();

        LambdaQueryWrapper<FactoryInfo> factoryWrapper = new LambdaQueryWrapper<>();
        factoryWrapper.eq(FactoryInfo::getStatus, 1);
        if (factoryId != null) {
            factoryWrapper.eq(FactoryInfo::getFactoryId, factoryId);
        }
        List<FactoryInfo> factories = list(factoryWrapper);

        Set<Long> alarmDeviceIds = getAlarmDeviceIds();

        for (FactoryInfo factory : factories) {
            MonitorDeviceTreeVO factoryNode = MonitorDeviceTreeVO.builder()
                    .uniqueKey("factory_" + factory.getFactoryId())
                    .id(factory.getFactoryId())
                    .name(factory.getFactoryName())
                    .code(factory.getFactoryCode())
                    .type("factory")
                    .hasAlarm(false)
                    .children(new ArrayList<>())
                    .build();

            LambdaQueryWrapper<FactoryArea> areaWrapper = new LambdaQueryWrapper<>();
            areaWrapper.eq(FactoryArea::getFactoryId, factory.getFactoryId())
                    .eq(FactoryArea::getAreaStatus, 1);
            if (areaId != null) {
                areaWrapper.eq(FactoryArea::getAreaId, areaId);
            }
            List<FactoryArea> areas = factoryAreaMapper.selectList(areaWrapper);

            boolean factoryHasAlarm = false;

            for (FactoryArea area : areas) {
                MonitorDeviceTreeVO areaNode = MonitorDeviceTreeVO.builder()
                        .uniqueKey("area_" + area.getAreaId())
                        .id(area.getAreaId())
                        .name(area.getAreaName())
                        .code(area.getLocationCode())
                        .type("area")
                        .parentId(factory.getFactoryId())
                        .hasAlarm(false)
                        .children(new ArrayList<>())
                        .build();

                LambdaQueryWrapper<FactoryDevice> deviceWrapper = new LambdaQueryWrapper<>();
                deviceWrapper.eq(FactoryDevice::getLocationId, area.getAreaId());
                if (deviceId != null) {
                    deviceWrapper.eq(FactoryDevice::getDeviceId, deviceId);
                }
                List<FactoryDevice> devices = factoryDeviceMapper.selectList(deviceWrapper);

                boolean areaHasAlarm = false;

                for (FactoryDevice device : devices) {
                    boolean deviceHasAlarm = alarmDeviceIds.contains(device.getDeviceId());

                    String modelUrl = StringUtils.isNotBlank(device.getModelUrl())
                            ? ossManager.service().preview(device.getModelUrl())
                            : null;
                    String imageUrl = StringUtils.isNotBlank(device.getImageUrl())
                            ? ossManager.service().preview(device.getImageUrl())
                            : null;

                    MonitorDeviceTreeVO deviceNode = MonitorDeviceTreeVO.builder()
                            .uniqueKey("device_" + device.getDeviceId())
                            .id(device.getDeviceId())
                            .name(device.getDeviceName())
                            .code(device.getDeviceCode())
                            .type("device")
                            .parentId(area.getAreaId())
                            .modelUrl(modelUrl)
                            .imageUrl(imageUrl)
                            .deviceStatus(device.getDeviceStatus())
                            .hasAlarm(deviceHasAlarm)
                            .children(new ArrayList<>())
                            .build();

                    LambdaQueryWrapper<DevicePart> partWrapper = new LambdaQueryWrapper<>();
                    partWrapper.eq(DevicePart::getDeviceId, device.getDeviceId());
                    List<DevicePart> parts = devicePartMapper.selectList(partWrapper);

                    for (DevicePart part : parts) {
                        MonitorDeviceTreeVO partNode = MonitorDeviceTreeVO.builder()
                                .uniqueKey("part_" + part.getPartId())
                                .id(part.getPartId())
                                .name(part.getPartName())
                                .code(part.getPartCode())
                                .type("part")
                                .parentId(device.getDeviceId())
                                .deviceId(device.getDeviceId())
                                .modelUrl(modelUrl)
                                .modelNodeName(part.getModelNodeName())
                                .partStatus(part.getPartStatus())
                                .hasAlarm(false)
                                .build();
                        deviceNode.getChildren().add(partNode);
                    }

                    areaNode.getChildren().add(deviceNode);
                    if (deviceHasAlarm) {
                        areaHasAlarm = true;
                        factoryHasAlarm = true;
                    }
                }

                areaNode.setHasAlarm(areaHasAlarm);
                factoryNode.getChildren().add(areaNode);
            }

            factoryNode.setHasAlarm(factoryHasAlarm);
            result.add(factoryNode);
        }

        return result;
    }

    @Override
    public LatestAlarmDeviceVO getLatestAlarmDevice() {
        LambdaQueryWrapper<DeviceAlarm> alarmWrapper = new LambdaQueryWrapper<>();
        alarmWrapper.eq(DeviceAlarm::getClearStatus, 0)
                .orderByDesc(DeviceAlarm::getAlarmTime)
                .last("LIMIT 1");
        DeviceAlarm latestAlarm = deviceAlarmMapper.selectOne(alarmWrapper);

        if (latestAlarm == null) {
            LambdaQueryWrapper<FactoryDevice> deviceWrapper = new LambdaQueryWrapper<>();
            deviceWrapper.isNotNull(FactoryDevice::getModelUrl)
                    .ne(FactoryDevice::getModelUrl, "")
                    .last("LIMIT 1");
            FactoryDevice device = factoryDeviceMapper.selectOne(deviceWrapper);
            if (device != null) {
                String modelUrl = StringUtils.isNotBlank(device.getModelUrl())
                        ? ossManager.service().preview(device.getModelUrl())
                        : null;
                String imageUrl = StringUtils.isNotBlank(device.getImageUrl())
                        ? ossManager.service().preview(device.getImageUrl())
                        : null;
                return LatestAlarmDeviceVO.builder()
                        .deviceId(device.getDeviceId())
                        .deviceCode(device.getDeviceCode())
                        .deviceName(device.getDeviceName())
                        .modelUrl(modelUrl)
                        .imageUrl(imageUrl)
                        .hasModel(StringUtils.isNotBlank(device.getModelUrl()))
                        .build();
            }
            return null;
        }

        FactoryDevice device = factoryDeviceMapper.selectById(latestAlarm.getDeviceId());
        if (device == null) {
            return null;
        }

        DevicePart part = devicePartMapper.selectById(latestAlarm.getPartId());

        String modelUrl = StringUtils.isNotBlank(device.getModelUrl())
                ? ossManager.service().preview(device.getModelUrl())
                : null;
        String imageUrl = StringUtils.isNotBlank(device.getImageUrl())
                ? ossManager.service().preview(device.getImageUrl())
                : null;

        return LatestAlarmDeviceVO.builder()
                .deviceId(device.getDeviceId())
                .deviceCode(device.getDeviceCode())
                .deviceName(device.getDeviceName())
                .modelUrl(modelUrl)
                .imageUrl(imageUrl)
                .alarmPartId(latestAlarm.getPartId())
                .alarmPartCode(part != null ? part.getPartCode() : null)
                .alarmPartName(part != null ? part.getPartName() : null)
                .alarmPartModelNodeName(part != null ? part.getModelNodeName() : null)
                .alarmLevel(latestAlarm.getAlarmLevel())
                .alarmTime(latestAlarm.getAlarmTime() != null ? latestAlarm.getAlarmTime().toString() : null)
                .hasModel(StringUtils.isNotBlank(device.getModelUrl()))
                .build();
    }

    private Set<Long> getAlarmDeviceIds() {
        LambdaQueryWrapper<DeviceAlarm> alarmWrapper = new LambdaQueryWrapper<>();
        alarmWrapper.eq(DeviceAlarm::getClearStatus, 0);
        List<DeviceAlarm> alarms = deviceAlarmMapper.selectList(alarmWrapper);
        return alarms.stream()
                .map(DeviceAlarm::getDeviceId)
                .collect(Collectors.toSet());
    }
}
