package com.izpan.modules.equipment.domain.dto;

import java.io.Serial;
import java.io.Serializable;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ColumnWidth(20)
@HeadRowHeight(value = 20)
@ContentRowHeight(value = 18)
@ExcelIgnoreUnannotated
@Schema(name = "EquipmentImportDTO", description = "设备导入 DTO 对象")
public class EquipmentImportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "工厂编码", index = 0)
    @Schema(description = "工厂编码")
    private String factoryCode;

    @ExcelProperty(value = "工厂名称", index = 1)
    @Schema(description = "工厂名称")
    private String factoryName;

    @ExcelProperty(value = "工厂地址", index = 2)
    @Schema(description = "工厂地址")
    private String factoryAddress;

    @ExcelProperty(value = "联系人", index = 3)
    @Schema(description = "联系人")
    private String contactPerson;

    @ExcelProperty(value = "联系电话", index = 4)
    @Schema(description = "联系电话")
    private String contactPhone;

    @ExcelProperty(value = "工厂状态", index = 5)
    @Schema(description = "工厂状态 1-启用 0-停用")
    private String factoryStatus;

    @ExcelProperty(value = "厂区编码", index = 6)
    @Schema(description = "厂区编码")
    private String areaCode;

    @ExcelProperty(value = "厂区名称", index = 7)
    @Schema(description = "厂区名称")
    private String areaName;

    @ExcelProperty(value = "厂区类型", index = 8)
    @Schema(description = "厂区类型")
    private String areaType;

    @ExcelProperty(value = "厂区状态", index = 9)
    @Schema(description = "厂区状态 1-启用 0-停用")
    private String areaStatus;

    @ExcelProperty(value = "厂区排序", index = 10)
    @Schema(description = "厂区排序")
    private String areaOrder;

    @ExcelProperty(value = "设备编码", index = 11)
    @Schema(description = "设备编码")
    private String deviceCode;

    @ExcelProperty(value = "设备名称", index = 12)
    @Schema(description = "设备名称")
    private String deviceName;

    @ExcelProperty(value = "设备型号", index = 13)
    @Schema(description = "设备型号")
    private String deviceModel;

    @ExcelProperty(value = "设备SN", index = 14)
    @Schema(description = "设备SN")
    private String deviceSn;

    @ExcelProperty(value = "制造商", index = 15)
    @Schema(description = "制造商")
    private String manufacturer;

    @ExcelProperty(value = "设备状态", index = 16)
    @Schema(description = "设备状态 1-正常 2-维修 0-停用")
    private String deviceStatus;

    @ExcelProperty(value = "安装时间", index = 17)
    @Schema(description = "安装时间")
    private String installTime;

    @ExcelProperty(value = "启用时间", index = 18)
    @Schema(description = "启用时间")
    private String startUseTime;

    @ExcelProperty(value = "维护周期(天)", index = 19)
    @Schema(description = "维护周期(天)")
    private String maintainCycle;

    @ExcelProperty(value = "上次维护时间", index = 20)
    @Schema(description = "上次维护时间")
    private String lastMaintainTime;

    @ExcelProperty(value = "保修期(月)", index = 21)
    @Schema(description = "保修期(月)")
    private String warrantyPeriod;

    @ExcelProperty(value = "设备备注", index = 22)
    @Schema(description = "设备备注")
    private String deviceNote;

    @ExcelProperty(value = "3D模型URL", index = 23)
    @Schema(description = "3D模型URL")
    private String modelUrl;

    @ExcelProperty(value = "设备图片URL", index = 24)
    @Schema(description = "设备图片URL")
    private String imageUrl;

    @ExcelProperty(value = "部件编码", index = 25)
    @Schema(description = "部件编码")
    private String partCode;

    @ExcelProperty(value = "部件名称", index = 26)
    @Schema(description = "部件名称")
    private String partName;

    @ExcelProperty(value = "部件类型", index = 27)
    @Schema(description = "部件类型")
    private String partType;

    @ExcelProperty(value = "监控启用", index = 28)
    @Schema(description = "监控启用 1-启用 0-停用")
    private String monitorEnabled;

    @ExcelProperty(value = "部件状态", index = 29)
    @Schema(description = "部件状态 1-正常 0-停用")
    private String partStatus;
}
