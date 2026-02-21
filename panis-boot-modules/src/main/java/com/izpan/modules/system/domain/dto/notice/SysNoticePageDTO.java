package com.izpan.modules.system.domain.dto.notice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通知公告分页查询 DTO
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName SysNoticePageDTO
 * @CreateTime 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SysNoticePageDTO", description = "通知公告分页查询对象")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysNoticePageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码")
    @Builder.Default
    private Integer page = 1;

    @Schema(description = "每页显示数量")
    @Builder.Default
    private Integer pageSize = 20;

    @Schema(description = "分类 1:通知 2:公告")
    private String category;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "时间范围数组，包含开始时间和结束时间")
    private String[] timeRange;
}