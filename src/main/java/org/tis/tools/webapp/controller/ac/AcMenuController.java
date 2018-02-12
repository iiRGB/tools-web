package org.tis.tools.webapp.controller.ac;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.ac.AcFunc;
import org.tis.tools.model.po.ac.AcMenu;
import org.tis.tools.model.po.ac.AcOperatorMenu;
import org.tis.tools.model.vo.ac.AcMenuDetail;
import org.tis.tools.rservice.ac.capable.IApplicationRService;
import org.tis.tools.rservice.ac.capable.IMenuRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaoch on 2017/7/16.
 */
@Controller
@RequestMapping("/AcMenuController")
public class AcMenuController extends BaseController {

    @Autowired
    IMenuRService menuRService;
    @Autowired
    IApplicationRService applicationRService;


    /**
     * 查询所有应用
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllAcApp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAllAcApp() {
        return getReturnMap(menuRService.queryAllAcApp());
    }


    /**
     * 查询应用下的菜单
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryRootMenuTree", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryRootMenuTree(@RequestBody String content) {
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsonObject = JSONObject.parseObject(content);
        String guidApp = jsonObject.getString("guidApp");//所属应用GUID
        if ("#".equals(guidApp)) {
            Map map = new HashMap();
            map.put("rootName", "应用菜单");
            map.put("rootCode", "AC0000");
            result.put("data", map);//返回给前台的数据
        } else {
            List<AcMenu> acMenuList = menuRService.queryRootMenu(guidApp);
            result.put("data", acMenuList);//返回给前台的数据
        }
        return getReturnMap(result);
    }

    /**
     * 查询应用下的根菜单
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryRootMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryRootMenu(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String guidApp = jsonObject.getString("guidApp");//所属应用GUID
        List<AcMenu> acMenuList = menuRService.queryRootMenu(guidApp);
        return getReturnMap(acMenuList);
    }

    /**
     * 查询菜单的子菜单
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryChildMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryChildMenu(@RequestBody String content) {

        JSONObject jsonObject = JSONObject.parseObject(content);
        String guidApp = jsonObject.getString("guidMenu");//所属应用GUID
        List<AcMenu> acMenuList = menuRService.queryChildMenu(guidApp);
        return getReturnMap(acMenuList);
    }

    /**
     * 新增根菜单
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "新增根菜单", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "menuName", // 操作对象名
            keys = {"guidApp", "menuCode", "menuLabel"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/createRootMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createRootMenu(@RequestBody String content) {
        AcMenu acMenu = JSONObject.parseObject(content, AcMenu.class);
        AcMenu menu = menuRService.createRootMenu(acMenu);
        return getReturnMap(menu);
    }

    /**
     * 添加子菜单
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",
            operateDesc = "添加子菜单",
            retType = ReturnType.Object,
            id = "guid",
            name = "menuName",
            keys = {"guidApp", "menuCode", "menuLabel"})
    @ResponseBody
    @RequestMapping(value = "/createChildMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createChildMenu(@RequestBody String content) {

        AcMenu acMenu = menuRService.createChildMenu(JSONObject.parseObject(content, AcMenu.class));
        return getReturnMap(acMenu);
    }


    /**
     * 修改菜单
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "update",
            operateDesc = "修改菜单",
            retType = ReturnType.Object,
            id = "guid",
            name = "menuName",
            keys = {"guidApp", "menuCode", "menuLabel"})
    @ResponseBody
    @RequestMapping(value = "/editMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> editMenu(@RequestBody String content) {
        AcMenu acMenu = menuRService.editMenu(JSONObject.parseObject(content, AcMenu.class));
        return getReturnMap(acMenu);

    }

    /**
     * 删除菜单
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",
            operateDesc = "删除菜单",
            retType = ReturnType.List,
            id = "guid",
            name = "menuName",
            keys = {"guidApp", "guidFunc", "menuCode", "menuLabel"})
    @ResponseBody
    @RequestMapping(value = "/deleteMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteMenu(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String menuGuid = jsonObject.getString("menuGuid");//菜单GUID
        return getReturnMap(menuRService.deleteMenu(menuGuid));
    }

    /**
     * 移动菜单
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改菜单（移动）",
            retType = ReturnType.Object,
            id = "guid",
            name = "menuName",
            keys = {"guidParents", "menuSeq", "displayOrder"})
    @ResponseBody
    @RequestMapping(value = "/moveMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> moveMenu(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String targetGuid = jsonObject.getString("targetGuid");
        String moveGuid = jsonObject.getString("moveGuid");
        BigDecimal order = jsonObject.getBigDecimal("order");
        return getReturnMap(menuRService.moveMenu(targetGuid, moveGuid, order));
    }

    /**
     * 获取应用下的功能列表
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllFuncInApp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAllFuncInApp(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String appGuid = jsonObject.getString("appGuid");//所属应用GUID
        List<AcFunc> funcList = applicationRService.queryFuncListInApp(appGuid);
        return getReturnMap(funcList);
    }


    /**
     * 获取操作员拥有的菜单
     */
    @ResponseBody
    @RequestMapping(value = "/getMenuByUserId", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> getMenuByUserId(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String appGuid = jsonObject.getString("appGuid");//所属应用GUID
        String userId = jsonObject.getString("userId");//登陆GUID
        AcMenuDetail menu = menuRService.getMenuByUserId(userId, appGuid);
        return getReturnMap(menu.toJson());
    }


    /**
     * 获取操作员的重组菜单
     */
    @ResponseBody
    @RequestMapping(value = "/getOperatorMenuByUserId", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> getOperatorMenuByUserId(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String appGuid = jsonObject.getString("appGuid");//所属应用GUID
        String userId = jsonObject.getString("userId");//所属应用GUID
        AcMenuDetail menu = menuRService.getOperatorMenuByUserId(userId, appGuid);
        return getReturnMap(menu.toJson());
    }

    /**
     * 复制菜单到个人重组菜单
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增个人重组菜单（复制应用菜单）",
            retType = ReturnType.List,
            id = "guid",
            name = "menuName",
            keys = {"guidApp", "guidFunc", "menuCode", "menuLabel"})
    @ResponseBody
    @RequestMapping(value = "/copyMenuToOperatorMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> copyMenuToOperatorMenu(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String operatorGuid = jsonObject.getString("operatorGuid");
        String copyGuid = jsonObject.getString("copyGuid");
        String goalGuid = jsonObject.getString("goalGuid");
        BigDecimal order = jsonObject.getBigDecimal("order");
        return getReturnMap(menuRService.copyMenuToOperatorMenu(operatorGuid, copyGuid, goalGuid, order));
    }


    /**
     * 移动个人重组菜单 (String , String , BigDecimal )
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改菜单（移动）",
            retType = ReturnType.Object,
            id = "guid",
            name = "menuName",
            keys = {"guidParents", "menuSeq", "displayOrder"})
    @ResponseBody
    @RequestMapping(value = "/moveOperatorMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> moveOperatorMenu(@RequestBody String content) {

        JSONObject jsonObject = JSONObject.parseObject(content);
        String targetGuid = jsonObject.getString("targetGuid");
        String moveGuid = jsonObject.getString("moveGuid");
        BigDecimal order = jsonObject.getBigDecimal("order");
        return getReturnMap(menuRService.moveOperatorMenu(targetGuid, moveGuid, order));
    }


    /**
     * 新增重组根菜单
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增个人重组菜单（根菜单）",
            retType = ReturnType.Object,
            id = "guid",
            name = "menuName",
            keys = {"guidApp", "guidFunc", "menuCode", "menuLabel"})
    @ResponseBody
    @RequestMapping(value = "/createRootOperatorMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createRootOperatorMenu(@RequestBody String content) {
        AcOperatorMenu acMenu = JSONObject.parseObject(content, AcOperatorMenu.class);
        return getReturnMap(menuRService.createRootOperatorMenu(acMenu));
    }


    /**
     * 新增子重组菜单
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增个人重组菜单（子菜单）",
            retType = ReturnType.Object,
            id = "guid",
            name = "menuName",
            keys = {"guidApp", "guidFunc", "menuCode", "menuLabel"})
    @ResponseBody
    @RequestMapping(value = "/createChildOperatorMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createChildOperatorMenu(@RequestBody String content) {
        AcOperatorMenu acOperatorMenu = JSON.parseObject(content, AcOperatorMenu.class);
        return getReturnMap(menuRService.createChildOperatorMenu(acOperatorMenu));
    }


    /**
     * 修改重组菜单
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改个人重组菜单",
            retType = ReturnType.Object,
            id = "guid",
            name = "menuName",
            keys = {"guidApp", "guidFunc", "menuCode", "menuLabel"})
    @ResponseBody
    @RequestMapping(value = "/editOperatorMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> editOperatorMenu(@RequestBody String content) {
        AcOperatorMenu acOperatorMenu = JSON.parseObject(content, AcOperatorMenu.class);
        return getReturnMap(menuRService.editOperatorMenu(acOperatorMenu));
    }


    /**
     * 删除重组菜单
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除个人重组菜单",
            retType = ReturnType.List,
            id = "guid",
            name = "menuName",
            keys = {"guidApp", "guidFunc", "menuCode", "menuLabel"})
    @ResponseBody
    @RequestMapping(value = "/deleteOperatorMenu", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteOperatorMenu(@RequestBody String content) {

            JSONObject jsonObject = JSONObject.parseObject(content);
            String operatorMenuGuid = jsonObject.getString("operatorMenuGuid");//菜单GUID
        return getReturnMap(menuRService.deleteOperatorMenu(operatorMenuGuid));

    }

}
