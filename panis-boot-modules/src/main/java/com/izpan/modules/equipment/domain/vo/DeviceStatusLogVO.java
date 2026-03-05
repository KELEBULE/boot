package com.izpan.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(name = "DeviceStatusLogVO", description = "设备状态切换记录 VO 对象")
public class DeviceStatusLogVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("logId")
    private Long logId;

    @JsonProperty("deviceId")
    private Long deviceId;

    @JsonProperty("deviceCode")
    private String deviceCode;

    @JsonProperty("deviceName")
    private String deviceName;

    @JsonProperty("fromStatus")
    private Integer fromStatus;

    @JsonProperty("fromStatusName")
    private String fromStatusName;

    @JsonProperty("toStatus")
    private Integer toStatus;

    @JsonProperty("toStatusName")
    private String toStatusName;

    @JsonProperty("changeReason")
    private String changeReason;

    @JsonProperty("imageUrls")
    private String imageUrls;

    @JsonProperty("relatedOrderId")
    private Long relatedOrderId;

    @JsonProperty("relatedOrderCode")
    private String relatedOrderCode;

    @JsonProperty("operatorId")
    private Long operatorId;

    @JsonProperty("operatorName")
    private String operatorName;

    @JsonProperty("createTime")
    private LocalDateTime createTime;
}
