package org.tis.tools.webapp.controller.ac;

/**
 * Created by Administrator on 2017/7/20.
 **/

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.model.def.ACConstants;
import org.tis.tools.model.def.CommonConstants;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.dto.route.AbfRoute;
import org.tis.tools.model.dto.route.RouteState;
import org.tis.tools.model.po.ac.AcFunc;
import org.tis.tools.model.po.ac.AcOperator;
import org.tis.tools.model.po.ac.AcOperatorIdentity;
import org.tis.tools.rservice.ac.capable.IApplicationRService;
import org.tis.tools.rservice.ac.capable.IAuthenticationRService;
import org.tis.tools.rservice.ac.capable.IOperatorRService;
import org.tis.tools.shiro.authenticationToken.UserIdPasswordIdentityToken;
import org.tis.tools.shiro.realm.UserRealm;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.exception.WebAppException;
import org.tis.tools.webapp.log.LogThreadLocal;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zhaoch on 2017/7/16.
 */
@Controller
@RequestMapping("/AcAuthenticationController")
public class AcAuthenticationController extends BaseController {


    @Autowired
    IAuthenticationRService authenticationRService;
    @Autowired
    IOperatorRService operatorRService;

    @Autowired
    IApplicationRService applicationRService;

    @ResponseBody
    @RequestMapping(value="/checkUserStatus", produces ="application/json; charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> checkUserStatus(@RequestBody String content)  {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");
        List<AcOperatorIdentity> acOperatorIdentityList = authenticationRService.userStatusCheck(userId);
        return getReturnMap(acOperatorIdentityList);
    }

    /**
     * 登录
     */
   /* @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_LOGIN,
            operateDesc = "登录ABF",
            retType = ReturnType.Object,
            id = "userId",
            name = "operatorName"
    )
    @ResponseBody
    @RequestMapping(value="/login", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> login(@RequestBody String content, HttpSession httpSession) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");
        String password = jsonObject.getString("password");
        String identityGuid = jsonObject.getString("identityGuid");
        String appGuid = jsonObject.getString("appGuid");
        LogThreadLocal.getLogBuilderLocal().getLog().setUserId(userId);
        AcOperator acOperator = authenticationRService.loginCheck(userId, password, identityGuid, appGuid);
        LogThreadLocal.getLogBuilderLocal().getLog().setOperatorName(acOperator.getOperatorName());
        httpSession.setAttribute("userId", userId);
        httpSession.setAttribute("identity", identityGuid);
        httpSession.setAttribute("operatorName", acOperator.getOperatorName());
        httpSession.setAttribute("app", appGuid);
        return getReturnMap(acOperator);
    }*/

    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_LOGIN,
            operateDesc = "登录ABF",
            retType = ReturnType.Object,
            id = "userId",
            name = "operatorName"
    )
    @ResponseBody
    @RequestMapping(value="/login", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> login(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");
        if (StringUtils.isBlank(userId)) {
            throw new WebAppException("WEB-401", "用户名不能为空！");
        }
        String password = jsonObject.getString("password");
        if (StringUtils.isBlank(password)) {
            throw new WebAppException("WEB-401", "密码不能为空！");
        }
        String identityGuid = jsonObject.getString("identityGuid");
        if (StringUtils.isBlank(identityGuid)) {
            throw new WebAppException("WEB-401", "身份不能为空！");
        }
        String appCode = jsonObject.getString("appCode");
        if (StringUtils.isBlank(appCode)) {
            throw new WebAppException("WEB-401", "应用代码不能为空！");
        }
        UserIdPasswordIdentityToken token = new UserIdPasswordIdentityToken(userId, password, identityGuid, appCode);
        //this is all you have to do to support 'remember me' (no config - built in!):
//        token.setRememberMe(true);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        Session session = subject.getSession();
        AcOperator acOperator = operatorRService.queryOperatorByUserId(userId);
        LogThreadLocal.getLogBuilderLocal().getLog().setUserId(userId).setOperatorName(acOperator.getOperatorName());
        session.setAttribute("identity", identityGuid);
        session.setAttribute("userInfo", acOperator);
        session.setAttribute("userId", userId);
        session.setAttribute("appCode", appCode);
        return getReturnMap(acOperator);
    }

    /**
     * 修改密码
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改密码",
            retType = ReturnType.Object,
            id = "userId",
            name = "operatorName"
    )
    @ResponseBody
    @RequestMapping(value="/updatePassword" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String,Object> updatePassword(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");
        String newPwd = jsonObject.getString("newPwd");
        String oldPwd = jsonObject.getString("oldPwd");
        AcOperator acOperator = authenticationRService.updatePassword(userId, oldPwd, newPwd);
        // 清楚缓存
        RealmSecurityManager securityManager =
                (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm userRealm = (UserRealm) securityManager.getRealms().iterator().next();
        userRealm.clearCachedAuthenticationInfo(SecurityUtils.getSubject().getPrincipals());
        // 退出登录
        SecurityUtils.getSubject().logout();
        return getReturnMap(acOperator);
    }

    /**
     * 注销登陆
     */
    @ResponseBody
    @RequestMapping(value="/logout" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String,Object> logout(HttpSession httpSession) {
        String userId = (String) httpSession.getAttribute("userId");
        operatorRService.changeOperatorStatus(userId, ACConstants.OPERATE_STATUS_LOGOUT);
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return getReturnMap(null);
    }

    /**
     * 页面初始化
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/pageInit" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> pageInit() {
//        String userId = (String) httpSession.getAttribute("userId");
//        String identityGuid = (String) httpSession.getAttribute("identity");
//        String appGuid = (String) httpSession.getAttribute("app");
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
//        String userId = (String) subject.getPrincipal();
        String userId = (String) session.getAttribute("userId");
        String identity = (String) session.getAttribute("identity");
        String appCode = (String) session.getAttribute("appCode");
        Map<String, Object> initInfo = authenticationRService.getInitInfoByUserIdAndIden(userId, identity, appCode);
        Map<String, List<Map>> collect = ((List<Map>) initInfo.get("resources"))
                .stream()
                .collect(Collectors.groupingBy(m -> (String) m.get("funcCode")));
        AbfRoute abfRoute = new AbfRoute();
        for (String code : collect.keySet()) {
            RouteState state = new RouteState();
            for (Map map : collect.get(code)) {
                String attrValue = (String) map.get("attrValue");
                switch ((String) map.get("attrKey")) {
                    case "funcName" :
                        state.setRouteName(attrValue);
                        state.setFuncCode((String) map.get("funcCode"));
                        break;
                    case "url" :
                        state.setUrl(attrValue);
                        break;
                    case "templateUrl" :
                        state.setTemplateUrl(attrValue);
                        break;
                    case "data" :
                        state.setData(attrValue);
                        break;
                    case "controller" :
                        state.setController(attrValue);
                        break;

                    default:
                        throw new WebAppException("WEB-555", "功能资源配置格式不正确");
                }
            }
            abfRoute.add(state);
        }
        initInfo.put("resources", abfRoute.toString());
        return getReturnMap(initInfo);
    }

    /**
     * 视图检查
     * 用于路由跳转判断
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/viewCheck" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> viewCheck(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        // TODO 设置缓存生效需要清空缓存 clearAllCachedAuthorizationInfo、clearAllCachedAuthenticationInfo、clearAllCache
        // 视图权限，检查funCode是否在操作员权限中
        String funcCode = data.getString("funcCode");
        Map<String, Object> result = new HashMap<>();
        AcFunc acFunc = applicationRService.queryAcFuncByCode(funcCode);
        if (StringUtils.equals(acFunc.getIscheck(), CommonConstants.NO)) {
            result.put("isCheck", CommonConstants.NO);
            return getReturnMap(result);
        } else {
            result.put("isCheck", CommonConstants.YES);
        }
        if(SecurityUtils.getSubject().isPermitted("+" + funcCode + "+view")) {
            // TODO 获取页面权限信息返回
            List<String> bhvCodes = operatorRService
                    .getPmtFuncBhvByCode(
                            (String) SecurityUtils.getSubject().getSession().getAttribute("userId"),
                            funcCode);
            result.put("bhvCodes", bhvCodes);
            return getReturnMap(result);
        } else {
            throw new UnauthorizedException("功能权限不足！");
        }
    }



}
