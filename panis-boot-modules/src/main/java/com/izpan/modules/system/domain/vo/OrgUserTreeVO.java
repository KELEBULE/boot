package com.izpan.modules.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OrgUserTreeVO", description = "组织用户树形结构 VO 对象")
public class OrgUserTreeVO {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型: org-组织, user-用户")
    private String type;

    @Schema(description = "子节点")
    private List<OrgUserTreeVO> children;
}
