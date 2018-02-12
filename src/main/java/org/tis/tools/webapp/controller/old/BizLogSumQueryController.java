package org.tis.tools.webapp.controller.old;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping("/BizLogSumQueryController")
public class BizLogSumQueryController extends BaseController{
	
	@RequestMapping("/dayLogSumInfoReport")
	public String dayLogSumInfoReport(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {
			if(logger.isInfoEnabled()){
				logger.info("BizLogSumQueryController dayLogSumInfoReport request : " + content);
			}
			JSONObject jsonObj = JSONObject.fromObject(content);
			String transDate_start = jsonObj.getString("transDate_start");
			String transDate_end = jsonObj.getString("transDate_end");
			if(transDate_start==null || "".equals(transDate_start)){
				return null;
			}
			if(transDate_end==null || "".equals(transDate_end)){
				transDate_end = transDate_start;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date_start = sdf.parse(transDate_start);
			Date date_end = sdf.parse(transDate_end);
			long start_millis = date_start.getTime();
			long end_millis = date_end.getTime();
			
			long oneDayMillis = 24*60*60*1000;
			List<Map<String,String>> results = new ArrayList<Map<String,String>>();
			for(long n=start_millis;n<=end_millis;n+=oneDayMillis){
				Date date = new Date(n);
				String dataStr = sdf.format(date);
				Map<String,String> result = null ; 
				//result = DayLogInfoSumReport.instance.report(dataStr);
				if(result == null || result.isEmpty()){
					continue;
				}
				result.put("transDate", dataStr);
				results.add(result); 
			}
			
			logger.info("BizLogSumQueryController dayLogSumInfoReport response : "+JSONArray.fromObject(results, jsonConfig).toString());
			AjaxUtils.ajaxJson(response, JSONArray.fromObject(results, jsonConfig).toString());
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("BizLogSumQueryController dayLogSumInfoReport exception : " ,e);
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
