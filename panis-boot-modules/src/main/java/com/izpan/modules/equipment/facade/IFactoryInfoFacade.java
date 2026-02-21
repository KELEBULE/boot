package com.izpan.modules.equipment.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryInfoAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoUpdateDTO;
import com.izpan.modules.equipment.domain.vo.FactoryInfoVO;

import java.util.List;

public interface IFactoryInfoFacade {

    RPage<FactoryInfoVO> listFactoryInfoPage(PageQuery pageQuery, FactoryInfoSearchDTO searchDTO);

    FactoryInfoVO getFactoryInfoById(Long id);

    List<FactoryInfoVO> listAllFactoryInfo();

    boolean addFactoryInfo(FactoryInfoAddDTO addDTO);

    boolean updateFactoryInfo(FactoryInfoUpdateDTO updateDTO);

    boolean deleteFactoryInfo(FactoryInfoDeleteDTO deleteDTO);

    boolean deleteFactoryInfoByIds(List<Long> ids);
}
