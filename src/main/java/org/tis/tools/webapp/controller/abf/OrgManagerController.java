/**
 * auto generated
 * Copyright (C) 2016 bronsp.com, All rights reserved.
 */
package org.tis.tools.webapp.controller.abf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.base.WhereCondition;
import org.tis.tools.base.exception.ToolsRuntimeException;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.ac.*;
import org.tis.tools.model.po.om.OmEmpOrg;
import org.tis.tools.model.po.om.OmEmployee;
import org.tis.tools.model.po.om.OmOrg;
import org.tis.tools.model.po.om.OmPosition;
import org.tis.tools.rservice.ac.capable.IRoleRService;
import org.tis.tools.rservice.om.capable.IEmployeeRService;
import org.tis.tools.rservice.om.capable.IOrgRService;
import org.tis.tools.rservice.om.capable.IPositionRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;
import org.tis.tools.webapp.util.AjaxUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;


/**
 * 机构管理功能
 *
 * @author
 */
@Controller
@RequestMapping(value = "/om/org")
public class OrgManagerController extends BaseController {

    @Autowired
    IOrgRService orgRService;
    @Autowired
    IPositionRService positionRService;
    @Autowired
    IEmployeeRService employeeRService;
    @Autowired
    IRoleRService roleRService;


    /**
     * 展示机构树
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/tree")
    public String tree(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                       HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String id = jsonObj.getString("id");
            String guidOrg = jsonObj.getString("guidOrg");
            String positionCode = jsonObj.getString("positionCode");
            List<OmOrg> rootOrgs = new ArrayList<OmOrg>();
            List<OmPosition> omp = new ArrayList<OmPosition>();
            // 通过id判断需要加载的节点
            if ("#".equals(id)) {
                // #:根
                OmOrg om = new OmOrg();
                om.setOrgName("组织机构");
                om.setOrgCode("99999");
                rootOrgs.add(om);
            } else if (id.equals("99999")) {
                // 加载根机构
                rootOrgs = orgRService.queryAllRoot();
            } else if (id.startsWith("GW")) {
                // TODO
                // 返回机构下岗位信息.根据id查询岗位信息并返回生成树节点.
                omp = positionRService.queryPositionByOrg(guidOrg, null);

            } else if (!id.startsWith("POSITION")) {
                // 加载子机构
                rootOrgs = orgRService.queryChilds(id);
                OmOrg og = new OmOrg();
                // 为每一个节点增加岗位信息节点
                og.setOrgName("岗位信息");
                og.setOrgCode("GW" + id);
                og.setSortNo(new BigDecimal("9999999"));
                og.setGuid(guidOrg);

                rootOrgs.add(og);
            } else {
                omp = positionRService.queryChilds(positionCode);
            }

            if (rootOrgs == null || rootOrgs.isEmpty()) {
                AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, omp, "yyyy-MM-dd");
            } else {
                AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, rootOrgs, "yyyy-MM-dd");
            }

        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 展示筛选机构树
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/search")
    public String search(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                         HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String id = jsonObj.getString("id");
            String name = jsonObj.getString("searchitem");
            String guidOrg = jsonObj.getString("guidOrg");
            List<OmOrg> rootOrgs = new ArrayList<OmOrg>();
            List<OmPosition> omp = new ArrayList<OmPosition>();
            // 通过id判断需要加载的节点
            if ("#".equals(id)) {
                rootOrgs = orgRService.queryOrgsByName(name);
            } else if (id.startsWith("GW")) {
                omp = positionRService.queryPositionByOrg(guidOrg, null);

            } else {
                rootOrgs = orgRService.queryChilds(id);
                OmOrg og = new OmOrg();
                // 为每一个节点增加岗位信息节点
                og.setOrgName("岗位信息");
                og.setOrgCode("GW" + id);
                og.setGuid(guidOrg);
                rootOrgs.add(og);
            }
            if (rootOrgs == null || rootOrgs.isEmpty()) {
                AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, omp, "yyyy-MM-dd");
            } else {
                AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, rootOrgs, "yyyy-MM-dd");
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
     * 新增机构综合.
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "新增机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "orgCode", // 操作对象标识
            name = "orgName", // 操作对象名
            keys = {"orgCode", "orgName"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/add")
    public Map<String, Object> add(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        //用于返回
        OmOrg org;
        OmOrg og = JSONObject.parseObject(content, OmOrg.class);
        String flag = jsonObj.getString("flag");
//            Date startDate = null;
//            Date endDate = null;
//            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//            if (map.get("startDate") != null) {
//                startDate = sf.parse(map.remove("startDate").toString());
//            }
//            if (map.get("endDate") != null) {
//                endDate = sf.parse(map.remove("endDate").toString());
//            }
//            OmOrg og = new OmOrg();
//            BeanUtils.populate(og, map);
//            System.out.println(og);
//            og.setEndDate(endDate);
//            og.setStartDate(startDate);
        String area = og.getArea();
        String orgName = og.getOrgName();
        String orgType = og.getOrgType();
        String orgDegree = og.getOrgDegree();
        if (flag.equals("root")) {
            org = orgRService.createRootOrg(area, orgDegree, orgName, orgType);
        } else {
            org = orgRService.createChildOrg(area, orgDegree, orgName, orgType, og.getGuidParents());
        }
        return getReturnMap(org);
    }

   /* @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "生成机构代码", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "orgCode", // 操作对象标识
            name = "orgCode", // 操作对象名
            keys = {"orgCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/initcode")
    public Map<String, Object> initcode(@RequestBody String content) {
        // Map<String, Object> result = new HashMap<String, Object>();
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String orgDegree = jsonObj.getString("orgDegree");
        String AREA = jsonObj.getString("AREA");
        String orgCode = orgRService.genOrgCode(AREA, orgDegree, null);
        Map map = new HashMap();
        map.put("orgCode", orgCode);
        return getReturnMap(map);
    }*/

