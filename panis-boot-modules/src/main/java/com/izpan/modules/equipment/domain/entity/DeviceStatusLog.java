package com.izpan.modules.equipment.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("device_status_log")
public class DeviceStatusLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
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

    @JsonProperty("toStatus")
    private Integer toStatus;

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
