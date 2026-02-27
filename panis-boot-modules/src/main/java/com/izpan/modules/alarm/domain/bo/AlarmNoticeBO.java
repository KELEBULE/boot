package com.izpan.modules.alarm.domain.bo;

import com.izpan.modules.alarm.domain.entity.AlarmNotice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlarmNoticeBO extends AlarmNotice {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> ids;
}