    /**
     * 更新机构
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "update",  // 操作类型
            operateDesc = "更新机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "orgName", // 操作对象名
            keys = {"orgCode", "orgName"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/updateOrg")
    public Map<String, Object> updateOrg(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        OmOrg og = JSONObject.parseObject(content, OmOrg.class);
        OmOrg reOrg = orgRService.updateOrg(og);
        return getReturnMap(reOrg);
    }

    /**
     * 删除机构
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "删除机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "orgName", // 操作对象名
            keys = {"orgCode", "orgName"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deleteOrg")
    public Map<String, Object> deleteOrg(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String orgCode = jsonObj.getString("orgCode");
        OmOrg og = orgRService.deleteEmptyOrg(orgCode);
        return getReturnMap(og);
    }

    /**
     * 新增岗位信息
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,  // 操作类型
            operateDesc = "新增岗位", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "positionName", // 操作对象名
            keys = {"positionCode", "guidOrg"}) // 操作对象的关键值的键值名
    @RequestMapping(value = "/createPosition", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createPosition(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        OmPosition op = JSONObject.parseObject(jsonObj.getString("data"), OmPosition.class);
        return getReturnMap(positionRService.createPosition(op));
    }
    /**
     * 修改岗位信息
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,  // 操作类型
            operateDesc = "修改岗位", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "positionName", // 操作对象名
            keys = {"positionCode", "guidOrg"}) // 操作对象的关键值的键值名
    @RequestMapping(value = "/updatePosition", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updatePosition(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        OmPosition op = JSONObject.parseObject(jsonObj.getString("data"), OmPosition.class);
        OmPosition omPosition = positionRService.updatePosition(op);
        return getReturnMap(omPosition);
    }


    /**
     * 拉取员工-机构关系表数据
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadempbyorg")
    public String loadempbyorg(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String orgGuid = jsonObj.getString("guid");
            List<OmEmployee> list = employeeRService.queryEmployeeByGuid(orgGuid);
            List l = new ArrayList<>();
            for (OmEmployee oe : list) {
                Map map = BeanUtils.describe(oe);
                l.add(map);
            }
            AjaxUtils.ajaxJsonSuccessMessage(response, l);
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 拉取不在当前机构下的员工列表,用于为该机构新添人员.
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadempNotinorg")
    public String loadempNotinorg(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                                  HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String orgGuid = jsonObj.getString("guid");
            List<OmEmployee> list = employeeRService.queryEmployeeNotinGuid(orgGuid);
            List l = new ArrayList<>();
            for (OmEmployee oe : list) {
                Map map = BeanUtils.describe(oe);
                l.add(map);
            }
            AjaxUtils.ajaxJsonSuccessMessage(response, l);
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 添加机构-人员关系表数据
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "update",  // 操作类型
            operateDesc = "添加机构人员数据", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "guidEmp", // 操作对象名
            keys = {"guidEmp", "guidOrg"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addEmpOrg")
    public Map<String, Object> addEmpOrg(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String orgGuid = jsonObj.getString("orgGuid");
        JSONArray array = jsonObj.getJSONArray("empGuidlist");
        OmEmpOrg oeo = new OmEmpOrg();
        for (int i = 0; i < array.size(); i++) {
            String empGuid = array.get(i).toString();
            oeo = employeeRService.insertEmpOrg(orgGuid, empGuid);
        }

        return getReturnMap(oeo);
    }

    /**
     * 删除机构-人员关系表数据
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "删除机构人员数据", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidEmp", // 操作对象标识
            name = "guidEmp", // 操作对象名
            keys = {"guidEmp", "guidOrg"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deleteEmpOrg")
    public Map<String, Object> deleteEmpOrg(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String orgGuid = jsonObj.getString("orgGuid");
        JSONArray array = jsonObj.getJSONArray("empGuidlist");
        for (int i = 0; i < array.size(); i++) {
            String empGuid = array.get(i).toString();
            employeeRService.deleteEmpOrg(orgGuid, empGuid);
        }
        Map map = new HashMap();
        map.put("guidEmp", array.get(0).toString());
        map.put("guidOrg", orgGuid);
        return getReturnMap(map);
    }

    /**
     * 加载下级机构列表数据
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadxjjg")
    public String loadxjjg(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                           HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String orgCode = jsonObj.getString("orgCode");
            List<OmOrg> list = orgRService.queryChilds(orgCode);
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
     * 加载岗位下员工数据
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadEmpbyPosition")
    public String loadEmpbyPosition(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                                    HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String positionCode = jsonObj.getString("positionCode");
            List<OmEmployee> list = positionRService.queryEmployee(positionCode);
            list = positionRService.queryEmployee(positionCode);
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
     * 加载不在岗位下员工数据
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadempNotinposit")
    public String loadempNotinposit(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                                    HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String positionCode = jsonObj.getString("positionCode");
            List<OmEmployee> list = positionRService.queryEmployeeNotin(positionCode);
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
     * 添加岗位-人员关系表数据
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "岗位添加人员", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidPos", // 操作对象标识
            name = "guidPos", // 操作对象名
            keys = {"guidEmp", "guidPos"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addEmpPosition")
    public Map<String,Object> addEmpPosition(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String posGuid = jsonObj.getString("posGuid");
        JSONArray array = jsonObj.getJSONArray("empGuidlist");
        for (int i = 0; i < array.size(); i++) {
            String empGuid = array.get(i).toString();
            employeeRService.insertEmpPosition(posGuid, empGuid);
        }
        Map map = new HashMap();
        map.put("guidPos", posGuid);
        map.put("guidEmp", array.get(0).toString());
        return getReturnMap(map);
    }

    /**
     * 删除岗位-人员关系表数据
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "删除岗位人员信息", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidPos", // 操作对象标识
            name = "guidPos", // 操作对象名
            keys = {"guidEmp", "guidPos"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deleteEmpPosition")
    public Map<String, Object> deleteEmpPosition(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String posGuid = jsonObj.getString("posGuid");
        JSONArray array = jsonObj.getJSONArray("empGuidlist");
        for (int i = 0; i < array.size(); i++) {
            String empGuid = array.get(i).toString();
            employeeRService.deleteEmpPosition(posGuid, empGuid);
        }
        Map map = new HashMap();
        map.put("guidPos", posGuid);
        map.put("guidEmp", array.get(0).toString());
        return getReturnMap(map);
    }

    /**
     * 加载下级岗位数据
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/loadxjposit")
    public String loadxjposit(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String posCode = jsonObj.getString("posCode");
            List<OmPosition> list = positionRService.queryChilds(posCode);
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
     * 启用-注销-停用机构
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "update",  // 操作类型
            operateDesc = "改变机构状态", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "orgName", // 操作对象名
            keys = {"orgStatus"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/enableorg")
    public Map<String,Object> enableorg(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String orgCode = jsonObj.getString("orgCode");
        Date startDate = jsonObj.getDate("startDate");
        Date endDate = jsonObj.getDate("endDate");
        String flag = jsonObj.getString("flag");
        if ("0".equals(flag)) {
            OmOrg org = orgRService.enabledOrg(orgCode, startDate, endDate);
            return getReturnMap(org);
        } else if ("3".equals(flag)) {//停用
            OmOrg org = orgRService.disabledOrg(orgCode);
            return getReturnMap(org);
        } else if ("1".equals(flag)) {//注销
            OmOrg org = orgRService.cancelOrg(orgCode);
            return getReturnMap(org);
        } else if ("2".equals(flag)) {//重新启用
            OmOrg org = orgRService.reenabledOrg(orgCode);
            return getReturnMap(org);
        }
        return null;
    }

    /**
     * 启用-注销-岗位
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "update",  // 操作类型
            operateDesc = "改变岗位状态", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "positionName", // 操作对象名
            keys = {"positionStatus"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/enableposition")
    public Map<String, Object> enableposition(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String posCode = jsonObj.getString("posCode");
        //0-注销,1-启用
        String flag = jsonObj.getString("flag");
        if ("1".equals(flag)) {
            OmPosition pos = positionRService.reenablePosition(posCode);
            return getReturnMap(pos);
        } else {
            OmPosition pos = positionRService.cancelPosition(posCode);
            return getReturnMap(pos);
        }
    }

    /**
     * 删除岗位
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "删除岗位", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "positionName", // 操作对象名
            keys = {"positionName","guid"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deletePosition")
    public Map<String, Object> deletePosition(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String posCode = jsonObj.getString("posCode");
        OmPosition pos = positionRService.deletePosition(posCode);
        return getReturnMap(pos);
    }

    /**
     * 查询机构已经拥有的角色
     *
     * @param content
     * @return
     */
    @RequestMapping(value = "/queryRole")
    public String queryRole(@RequestBody String content,HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String guid = jsonObj.getString("guid");
            List<AcRole> list = orgRService.queryRolebyOrgGuid(guid);
            AjaxUtils.ajaxJsonSuccessMessage(response, list);
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 新增权限
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "为组织添加权限", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidRole","guidParty"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addRoleParty")
    public Map<String, Object> addRoleParty(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String partyGuid = jsonObj.getString("partyGuid");
        String roleGuid = jsonObj.getString("roleGuid");
        String partyType = jsonObj.getString("partyType");
        AcPartyRole apr = new AcPartyRole();
        apr.setGuidParty(partyGuid);
        apr.setGuidRole(roleGuid);
        apr.setPartyType(partyType);
        ArrayList<AcPartyRole> acPartyRoles = new ArrayList<>();
        acPartyRoles.add(apr);
        roleRService.addRoleParty(acPartyRoles);
        return getReturnMap(apr);
    }

