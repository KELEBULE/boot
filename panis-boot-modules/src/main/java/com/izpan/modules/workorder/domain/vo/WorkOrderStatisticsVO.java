package com.izpan.modules.workorder.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 工单统计 VO
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @CreateTime 2026-02-23
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "WorkOrderStatisticsVO", description = "工单统计 VO")
public class WorkOrderStatisticsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "本月工单总数")
    private Long total;

    @Schema(description = "已完成工单数")
    private Long completed;

    @Schema(description = "待处理工单数")
    private Long pending;

    @Schema(description = "处理中工单数")
    private Long processing;

    @Schema(description = "已取消工单数")
    private Long cancelled;

    @Schema(description = "完成率(百分比)")
    private Double completionRate;

    @Schema(description = "平均处理时长(分钟)")
    private Double avgHandleDuration;

    @Schema(description = "平均评价分数")
    private Double avgEvaluationScore;

    @Schema(description = "工单状态分布")
    private List<StatusDistribution> statusDistribution;

    @Schema(description = "工单优先级分布")
    private List<PriorityDistribution> priorityDistribution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusDistribution implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "状态值")
        private Integer status;

        @Schema(description = "状态名称")
        private String statusName;

        @Schema(description = "数量")
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityDistribution implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "优先级值")
        private Integer priority;

        @Schema(description = "优先级名称")
        private String priorityName;

        @Schema(description = "数量")
        private Long count;
    }
}
