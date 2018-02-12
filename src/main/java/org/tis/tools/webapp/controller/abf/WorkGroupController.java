package org.tis.tools.webapp.controller.abf;

import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.base.exception.ToolsRuntimeException;
import org.tis.tools.model.po.ac.AcApp;
import org.tis.tools.model.po.om.*;
import org.tis.tools.rservice.om.capable.IEmployeeRService;
import org.tis.tools.rservice.om.capable.IGroupRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;
import org.tis.tools.webapp.util.AjaxUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机构管理功能
 *
 * @author
 */
@Controller
@RequestMapping(value = "/om/workgroup")
public class WorkGroupController extends BaseController {
    @Autowired
    IGroupRService groupRService;
    @Autowired
    IEmployeeRService employeeRService;

    /**
     * 展示工作组树
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/workgrouptree")
    public String workgroup(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String id = jsonObj.getString("id");
            // 通过id判断需要加载的节点
            if ("#".equals(id)) {
                // 调用远程服务,#:根
                Map<String, String> map = new HashMap<>();
                map.put("groupCode", "00000");
                map.put("groupName", "工作组树");
                List<Map> list = new ArrayList<>();
                list.add(map);
                AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list, "yyyy-MM-dd");
            } else if ("00000".equals(id)) {
                List<OmGroup> list = groupRService.queryRootGroup();
                AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list, "yyyy-MM-dd");
            } else {
                List<OmGroup> list = groupRService.queryChildGroup(id);
                AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list, "yyyy-MM-dd");
            }
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 展示筛选树
     *
     * @param model
     * @param content
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/searchtree")
    public String searchtree(ModelMap model, @RequestBody String content, HttpServletRequest request,
                             HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupName = jsonObj.getString("searchitem");
            String id = jsonObj.getString("id");
            List<OmGroup> dutyList = groupRService.queryBygroupName(groupName);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, dutyList, "yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }


    /**
     * 新增根工作组
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "新增工作组", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "groupName", // 操作对象名
            keys = {"groupName","groupCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/add")
    public Map<String, Object> add(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String flag = jsonObj.getString("flag");
        jsonObj.remove("flag");
        OmGroup og = new OmGroup();
        OmGroup reog = new OmGroup();
        if (flag.equals("root")) {
            og = JSONObject.parseObject(content, OmGroup.class);
            reog = groupRService.createRootGroup(og);
        } else {// 新增子节点
            og = JSONObject.parseObject(content, OmGroup.class);
            reog = groupRService.createGroup(og);
        }
        return getReturnMap(reog);
    }

//    /**
//     * 编辑工作组
//     *
//     * @param model
//     * @param content
//     * @param age
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/edit")
//    public String edit(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
//                       HttpServletResponse response) {
//        try {
//
//            JSONObject jsonObj = JSONObject.parseObject(content);
//            OmGroup og = new OmGroup();
//            BeanUtils.populate(og, jsonObj);
//            groupRService.createRootGroup(og);
//            AjaxUtils.ajaxJsonSuccessMessage(response, "新增根工作组成功!");
//        } catch (Exception e) {// TODO
//            AjaxUtils.ajaxJsonErrorMessage(response, "新增根工作组失败!");
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 删除工作组
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "删除工作组", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "groupName", // 操作对象名
            keys = {"groupName","groupCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/delete")
    public Map<String, Object> delete(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String groupCode = jsonObj.getString("groupCode");
        OmGroup og = groupRService.deleteGroup(groupCode);
        return getReturnMap(og);
    }

    /**
     * 生成工作组代码
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/initGroupCode")
    public String initGroupCode(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupType = jsonObj.getString("groupType");
            String groupCode = groupRService.genGroupCode(groupType);
            AjaxUtils.ajaxJsonSuccessMessage(response, groupCode, "生成工作组编号成功!");
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 查询所有工作组列表
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryall", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryall(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
       
            return getReturnMap(groupRService.queryAllGroup());
    }

    /**
     * 条件查询工作组列表
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryChild")
    public String queryby(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                          HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupCode = jsonObj.getString("groupCode");
            List<OmGroup> ogList = groupRService.queryAllchild(groupCode);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, ogList, "yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "查询失败!", e.getMessage());
        }
        return null;
    }

    /**
     * 启用---注销工作组
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "update",  // 操作类型
            operateDesc = "更改工作组状态", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "groupName", // 操作对象名
            keys = {"groupName","groupStatus"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/enableGroup")
    public Map<String, Object> enableGroup(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String groupCode = jsonObj.getString("groupCode");
        String flag = jsonObj.getString("flag");
        OmGroup og = new OmGroup();
        if ("running".equals(flag)) {
            og = groupRService.cancelGroup(groupCode);
        } else if ("cancel".equals(flag)) {
            og = groupRService.reenableGroup(groupCode, true);
        }
        return getReturnMap(og);
    }

    /**
     * 更新修改工作组
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "update",  // 操作类型
            operateDesc = "更新工作组", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "groupName", // 操作对象名
            keys = {"groupName","groupCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/updateGroup", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> updateGroup(@RequestBody String content) {
        OmGroup og = JSONObject.parseObject(content, OmGroup.class);
        OmGroup reog = groupRService.updateGroup(og);
        return getReturnMap(reog);
    }

    /**
     * 生成下级岗位列表
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadPosition")
    public String loadPosition(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupCode = jsonObj.getString("groupCode");
            //TODO
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 生成下级人员列表
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadempin")
    public String loadempin(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupCode = jsonObj.getString("groupCode");
            List<OmEmployee> empList = groupRService.queryEmployee(groupCode);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, empList, "yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 加载不在此工作组的人员列表(同属同一机构)
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadempNotin")
    public String loadempNotin(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupCode = jsonObj.getString("groupCode");
            String guidOrg = jsonObj.getString("guidOrg");
            List<OmEmployee> empList = groupRService.queryEmpNotInGroup(guidOrg, groupCode);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, empList, "yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 新添人员
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "为工作组新增员工", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidGroup", // 操作对象标识
            name = "guidGroup", // 操作对象名
            keys = {"guidGroup","guidEmp"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addEmpGroup", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> addEmpGroup(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        OmEmpGroup data = jsonObj.getObject("data", OmEmpGroup.class);
        String groupGuid = data.getGuidGroup();
        String empGuid = data.getGuidEmp();
        employeeRService.insertEmpGroup(groupGuid, empGuid);
        return getReturnMap(data);
    }

    /**
     * 删除人员-工作组关联
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "为工作组删除员工", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidGroup", // 操作对象标识
            name = "guidGroup", // 操作对象名
            keys = {"guidGroup","guidEmp"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deleteEmpGroup", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> deleteEmpGroup(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        OmEmpGroup data = jsonObj.getObject("data", OmEmpGroup.class);
        String groupGuid = data.getGuidGroup();
        String empGuid = data.getGuidEmp();
        employeeRService.deleteEmpGroup(groupGuid, empGuid);
        return getReturnMap(data);
    }

    /**
     * 生成下级岗位列表
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadpositionin")
    public String loadpositionin(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupCode = jsonObj.getString("groupCode");
            List<OmPosition> list = groupRService.queryPositionInGroup(groupCode);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list, "yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 加载不在此工作组的岗位列表(同属同一机构)
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadpositionNotin")
    public String loadpositionNotin(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                                    HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupCode = jsonObj.getString("groupCode");
            List<OmPosition> list = groupRService.queryPositionNotInGroup(groupCode);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list, "yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 新添岗位
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "为工作组新增岗位", // 操作描述
            retType = ReturnType.List, // 返回类型，对象或数组
            id = "guidGroup", // 操作对象标识
            name = "guidGroup", // 操作对象名
            keys = {"guidGroup","guidPosition"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addGroupPosition")
    public Map<String, Object> addGroupPosition(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String groupGuid = jsonObj.getString("groupGuid");
        List<String> posGuidlist = JSON.parseArray(jsonObj.getJSONArray("posGuidlist").toJSONString(), String.class);
        groupRService.insertGroupPosition(groupGuid, posGuidlist);
        List<OmGroupPosition> list = new ArrayList<>();
        for (String posGuid : posGuidlist) {
            OmGroupPosition ogp = new OmGroupPosition();
            ogp.setGuidGroup(groupGuid);
            ogp.setGuidPosition(posGuid);
            list.add(ogp);
        }
        return getReturnMap(list);
    }

    /**
     * 删除岗位
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "为工作组删除岗位", // 操作描述
            retType = ReturnType.List, // 返回类型，对象或数组
            id = "guidGroup", // 操作对象标识
            name = "guidGroup", // 操作对象名
            keys = {"guidGroup","guidPosition"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deleteGroupPosition")
    public Map<String, Object> deleteGroupPosition(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String groupGuid = jsonObj.getString("groupGuid");
        List<String> posGuidlist = JSON.parseArray(jsonObj.getJSONArray("posGuidlist").toJSONString(), String.class);
        groupRService.deleteGroupPosition(groupGuid, posGuidlist);
        List<OmGroupPosition> list = new ArrayList<>();
        for (String posGuid : posGuidlist) {
            OmGroupPosition ogp = new OmGroupPosition();
            ogp.setGuidGroup(groupGuid);
            ogp.setGuidPosition(posGuid);
            list.add(ogp);
        }
        return getReturnMap(list);
    }


    /**
     * 查询所有工作组
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllGroup", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public String queryAllGroup(@RequestBody String content, HttpServletRequest request,
                                HttpServletResponse response) throws ToolsRuntimeException, ParseException {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("queryAllPosition request : " + content);
            }
            List<OmGroup> acDucys = groupRService.queryAllGroup();
            AjaxUtils.ajaxJsonSuccessMessage(response, acDucys);
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            logger.error("queryAllGroup exception : ", e);
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
            logger.error("queryAllGroup exception : ", e);
        }
        return null;
    }


    /**
     * 查询所有工作组
     */
    @RequestMapping(value = "/queryApp")
    public String queryApp(@RequestBody String content, HttpServletRequest request,
                                HttpServletResponse response) throws ToolsRuntimeException, ParseException {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("queryApp request : " + content);
            }
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupCode = jsonObj.getString("groupCode");
            List<AcApp> list = groupRService.queryApp(groupCode);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list,"yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            logger.error("queryApp exception : ", e);
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
            logger.error("queryApp exception : ", e);
        }
        return null;
    }

    /**
     * 查询可以为工作组添加的应用
     */
    @RequestMapping(value = "/queryNotInApp")
    public String queryNotInApp(@RequestBody String content, HttpServletRequest request,
                                HttpServletResponse response) throws ToolsRuntimeException, ParseException {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("queryApp request : " + content);
            }
            JSONObject jsonObj = JSONObject.parseObject(content);
            String groupCode = jsonObj.getString("groupCode");
            List<AcApp> list = groupRService.queryAppnotInGroup(groupCode);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list,"yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            logger.error("queryApp exception : ", e);
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
            logger.error("queryApp exception : ", e);
        }
        return null;
    }
    /**
     * 新增工作组-应用记录
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "为工作组添加应用", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidGroup", // 操作对象标识
            name = "guidGroup", // 操作对象名
            keys = {"guidGroup","guidApp"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addGroupApp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addGroupApp(@RequestBody String content) throws ToolsRuntimeException, ParseException {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String groupGuid = jsonObj.getString("groupGuid");
        String appGuid = jsonObj.getString("appGuid");
        groupRService.addGroupApp(appGuid, groupGuid);
        Map map = new HashMap();
        map.put("guidGroup", groupGuid);
        map.put("guidApp", appGuid);
        return getReturnMap(map);
    }
    /**
     * 删除工作组-应用记录
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "为工作组删除应用", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidGroup", // 操作对象标识
            name = "guidGroup", // 操作对象名
            keys = {"guidGroup","guidApp"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deleteGroupApp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteGroupApp(@RequestBody String content) throws ToolsRuntimeException, ParseException {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String groupGuid = jsonObj.getString("groupGuid");
        String appGuid = jsonObj.getString("appGuid");
        groupRService.deleteGroupApp(appGuid, groupGuid);
        Map map = new HashMap();
        map.put("guidGroup", groupGuid);
        map.put("guidApp", appGuid);
        return getReturnMap(map);
    }


    /**
     * 每个controller定义自己的返回信息变量
     */
    private Map<String, Object> responseMsg;

    @Override
    public Map<String, Object> getResponseMessage() {
        if (null == responseMsg) {
            responseMsg = new HashMap<String, Object>();
        }
        return responseMsg;
    }
}
