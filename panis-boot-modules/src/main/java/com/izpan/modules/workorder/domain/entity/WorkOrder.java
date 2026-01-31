package com.izpan.modules.workorder.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.izpan.infrastructure.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 设备工单 Entity 实体类
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.entity.WorkOrder
 * @CreateTime 2026-01-27
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("device_work_order")
public class WorkOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    private Long orderId;

    /**
     * 工单编号
     */
    private String orderCode;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 关联告警ID
     */
    private Long alarmId;

    /**
     * 工单类型 1-维修 2-保养 3-巡检 4-校准
     */
    private Integer orderType;

    /**
     * 工单来源 1-告警生成 2-计划任务 3-手动创建
     */
    private Integer orderSource;

    /**
     * 故障时间
     */
    private String faultTime;

    /**
     * 故障描述
     */
    private String faultDescription;

    /**
     * 修复要求
     */
    private String repairRequirement;

    /**
     * 优先级 1-紧急 2-高 3-中 4-低
     */
    private Integer priority;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 指派处理人ID
     */
    private Long assigneeId;

    /**
     * 实际处理人ID
     */
    private Long processorId;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 计划开始时间
     */
    private String planStartTime;

    /**
     * 计划完成时间
     */
    private String planEndTime;

    /**
     * 实际开始时间
     */
    private String actualStartTime;

    /**
     * 实际完成时间
     */
    private String actualEndTime;

    /**
     * 工单状态 0-待处理 1-处理中 2-待审核 3-已完成 4-已取消
     */
    private Integer orderStatus;

    /**
     * 处理耗时(分钟)
     */
    private Integer handleDuration;

    /**
     * 维修结果
     */
    private String repairResult;

    /**
     * 维修费用
     */
    private BigDecimal repairCost;

    /**
     * 更换备件
     */
    private String spareParts;

    /**
     * 审核结果
     */
    private String reviewResult;

    /**
     * 审核时间
     */
    private String reviewTime;

    /**
     * 评价分数(1-5)
     */
    private Integer evaluationScore;

    /**
     * 评价备注
     */
    private String evaluationRemark;
    
    // 重写deleted字段，使用@TableField(exist = false)来忽略逻辑删除
    @TableField(exist = false)
    private Integer deleted;
}