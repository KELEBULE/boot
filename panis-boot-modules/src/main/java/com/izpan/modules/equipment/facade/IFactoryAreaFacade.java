package com.izpan.modules.equipment.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryAreaAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaUpdateDTO;
import com.izpan.modules.equipment.domain.vo.FactoryAreaVO;

import java.util.List;

public interface IFactoryAreaFacade {

    RPage<FactoryAreaVO> listFactoryAreaPage(PageQuery pageQuery, FactoryAreaSearchDTO searchDTO);

    FactoryAreaVO getFactoryAreaById(Long id);

    List<FactoryAreaVO> listAllFactoryArea();

    boolean addFactoryArea(FactoryAreaAddDTO addDTO);

    boolean updateFactoryArea(FactoryAreaUpdateDTO updateDTO);

    boolean deleteFactoryArea(FactoryAreaDeleteDTO deleteDTO);

    boolean deleteFactoryAreaByIds(List<Long> ids);
}
