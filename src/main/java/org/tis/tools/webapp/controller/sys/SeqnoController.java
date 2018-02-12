package org.tis.tools.webapp.controller.sys;

import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.base.exception.ToolsRuntimeException;
import org.tis.tools.model.po.ac.AcRole;
import org.tis.tools.model.po.sys.SysRunConfig;
import org.tis.tools.model.po.sys.SysSeqno;
import org.tis.tools.rservice.sys.basic.ISysRunConfigRService;
import org.tis.tools.rservice.sys.basic.ISysSeqnoRService;
import org.tis.tools.rservice.sys.capable.IRunConfigRService;
import org.tis.tools.rservice.sys.capable.ISeqnoRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.util.AjaxUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/SeqnoController")
public class SeqnoController extends BaseController {

    private Map<String, Object> responseMsg ;

    @Autowired
    ISeqnoRService seqnoRService;

    /**
     * 查询序号资源表
     *
     */
    @ResponseBody
    @RequestMapping(value="/querySeqnoList" ,produces = "text/plain;charset=UTF-8",method= RequestMethod.POST)
    public String querySeqnoList(@RequestBody String content, HttpServletRequest request,
                                     HttpServletResponse response)  {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("querySeqnoList request : " + content);
            }
            List<SysSeqno> sysSeqnos = seqnoRService.queryAllSeqno();
            AjaxUtils.ajaxJsonSuccessMessage(response,sysSeqnos);
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response,e.getCode(), e.getMessage());
            logger.error("querySeqnoList exception : ", e);
        }catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response,"SYS_0001", e.getMessage());
            logger.error("querySeqnoList exception : ", e);
        }
        return null;
    }

    /**
     * 新增序号资源表
     */
    /*@ResponseBody
    @RequestMapping(value="/createSeqno" ,produces = "text/plain;charset=UTF-8",method= RequestMethod.POST)
    public String createSeqno(@RequestBody String content, HttpServletRequest request,
                                  HttpServletResponse response) throws ToolsRuntimeException, ParseException {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("createSeqno request : " + content);
            }
            SysSeqno sysSeqno = JSON.parseObject(content, SysSeqno.class);
            SysSeqno seqno = seqnoRService.createSeqno(sysSeqno);
            AjaxUtils.ajaxJsonSuccessMessage(response,seqno);
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response,e.getCode(), e.getMessage());
            logger.error("createSeqno exception : ", e);
        }catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response,"SYS_0001", e.getMessage());
            logger.error("createSeqno exception : ", e);
        }
        return null;
    }*/

    /**
     * 修改序号资源表
     */
    @ResponseBody
    @RequestMapping(value="/editSeqno" ,produces = "text/plain;charset=UTF-8",method= RequestMethod.POST)
    public String editRole(@RequestBody String content, HttpServletRequest request,
                           HttpServletResponse response) {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("editSeqno request : " + content);
            }
            SysSeqno sysSeqno = JSON.parseObject(content, SysSeqno.class);
            SysSeqno seqno = seqnoRService.editSeqno(sysSeqno);
            AjaxUtils.ajaxJsonSuccessMessage(response,seqno);
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response,e.getCode(), e.getMessage());
            logger.error("editSeqno exception : ", e);
        }catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response,"SYS_0001", e.getMessage());
            logger.error("editSeqno exception : ", e);
        }
        return null;
    }

    /**
     * 删除序号资源表
     */
    @ResponseBody
    @RequestMapping(value="/deleteSeqno" ,produces = "text/plain;charset=UTF-8",method= RequestMethod.POST)
    public String deleteSeqno(@RequestBody String content, HttpServletRequest request,
                             HttpServletResponse response) throws ToolsRuntimeException, ParseException {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("deleteSeqno request : " + content);
            }
            JSONObject jsonObject= JSONObject.parseObject(content);
            String seqKey = jsonObject.getString("seqKey");
            seqnoRService.deleteSeqno(seqKey);
            AjaxUtils.ajaxJsonSuccessMessage(response,"");
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response,e.getCode(), e.getMessage());
            logger.error("deleteSeqno exception : ", e);
        }catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response,"SYS_0001", e.getMessage());
            logger.error("deleteSeqno exception : ", e);
        }
        return null;
    }

    /**
     * 要求子类构造自己的响应数据
     *
     * @return
     */
    @Override
    public Map<String, Object> getResponseMessage() {
        if( null == responseMsg ){
            responseMsg = new HashMap<String, Object>() ;
        }
        return responseMsg;
    }
}
