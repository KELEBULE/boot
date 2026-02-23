package com.izpan.modules.equipment.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.equipment.domain.entity.FactoryInfo;
import com.izpan.modules.equipment.domain.vo.FactoryAreaTreeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FactoryInfoMapper extends BaseMapper<FactoryInfo> {
    
    List<FactoryAreaTreeVO> selectFactoryAreaTree();
}
