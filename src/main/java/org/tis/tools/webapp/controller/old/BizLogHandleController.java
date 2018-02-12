package org.tis.tools.webapp.controller.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tis.tools.service.api.biztrace.BiztraceFileInfo;
import org.tis.tools.service.api.biztrace.IBiztraceRService;
import org.tis.tools.service.api.biztrace.ParseProcessInfo;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.util.AjaxUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Controller
@RequestMapping("/biztrace") //REBUILD BizLogHandleController--> biztrace
public class BizLogHandleController extends BaseController{
	
	
	@Autowired
	IBiztraceRService biztraceRService ;
	
	boolean resolverOverFlag = false;
	
	@RequestMapping("/reslover")
	public String reslover(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		
		try {
			
			logger.info("BizLogHandleController reslover request : " + content);
			
			//TODO content转为 fixedBiztraces
			List<BiztraceFileInfo> fixedBiztraces = new ArrayList<BiztraceFileInfo>() ; 
			
			biztraceRService.resolveAndAnalyseBiztraceFixed(fixedBiztraces) ; 
			
			AjaxUtils.ajaxJsonSuccessMessage(response, "success");
			
			logger.info("BizLogHandleController reslover response : " + response);
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("BizLogHandleController reslover exception : " ,e);
		}
		return null;		
	}
	
	@RequestMapping("/getResloverProcess")
	public String getResloverProcess(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {			
			logger.info("BizLogHandleController getResloverProcess request : " + content);
			
			ParseProcessInfo processInfo = biztraceRService.getResolveProcess()  ;
			
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("resolverOverFlag", resolverOverFlag);
			result.put("fileTotalNum", processInfo.getTotalLogFiles());
//			result.put("fileReadNum", processInfo.fileReadNum);
//			result.put("fileParsedNum", processInfo.fileParsedNum);
//			result.put("fileParseRecord", processInfo.fileParseRecord);

			AjaxUtils.ajaxJson(response, JSONObject.fromObject(result, jsonConfig).toString());			
			
			logger.info("BizLogHandleController getResloverProcess response : " + result.toString());
			
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("BizLogHandleController getResloverProcess exception : " ,e);
		}
		return null;		
	}
	
		
	@RequestMapping("/analyzer")
	public String analyzer(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {
			logger.info("BizLogHandleController analyzer request : " + content);
			
			List<String> analyseDate = new ArrayList<String>() ;
			//TOOD content 中传输指定好的日期
			
			//biztraceRService.analyseBiztrace(analyseDate) ; 
			
			AjaxUtils.ajaxJsonSuccessMessage(response, "success");
			logger.info("BizLogHandleController analyzer response : ok" );
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("BizLogHandleController analyzer exception : " ,e);
		}
		return null;		
	}
	
	@RequestMapping("/getUnanalyedDate")
	public String getUnanalyedDate(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {
			if(logger.isInfoEnabled()){
				logger.info("BizLogHandleController getUnanalyedDate request : " + content);
			}
			
			Set<String> unanalyedDates = null ; 
			
			//unanalyedDates = BizLogDateHandler.instance.getUnanalyzedBizLogDate();
				
			AjaxUtils.ajaxJson(response, JSONArray.fromObject(unanalyedDates, jsonConfig).toString());
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("BizLogHandleController getUnanalyedDate exception : " ,e);
		}
		return null;		
	}
	
	private Map<String, Object> responseMsg ;
	@Override
	public Map<String, Object> getResponseMessage() {
		if( null == responseMsg ){
			responseMsg = new HashMap<String, Object> () ;
		}
		return responseMsg ;
	}
}
