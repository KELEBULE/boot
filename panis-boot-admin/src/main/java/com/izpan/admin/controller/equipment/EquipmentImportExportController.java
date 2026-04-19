package com.izpan.admin.controller.equipment;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import com.izpan.common.api.Result;
import com.izpan.modules.equipment.domain.dto.EquipmentImportDTO;
import com.izpan.modules.equipment.domain.dto.EquipmentImportResultDTO;
import com.izpan.modules.equipment.domain.vo.EquipmentExportVO;
import com.izpan.modules.equipment.facade.IEquipmentImportExportFacade;
import com.izpan.starter.excel.util.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Tag(name = "设备导入导出管理")
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentImportExportController {

    private final IEquipmentImportExportFacade equipmentImportExportFacade;

    @SneakyThrows
    @PostMapping("/import")
    @SaCheckPermission("equipment:import")
    @Operation(summary = "导入设备数据")
    public Result<EquipmentImportResultDTO> importEquipment(MultipartFile file) {
        List<EquipmentImportDTO> importList = ExcelUtil.read(EquipmentImportDTO.class)
                .sync()
                .fromInputStream(file.getInputStream());
        
        EquipmentImportResultDTO result = equipmentImportExportFacade.importEquipment(importList);
        return Result.data(result);
    }

    @SneakyThrows
    @PostMapping("/export")
    @SaCheckPermission("equipment:export")
    @Operation(summary = "导出设备数据")
    public void exportEquipment(@RequestBody List<Long> deviceIds, HttpServletResponse response) {
        List<EquipmentExportVO> exportList = equipmentImportExportFacade.exportEquipment(deviceIds);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("设备数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), EquipmentExportVO.class)
                .sheet("设备数据")
                .doWrite(exportList);
    }

    @SneakyThrows
    @GetMapping("/template")
    @Operation(summary = "下载导入模板")
    public void downloadTemplate(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("设备导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), EquipmentImportDTO.class)
                .sheet("设备导入模板")
                .doWrite(List.of());
    }
}
