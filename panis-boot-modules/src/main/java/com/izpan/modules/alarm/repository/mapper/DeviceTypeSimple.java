package com.izpan.modules.alarm.repository.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeSimple {
    private Long typeId;
    private String typeCode;
    private String typeName;
}
