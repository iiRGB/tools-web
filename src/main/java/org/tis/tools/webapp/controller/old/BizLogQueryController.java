package org.tis.tools.webapp.controller.old;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.util.AjaxUtils;

@Controller
@RequestMapping("/BizLogQueryController")
public class BizLogQueryController extends BaseController{
//	@RequestMapping("/reslover")
//	public String reslover(ModelMap model,@RequestBody String content,
//			HttpServletRequest request,HttpServletResponse response){
//		try {
//			if(logger.isInfoEnabled()){
//				logger.info("BizLogQueryController reslover request : " + content);
//			}
//			
//			String logFilesPath = content;
//			
//			BizTraceAnalyManage.instance.resolve(logFilesPath, RunConfig.threadNum);
//			
//			AjaxUtils.ajaxJsonSuccessMessage(response, "success");
//		} catch (Exception e) {
//			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
//			logger.error("BizLogQueryController reslover exception : " ,e);
//		}
//		return null;		
//	}
	
		
//	@RequestMapping("/analyzer")
//	public String analyzer(ModelMap model,@RequestBody String content,
//			HttpServletRequest request,HttpServletResponse response){
//		try {
//			if(logger.isInfoEnabled()){
//				logger.info("BizLogQueryController analyzer request : " + content);
//			}
//			
//			JSONArray jsonArry = JSONArray.fromObject(content);			
//			Object[] dateArry = jsonArry.toArray();
//			
//			for(int i=0;i<dateArry.length;i++){
//				BizTraceAnalyManage.instance.analyze(dateArry[i].toString());
//			}
//			
//			BizLogDateHandler.instance.deleteAnalyzedBizLogDate(dateArry);
//			
//			AjaxUtils.ajaxJsonSuccessMessage(response, "success");
//		} catch (Exception e) {
//			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
//			logger.error("BizLogQueryController analyzer exception : " ,e);
//		}
//		return null;		
//	}
	
	@RequestMapping("/bodyInfoReport")
	public String bodyInfoReport(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {
			if(logger.isInfoEnabled()){
				logger.info("BizLogQueryController bodyInfoReport request : " + content);
			}
			JSONObject jsonObj = JSONObject.fromObject(content);
			String trans_serial = jsonObj.getString("trans_serial");
			String trans_date = jsonObj.getString("trans_date");
			
			List<Map<String,String>> results = null ; 
			//results = BodyInfoReport.instance.report(trans_serial, trans_date);
			
			logger.info("BizLogQueryController bodyInfoReport response : "+JSONArray.fromObject(results, jsonConfig).toString());
			AjaxUtils.ajaxJson(response, JSONArray.fromObject(results, jsonConfig).toString());
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("BizLogQueryController bodyInfoReport exception : " ,e);
		}
		return null;		
	}
	
	@RequestMapping("/bodyInfoByRankReport")
	public String bodyInfoByRankReport(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {
			if(logger.isInfoEnabled()){
				logger.info("BizLogQueryController bodyInfoByRankReport request : " + content);
			}
						
			List<Map<String,String>> results = null ; 
			//results = BodyInfoByRankReport.instance.report(Integer.parseInt(content));
			
			logger.info("BizLogQueryController bodyInfoByRankReport response : "+JSONArray.fromObject(results, jsonConfig).toString());
			AjaxUtils.ajaxJson(response, JSONArray.fromObject(results, jsonConfig).toString());
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("BizLogQueryController bodyInfoByRankReport exception : " ,e);
		}
		return null;		
	}
	
	@RequestMapping("/detailInfoReport")
	public String detailInfoReport(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {
			if(logger.isInfoEnabled()){
				logger.info("BizLogQueryController detailInfoReport request : " + content);
			}
					
			List<Map<String,String>> results = null ; 
//			BizTraceAnalyManage.instance.showSerialDetail(content);
//			results = ShowTransTimeConsumingDetailReport.results;
			
			logger.info("BizLogQueryController detailInfoReport response : " + JSONArray.fromObject(results, jsonConfig).toString());
			AjaxUtils.ajaxJson(response, JSONArray.fromObject(results, jsonConfig).toString());
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("BizLogQueryController detailInfoReport exception : " ,e);
		}
		return null;		
	}
		
//	@RequestMapping("/getUnanalyedDate")
//	public String getUnanalyedDate(ModelMap model,@RequestBody String content,
//			HttpServletRequest request,HttpServletResponse response){
//		try {
//			if(logger.isInfoEnabled()){
//				logger.info("BizLogQueryController getUnanalyedDate request : " + content);
//			}
//			
//			Set<String> unanalyedDates = BizLogDateHandler.instance.getUnanalyzedBizLogDate();
//				
//			AjaxUtils.ajaxJson(response, JSONArray.fromObject(unanalyedDates, jsonConfig).toString());
//		} catch (Exception e) {
//			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
//			logger.error("BizLogQueryController getUnanalyedDate exception : " ,e);
//		}
//		return null;		
//	}
	
	private Map<String, Object> responseMsg ;
	@Override
	public Map<String, Object> getResponseMessage() {
		if( null == responseMsg ){
			responseMsg = new HashMap<String, Object> () ;
		}
		return responseMsg ;
	}
		
}
