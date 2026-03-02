package com.izpan.modules.detection.facade;

import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.detection.domain.dto.devicedetection.DeviceDetectionRecordSearchDTO;
import com.izpan.modules.detection.domain.dto.devicedetection.DetectionDataBatchDTO;
import com.izpan.modules.detection.domain.dto.devicedetection.DetectionDataDTO;
import com.izpan.modules.detection.domain.vo.DeviceDetectionRecordVO;

public interface IDeviceDetectionRecordFacade {

    RPage<DeviceDetectionRecordVO> listDeviceDetectionRecordPage(PageQuery pageQuery, DeviceDetectionRecordSearchDTO deviceDetectionRecordSearchDTO);

    DeviceDetectionRecordVO get(Long id);

    void receiveDetectionData(DetectionDataDTO dto);

    void receiveDetectionDataBatch(DetectionDataBatchDTO batchDTO);
}
