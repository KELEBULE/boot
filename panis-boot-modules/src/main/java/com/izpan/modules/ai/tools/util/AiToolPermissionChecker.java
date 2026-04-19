package com.izpan.modules.ai.tools.util;

import java.util.Set;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AiToolPermissionChecker {

    private static final ThreadLocal<Set<String>> PERMISSION_CONTEXT = new ThreadLocal<>();

    private AiToolPermissionChecker() {
    }

    public static void setPermissions(Set<String> permissions) {
        PERMISSION_CONTEXT.set(permissions);
    }

    public static void clearPermissions() {
        PERMISSION_CONTEXT.remove();
    }

    public static Set<String> getPermissions() {
        return PERMISSION_CONTEXT.get();
    }

    public static boolean hasPermission(String permission) {
        Set<String> userPermissions = PERMISSION_CONTEXT.get();
        if (userPermissions != null && !userPermissions.isEmpty()) {
            return userPermissions.contains(permission);
        }
        try {
            return StpUtil.hasPermission(permission);
        } catch (Exception e) {
            log.error("检查权限失败: {}", permission, e);
            return false;
        }
    }

    public static boolean hasPermission(String permission, Set<String> userPermissions) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            return hasPermission(permission);
        }
        return userPermissions.contains(permission);
    }

    public static void checkPermission(String permission) {
        if (!hasPermission(permission)) {
            throw new RuntimeException("您没有权限执行此操作，需要权限: " + permission);
        }
    }

    public static String getPermissionDeniedMessage(String permission) {
        return "您没有权限查询此数据，需要权限: " + permission;
    }

    public static Set<String> getCurrentUserPermissions() {
        try {
            return Set.copyOf(StpUtil.getPermissionList());
        } catch (Exception e) {
            log.error("获取当前用户权限列表失败", e);
            return Set.of();
        }
    }
}
