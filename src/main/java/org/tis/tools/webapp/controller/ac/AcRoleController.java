package org.tis.tools.webapp.controller.ac;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.ac.*;
import org.tis.tools.rservice.ac.capable.IApplicationRService;
import org.tis.tools.rservice.ac.capable.IEntityRService;
import org.tis.tools.rservice.ac.capable.IRoleRService;
import org.tis.tools.rservice.sys.capable.IDictRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.exception.WebAppException;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zhaoch on 2017/7/16.
 */
@Controller
@RequestMapping("/AcRoleController")
public class AcRoleController extends BaseController {

    @Autowired
    IRoleRService roleRService;

    @Autowired
    IEntityRService entityRService;

    @Autowired
    IApplicationRService applicationRService;
    @Autowired
    IDictRService dictRService;


    /**
     * 查询角色列表
     */
    @ResponseBody
    @RequestMapping(value = "/queryRoleList", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryRoleList() {
        return getReturnMap(roleRService.queryAllRoleExt());
    }

    /**
     * 新增角色
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,  // 操作类型
            operateDesc = "新增角色", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "roleName", // 操作对象名
            keys = {"guidApp", "roleCode"})
    @ResponseBody
    @RequestMapping(value = "/createRole", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createRole(@RequestBody String content) {
        AcRole acRole = JSON.parseObject(content, AcRole.class);
        return getReturnMap(roleRService.createAcRole(acRole));
    }

    /**
     * 修改角色
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,  // 操作类型
            operateDesc = "修改角色", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "roleName", // 操作对象名
            keys = {"guidApp", "roleCode"})
    @ResponseBody
    @RequestMapping(value = "/editRole", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> editRole(@RequestBody String content) {
        AcRole acRole = JSON.parseObject(content, AcRole.class);
        return getReturnMap(roleRService.eidtAcRole(acRole));
    }

    /**
     * 删除角色
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,  // 操作类型
            operateDesc = "删除角色", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "roleName", // 操作对象名
            keys = {"guidApp", "roleCode"})
    @ResponseBody
    @RequestMapping(value = "/deleteRole", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteRole(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String guid = jsonObject.getString("roleGuid");
        return getReturnMap(roleRService.deleteAcRole(guid));
    }

    /**
     * appQuery查询对应的应用应用
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/appQuery", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> appQuery(@RequestBody String content) {
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsonObj = JSONObject.parseObject(content);
        String id = jsonObj.getString("id");
        //通过id判断需要加载的节点
        if ("#".equals(id.substring(0, 1))) {
            AcApp app = applicationRService.queryAcApp(id.substring(1));
            List<Map> list = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("rootName", app.getAppName());
            map.put("rootCode", app.getGuid());
            map.put("guid", app.getGuid());
            list.add(map);
            result.put("data", list);//返回给前台的数据
            result.put("type", "root");//返回给前台的数据
        } else if (id.length() > 3 && "APP".equals(id.substring(0, 3))) {
            List<AcFuncgroup> group = applicationRService.queryAcRootFuncgroup(id);
            result.put("data", group);//返回给前台的数据
            result.put("type", "app");//返回给前台的数据
        } else if (id.length() > 9 && "FUNCGROUP".equals(id.substring(0, 9))) {
            Map<String, Object> map = new HashMap<>();
            List<AcFuncgroup> groupList = applicationRService.queryAcChildFuncgroup(id);
            if (groupList.size() > 0) {
                map.put("groupList", groupList);
            }
            List<AcFunc> funcList = applicationRService.queryAcFunc(id);
            if (funcList.size() > 0) {
                map.put("funcList", funcList);
            }
            result.put("data", map);//返回给前台的数据
            result.put("type", "group");//返回给前台的数据
        }
        return getReturnMap(result);
    }


    /**
     * 角色配置功能权限
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改角色组织权限",
            retType = ReturnType.List,
            id = "guidRole",
            keys = "guidFunc"
    )
    @ResponseBody
    @RequestMapping(value = "/configRoleFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> configRoleFunc(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String roleGuid = data.getString("roleGuid");
        List<AcRoleFunc> funcList = JSON.parseArray(data.getJSONArray("funcList").toJSONString(), AcRoleFunc.class);
        return getReturnMap(roleRService.configRoleFunc(roleGuid, funcList));
    }


    /**
     * 查询角色的功能权限
     */
    @ResponseBody
    @RequestMapping(value = "/queryRoleFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryRoleFunc(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String roleGuid = jsonObject.getString("roleGuid");
        List<AcRoleFunc> acRoleFuncs = roleRService.queryAllRoleFunByRoleGuid(roleGuid);
        return getReturnMap(acRoleFuncs);
    }


    /**
     * 角色添加组织权限
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增角色组织权限",
            retType = ReturnType.List,
            id = "guidRole",
            keys = {"guidParty", "partyType"}
    )
    @ResponseBody
    @RequestMapping(value = "/addPartyRole", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> removeRoleFunc(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcPartyRole> acPartyRoleList = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), AcPartyRole.class);
        return getReturnMap(roleRService.addRoleParty(acPartyRoleList));
    }


    /**
     * 查询角色下某组织类型的权限信息
     */
    @ResponseBody
    @RequestMapping(value = "/queryRoleInParty", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryRoleInParty(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String roleGuid = jsonObject.getString("roleGuid");
        String partyType = jsonObject.getString("partyType");
        List<Map> acPartyRoleList = roleRService.queryAllRolePartyExt(roleGuid, partyType);
        return getReturnMap(acPartyRoleList);
    }

    /**
     * 移除角色下的组织对象
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除角色组织权限",
            retType = ReturnType.List,
            id = "guidRole",
            keys = {"guidParty", "partyType"}
    )
    @ResponseBody
    @RequestMapping(value = "/removePartyRole", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> removePartyRole(@RequestBody String content) {
        JSONObject object = JSON.parseObject(content);
        List<AcPartyRole> acPartyRoleList = JSON.parseArray(object.getJSONArray("data").toJSONString(), AcPartyRole.class);
        return getReturnMap(roleRService.removeRoleParty(acPartyRoleList));
    }

    /**
     * 角色添加操作员
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增角色与操作员关系",
            retType = ReturnType.List,
            id = "guidOperator",
            keys = "guidRole"
    )
    @ResponseBody
    @RequestMapping(value = "/addOperatorRole", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addOperatorRole(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcOperatorRole> acOperatorRoleList = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), AcOperatorRole.class);
        return getReturnMap(roleRService.addOperatorRole(acOperatorRoleList));
    }


    /**
     * 查询角色下的操作员集合
     */
    @ResponseBody
    @RequestMapping(value = "/queryOperatorRole", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryOperatorRole(@RequestBody String content) {

        JSONObject jsonObject = JSONObject.parseObject(content);
        String roleGuid = jsonObject.getString("roleGuid");
        List<Map> acOperatorRoles = roleRService.queryAllOperatorRoleExt(roleGuid);
        return getReturnMap(acOperatorRoles);
    }

    /**
     * 移除角色下的操作员
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除角色与操作员关系",
            retType = ReturnType.List,
            id = "guidOperator",
            keys = "guidRole"
    )
    @ResponseBody
    @RequestMapping(value = "/removeOperatorRole", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> removeOperatorRole(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcOperatorRole> acOperatorRoleList = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), AcOperatorRole.class);
        return getReturnMap(roleRService.removeOperatorRole(acOperatorRoleList));
    }

    /**
     * 查询角色拥有功能的权限行为
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryAcRoleBhvsByFuncGuid", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAcRoleBhvsByFuncGuid(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String roleGuid = data.getString("roleGuid");
        String funcGuid = data.getString("funcGuid");
        return getReturnMap(roleRService.queryAcRoleBhvsByFuncGuid(roleGuid, funcGuid));
    }

    /**
     * 角色添加功能的权限行为
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增角色功能行为定义",
            retType = ReturnType.List,
            id = "guid",
            keys = {"guidFuncBhv", "guidApp"}
    )
    @ResponseBody
    @RequestMapping(value = "/addAcRoleBhvs", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addAcRoleBhvs(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray data = jsonObject.getJSONArray("data");
        List<AcRoleBhv> acRoleBhvs = JSON.parseArray(data.toJSONString(), AcRoleBhv.class);
        roleRService.addAcRoleBhvs(acRoleBhvs);
        return getReturnMap(acRoleBhvs);
    }

    /**
     * 角色删除功能的权限行为
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除角色功能行为定义",
            retType = ReturnType.List,
            id = "guid",
            keys = {"guidFuncBhv", "guidApp"}
    )
    @ResponseBody
    @RequestMapping(value = "/removeAcRoleBhvs", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> removeAcRoleBhvs(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray data = jsonObject.getJSONArray("data");
        List<AcRoleBhv> acRoleBhvs = JSON.parseArray(data.toJSONString(), AcRoleBhv.class);
        roleRService.removeAcRoleBhvs(acRoleBhvs);
        return getReturnMap(acRoleBhvs);
    }


    /**
     * 角色与实体树查询
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/acRoleEntityTree", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> acRoleEntityTree(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String type = data.getString("type"); // 1.entityType实体类型 2.entity 实体
        if (StringUtils.equals(type, "entityType")) {
            return getReturnMap(dictRService.queryDictItemListByDictKey("DICT_AC_ENTITYTYPE"));
        } else if (StringUtils.equals(type, "entity")) {
            String roleGuid = data.getString("roleGuid");
            String entityType = data.getString("entityType");
            String isEdit = data.getString("isEdit");
            if(StringUtils.equals(isEdit, "y")) {
                String appGuid = data.getString("appGuid");
                List<String> guids = roleRService.getAcRoleEntitiesByEntityType(roleGuid, entityType)
                        .stream()
                        .map(map -> (String) map.get("guidEntity")).collect(Collectors.toList());
                Map<String, Object> map = new HashMap<>();
                map.put("all", entityRService.queryAcEntityList(appGuid, entityType));
                map.put("own", guids);
                return getReturnMap(map);
            } else {
                return getReturnMap(roleRService.getAcRoleEntitiesByEntityType(roleGuid, entityType));
            }
        } else {
            throw new WebAppException("WEB-444", "未知的请求数据！");
        }
    }

    /**
     * 角色配置实体权限
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "配置角色与实体关系",
            retType = ReturnType.List,
            id = "guidRole",
            keys = "guidEntity"
    )
    @ResponseBody
    @RequestMapping(value = "/configRoleEntity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> configRoleEntity(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String roleGuid = data.getString("roleGuid");
        List<String> entityGuids = JSON.parseArray(data.getString("entityGuids"), String.class);
        return getReturnMap(roleRService.configRoleEntity(roleGuid, entityGuids));
    }


    /**
     * 新增角色与实体关系
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,  // 操作类型
            operateDesc = "新增角色与实体关系", // 操作描述
            retType = ReturnType.List, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidEntity"})
    @ResponseBody
    @RequestMapping(value = "/addAcRoleEntity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addAcRoleEntity(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcRoleEntity> acRoleEntities = JSON.parseArray(jsonObject.getString("data"), AcRoleEntity.class);
        roleRService.addAcRoleEntity(acRoleEntities);
        return getReturnMap(acRoleEntities);
    }

    /**
     * 删除角色与实体关系
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,  // 操作类型
            operateDesc = "删除角色与实体关系", // 操作描述
            retType = ReturnType.List, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidEntity"})
    @ResponseBody
    @RequestMapping(value = "/deleteAcRoleEntity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> removeAcRoleEntity(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcRoleEntity> acRoleEntities = JSON.parseArray(jsonObject.getString("data"), AcRoleEntity.class);
        roleRService.removeAcRoleEntity(acRoleEntities);
        return getReturnMap(acRoleEntities);
    }

    /**
     * 修改角色与实体关系
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,  // 操作类型
            operateDesc = "修改角色与实体关系", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidEntity"})
    @ResponseBody
    @RequestMapping(value = "/updateAcRoleEntity", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> updateAcRoleEntity(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        AcRoleEntity acRoleEntity = JSON.parseObject(jsonObject.getString("data"), AcRoleEntity.class);
        roleRService.updateAcRoleEntity(acRoleEntity);
        return getReturnMap(acRoleEntity);
    }


    /**
     * 新增角色与实体属性关系
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,  // 操作类型
            operateDesc = "新增角色与实体属性关系", // 操作描述
            retType = ReturnType.List, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidEntityfield"})
    @ResponseBody
    @RequestMapping(value = "/addAcRoleEntityfield", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addAcRoleEntityfield(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcRoleEntityfield> acRoleEntityfields = JSON.parseArray(jsonObject.getString("data"), AcRoleEntityfield.class);
        roleRService.addAcRoleEntityfield(acRoleEntityfields);
        return getReturnMap(acRoleEntityfields);
    }

    /**
     * 删除角色与实体属性关系
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,  // 操作类型
            operateDesc = "删除角色与实体属性关系", // 操作描述
            retType = ReturnType.List, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidEntityfield"})
    @ResponseBody
    @RequestMapping(value = "/deleteAcRoleEntityfield", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteAcRoleEntityfield(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcRoleEntityfield> acRoleEntityfields = JSON.parseArray(jsonObject.getString("data"), AcRoleEntityfield.class);
        roleRService.removeAcRoleEntityfield(acRoleEntityfields);
        return getReturnMap(acRoleEntityfields);
    }

    /**
     * 修改角色与实体属性关系
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,  // 操作类型
            operateDesc = "修改角色与实体属性关系", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidEntityfield"})
    @ResponseBody
    @RequestMapping(value = "/updateAcRoleEntityfield", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> updateAcRoleEntityfield(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        AcRoleEntityfield acRoleEntityfields = JSON.parseObject(jsonObject.getString("data"), AcRoleEntityfield.class);
        roleRService.updateAcRoleEntityfield(acRoleEntityfields);
        return getReturnMap(acRoleEntityfields);
    }

    /**
     * 查询实体属性列表
     */
    @ResponseBody
    @RequestMapping(value = "/queryAcEntityfieldList", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAcEntityfieldList(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String guid = jsonObject.getJSONObject("data").getString("entityGuid");
        return getReturnMap(entityRService.queryAcEntityfieldList(guid));
    }


    /**
     * 查询角色实体下的属性
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAcRoleEntitityfields", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> getAcRoleEntitityfields(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String roleGuid = data.getString("roleGuid");
        String entityGuid = data.getString("entityGuid");
        return getReturnMap(roleRService.getAcRoleEntitityfieldsByEntityGuid(roleGuid, entityGuid));
    }

    /**
     * 新增角色与实体数据范围关系
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,  // 操作类型
            operateDesc = "新增角色与实体数据范围关系", // 操作描述
            retType = ReturnType.List, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidDatascope"})
    @ResponseBody
    @RequestMapping(value = "/addAcRoleDatascope", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addAcRoleDatascope(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcRoleDatascope> acRoleDatascopes = JSON.parseArray(jsonObject.getString("data"), AcRoleDatascope.class);
        roleRService.addAcRoleDatascope(acRoleDatascopes);
        return getReturnMap(acRoleDatascopes);
    }

    /**
     * 删除角色与实体数据范围关系
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,  // 操作类型
            operateDesc = "新增角色与实体数据范围关系", // 操作描述
            retType = ReturnType.List, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidDatascope"})
    @ResponseBody
    @RequestMapping(value = "/deleteAcRoleDatascope", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteAcRoleDatascope(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        List<AcRoleDatascope> acRoleDatascopes = JSON.parseArray(jsonObject.getString("data"), AcRoleDatascope.class);
        roleRService.removeAcRoleDatascope(acRoleDatascopes);
        return getReturnMap(acRoleDatascopes);
    }

    /**
     * 查询角色实体下的数据范围
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAcRoleDatascopes", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> getAcRoleDatascopes(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String roleGuid = data.getString("roleGuid");
        String entityType = data.getString("entityType");
        return getReturnMap(roleRService.getAcRoleDatascopesByEntityGuid(roleGuid, entityType));
    }


    /**
     * 查询数据范围权限列表
     */
    @ResponseBody
    @RequestMapping(value = "/queryAcDatascopeList", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAcDatascopeList(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String guid = jsonObject.getJSONObject("data").getString("entityGuid");
        return getReturnMap(entityRService.queryAcDatascopeList(guid));
    }




}
