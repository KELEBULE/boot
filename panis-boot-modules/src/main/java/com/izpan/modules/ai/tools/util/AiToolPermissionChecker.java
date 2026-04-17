package com.izpan.modules.ai.tools.util;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AiToolPermissionChecker {

    private AiToolPermissionChecker() {
    }

    public static boolean hasPermission(String permission) {
        try {
            return StpUtil.hasPermission(permission);
        } catch (Exception e) {
            log.error("检查权限失败: {}", permission, e);
            return false;
        }
    }

    public static void checkPermission(String permission) {
        if (!hasPermission(permission)) {
            throw new RuntimeException("您没有权限执行此操作，需要权限: " + permission);
        }
    }

    public static String getPermissionDeniedMessage(String permission) {
        return "您没有权限查询此数据，需要权限: " + permission;
    }
}
