package org.tis.tools.webapp.controller.abf;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.om.OmBusiorg;
import org.tis.tools.model.po.om.OmOrg;
import org.tis.tools.model.po.sys.SysDictItem;
import org.tis.tools.rservice.om.capable.IBusiOrgRService;
import org.tis.tools.rservice.om.capable.IOrgRService;
import org.tis.tools.rservice.sys.capable.IDictRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务机构功能
 *
 * @author
 */
@Controller
@RequestMapping(value = "/om/busiorg")
public class BusiorgController  extends BaseController {
    @Autowired
    IDictRService dictRService;
    @Autowired
    IBusiOrgRService busiOrgRService;
    @Autowired
    IOrgRService orgRService;

    /**
     * 展示业务机构树
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/busitree", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> busitree(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String id = jsonObj.getString("id");
        if("#".equals(id)) {
            Map map = new HashMap<>();
            map.put("text", "业务机构树");
            map.put("id", "00000");
            List<Map> list = new ArrayList<>();
            list.add(map);
            return getReturnMap(list);
        }else if("00000".equals(id)){
            List<SysDictItem> list = dictRService.queryDictItemListByDictKey("DICT_OM_BUSIDOMAIN");
            return getReturnMap(list);
        }else if(id.length() == 3){
            List<OmBusiorg> list = busiOrgRService.queryRootBusiorgByDomain(id);
            return getReturnMap(list);
        }else {
            List<OmBusiorg> list = busiOrgRService.queryChildBusiorgByCode(id);
            return getReturnMap(list);
        }
    }

    /**
     * 展示业务机构筛选树
     *
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/searchtree", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> searchtree(@RequestBody String content) {
        // 收到请求
        JSONObject jsonObj = JSONObject.parseObject(content);
        String id = jsonObj.getString("id");
        String busiorgName = jsonObj.getString("searchitem");
        List<OmBusiorg> list = busiOrgRService.queryBusiorgByName(busiorgName);
        return getReturnMap(list);
    }

    @ResponseBody
    @RequestMapping(value = "/busidomain", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> busidomain() {
        List<SysDictItem> list = dictRService.queryDictItemListByDictKey("DICT_OM_BUSIDOMAIN");
        return getReturnMap(list);
    }

    @ResponseBody
    @RequestMapping(value = "/initCode", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> initCode(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String nodeType = jsonObject.getString("nodeType");
        String busiDomain = jsonObject.getString("busiDomain");
        String busiOrgCode = busiOrgRService.genBusiorgCode(nodeType, busiDomain);
        return getReturnMap(busiOrgCode);
    }

    /**
     * 新增业务机构
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "add",  // 操作类型
            operateDesc = "新增业务机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "busiorgName", // 操作对象名
            keys = {"busiorgName","busiorgCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/addbusiorg", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String,Object> addbusiorg(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String busiorgCode = jsonObject.getString("busiorgCode");
        String busiorgName = jsonObject.getString("busiorgName");
        String busiDomain = jsonObject.getString("busiDomain");
        String guidOrg = jsonObject.getString("guidOrg");
        String orgCode = "";
        List<OmOrg> list = orgRService.queryAllOrg();
        for(OmOrg org : list){
            if(org.getGuid().equals(guidOrg)){
                orgCode = org.getOrgCode();
            }
        }
        String parentsBusiorgCode = jsonObject.getString("parentsBusiorgCode");
        String nodeType = jsonObject.getString("nodeType");
        OmBusiorg ob = new OmBusiorg();
        if("reality".equals(nodeType)){
            ob = busiOrgRService.createRealityBusiorg( busiorgName, orgCode, busiDomain, parentsBusiorgCode);
        }else if("dummy".equals(nodeType)){
            ob = busiOrgRService.createDummyBusiorg(busiorgName, busiDomain, parentsBusiorgCode);
        }
        return getReturnMap(ob);
    }

    /**
     * 生成下级业务机构列表
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadbusiorgbyType", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> loadbusiorgbyType(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String busiDomain = jsonObject.getString("busiDomain");
        List<OmBusiorg> list = busiOrgRService.queryAllBusiorgByDomain(busiDomain);
        return getReturnMap(list);
    }

    /**
     * 删除业务机构,删除当前节点和所有子节点
     * @param content
     * @return
     */
    @OperateLog(
            operateType = "delete",  // 操作类型
            operateDesc = "删除业务机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "busiorgName", // 操作对象名
            keys = {"busiorgName","busiorgCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/deletebusiorg", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> deletebusiorg(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        String busiorgCode = jsonObject.getString("busiorgCode");
        OmBusiorg obo = busiOrgRService.deleteBusiorg(busiorgCode);
        return getReturnMap(obo);
    }

    /**
     * 修改,更新
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,  // 操作类型
            operateDesc = "修改业务机构", // 操作描述
            retType = ReturnType.Object, // 返回类型，对象或数组
            id = "guid", // 操作对象标识
            name = "busiorgName", // 操作对象名
            keys = {"busiorgName","busiorgCode"}) // 操作对象的关键值的键值名
    @ResponseBody
    @RequestMapping(value = "/updatebusiorg", produces ="application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> updatebusiorg(@RequestBody String content) {
        OmBusiorg obg = JSONObject.parseObject(content, OmBusiorg.class);
        busiOrgRService.updateBusiorg(obg);
        return getReturnMap(obg);
    }

    /**
     * 根据GUID查询业务机构信息
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryBusiorgByGuid", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryBusiorgByGuid(@RequestBody String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        return getReturnMap(busiOrgRService.queryBusiorgByGuid(jsonObject.getJSONObject("data").getString("guid")));
    }
}
