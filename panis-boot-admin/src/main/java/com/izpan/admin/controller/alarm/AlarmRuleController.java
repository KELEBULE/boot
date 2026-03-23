package com.izpan.admin.controller.alarm;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleAddDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleDeleteDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleSearchDTO;
import com.izpan.modules.alarm.domain.dto.alarmrule.AlarmRuleUpdateDTO;
import com.izpan.modules.alarm.domain.vo.AlarmRuleVO;
import com.izpan.modules.alarm.domain.vo.DeviceTreeVO;
import com.izpan.modules.alarm.domain.vo.OrgUserTreeVO;
import com.izpan.modules.alarm.facade.IAlarmRuleFacade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "报警规则管理")
@RequiredArgsConstructor
@RequestMapping("/alarm_rule")
public class AlarmRuleController {

    @NonNull
    private IAlarmRuleFacade alarmRuleFacade;

    @GetMapping("/page")
    @SaCheckPermission("alarm:rule:page")
    @Operation(operationId = "1", summary = "获取报警规则列表")
    public Result<RPage<AlarmRuleVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
            @Parameter(description = "查询对象") AlarmRuleSearchDTO alarmRuleSearchDTO) {
        return Result.data(alarmRuleFacade.listAlarmRulePage(pageQuery, alarmRuleSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("alarm:rule:get")
    @Operation(operationId = "2", summary = "根据ID获取报警规则详细信息")
    public Result<AlarmRuleVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(alarmRuleFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("alarm:rule:add")
    @Operation(operationId = "3", summary = "新增报警规则")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody AlarmRuleAddDTO alarmRuleAddDTO) {
        return Result.status(alarmRuleFacade.add(alarmRuleAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("alarm:rule:update")
    @Operation(operationId = "4", summary = "更新报警规则信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody AlarmRuleUpdateDTO alarmRuleUpdateDTO) {
        return Result.status(alarmRuleFacade.update(alarmRuleUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("alarm:rule:delete")
    @Operation(operationId = "5", summary = "批量删除报警规则信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody AlarmRuleDeleteDTO alarmRuleDeleteDTO) {
        return Result.status(alarmRuleFacade.batchDelete(alarmRuleDeleteDTO));
    }

    @GetMapping("/device_tree")
    @SaCheckPermission("alarm:rule:deviceTree")
    @Operation(operationId = "6", summary = "获取设备树形结构(工厂-厂区-设备)")
    public Result<List<DeviceTreeVO>> queryDeviceTree() {
        return Result.data(alarmRuleFacade.queryDeviceTree());
    }

    @GetMapping("/org_user_tree")
    @SaCheckPermission("alarm:rule:orgUserTree")
    @Operation(operationId = "7", summary = "获取组织用户树形结构(公司-部门-用户)")
    public Result<List<OrgUserTreeVO>> queryOrgUserTree() {
        return Result.data(alarmRuleFacade.queryOrgUserTree());
    }
}
