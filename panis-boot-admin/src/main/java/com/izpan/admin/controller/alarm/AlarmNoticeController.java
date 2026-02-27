package com.izpan.admin.controller.alarm;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.dto.alarmnotice.AlarmNoticeReadDTO;
import com.izpan.modules.alarm.domain.dto.alarmnotice.AlarmNoticeSearchDTO;
import com.izpan.modules.alarm.domain.vo.AlarmNoticeVO;
import com.izpan.modules.alarm.facade.IAlarmNoticeFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "告警通知管理")
@RequiredArgsConstructor
@RequestMapping("/alarm_notice")
public class AlarmNoticeController {

    @NonNull
    private IAlarmNoticeFacade alarmNoticeFacade;

    @GetMapping("/page")
    @SaCheckPermission("alarm:notice:page")
    @Operation(operationId = "1", summary = "获取告警通知列表")
    public Result<RPage<AlarmNoticeVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
            @Parameter(description = "查询对象") AlarmNoticeSearchDTO alarmNoticeSearchDTO) {
        return Result.data(alarmNoticeFacade.listAlarmNoticePage(pageQuery, alarmNoticeSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("alarm:notice:get")
    @Operation(operationId = "2", summary = "根据ID获取告警通知详细信息")
    public Result<AlarmNoticeVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(alarmNoticeFacade.get(id));
    }

    @PutMapping("/read")
    @SaCheckPermission("alarm:notice:read")
    @Operation(operationId = "3", summary = "标记告警通知已读")
    public Result<Boolean> markAsRead(@Parameter(description = "标记已读对象") @RequestBody AlarmNoticeReadDTO alarmNoticeReadDTO) {
        return Result.status(alarmNoticeFacade.markAsRead(alarmNoticeReadDTO));
    }

    @GetMapping("/unread_count")
    @SaCheckPermission("alarm:notice:unreadCount")
    @Operation(operationId = "4", summary = "获取未读告警数量")
    public Result<Long> unreadCount(@Parameter(description = "用户ID") @RequestParam Long notifyUserId) {
        return Result.data(alarmNoticeFacade.countUnread(notifyUserId));
    }
}
