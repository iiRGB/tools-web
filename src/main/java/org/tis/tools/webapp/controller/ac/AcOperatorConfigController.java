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
import org.tis.tools.model.po.ac.AcConfig;
import org.tis.tools.rservice.ac.capable.IOperatorRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/AcOperatorConfig")
public class AcOperatorConfigController extends BaseController {

    @Autowired
    IOperatorRService operatorRService;

    /**
     * 查询个性化配置列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/queryConfigList", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> queryConfigList() {
        List<AcConfig> configs = operatorRService.queryConfigList();
        return getReturnMap(configs);
    }

    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除个性化配置",
            retType = ReturnType.List,
            id = "guid",
            name = "configName",
            keys = {"guidApp", "configName"}
    )
    @ResponseBody
    @RequestMapping(value="/deleteConfig", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> deleteConfig(@RequestBody String content) {
        List<AcConfig> configs = JSON.parseArray(content, AcConfig.class);
        return getReturnMap(operatorRService.deleteConfig(configs));
    }

    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增个性化配置",
            retType = ReturnType.Object,
            id = "guid",
            name = "configName",
            keys = {"guidApp", "configName"}
    )
    @ResponseBody
    @RequestMapping(value="/addConfig", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> addConfig(@RequestBody String content) {
        return getReturnMap(operatorRService.addConfig(JSONObject.parseObject(content, AcConfig.class)));
    }

    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改操作员个性化配置",
            retType = ReturnType.Object,
            id = "guid",
            name = "configName",
            keys = {"guidApp", "configName"}
    )
    @ResponseBody
    @RequestMapping(value="/updateConfig", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> updateConfig(@RequestBody String content) {
        return getReturnMap(operatorRService.updateConfig(JSONObject.parseObject(content, AcConfig.class)));
    }



}
