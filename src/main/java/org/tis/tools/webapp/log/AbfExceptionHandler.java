package org.tis.tools.webapp.log;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.base.exception.ToolsRuntimeException;
import org.tis.tools.webapp.exception.WebAppException;

import java.util.HashMap;
import java.util.Map;

import static org.tis.tools.webapp.util.AjaxUtils.*;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class AbfExceptionHandler {


    /**
     * 服务异常处理
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ToolsRuntimeException.class)
    public Map<String, Object> handleServiceRequestError(ToolsRuntimeException ex) {
        Map<String, Object> map = new HashMap<>(3);
        map.put(RETCODE, ex.getCode());
        map.put(STATUS, ERROR);
        map.put(RETMESSAGE, ex.getMessage());
        return map;
    }

    /**
     * 权限异常
     */
    @ResponseBody
    @ExceptionHandler(ShiroException.class)
    public Map<String, Object> handleShiroException(ShiroException ex) {
        String msg ;
        String status;
        String code;
        if (ex instanceof IncorrectCredentialsException) {
            code = "AUTH-440";
            status = ERROR;
            msg = "密码错误，连续五次错误帐号会被锁定！";
        } else if (ex instanceof ExcessiveAttemptsException) {
            code = "AUTH-445";
            status = ERROR;
            msg = "达到最大错误次数，请联系管理员或稍后再试！";
        } else if (ex instanceof UnauthenticatedException || ex instanceof AuthenticationException) {
            code = "AUTH-401";
            status = ERROR;
            msg = "尚未登录或登录失效，请重新登录！";
        } else if (ex instanceof UnauthorizedException || ex instanceof AuthorizationException) {
            code = "AUTH-403";
            status = FORBID;
            msg = "没有当前功能或行为的权限！";
        } else {
            code = "AUTH-444";
            status = ERROR;
            msg = StringUtils.isBlank(ex.getMessage()) ? ex.getMessage() : ex.getCause().getMessage();
        }
        Map<String, Object> map = new HashMap<>();
        map.put(RETCODE, code);
        map.put(STATUS, status);
        map.put(RETMESSAGE, msg);
        return map;
    }

    /**
     * WebApp层异常
     */
    @ResponseBody
    @ExceptionHandler(WebAppException.class)
    public Map<String, Object> handleWebAppException(WebAppException ex) {
        Map<String, Object> map = new HashMap<>(3);
        map.put(RETCODE, ex.getCode());
        map.put(STATUS, FAILED);
        map.put(RETMESSAGE, ex.getMessage());
        return map;
    }


    /**
     * 请求异常处理
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleUnexpectedServerError(Exception ex) {
        Map<String, Object> map = new HashMap<>(3);
        map.put(RETCODE, "SYS_0001");
        map.put(STATUS, ERROR);
        map.put(RETMESSAGE, ex.getMessage());
        return map;
    }
}
