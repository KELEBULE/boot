package com.izpan.modules.system.facade.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.google.common.collect.Lists;
import com.izpan.common.api.ResultCode;
import com.izpan.common.constants.SystemCacheConstant;
import com.izpan.common.domain.KVPairs;
import com.izpan.common.exception.BizException;
import com.izpan.common.exception.RouteException;
import com.izpan.common.pool.StringPools;
import com.izpan.common.util.CglibUtil;
import com.izpan.infrastructure.holder.GlobalUserHolder;
import com.izpan.infrastructure.util.GsonUtil;
import com.izpan.infrastructure.util.RedisUtil;
import com.izpan.modules.monitor.domain.entity.MonLogsLogin;
import com.izpan.modules.monitor.service.IMonLogsLoginService;
import com.izpan.modules.system.domain.bo.SysMenuBO;
import com.izpan.modules.system.domain.bo.SysPermissionBO;
import com.izpan.modules.system.domain.entity.SysPermission;
import com.izpan.modules.system.domain.bo.SysUserBO;
import com.izpan.modules.system.domain.dto.EmailCodeLoginDTO;
import com.izpan.modules.system.domain.dto.EmailRegisterDTO;
import com.izpan.modules.system.domain.dto.LoginFormDTO;
import com.izpan.modules.system.domain.dto.menu.SysUserRouteVO;
import com.izpan.modules.system.domain.dto.user.SysUserUpdateCurrentInfoDTO;
import com.izpan.modules.system.domain.entity.SysUser;
import com.izpan.modules.system.domain.vo.SysUserVO;
import com.izpan.modules.system.facade.IAuthenticationFacade;
import com.izpan.modules.system.service.IEmailService;
import com.izpan.modules.system.service.ISysRoleMenuService;
import com.izpan.modules.system.service.ISysRolePermissionService;
import com.izpan.modules.system.service.ISysUserService;
import com.izpan.modules.system.service.impl.SysUserServiceImpl;
import java.time.LocalDateTime;

