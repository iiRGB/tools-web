package org.tis.tools.webapp.controller.ac;

import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.base.exception.ToolsRuntimeException;
import org.tis.tools.model.def.ACConstants;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.ac.*;
import org.tis.tools.model.vo.ac.AcOperatorFuncDetail;
import org.tis.tools.rservice.ac.capable.IApplicationRService;
import org.tis.tools.rservice.ac.capable.IOperatorRService;
import org.tis.tools.rservice.ac.capable.IRoleRService;
import org.tis.tools.shiro.realm.UserRealm;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/AcOperatorController")
public class AcOperatorController extends BaseController {

    @Autowired
    IOperatorRService operatorRService;
    @Autowired
    IRoleRService roleRService;
    @Autowired
    IApplicationRService applicationRService;

    @Autowired
    CacheManager cacheManager;

    /**
     * 查询操作员列表
     *
     * @param content
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllOperator", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAllOperator(@RequestBody String content) {
        List<AcOperator> acOperators = operatorRService.queryOperators();
        return getReturnMap(acOperators);
    }

    /**
     * 新增操作员
     *
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增操作员",
            retType = ReturnType.Object,
            id = "guid",
            name = "operatorName",
            keys = "userId"
    )
    @ResponseBody
    @RequestMapping(value = "/createOperator", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createOperator(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        AcOperator acOperator = jsonObject.getObject("data", AcOperator.class);
        return getReturnMap(operatorRService.createOperator(acOperator));
    }

    /**
     * 删除操作员
     *
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除操作员",
            retType = ReturnType.Object,
            id = "guid",
            name = "operatorName",
            keys = "userId"
    )
    @ResponseBody
    @RequestMapping(value = "/deleteOperator", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteOperator(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String operatorGuid = jsonObject.getJSONObject("data").getString("operatorGuid");
        return getReturnMap(operatorRService.deleteOperator(operatorGuid));
    }

    /**
     * 修改操作员
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改操作员",
            retType = ReturnType.Object,
            id = "guid",
            name = "operatorName",
            keys = "userId"
    )
    @ResponseBody
    @RequestMapping(value = "/editOperator", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> editOperator(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        AcOperator acOperator = jsonObject.getObject("data", AcOperator.class);
        return getReturnMap(operatorRService.editOperator(acOperator));
    }

    /**
     * 设置默认操作员身份
     *
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改操作员身份(设置默认身份)",
            retType = ReturnType.Object,
            id = "guid",
            name = "identityName",
            keys = "identityFlag"
    )
    @ResponseBody
    @RequestMapping(value = "/setDefaultOperatorIdentity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> setDefaultOperator(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String identityGuid = jsonObject.getString("identityGuid");
        return getReturnMap(operatorRService.setDefaultOperatorIdentity(identityGuid));
    }


    /**
     * 查询操作员身份列表
     * 传入操作员GUID
     *
     * @param content
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllOperatorIdentity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAllOperatorIdentity(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");
        return getReturnMap(operatorRService.queryOperatorIdentitiesByUserId(userId));
    }

    /**
     * 新增操作员身份
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增操作员身份",
            retType = ReturnType.Object,
            id = "guid",
            name = "identityName",
            keys = "identityFlag"
    )
    @ResponseBody
    @RequestMapping(value = "/createOperatorIdentity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createOperatorIdentity(@RequestBody String content) {
        AcOperatorIdentity acOperatorIdentity = JSONObject.parseObject(content, AcOperatorIdentity.class);
        return getReturnMap(operatorRService.createOperatorIdentity(acOperatorIdentity));

    }

    /**
     * 删除操作员身份
     *
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除操作员身份",
            retType = ReturnType.Object,
            id = "guid",
            name = "identityName",
            keys = "identityFlag"
    )
    @ResponseBody
    @RequestMapping(value = "/deleteOperatorIdentity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteOperatorIdentity(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String identityGuid = jsonObject.getString("identityGuid");//所属身份GUID
        return getReturnMap(operatorRService.deleteOperatorIdentity(identityGuid));
    }

    /**
     * 修改操作员身份
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改操作员身份",
            retType = ReturnType.Object,
            id = "guid",
            name = "identityName",
            keys = "identityFlag"
    )
    @ResponseBody
    @RequestMapping(value = "/editOperatorIdentity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> editOperatorIdentity(@RequestBody String content) {
        AcOperatorIdentity acOperatorIdentity = JSONObject.parseObject(content, AcOperatorIdentity.class);
        return getReturnMap(operatorRService.editOperatorIdentity(acOperatorIdentity));
    }

    /**
     * 查询操作员身份权限集列表
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllOperatorIdentityRes", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAllOperatorIdentityRes(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String identityGuid = jsonObject.getString("identityGuid");
        List<Map> acOperatorIdentityreses = operatorRService.queryOperatorIdentityreses(identityGuid);
        return getReturnMap(acOperatorIdentityreses);
    }

    /**
     * 新增操作员身份权限
     *
     * @param content
     * @return
     * @throws ToolsRuntimeException
     * @throws ParseException
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增操作员身份权限",
            retType = ReturnType.List,
            id = "guidIdentity",
            keys = {"acResourcetype", "guidAcResource"}
    )
    @ResponseBody
    @RequestMapping(value = "/createOperatorIdentityres", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createOperatorIdentityres(@RequestBody String content) {
        List<AcOperatorIdentityres> acOperatorIdentityreses = JSON.parseArray(content, AcOperatorIdentityres.class);
        operatorRService.createOperatorIdentityres(acOperatorIdentityreses);
        return getReturnMap(acOperatorIdentityreses);
    }

    /**
     * 删除操作员身份权限
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除操作员身份权限",
            retType = ReturnType.List,
            id = "guidIdentity",
            keys = {"acResourcetype", "guidAcResource"}
    )
    @ResponseBody
    @RequestMapping(value = "/deleteOperatorIdentityres", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteOperatorIdentityres(@RequestBody String content) {
        List<AcOperatorIdentityres> acOperatorIdentityreses = JSON.parseArray(content, AcOperatorIdentityres.class);
        operatorRService.deleteOperatorIdentityres(acOperatorIdentityreses);
        return getReturnMap(acOperatorIdentityreses);
    }

    /**
     * 根据资源类型查询操作员对应角色
     */
    @ResponseBody
    @RequestMapping(value = "/queryRoleInOperatorByResType", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryRoleInOperatorByResType(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String operatorGuid = jsonObject.getString("operatorGuid");//操作员GUID
        String resType = jsonObject.getString("resType");//资源类型
        List<AcRole> roleList = operatorRService.queryOperatorResByResType(operatorGuid, resType);
        return getReturnMap(roleList);
    }


    /**
     * 根据USERID查询操作员GUID
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorByUserId", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorByUserId(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");//操作员USER_ID
        AcOperator acOperator = operatorRService.queryOperatorByUserId(userId);
        return getReturnMap(acOperator);
    }


    /**
     * 根据USERID查询操作员未授权角色
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorUnauthorizedRoleList", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorUnauthorizedRoleList(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");//操作员USER_ID
        List<AcRole> acRoleList = roleRService.queryOperatorUnauthorizedRoleList(userId);
        return getReturnMap(acRoleList);
    }

    /**
     * 根据USERID查询操作员已授权角色
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorAuthorizedRoleList", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorAuthorizedRoleList(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");//操作员USER_ID
        List<AcRole> acRoleList = roleRService.queryOperatorAuthorizedRoleList(userId);
        return getReturnMap(acRoleList);
    }

    /**
     * 根据USERID查询操作员继承角色
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorInheritRoleList", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorInheritRoleList(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");//操作员USER_ID
        List<AcRole> acRoleList = roleRService.queryOperatorInheritRoleList(userId);
        return getReturnMap(acRoleList);

    }

    /**
     * 根据USERID查询特殊权限树
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorFuncInfoInApp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorFuncInfoInApp(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");//操作员USER_ID
        AcOperatorFuncDetail info = operatorRService.queryOperatorFuncInfoInApp(userId);
        return getReturnMap(info.toString());
    }

    /**
     * 查询用户的所有功能列表
     * 用于展示特殊功能列表
     * 用于展示用户已有的功能权限
     */
    @ResponseBody
    @RequestMapping(value = "/queryAcOperatorFunListByUserId", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAcOperatorFunListByUserId(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId");//操作员USER_ID
        List<Map> info = operatorRService.queryAcOperatorFunListByUserId(userId);
        return getReturnMap(info);
    }


    /**
     * 新增用户特殊功能权限
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增操作员特殊功能",
            retType = ReturnType.Object,
            id = "guidOperator",
            keys = {"guidFunc", "authType"}
    )
    @ResponseBody
    @RequestMapping(value = "/addAcOperatorFun", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addAcOperatorFun(@RequestBody String content) {
        AcOperatorFunc acOperatorFunc = JSON.parseObject(content, AcOperatorFunc.class);
        return getReturnMap(operatorRService.addAcOperatorFunc(acOperatorFunc));
    }

    /**
     * 移除用户特殊功能权限
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除操作员特殊功能",
            retType = ReturnType.Object,
            id = "guidOperator",
            keys = {"guidFunc", "authType"}
    )
    @ResponseBody
    @RequestMapping(value = "/removeAcOperatorFun", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> removeAcOperatorFun(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String operatorGuid = jsonObject.getString("operatorGuid");//操作员GUID
        String funcGuid = jsonObject.getString("funcGuid");// 功能GUID
        return getReturnMap(operatorRService.removeAcOperatorFunc(operatorGuid, funcGuid));
    }

    /**
     * 查询操作员的所有应用
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorAllApp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorAllApp(@RequestBody String content) {
        return getReturnMap(applicationRService.queryOperatorAllApp(JSONObject.parseObject(content).getString("userId")));
    }

    /**
     * 修改个人配置
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改个人配置",
            retType = ReturnType.Object,
            id = "guidConfig"
    )
    @ResponseBody
    @RequestMapping(value = "/saveOperatorLog", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> saveOperatorLog(@RequestBody String content) {
        return getReturnMap(operatorRService.saveOperatorLog(JSONObject.parseObject(content, AcOperatorConfig.class)));
    }


    /**
     * 查询操作员的个性化配置
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorConfig", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorConfig(@RequestBody String content) {
        String userId = JSONObject.parseObject(content).getString("userId");
        String appGuid = JSONObject.parseObject(content).getString("appGuid");
        return getReturnMap(operatorRService.queryOperatorConfig(userId, appGuid)
                .stream()
                .collect(Collectors.groupingBy(AcConfig::getConfigType)));
    }

    /**
     * 查询操作员在某功能的行为白名单和黑名单
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorBhvListInFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorBhvListInFunc(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String funcGuid = jsonObject.getString("funcGuid");
        String userId = jsonObject.getString("userId");
        return getReturnMap(operatorRService.queryOperatorBhvListInFunc(funcGuid, userId));
    }


    /**
     * 操作员功能行为添加黑名单
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "添加操作员功能行为黑名单",
            retType = ReturnType.List,
            id = "guidFuncBhv",
            name = "bhvName",
            keys = "bhvCode"
    )
    @ResponseBody
    @RequestMapping(value = "/addOperatorBhvBlackList", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addOperatorBhvBlackList(@RequestBody String content) {
        return getReturnMap(operatorRService.addOperatorBhvBlackList(JSON.parseArray(content, AcOperatorBhv.class)));
    }

    /**
     * 操作员功能行为移除黑名单
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "移除操作员功能行为黑名单",
            retType = ReturnType.List,
            id = "guidFuncBhv",
            name = "bhvName",
            keys = "bhvCode"
    )
    @ResponseBody
    @RequestMapping(value = "/deleteOperatorBhvBlackList", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteOperatorBhvBlackList(@RequestBody String content) {
        return getReturnMap(operatorRService.deleteOperatorBhvBlackList(JSON.parseArray(content, AcOperatorBhv.class)));
    }

    /**
     * 根据用户和应用id查询功能树
     */
    @ResponseBody
    @RequestMapping(value = "/getOperatorFuncInfo", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> getOperatorFuncInfo(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String userId = jsonObject.getString("userId"); // 操作员USER_ID
        String appGuid = jsonObject.getString("appGuid");// 应用id
        AcOperatorFuncDetail info = operatorRService.getOperatorFuncInfo(userId, appGuid);
        return getReturnMap(info.toString());
    }

    /**
     * 获取没有关联员工的操作员
     */
    @ResponseBody
    @RequestMapping(value = "/getOperatorsNotLinkEmp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> getOperatorsNotLinkEmp() {
        return getReturnMap(operatorRService.getOperatorsNotLinkEmp());
    }

    /**
     * 改变操作员状态
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改操作员状态",
            retType = ReturnType.Object,
            id = "guid",
            name = "operatorName",
            keys = "operatorStatus"
    )
    @ResponseBody
    @RequestMapping(value = "/changeOperatorStatus", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> changeOperatorStatus(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String userId = data.getString("userId");
        String status = data.getString("status");
        AcOperator acOperator = operatorRService.changeOperatorStatus(userId, status);
        if(StringUtils.equals(status, ACConstants.OPERATE_STATUS_LOGIN)) {
            cacheManager.getCache("passwordRetryCache").remove(userId);
        }
        return getReturnMap(acOperator);
    }

    /**
     * 获取操作员功能行为信息
     * 包含 已授权（从角色授权） 特别禁止
     * 未授权（从功能所有行为筛选掉角色授权） 和 特别允许 列表
     */
    @ResponseBody
    @RequestMapping(value = "/getOperatorFuncBhvInfo", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> getOperatorFuncBhvInfo(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String userId = data.getString("userId");
        String funcGuid = data.getString("funcGuid");
        return getReturnMap(operatorRService.getOperatorFuncBhvInfo(userId, funcGuid));
    }


    /**
     * 添加操作员特殊功能行为权限
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增操作员特殊功能行为权限",
            retType = ReturnType.List,
            id = "guidOperator",
            keys = {"guidFuncBhv", "authType"}
    )
    @ResponseBody
    @RequestMapping(value = "/addAcOperatorBhv", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addAcOperatorBhv(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray data = jsonObject.getJSONArray("data");
        List<AcOperatorBhv> acOperatorBhvs = JSON.parseArray(data.toJSONString(), AcOperatorBhv.class);
        operatorRService.addAcOperatorBhv(acOperatorBhvs);
        return getReturnMap(acOperatorBhvs);
    }

    /**
     * 移除操作员特殊功能行为权限
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除操作员特殊功能行为权限",
            retType = ReturnType.List,
            id = "guidOperator",
            keys = {"guidFuncBhv", "authType"}
    )
    @ResponseBody
    @RequestMapping(value = "/removeAcOperatorBhv", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> removeAcOperatorBhv(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray data = jsonObject.getJSONArray("data");
        List<AcOperatorBhv> acOperatorBhvs = JSON.parseArray(data.toJSONString(), AcOperatorBhv.class);
        operatorRService.removeAcOperatorBhv(acOperatorBhvs);
        return getReturnMap(acOperatorBhvs);
    }

    /**
     * 获取操作员功能信息
     * 包含 已授权（从角色授权） 特别禁止
     * 未授权 和 特别允许 列表
     */
    @ResponseBody
    @RequestMapping(value = "/getOperatorFuncAuthInfo", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> getOperatorFuncAuthInfo(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String userId = data.getString("userId");
        String appGuid = data.getString("appGuid");
        return getReturnMap(operatorRService.getOperatorFuncAuthInfo(userId, appGuid));
    }

    /**
     * 添加操作员特殊功能权限
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增操作员特殊功能权限",
            retType = ReturnType.List,
            id = "guidOperator",
            keys = {"guidFunc", "authType"}
    )
    @ResponseBody
    @RequestMapping(value = "/addAcOperatorFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addAcOperatorFunc(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray data = jsonObject.getJSONArray("data");
        List<AcOperatorFunc> acOperatorfuncs = JSON.parseArray(data.toJSONString(), AcOperatorFunc.class);
        operatorRService.addAcOperatorFunc(acOperatorfuncs);
        return getReturnMap(acOperatorfuncs);
    }

    /**
     * 移除操作员特殊功能行为权限
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除操作员特殊功能权限",
            retType = ReturnType.List,
            id = "guidOperator",
            keys = {"guidFunc", "authType"}
    )
    @ResponseBody
    @RequestMapping(value = "/removeAcOperatorFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> removeAcOperatorFunc(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray data = jsonObject.getJSONArray("data");
        List<AcOperatorFunc> acOperatorfuncs = JSON.parseArray(data.toJSONString(), AcOperatorFunc.class);
        operatorRService.removeAcOperatorFunc(acOperatorfuncs);
        return getReturnMap(acOperatorfuncs);
    }


    @ResponseBody
    @RequestMapping(value = "/refreshOperator", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> refreshOperator(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String userId = data.getString("userId");
        RealmSecurityManager securityManager =
                (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm userRealm = (UserRealm) securityManager.getRealms().iterator().next();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(userId, userRealm.getName());
        Subject subject = SecurityUtils.getSubject();
        subject.runAs(principals);
        userRealm.clearCachedAuthorizationInfo(subject.getPrincipals());
        userRealm.clearCachedAuthenticationInfo(subject.getPrincipals());
        subject.releaseRunAs();
        return getReturnMap(null);
    }

    @ResponseBody
    @RequestMapping(value = "/refreshAll", produces = "application/json;charset=UTF-8", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> refreshAll() {
        RealmSecurityManager securityManager =
                (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm userRealm = (UserRealm) securityManager.getRealms().iterator().next();
        userRealm.clearAllCache();
        return getReturnMap(null);
    }
}