    /**
     * 删除权限
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "为组织删除权限", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidRole", // 操作对象标识
            keys = {"guidRole","guidParty"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deleteRoleParty")
    public Map<String, Object> deleteRoleParty(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String partyGuid = jsonObj.getString("partyGuid");
        String roleGuid = jsonObj.getString("roleGuid");
        String partyType = jsonObj.getString("partyType");
        AcPartyRole apr = new AcPartyRole();
        apr.setGuidParty(partyGuid);
        apr.setGuidRole(roleGuid);
        apr.setPartyType(partyType);
        ArrayList<AcPartyRole> acPartyRoles = new ArrayList<>();
        acPartyRoles.add(apr);
        roleRService.removeRoleParty(acPartyRoles);
        return getReturnMap(apr);
    }

    /**
     * 查询角色对应权限
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryRoleFun")
    public String queryRoleFun(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String roleGuid = jsonObj.getString("roleGuid");
            List<AcRoleFunc> list = roleRService.queryAllRoleFunByRoleGuid(roleGuid);
            List<String> guidList = new ArrayList<>();
            for (AcRoleFunc acf : list) {
                guidList.add(acf.getGuidFunc());
            }
            List<AcFunc> afList = orgRService.queryFunCByGuidList(guidList);
            AjaxUtils.ajaxJsonSuccessMessage(response, afList);
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }


    /**
     * 查询未授予角色
     *
     * @param model
     * @param content
     * @param age
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryRoleNot")
    public String queryRoleNot(ModelMap model, @RequestBody String content, String age, HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            // 收到请求
            JSONObject jsonObj = JSONObject.parseObject(content);
            String guid = jsonObj.getString("guid");
            List<AcRole> list = orgRService.queryRoleNotInOrg(guid);
            AjaxUtils.ajaxJsonSuccessMessage(response, list);
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 查询所有机构
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryAllorg")
    public String queryAllorg(ModelMap model, HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            WhereCondition wc = new WhereCondition();
            List<OmOrg> list = orgRService.queryAllOrg();
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
     * 查询岗位拥有的应用列表
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryAppinPos")
    public String queryAppinPos(ModelMap model, @RequestBody String content, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String posCode = jsonObj.getString("posCode");
            List<AcApp> list = positionRService.queryApp(posCode);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list, "yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }


    @RequestMapping(value = "/queryAppNotinPos")
    public String queryAppNotinPos(ModelMap model, @RequestBody String content, HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String posCode = jsonObj.getString("posCode");
            List<AcApp> list = positionRService.queryAppNotInPosition(posCode);
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
     * 新增岗位应用
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "新增岗位应用", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidPosition", // 操作对象标识
            name = "guidPosition", // 操作对象名
            keys = {"guidPosition","guidApp"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addAppPosition")
    public Map<String, Object> addAppPosition(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String posGuid = jsonObj.getString("posGuid");
        String appGuid = jsonObj.getString("appGuid");
        positionRService.addAppPosition(appGuid, posGuid);
        Map map = new HashMap();
        map.put("guidPosition", posGuid);
        map.put("guidApp", appGuid);
        return getReturnMap(map);
    }

    /**
     * 删除岗位应用
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "删除岗位应用", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidPosition", // 操作对象标识
            name = "guidPosition", // 操作对象名
            keys = {"guidPosition","guidApp"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deleteAppPosition")
    public Map<String, Object> deleteAppPosition(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String posGuid = jsonObj.getString("posGuid");
        String appGuid = jsonObj.getString("appGuid");
        positionRService.deleteAppPosition(appGuid, posGuid);
        Map map = new HashMap();
        map.put("guidPosition", posGuid);
        map.put("guidApp", appGuid);
        return getReturnMap(map);
    }


    @RequestMapping(value = "/queryAllposbyOrg")
    public String queryAllposbyOrg(ModelMap model, @RequestBody String content, HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String orgGuid = jsonObj.getString("orgGuid");
            List<OmPosition> list = positionRService.queryAllPositionByOrg(orgGuid);
            AjaxUtils.ajaxJsonSuccessMessageWithDateFormat(response, list, "yyyy-MM-dd");
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/initPosCode")
    public String initPosCode(ModelMap model, @RequestBody String content, HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String positionType = jsonObj.getString("positionType");
            String positionCode = positionRService.genPositionCode(positionType);
            AjaxUtils.ajaxJsonSuccessMessage(response, positionCode);
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 查询所有机构
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryAllposition")
    public String queryAllposition(ModelMap model, HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            List<OmPosition> list = positionRService.queryAllPosition();
            AjaxUtils.ajaxJsonSuccessMessage(response, list);
        } catch (ToolsRuntimeException e) {// TODO
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
    }

    /**
     * 拷贝机构
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "拷贝机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidPosition", // 操作对象标识
            name = "guidPosition", // 操作对象名
            keys = {"guidPosition","guidApp"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/copyOrg")
    public Map<String, Object> copyOrg(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String copyCode = jsonObj.getString("copyCode");
        OmOrg org = orgRService.copyOrg(copyCode);
        return getReturnMap(org);
    }

    /**
     * 拖动机构
     *
     * @param model
     * @param request
     * @param content
     * @param response
     * @return
     */
    @RequestMapping(value = "/moveOrg", method = RequestMethod.POST)
    public String moveOrg(ModelMap model, HttpServletRequest request, @RequestBody String content,
                          HttpServletResponse response) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(content);
            String mvOrgCode = jsonObj.getString("mvOrgCode");
            String toOrgCode = jsonObj.getString("toOrgCode");
            String fromOrgCode = jsonObj.getString("fromOrgCode");
            int position = jsonObj.getInteger("position");
            orgRService.moveOrg(mvOrgCode, fromOrgCode, toOrgCode, position);
            AjaxUtils.ajaxJsonSuccessMessage(response, "移动成功!");
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
        }
        return null;
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