import com.izpan.common.util.IPUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.izpan.common.constants.RequestConstant;
import com.izpan.infrastructure.util.ServletHolderUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户认证门面接口实现层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.system.facade.impl.AuthenticationFacadeImpl
 * @CreateTime 2023/7/17 - 18:34
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements IAuthenticationFacade {

    @NonNull
    private ISysRoleMenuService sysRoleMenuService;

    @NonNull
    private ISysRolePermissionService sysRolePermissionService;

    @NonNull
    private ISysUserService sysUserService;

    @NonNull
    private IEmailService emailService;

    /**
     * 初始化菜单路由
     *
     * @param parentId          父级菜单ID
     * @param sysMenus          菜单列表
     * @param menuPermissionMap 菜单对应的权限按钮 Map 结构
     * @return {@link SysUserRouteVO.Route} 路由对象列表
     * @author payne.zhuang
     * @CreateTime 2024-02-04 23:42
     */
    private static List<SysUserRouteVO.Route> initMenuRoute(Long parentId, Set<SysMenuBO> sysMenus,
            Map<Long, List<String>> menuPermissionMap) {
        // 根据 parentId 获取菜单列表
        List<SysMenuBO> parentMenuList = sysMenus.stream()
                .filter(item -> item.getParentId().equals(parentId)).toList();
        List<SysUserRouteVO.Route> routes = Lists.newArrayList();
        parentMenuList.forEach(menu -> {
            // 路由元数据
            SysUserRouteVO.Meta routeMeta = SysUserRouteVO.Meta.builder()
                    .title(menu.getName())
                    .i18nKey(menu.getI18nKey())
                    .order(menu.getSort())
                    .keepAlive(StringPools.Y.equals(menu.getKeepAlive()))
                    .hideInMenu(StringPools.Y.equals(menu.getHide()))
                    .multiTab(StringPools.Y.equals(menu.getMultiTab()))
                    .fixedIndexInTab(menu.getFixedIndexInTab())
                    .href(menu.getHref())
                    .query(GsonUtil.fromJsonList(menu.getQuery(), KVPairs.class))
                    .permissions(menuPermissionMap.getOrDefault(menu.getId(), Lists.newArrayList()))
                    .build();
            if (menu.getIconType().equals(StringPools.TWO)) {
                routeMeta.setLocalIcon(menu.getIcon());
            } else {
                routeMeta.setIcon(menu.getIcon());
            }
            // 路由道具
            SysUserRouteVO.Props props = SysUserRouteVO.Props.builder()
                    .url(menu.getIframeUrl())
                    .build();
            // 路由对象
            SysUserRouteVO.Route route = SysUserRouteVO.Route.builder()
                    .name(menu.getRouteName())
                    .path(menu.getRoutePath())
                    .component(menu.getComponent().replace(StringPools.HASH, StringPools.DOLLAR))
                    .props(props)
                    .meta(routeMeta)
                    .children(initMenuRoute(menu.getId(), sysMenus, menuPermissionMap))
                    .build();
            // 添加到路由列表
            routes.add(route);
        });
        // 按照排序值排序
        routes.sort(Comparator.comparing(route -> route.getMeta().getOrder()));
        return routes;
    }

    @Override
    @Transactional
    public Map<String, String> userNameLogin(LoginFormDTO loginFormDTO) {
        SysUserBO sysUserBO = CglibUtil.convertObj(loginFormDTO, SysUserBO::new);
        return sysUserService.userLogin(sysUserBO);
    }

    @Override
    public boolean logout() {
        RedisUtil.del(SystemCacheConstant.userRouteKey(GlobalUserHolder.getUserId()));
        StpUtil.logout();
        return true;
    }

    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        return sysUserService.refreshToken(refreshToken, null, null);
    }

    @Override
    public SysUserVO getCurrentUserInfo() {
        SysUserBO sysUserBO = sysUserService.currentUserInfo();
        return CglibUtil.convertObj(sysUserBO, SysUserVO::new);
    }

    @Override
    @Transactional
    public SysUserVO updateCurrentUserInfo(SysUserUpdateCurrentInfoDTO currentInfoDTO) {
        SysUserBO sysUserBO = CglibUtil.convertObj(currentInfoDTO, SysUserBO::new);
        boolean updated = sysUserService.updateCurrentUserInfo(sysUserBO);
        if (!updated) {
            throw new BizException("更新用户信息异常");
        }
        return getCurrentUserInfo();
    }

    @Override
    @Cacheable(value = SystemCacheConstant.SYSTEM_USER_ROUTE, key = "#userId")
    public SysUserRouteVO queryUserRouteWithUserId(Long userId) {
        try {
            Set<Long> currentUserRoleIds = GlobalUserHolder.getRoleIds();
            // 获取当前用户的菜单列表以及权限按钮列表
            Set<SysMenuBO> sysMenuBOS = currentUserRoleIds.stream()
                    .flatMap(roleId -> sysRoleMenuService.queryMenuListWithRoleId(roleId).stream())
                    .collect(Collectors.toSet());
            Set<SysPermissionBO> sysPermissionBOS = currentUserRoleIds.stream()
                    .flatMap(roleId -> sysRolePermissionService.queryPermissionListWithRoleId(roleId).stream())
                    .collect(Collectors.toSet());
            // 将权限集合分组成菜单对应按钮集合
            Map<Long, List<String>> menuPermissionMap = transform(sysPermissionBOS);
            // 返回路由对象
            return SysUserRouteVO.builder()
                    .home("home")
                    // 组装路由集合
                    .routes(initMenuRoute(0L, sysMenuBOS, menuPermissionMap))
                    .build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new RouteException(ResultCode.USER_ROUTE_ERROR.getCode(), ResultCode.USER_ROUTE_ERROR.getValue());
        }
    }

    @Override
    public boolean sendEmailCaptcha(String email) {
        emailService.sendVerificationCode(email);
        return true;
    }

    @Override
    public Map<String, String> emailCodeLogin(EmailCodeLoginDTO emailCodeLoginDTO) {
        // 验证验证码
        boolean isValid = emailService.verifyCode(emailCodeLoginDTO.getEmail(), emailCodeLoginDTO.getCode());
        if (!isValid) {
            throw new BizException("验证码无效或已过期");
        }

        // 根据邮箱查找用户
        SysUser user = sysUserService.getUserByEmail(emailCodeLoginDTO.getEmail());
        if (user == null) {
            throw new BizException("查找不到该邮箱对应的用户");
        }

        if (StringPools.ZERO.equals(user.getStatus())) {
            throw new BizException("当前用户已被禁止登录");
        }

        // 生成登录日志
        MonLogsLogin loginLogs = new MonLogsLogin();
        loginLogs.setUserName(user.getUserName());
        loginLogs.setStatus(StringPools.ONE);
        loginLogs.setMessage("登录成功");
        
        // 设置IP和userAgent信息
        HttpServletRequest request = ServletHolderUtil.getRequest();
        String ip = JakartaServletUtil.getClientIP(request);
        if (ip == null || ip.isEmpty()) {
            ip = "127.0.0.1";
        }
        String userAgent = request.getHeader(RequestConstant.USER_AGENT);
        if (userAgent == null) {
            userAgent = "";
        }
        loginLogs.setIp(ip);
        loginLogs.setIpAddr(IPUtil.getIpAddr(ip));
        loginLogs.setUserAgent(userAgent);

        try {
            // 使用sa-token登录
            StpUtil.login(user.getId());

            // 更新用户登录时间
            user.setLastLoginTime(LocalDateTime.now());
            sysUserService.updateById(user);

            // 保存用户到session
            SysUserBO sysUserBO = CglibUtil.convertObj(user, SysUserBO::new);
            ((SysUserServiceImpl) sysUserService).saveUserToSession(user, false);

            loginLogs.setUserId(user.getId());
            loginLogs.setUserRealName(user.getRealName());
        } catch (BizException e) {
            loginLogs.setStatus(StringPools.ZERO);
            loginLogs.setMessage(e.getMessage());
            throw e;
        } finally {
            // 保存登录日志
            ((SysUserServiceImpl) sysUserService).getMonLogsLoginService().save(loginLogs);
        }

        return Map.of("token", StpUtil.getTokenValue());
    }

    @Override
    @Transactional
    public Boolean emailRegister(EmailRegisterDTO emailRegisterDTO) {
        emailService.registerWithEmail(emailRegisterDTO);
        return true;
    }

    /**
     * 将权限集合分组成菜单对应按钮集合
     *
     * @param sysPermissionBOS 权限集合
     * @return {@linkplain Map} 菜单对应按钮集合
     * @author payne.zhuang
     * @CreateTime 2024-04-27 19:33
     */
    private Map<Long, List<String>> transform(Set<SysPermissionBO> sysPermissionBOS) {
        return sysPermissionBOS.stream()
                .collect(Collectors.groupingBy(
                        // 以menuId作为分组依据
                        SysPermissionBO::getMenuId,
                        Collectors.mapping(
                                // 将每个SysPermission对象的resource属性按照分号分割后进行处理
                                permission -> Arrays.stream(permission.getResource().split(StringPools.SEMICOLON))
                                        // 去除空格，过滤空字符串
                                        .map(String::trim).filter(s -> !s.isEmpty())
                                        // 去除重复元素
                                        .distinct().toList(),
                                // 对List<String>进行处理
                                Collectors.collectingAndThen(Collectors.toList(),
                                        // 将多个List<String>合并为一个流
                                        list -> list.stream()
                                                .flatMap(Collection::stream)
                                                // 去除重复元素，排序
                                                .distinct().sorted(String::compareTo).toList()))));
    }
}
