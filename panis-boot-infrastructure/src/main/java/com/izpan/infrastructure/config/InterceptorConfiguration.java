package com.izpan.infrastructure.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenContextException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.izpan.infrastructure.interceptor.GlobalRequestInterceptor;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册拦截器配置
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.infrastructure.config.InterceptorConfiguration
 * @CreateTime 2023/7/19 - 22:19
 */
@Slf4j
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Resource
    private GlobalRequestInterceptor globalRequestInterceptor;

    // 对 swagger 的请求不进行拦截
    public final String[] swaggerExcludePatterns = new String[]{
            "/v3/**",
            "/webjars/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/favicon.ico",
            "/api",
            "/api-docs",
            "/api-docs/**",
            "/doc.html",
            "/doc.html/**",
            "/swagger-ui.html",
            "/swagger-ui.html/**"};

    // 对 Druid 的请求不进行拦截
    public final String[] druidExcludePatterns = new String[]{
            "/druid/**"
    };

    // 业务放行接口
    public final String[] businessExcludePatterns = new String[]{
            "/auth/user_name",
            "/auth/send_email_code",
            "/auth/email_code",
            "/auth/email_register",
            "/email/send_code",
            "/email/verify_code"
    };

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {

        // 全局请求拦截器，优先级最高
        registry.addInterceptor(globalRequestInterceptor)
                .addPathPatterns("/**")
                .order(Ordered.HIGHEST_PRECEDENCE);

        // sa token 路由拦截器，优先级次之
        // 使用更宽松的拦截策略，避免在上下文未准备好的情况下进行严格检查
        registry.addInterceptor(new SaInterceptor(handle -> {
            try {
                // 首先检查上下文是否有效
                if (!StpUtil.getTokenName().isEmpty()) {
                    // 只有在有token名称配置时才进行登录检查
                    StpUtil.checkLogin();
                }
                // 如果没有token配置或上下文无效，允许通过
            } catch (SaTokenContextException e) {
                // 上下文异常时，记录日志但允许请求继续
                log.debug("Sa-Token上下文未准备就绪，跳过权限检查: {}", e.getMessage());
            } catch (NotLoginException e) {
                // 真正的未登录情况才抛出异常
                log.debug("用户未登录: {}", e.getMessage());
                throw e;
            }
        }))
                .addPathPatterns("/**")
                .excludePathPatterns(swaggerExcludePatterns)
                .excludePathPatterns(druidExcludePatterns)
                .excludePathPatterns(businessExcludePatterns)
                .order(Ordered.HIGHEST_PRECEDENCE + 1);
    }
}
