package com.izpan.modules.equipment.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.equipment.domain.dto.FactoryInfoAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoUpdateDTO;
import com.izpan.modules.equipment.domain.entity.FactoryInfo;
import com.izpan.modules.equipment.domain.vo.FactoryAreaTreeVO;
import com.izpan.modules.equipment.domain.vo.LatestAlarmDeviceVO;
import com.izpan.modules.equipment.domain.vo.MonitorDeviceTreeVO;

public interface IFactoryInfoService extends IService<FactoryInfo> {

    IPage<FactoryInfo> listFactoryInfoPage(PageQuery pageQuery, FactoryInfoSearchDTO searchDTO);

    FactoryInfo getFactoryInfoById(Long id);

    List<FactoryInfo> listAllFactoryInfo();

    List<FactoryAreaTreeVO> getFactoryAreaTree();

    boolean addFactoryInfo(FactoryInfoAddDTO addDTO);

    boolean updateFactoryInfo(FactoryInfoUpdateDTO updateDTO);

    boolean deleteFactoryInfo(FactoryInfoDeleteDTO deleteDTO);

    boolean deleteFactoryInfoByIds(List<Long> ids);

    List<MonitorDeviceTreeVO> getMonitorDeviceTree(Long factoryId, Long areaId, Long deviceId);

    LatestAlarmDeviceVO getLatestAlarmDevice();
}
