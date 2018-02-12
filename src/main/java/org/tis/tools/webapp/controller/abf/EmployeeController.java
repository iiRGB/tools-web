package org.tis.tools.webapp.controller.abf;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.om.OmEmployee;
import org.tis.tools.model.po.om.OmGroup;
import org.tis.tools.model.po.om.OmOrg;
import org.tis.tools.model.po.om.OmPosition;
import org.tis.tools.rservice.om.capable.IEmployeeRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 员工管理功能
 *
 * @author
 */
@Controller
@RequestMapping(value = "/om/emp")
public class EmployeeController extends BaseController {
    @Autowired
    IEmployeeRService employeeRService;


    /**
     * 查询所有人员信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryemployee", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryemployee() {
        List<OmEmployee> list = employeeRService.queryAllEmployyee();
        return getReturnMap(list);
    }

    /**
     * 新增人员信息
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "新增员工", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "employeeName", // 操作对象名
            keys = {"empCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addemployee", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addemployee( @RequestBody String content) {
        OmEmployee oe = JSONObject.parseObject(content, OmEmployee.class);
        return getReturnMap(employeeRService.createEmployee(oe));
    }
    /**
     * 修改人员信息
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "update",  // 操作类型
            operateDesc = "修改员工", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "employeeName", // 操作对象名
            keys = {"empCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/updateemployee", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> updateemployee( @RequestBody String content) {
        OmEmployee oe = JSONObject.parseObject(content, OmEmployee.class);
        return getReturnMap(employeeRService.updateEmployee(oe));
    }

    /**
     * 删除人员信息
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "删除员工信息", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "employeeName", // 操作对象名
            keys = {"guidPosition", "empCode", "guidOrg"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deletemp",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deletemp(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        OmEmployee emp = employeeRService.deleteEmployee(empCode);
        return getReturnMap(emp);
    }

    /**
     * 拉取人员-机构表
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadEmpOrg",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> loadEmpOrg( @RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        List<OmOrg> list = employeeRService.queryOrgbyEmpCode(empCode);
        return getReturnMap(list);
    }

    /**
     * 拉取人员-岗位表
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadEmpPos",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> loadEmpPos(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        List<OmPosition> list = employeeRService.queryPosbyEmpCode(empCode);
        return getReturnMap(list);
    }

    /**
     * 拉取人员-岗位及职务表
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadEmpPosDuty",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> loadEmpPosDuty(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        List<Map> list = employeeRService.queryPosDutybyEmpCode(empCode);
        return getReturnMap(list);
    }

    /**
     * 拉取人员-工作组表
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadEmpGroup",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> loadEmpGroup(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getJSONObject("data").getString("empCode");
        List<OmGroup> list = employeeRService.queryGroupByEmpCode(empCode);
        return getReturnMap(list);
    }

    /**
     * 拉取可指派机构列表
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadOrgNotInbyEmp",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> loadOrgNotInbyEmp(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        List<OmOrg> list = employeeRService.queryCanAddOrgbyEmpCode(empCode);
        return getReturnMap(list);
    }

    /**
     * 拉取可指派岗位列表
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadPosNotInbyEmp",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> loadPosNotInbyEmp(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        JSONObject data = jsonObj.getJSONObject("data");
        String empCode = data.getString("empCode");
        String orgGuid = data.getString("orgGuid");
        List<OmPosition> list = employeeRService.queryCanAddPosbyEmpCode(empCode, orgGuid);
        return getReturnMap(list);
    }

    /**
     * 拉取可指派工作组列表
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadGroupNotInbyEmp",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> loadGroupNotInbyEmp(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        JSONObject data = jsonObj.getJSONObject("data");
        String empCode = data.getString("empCode");
        String orgGuid = data.getString("orgGuid");
        List<OmGroup> list = employeeRService.queryCanAddGroupbyEmpCode(empCode, orgGuid);
        return getReturnMap(list);
    }

    /**
     * 指派机构
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,  // 操作类型
            operateDesc = "指派员工机构", // 操作描述
            retType = ReturnType.Object, // 返回类型
            id = "guidEmp", // 操作对象标识
            keys = "guidOrg") // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/assignOrg",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> assignOrg( @RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        String orgCode = jsonObj.getString("orgCode");
        String isMain = jsonObj.getString("isMain");
        if ("true".equals(isMain)) {
            employeeRService.assignOrg(empCode, orgCode, true);
        } else {
            employeeRService.assignOrg(empCode, orgCode, false);
        }
        Map map = new HashMap();
        map.put("empCode", empCode);
        map.put("orgCode", orgCode);
        map.put("isMain", isMain);
        return getReturnMap(map);
    }

    /**
     * 取消指派机构
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,  // 操作类型
            operateDesc = "取消指派员工机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidEmp", // 操作对象标识
            keys = "guidOrg") // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/disassignOrg",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> disassignOrg( @RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empGuid = jsonObj.getString("empGuid");
        String orgGuid = jsonObj.getString("orgGuid");
        employeeRService.deleteEmpOrg(orgGuid, empGuid);
        return getReturnMap(jsonObj);
    }

    /**
     * 指派
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD, // 操作类型
            operateDesc = "指派员工机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidEmp", // 操作对象标识
            keys = "guidOrg") // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/assignPos",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> assignPos( @RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        String posCode = jsonObj.getString("posCode");
        String isMain = jsonObj.getString("isMain");
        if ("true".equals(isMain)) {
            return getReturnMap(employeeRService.assignPosition(empCode, posCode, true));
        } else {
            return getReturnMap(employeeRService.assignPosition(empCode, posCode, false));
        }
    }

    /**
     * 取消指派员工岗位
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,  // 操作类型
            operateDesc = "取消指派员工岗位", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidEmp", // 操作对象标识
            keys = "guidOrg") // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/disassignPos",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> disassignPos(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empGuid = jsonObj.getString("empGuid");
        String posGuid = jsonObj.getString("posGuid");
        employeeRService.deleteEmpPosition(posGuid, empGuid);
        return getReturnMap(jsonObj);
    }


    /**
     * 指定新的主机构
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,  // 操作类型
            operateDesc = "指定员工主机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidEmp", // 操作对象标识
            keys = "guidOrg") // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/fixmainOrg",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> fixmainOrg(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        String orgCode = jsonObj.getString("orgCode");
        return getReturnMap(employeeRService.fixMainOrg(empCode, orgCode));
    }

    /**
     * 指定新的主岗位
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,  // 操作类型
            operateDesc = "指定员工主岗位", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guidEmp", // 操作对象标识
            keys = "guidEmp") // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/fixmainPos",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> fixmainPos(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        String empCode = jsonObj.getString("empCode");
        String posCode = jsonObj.getString("posCode");
        return getReturnMap(employeeRService.fixMainPosition(empCode, posCode));
    }

    /**
     * 改变员工状态
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改员工状态",
            retType = ReturnType.Object,
            id = "guid",
            name = "empName",
            keys = "empstatus"
    )
    @ResponseBody
    @RequestMapping(value="/changeEmpStatus" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> changeEmpStatus(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        JSONObject data= jsonObject.getJSONObject("data");
        String empGuid = data.getString("empGuid");
        String status = data.getString("status");
        Date date = data.getDate("date");
        return getReturnMap(employeeRService.changeEmpStatus(empGuid, status, date));
    }
}
