package com.izpan.modules.alarm.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ColumnWidth(20)
@HeadRowHeight(value = 20)
@ContentRowHeight(value = 18)
@ExcelIgnoreUnannotated
@Schema(name = "DeviceAlarmExportVO", description = "设备报警导出 VO 对象")
public class DeviceAlarmExportVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "报警编码", index = 0)
    @Schema(description = "报警编码")
    private String alarmCode;

    @ExcelProperty(value = "设备名称", index = 1)
    @Schema(description = "设备名称")
    private String deviceName;

    @ExcelProperty(value = "部件名称", index = 2)
    @Schema(description = "部件名称")
    private String partName;

    @ExcelProperty(value = "报警级别", index = 3)
    @Schema(description = "报警级别")
    private String alarmLevelText;

    @ColumnWidth(25)
    @ExcelProperty(value = "报警时间", index = 4)
    @Schema(description = "报警时间")
    private LocalDateTime alarmTime;

    @ExcelProperty(value = "当前值", index = 5)
    @Schema(description = "当前值")
    private BigDecimal currentValue;

    @ExcelProperty(value = "阈值", index = 6)
    @Schema(description = "阈值")
    private BigDecimal thresholdValue;

    @ExcelProperty(value = "确认状态", index = 7)
    @Schema(description = "确认状态")
    private String confirmStatusText;

    @ExcelProperty(value = "确认人", index = 8)
    @Schema(description = "确认人")
    private String confirmUserName;

    @ColumnWidth(25)
    @ExcelProperty(value = "确认时间", index = 9)
    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @ExcelProperty(value = "清除状态", index = 10)
    @Schema(description = "清除状态")
    private String clearStatusText;

    @ExcelProperty(value = "清除人", index = 11)
    @Schema(description = "清除人")
    private String clearUserName;

    @ColumnWidth(25)
    @ExcelProperty(value = "清除时间", index = 12)
    @Schema(description = "清除时间")
    private LocalDateTime clearTime;

    @ExcelProperty(value = "是否误报", index = 13)
    @Schema(description = "是否误报")
    private String isFalseAlarmText;

    @ExcelProperty(value = "工单编号", index = 14)
    @Schema(description = "工单编号")
    private String workOrderCode;

    @ExcelProperty(value = "持续时长", index = 15)
    @Schema(description = "持续时长")
    private String alarmDurationText;

    @ColumnWidth(25)
    @ExcelProperty(value = "创建时间", index = 16)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
