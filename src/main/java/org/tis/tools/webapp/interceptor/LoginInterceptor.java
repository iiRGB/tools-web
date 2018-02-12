//package org.tis.tools.webapp.interceptor;
//
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.subject.Subject;
//import com.alibaba.dubbo.config.annotation.Reference;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//import org.tis.tools.rservice.ac.capable.IAuthenticationRService;
//import org.tis.tools.webapp.exception.WebAppException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class LoginInterceptor extends HandlerInterceptorAdapter {
//
//    @Reference
//    IAuthenticationRService authenticationRService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws WebAppException {
//
//        Subject subject = SecurityUtils.getSubject();
//        System.out.println("登陆状态-------->" + subject.isAuthenticated());
//        if(!subject.isAuthenticated()) {
//            throw new WebAppException("SYS_4444", "权限失效，请重新登录");
//        }
//        //获取Session
////        HttpSession session = request.getSession();
////        String userId = (String)session.getAttribute("userId");
////        if(userId == null){
////            throw new WebAppException("SYS_4444", "权限失效，请重新登录");
////        }
////        request.getParameter("data")
////        authenticationRService.operateAuthCheck(userId, request.getPathInfo(),)
////        if(request.getPathInfo()
//        //不符合条件的，跳转到登录界面
////        AjaxUtils.ajaxJsonFailMessage(response, "SYS_4444", "权限失效，请重新登录");
//        return true;
//    }
//
//
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//    }
//
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//    }
//}
