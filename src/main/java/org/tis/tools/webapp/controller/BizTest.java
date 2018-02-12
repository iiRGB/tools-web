package org.tis.tools.webapp.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tis.tools.base.WhereCondition;
import org.tis.tools.model.po.ac.AcApp;
import org.tis.tools.model.po.sys.SysDict;
import org.tis.tools.rservice.ac.basic.IAcAppRService;
import org.tis.tools.rservice.om.capable.IOrgRService;
import org.tis.tools.rservice.sys.capable.IDictRService;
import org.tis.tools.service.api.biztrace.BiztraceFileInfo;
import org.tis.tools.service.api.biztrace.IBiztraceRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.util.AjaxUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;


@Controller
@RequestMapping("/testController")
public class BizTest extends BaseController {
	
	@Autowired
	IBiztraceRService biztraceRService ;
	
	/**
	 * 示意controller开发的基本程序范式
	 * @param model
	 * @param content
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/test")
	public String test(ModelMap model,@RequestBody String dddd,
			HttpServletRequest request,HttpServletResponse response){
		
		/*
		 * 取：取请求数据
		 * 调：调用业务逻辑
		 * 返：返回响应结果
		 * 转：调整页面视图(一般不用，在前端MVC框架中自己已经完成了页面的跳转路由)
		 */
		try {
			
			if(logger.isInfoEnabled()){
				logger.info("testController test request : " + dddd);
			}
			
			// 取请求数据
			JSONObject jsonObj = JSONObject.fromObject(dddd);
			String trans_serial = jsonObj.getString("trans_serial");
			System.out.println("trans_serial:"+trans_serial);
			
			// 处理数据，调用业务逻辑
			// TODO 业务逻辑
			
			// 返回响应数据
			returnResponseData("trans_serial", trans_serial); 
			returnResponseData("date_time", new Date()); 
			
			String jsonData = JSONArray.fromObject(responseMsg).toString() ; 
			System.out.println("response json data :"+ jsonData);
			AjaxUtils.ajaxJsonSuccessMessage(response, jsonData);
			
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, "异常");
			logger.error("testController test exception : " ,e);
		}
		
		// 跳转视图
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value="/biztrace/{providerHost}/list",method=RequestMethod.GET)
	public String getBranchCode(HttpServletRequest request, @PathVariable String providerHost,
			HttpServletResponse response) {
		
		System.out.println("调用<"+providerHost+">biztrace服务，列出日志文件");
		List<BiztraceFileInfo> logFileList = biztraceRService.listBiztraces(providerHost) ;
		for(BiztraceFileInfo f : logFileList ){
			System.out.println( f );
		}
		
		return JSONUtils.valueToString(logFileList) ;
	}
	
	
	@Autowired
	IDictRService dictRService;
	
	@ResponseBody
	@RequestMapping(value="/abf",method=RequestMethod.POST)
	public String testABF(HttpServletRequest request, @RequestBody String body,
			HttpServletResponse response) {
		
		System.out.println("请求数据： "+body);
		
		SysDict dict = dictRService.queryDict("SHIYL_TEST_NEW") ;
		System.out.println("结果： "+dict);
		
		return JSONUtils.valueToString(body) ;
	}
	
	

	private Map<String, Object> responseMsg ;
	@Override
	public Map<String, Object> getResponseMessage() {
		responseMsg = new HashMap<String, Object> () ;
		return responseMsg ;
	}
}
