package org.tis.tools.webapp.controller.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.sys.SysRunConfig;
import org.tis.tools.rservice.sys.capable.IRunConfigRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import java.util.Map;

@Controller
@RequestMapping("/RunConfigController")
public class RunConfigController extends BaseController {

    @Autowired
    IRunConfigRService sysRunConfigRService;

    /**
     * 查询系统运行参数列表
     *
     */
    @ResponseBody
    @RequestMapping(value="/queryRunConfigList" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> queryRunConfigList()  {
        return getReturnMap(sysRunConfigRService.queryAllSysRunConfig());
    }

    /**
     * 新增系统参数
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增系统参数",
            retType = ReturnType.Object,
            id = "guid",
            name = "keyName",
            keys = {"guidApp", "groupName"}
    )
    @ResponseBody
    @RequestMapping(value="/createRunConfig" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> createRunConfig(@RequestBody String content) {
        SysRunConfig sysRunConfig = JSON.parseObject(content, SysRunConfig.class);
        return getReturnMap(sysRunConfigRService.createSysRunConfig(sysRunConfig));
    }

    /**
     * 修改系统参数
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改系统参数",
            retType = ReturnType.Object,
            id = "guid",
            name = "keyName",
            keys = {"guidApp", "groupName"}
    )
    @ResponseBody
    @RequestMapping(value="/editRunConfig" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> editRunConfig(@RequestBody String content) {
        SysRunConfig sysRunConfig = JSON.parseObject(content, SysRunConfig.class);
        sysRunConfigRService.editSysRunConfig(sysRunConfig);
        return getReturnMap(sysRunConfig);
    }

    /**
     * 删除系统参数
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除系统参数",
            retType = ReturnType.Object,
            id = "guid",
            name = "keyName",
            keys = {"guidApp", "groupName"}
    )
    @ResponseBody
    @RequestMapping(value="/deleteRunConfig" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> deleteRunConfig(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String guid = jsonObject.getString("cfgGuid");
        return getReturnMap(sysRunConfigRService.deleteSysRunConfig(guid));
    }
    
}
