package org.tis.tools.shiro.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.util.WebUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.tis.tools.rservice.ac.capable.IAuthenticationRService;
import org.tis.tools.webapp.util.AjaxUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * ABF的权限验证过滤器
 * 用于判断当前用户是否有当前该功能行为权限
 *
 * @author zhaoch
 */
public class AbfPermissionFilter extends AdviceFilter {

    @Autowired
    IAuthenticationRService authenticationRService;

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        String bhvCode = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
        if (authenticationRService == null) {//解决service为null无法注入问题
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            authenticationRService = (IAuthenticationRService) factory.getBean("authenticationRService");
        }
        List<String> permStrByBhvCode = authenticationRService.getPermStrByBhvCode(bhvCode);
        for (String s : permStrByBhvCode) {
            if(subject.isPermitted(s)) {
                return true;
            }
        }
        AjaxUtils.ajaxJsonForbidMessage((HttpServletRequest) request,(HttpServletResponse) response);
        return false;
    }

}
