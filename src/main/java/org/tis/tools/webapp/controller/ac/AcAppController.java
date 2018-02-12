package org.tis.tools.webapp.controller.ac;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.base.WhereCondition;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.ac.*;
import org.tis.tools.rservice.ac.capable.IApplicationRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/AcAppController")
public class AcAppController extends BaseController {

    @Autowired
    IApplicationRService applicationRService;

    /**
     * appAdd新增应用服务
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增应用",
            retType = ReturnType.Object,
            id = "guid",
            name = "appName",
            keys = "appCode"
    )
    @ResponseBody
    @RequestMapping(value = "/appAdd", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createAcApp(@RequestBody String content) {
        return getReturnMap(applicationRService.createAcApp(JSONObject.parseObject(content, AcApp.class)));
    }

    /**
     * appDel删除方法
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除应用",
            retType = ReturnType.Object,
            id = "guid",
            name = "appName",
            keys = "appCode"
    )
    @ResponseBody
    @RequestMapping(value = "/appDel", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> appDel(@RequestBody String content) {
        return getReturnMap(applicationRService.deleteAcApp(JSONObject.parseObject(content).getString("id")));
    }

    /**
     * appEdit修改方法
     *
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改应用",
            retType = ReturnType.Object,
            id = "guid",
            name = "appName",
            keys = "appCode"
    )
    @ResponseBody
    @RequestMapping(value = "/appEdit", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> appEdit(@RequestBody String content) {
        return getReturnMap(applicationRService.updateAcApp(JSONObject.parseObject(content, AcApp.class)));
    }

    /**
     * appQuery查询应用
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
        WhereCondition wc;
        if ("#".equals(id)) {
            Map map = new HashMap();
            map.put("rootName", "应用功能管理");
            map.put("rootCode", "AC0000");
            result.put("data", map);//返回给前台的数据
        } else if ("AC0000".equals(id)) {
            //调用远程服务,#:根
            List<AcApp> ac = applicationRService.queryAcRootList();
            result.put("data", ac);//返回给前台的数据
        } else if (id.length() > 3 && "APP".equals(id.substring(0, 3))) {
            List<AcFuncgroup> group = applicationRService.queryAcRootFuncgroup(id);
            result.put("data", group);//返回给前台的数据
        } else if (id.length() > 9 && "FUNCGROUP".equals(id.substring(0, 9))) {
            Map map = new HashMap();
            List<AcFuncgroup> groupList = applicationRService.queryAcChildFuncgroup(id);
            if (groupList.size() > 0) {
                map.put("groupList", groupList);
            }
            List<AcFunc> funcList = applicationRService.queryAcFunc(id);
            if (funcList.size() > 0) {
                map.put("funcList", funcList);
            }
            result.put("data", map);//返回给前台的数据
        }
        return getReturnMap(result.get("data"));
    }

    /**
     * groupAdd新增功能组
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增功能组",
            retType = ReturnType.Object,
            id = "guid",
            name = "funcgroupName"
    )
    @ResponseBody
    @RequestMapping(value = "/groupAdd", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> groupAdd(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String funcgroupName = jsonObj.getString("funcgroupName");
//        String groupLevel = jsonObj.getString("groupLevel");
        String guidApp = jsonObj.getString("guidApp");
        String guidParents = jsonObj.getString("guidParents");
        //转换成BigDecimal类型
        /*BigDecimal groupLevelBd = new BigDecimal(groupLevel);
        groupLevelBd = groupLevelBd.setScale(2, BigDecimal.ROUND_HALF_UP); //小数位2位，四舍五入
*/        AcFuncgroup acFuncgroup = new AcFuncgroup();//new 一个新对象
        acFuncgroup.setFuncgroupName(funcgroupName);
//        acFuncgroup.setGroupLevel(groupLevelBd);
        acFuncgroup.setGuidApp(guidApp);
        acFuncgroup.setGuidParents(guidParents);
        return getReturnMap(applicationRService.createAcFuncGroup(acFuncgroup));//把new的并且填入参数的对象，传入，返回
    }

    /**
     * groupDel删除功能组
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除功能组",
            retType = ReturnType.Object,
            id = "guid",
            name = "funcgroupName"
    )
    @ResponseBody
    @RequestMapping(value = "/groupDel", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> groupDel(@RequestBody String content) {
        return getReturnMap(applicationRService.deleteAcFuncGroup(JSONObject.parseObject(content).getString("id")));
    }

    /**
     * groupEdit修改功能组
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改功能组",
            retType = ReturnType.Object,
            id = "guid",
            name = "funcgroupName"
    )
    @ResponseBody
    @RequestMapping(value = "/groupEdit", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> groupEdit(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String guid = jsonObj.getString("id");
        AcFuncgroup acFuncgroup = applicationRService.queryFuncgroup(guid);
        String funcgroupName = jsonObj.getString("funcgroupName");
        String groupLevel = jsonObj.getString("groupLevel");
        String guidParents = jsonObj.getString("guidParents");
        acFuncgroup.setFuncgroupName(funcgroupName);
        acFuncgroup.setGroupLevel(new BigDecimal(groupLevel));
        acFuncgroup.setGuidParents(guidParents);
        return getReturnMap(applicationRService.updateAcFuncgroup(acFuncgroup));
    }

    /**
     * acFuncAdd新增功能
     *
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增功能",
            retType = ReturnType.Object,
            id = "guid",
            name = "funcName",
            keys = {"funcCode", "funcType"}
    )
    @ResponseBody
    @RequestMapping(value = "/acFuncAdd", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> acFuncAdd(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        JSONObject data = jsonObj.getJSONObject("data");
        AcFunc acFunc = JSONObject.parseObject(data.toJSONString(), AcFunc.class);
        AcFunc func = applicationRService.createAcFunc(acFunc);//把new的并且填入参数的对象，传入，返回
        return getReturnMap(func);
    }

    /**
     * acFuncDel删除功能
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除功能",
            retType = ReturnType.Object,
            id = "guid",
            name = "funcName",
            keys = {"funcCode", "funcType"}
    )
    @ResponseBody
    @RequestMapping(value = "/acFuncDel", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> acFuncDel(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        JSONObject data = jsonObj.getJSONObject("data");
        String guid = data.getString("id");
        AcFunc func = applicationRService.deleteAcFunc(guid);//把new的并且填入参数的对象，传入，返回
        return getReturnMap(func);
    }

    /**
     * acFuncEdit更新功能
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改功能",
            retType = ReturnType.Object,
            id = "guid",
            name = "funcName",
            keys = {"funcCode", "funcType"}
    )
    @ResponseBody
    @RequestMapping(value = "/acFuncEdit", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> acFuncEdit(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        JSONObject data = jsonObj.getJSONObject("data");
        AcFunc acFunc = JSONObject.parseObject(data.toJSONString(), AcFunc.class);
        AcFunc func = applicationRService.updateAcFunc(acFunc); // 把new的并且填入参数的对象，传入，返回
        return getReturnMap(func);
    }

    /**
     * acFuncResource更新功能对应资源
     *
     * @param content
     * @param request
     * @param response
     * @return
     */
    /*@ResponseBody
    @RequestMapping(value = "/acFuncResourceEdit", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> acFuncResourceEdit(@RequestBody String content, HttpServletRequest request,
                                     HttpServletResponse response) {

        try {
            if (logger.isInfoEnabled()) {
                logger.info("acFuncEdit request : " + content);
            }
            JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数

            //设置功能对应资源
            AcFuncResource acFuncResource = new AcFuncResource();

            String guid = jsonObj.getString("id");
            String resType = jsonObj.getString("resType");
            String compackName = jsonObj.getString("compackName");
            String resshowName = jsonObj.getString("resShowName");
            String resPath = jsonObj.getString("resPath");
            acFuncResource.setGuidFunc(guid);
            acFuncResource.setResType(resType);
            acFuncResource.setCompackName(compackName);
            acFuncResource.setResShowName(resshowName);
            acFuncResource.setResPath(resPath);

            applicationRService.updateAcFuncResource(acFuncResource);
            AjaxUtils.ajaxJsonSuccessMessage(response, "");//返回给前台的结
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response, e.getCode(), e.getMessage());
            logger.error("acFuncEdit exception : ", e);
        } catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response, "SYS_0001", e.getMessage());
            logger.error("acFuncEdit exception : ", e);
        }
        return null;
    }*/

    /**
     * acFuncResouceQuery功能对应资源查询
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/acFuncResouceQuery", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> acFuncResouceQuery(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String guid = jsonObj.getString("id");
        return getReturnMap(applicationRService.queryFuncResource(guid));
    }


    /**
     * queryAllFunc查询所有应用
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAllFunc(@RequestBody String content) {
        List<AcFunc> acfunc = applicationRService.queryAllFunc();
        return getReturnMap(acfunc);
    }

    /**
     * importFunc导入应用
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> importFunc(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String guid = jsonObj.getString("id");
        JSONArray jsonArray = jsonObj.getJSONArray("list");
        List list = JSONObject.parseArray(jsonArray.toJSONString(), String.class);
        applicationRService.importFunc(guid, list);
        return getReturnMap(null);
    }

    /**
     * functypeAdd新增行为类型
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增行为类型",
            retType = ReturnType.Object,
            id = "guid",
            name = "bhvtypeName",
            keys = "bhvtypeCode"
    )
    @ResponseBody
    @RequestMapping(value = "/functypeAdd", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> functypeAdd(@RequestBody String content) {
        AcBhvtypeDef acBhvtypeDef = JSONObject.parseObject(content, AcBhvtypeDef.class);   // 传入的参数
        return getReturnMap(applicationRService.functypeAdd(acBhvtypeDef));
    }

    /**
     * functypeDel删除行为类型
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除行为类型",
            retType = ReturnType.Object,
            id = "guid",
            name = "bhvtypeName",
            keys = "bhvtypeCode"
    )
    @ResponseBody
    @RequestMapping(value = "/functypeDel", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> functypeDel(@RequestBody String content) {

        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String guid = jsonObj.getString("id");
        AcBhvtypeDef acBhvtypeDef = applicationRService.functypeDel(guid);
        return getReturnMap(acBhvtypeDef);

    }

    /**
     * functypeEdit 修改行为类型
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改行为类型",
            retType = ReturnType.Object,
            id = "guid",
            name = "bhvtypeName",
            keys = "bhvtypeCode"
    )
    @ResponseBody
    @RequestMapping(value = "/functypeEdit", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> functypeEdit(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String guid = jsonObj.getString("id");
        String bhvtypeCode = jsonObj.getString("bhvtypeCode");
        String bhvtypeName = jsonObj.getString("bhvtypeName");
        AcBhvtypeDef acBhvtypeDef = new AcBhvtypeDef();
        acBhvtypeDef.setGuid(guid);
        acBhvtypeDef.setBhvtypeCode(bhvtypeCode);
        acBhvtypeDef.setBhvtypeName(bhvtypeName);
        return getReturnMap(applicationRService.functypeEdit(acBhvtypeDef));
    }

    /**
     * functypequery 查询行为类型
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/functypequery", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> functypequery(@RequestBody String content) {
        List<AcBhvtypeDef> list = applicationRService.functypequery();
        return getReturnMap(list);
    }

    /**
     * funactAdd 新增功能操作行为
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增功能行为",
            retType = ReturnType.Object,
            id = "guid",
            name = "bhvName",
            keys = {"guidBehtype", "bhvCode"}
    )
    @ResponseBody
    @RequestMapping(value = "/funactAdd", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> funactAdd(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String guidBehtype = jsonObj.getString("guid");
        String bhvCode = jsonObj.getString("bhvCode");
        String bhvName = jsonObj.getString("bhvName");
        AcBhvDef acBhvDef = new AcBhvDef();
        acBhvDef.setBhvCode(bhvCode);
        acBhvDef.setBhvName(bhvName);
        acBhvDef.setGuidBehtype(guidBehtype);
        return getReturnMap(applicationRService.funactAdd(acBhvDef));
    }

    /**
     * funactDel 删除功能操作行为
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除功能行为",
            retType = ReturnType.List,
            id = "guid",
            name = "bhvName",
            keys = {"guidBehtype", "bhvCode"}
    )
    @ResponseBody
    @RequestMapping(value = "/funactDel", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> funactDel(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String idstr = jsonObj.getJSONArray("ids").toJSONString();
        List<String> ids = JSONObject.parseArray(idstr, String.class);
        return getReturnMap(applicationRService.funactDel(ids));
    }

    /**
     * funactEdit 修改功能操作行为
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改功能行为",
            retType = ReturnType.Object,
            id = "guid",
            name = "bhvName",
            keys = {"guidBehtype", "bhvCode"}
    )
    @ResponseBody
    @RequestMapping(value = "/funactEdit", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> funactEdit(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String guid = jsonObj.getString("id");
        String bhvCode = jsonObj.getString("bhvCode");
        String bhvName = jsonObj.getString("bhvName");
        String guidBehtype = jsonObj.getString("guidBehtype");
        AcBhvDef acBhvDef = new AcBhvDef();
        acBhvDef.setBhvCode(bhvCode);
        acBhvDef.setBhvName(bhvName);
        acBhvDef.setGuidBehtype(guidBehtype);
        acBhvDef.setGuid(guid);
        return getReturnMap(applicationRService.funactEdit(acBhvDef));
    }


    /**
     * queryBhvtypeDefByFunc 根据功能的GUID查询行为类型定义
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryBhvtypeDefByFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryBhvtypeDefByFunc(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String funcGuid = jsonObj.getString("id");
        return getReturnMap(applicationRService.queryBhvtypeDefByFunc(funcGuid));
    }

    /**
     * queryBhvDefByBhvType 根据行为类型的GUID查询所有的操作行为
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryBhvDefByBhvType", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryBhvDefByBhvType(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String bhvtypeGuid = jsonObj.getString("id");
        return getReturnMap(applicationRService.queryBhvDefByBhvType(bhvtypeGuid));
    }

    /**
     * addBhvDefForFunc 功能添加行为定义
     *
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "功能添加操作行为",
            retType = ReturnType.List,
            id = "guidFunc",
            keys = {"guidBhv", "guid"}
    )
    @ResponseBody
    @RequestMapping(value = "/addBhvDefForFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> addBhcDefForFunc(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String funcGuid = jsonObj.getString("id");
        List bhvtypeGuids = JSONObject.parseArray(jsonObj.getJSONArray("typeGuidList").toJSONString(), String.class);
        return getReturnMap(applicationRService.addBhvDefForFunc(funcGuid, bhvtypeGuids));
    }

    /**
     * 设置功能行为是否有效
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "设置功能行为是否有效",
            retType = ReturnType.Object,
            id = "guidFunc",
            keys = {"isEffective", "guidBhv"}
    )
    @ResponseBody
    @RequestMapping(value = "/setFuncBhvStatus", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> setFuncBhvStatus(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String funcBhvGuid = data.getString("funcBhvGuid");
        String isEffective = data.getString("isEffective");
        return getReturnMap(applicationRService.setFuncBhvStatus(funcBhvGuid, isEffective));
    }

    /**
     * 修改功能行为类别
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改功能行为类型",
            retType = ReturnType.Object,
            id = "guid",
            name = "funcName",
            keys = "guidBhvtypeDef"
    )
    @ResponseBody
    @RequestMapping(value = "/updateFuncBhvType", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> updateFuncBhvType(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String funcGuid = data.getString("funcGuid");
        String bhvtypeGuid = data.getString("bhvtypeGuid");
        return getReturnMap(applicationRService.updateFuncBhvType(funcGuid, bhvtypeGuid));
    }


    /**
     * 查询功能下全部的操作行为
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllBhvDefForFunc", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAllBhcDefForFunc(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String funcGuid = jsonObj.getString("funcGuid"); // 功能GUID
        return getReturnMap(applicationRService.queryAllBhvDefForFunc(funcGuid));
    }
    /**
     * 删除功能对应的行为定义
     *
     * @param content
     * @param request
     * @param response
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "功能删除操作行为",
            retType = ReturnType.List,
            id = "guidFunc",
            keys = {"guidBhv", "guid"}
    )
    @ResponseBody
    @RequestMapping(value = "/delFuncBhvDef", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> delFuncBhvDef(@RequestBody String content, HttpServletRequest request,
                                HttpServletResponse response) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String funcGuid = jsonObj.getString("funcGuid"); // 功能GUID
        String idstr = jsonObj.getJSONArray("bhvDefGuids").toJSONString();
        List<String> bhvDefGuids = JSONObject.parseArray(idstr, String.class); // 行为定义GUID
        return getReturnMap(applicationRService.delFuncBhvDef(funcGuid, bhvDefGuids));
    }

    /**
     * 开启应用
     *
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "开通应用",
            retType = ReturnType.Object,
            id = "guid",
            name = "appName",
            keys = {"appCode", "isOpen", "openData"}
    )
    @ResponseBody
    @RequestMapping(value = "/enableApp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> enableApp(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String appGuid = jsonObj.getString("appGuid"); // 应用GUID
        Date openDate = jsonObj.getDate("openStr");//开通时间
        return getReturnMap(applicationRService.enableApp(appGuid, openDate));
    }

    /**
     * 关闭应用
     *
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "关闭应用",
            retType = ReturnType.Object,
            id = "guid",
            name = "appName",
            keys = {"appCode", "isOPen"}
    )
    @ResponseBody
    @RequestMapping(value = "/disableApp", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> disableApp(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);    //传入的参数
        String appGuid = jsonObj.getString("appGuid"); // 应用GUID
        return getReturnMap(applicationRService.disableApp(appGuid));
    }

    /**
     * 新增功能资源
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增功能资源",
            retType = ReturnType.Object,
            id = "guidFunc",
            keys = "attrKey")
    @ResponseBody
    @RequestMapping(value = "/createAcFuncResource", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createAcFuncResource(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        JSONObject data = jsonObj.getJSONObject("data");
        AcFuncResource acFuncResource = JSONObject.parseObject(data.toJSONString(), AcFuncResource.class);
        AcFuncResource funcResource = applicationRService.createAcFuncResource(acFuncResource);
        return getReturnMap(funcResource);
    }

    /**
     * 删除功能资源
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除功能资源",
            retType = ReturnType.List,
            id = "guidFunc",
            keys = "attrKey")
    @ResponseBody
    @RequestMapping(value = "/deleteAcFuncResource", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteAcFuncResource(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        JSONArray data = jsonObj.getJSONArray("data");
        List<AcFuncResource> acFuncResources = JSONObject.parseArray(data.toJSONString(), AcFuncResource.class);
        List<AcFuncResource> funcResources = applicationRService.deleteAcFuncResource(acFuncResources);
        return getReturnMap(funcResources);
    }

    /**
     * 修改功能资源
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改功能资源",
            retType = ReturnType.Object,
            id = "guidFunc",
            keys = "attrKey")
    @ResponseBody
    @RequestMapping(value = "/updateAcFuncResource", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> updateAcFuncResource(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        JSONObject data = jsonObj.getJSONObject("data");
        AcFuncResource acFuncResource = JSONObject.parseObject(data.toJSONString(), AcFuncResource.class);
        AcFuncResource funcResource = applicationRService.updateAcFuncResource(acFuncResource);
        return getReturnMap(funcResource);
    }

    /**
     * 查询功能资源
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryAcFuncResource", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAcFuncResource(@RequestBody String content) {
        JSONObject jsonObj = JSONObject.parseObject(content);
        JSONObject data = jsonObj.getJSONObject("data");
        String funcGuid = data.getString("funcGuid");
        List<AcFuncResource> funcResources = applicationRService.queryAcFuncResource(funcGuid);
        return getReturnMap(funcResources);
    }

}