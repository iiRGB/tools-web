package org.tis.tools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.util.AjaxUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/commonCaller")
public class ComCallerTest extends BaseController {

	/**
	 * postController测试方法
	 * @param content
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/testPostController",method=RequestMethod.POST)
	public String testPostController(@RequestBody String content, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info("testPostController request : " + content);
			}
			JSONObject jsonObj = JSONObject.fromObject(content);
			//{"type":"part","logs":["biztrace.log.1","biztrace.log.2"]}
			String type = jsonObj.getString("type");
			
			//String[] strArr = (String[]) jsonObj.get("logs");
			
			List<String> list = new ArrayList<>();
			JSONArray jsonArray = jsonObj.getJSONArray("logs");
			for(int i = 0; i < jsonArray.size(); i ++) {
				 String str = jsonArray.getString(i);
				 list.add(str);
			}
			result.put("type", type);
			result.put("logs", list);
			AjaxUtils.ajaxJsonSuccessMessage(response, JSONArray.fromObject(result).toString());
			
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "测试异常！");
			logger.error("testPostController exception : ", e);
		}
		return null;
	}
	/**
	 * testController测试方法
	 * @param content
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/testGetController",method=RequestMethod.GET)
	public String testGetController(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info("testGetController request " );
			}
			String id = request.getParameter("id");
			String name = request.getParameter("name");
			result.put("id", id);
			result.put("name", name);
			AjaxUtils.ajaxJsonSuccessMessage(response, JSONArray.fromObject(result).toString());
			
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "测试异常！");
			logger.error("testGetController exception : ", e);
		}
		return null;
	}
	
	
	
	private Map<String, Object> responseMsg ;
	@Override
	public Map<String, Object> getResponseMessage() {
		if( null == responseMsg ){
			responseMsg = new HashMap<String, Object> () ;
		}
		return responseMsg;
	}
	

}
