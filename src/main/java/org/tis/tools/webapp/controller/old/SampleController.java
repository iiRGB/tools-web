package org.tis.tools.webapp.controller.old;
//package d.webapp.controller;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import net.sf.json.JSONObject;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import d.dao.model.Yuangong;
//import d.service.biz.YuangongService;
//import d.util.AjaxUtils;
//
///**
// * @author su.zhang
// * 考虑的点：app rest请求，app freemarker,内部请求，内部管理系统，内部管理系统freemarker,内部管理系统
// * 如果是静态的资源需要在urlrewrite.xml里面配置，否则会被拦截，详情参考 
// *  <rule>
//        <from>/www/**</from>
//        <to>/www/$1</to>
//    </rule>
// *
// *还需要考虑安全拦截 SecurityInterceptor 的配置，否则可能请求被安全拦截了
// */
//@Controller
//@RequestMapping("/anios")
//public class SampleController extends BaseController{
//
//	final Logger       logger = LoggerFactory.getLogger(this.getClass());
//
//	@Autowired
//	YuangongService yuangongService ;
//	
//	@RequestMapping("/biz1/apprest")
//	public String exe1(ModelMap model, @RequestBody String content,
//			HttpServletRequest request, HttpServletResponse response){
//		
////1、app 请求参数，post body json参数的形式,angularjs 写法如下:
////		return $http.post(baseurl + '/phone/product/netValueList', item).success(function (reponse) {
////			return reponse;
////		});
//		{//业务逻辑
//			Yuangong t=new Yuangong();
//			t.setId(System.currentTimeMillis()+"");  
//			yuangongService.insert(t);
//		}
////		2、返回json
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("key", "value");
//		JSONObject jsonObj = JSONObject.fromObject(map,jsonConfig);
//		AjaxUtils.ajaxJson(response, jsonObj.toString());
//		return null ; 
//	}
//	
//	/**
//	 * 如果app或者微信模版为远程模版，可以才用如下方式，其中静态文件在服务端做过一次加工
//	 * @param model
//	 * @param content
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/biz1/apprest/f.html")
//	public String exe2(ModelMap model, @RequestBody String content,
//			HttpServletRequest request, HttpServletResponse response){
//		
////1、app 请求参数，post body json参数的形式,angularjs 写法如下:
////		return $http.post(baseurl + '/phone/product/netValueList', item).success(function (reponse) {
////			return reponse;
////		});
//		{//业务逻辑
//			Yuangong t=new Yuangong();
//			t.setId(System.currentTimeMillis()+"");  
//			yuangongService.insert(t);
//		}
////		2、返回freemarker
//		model.put("test", "hello world!");
////		return "www/ftl/f.ftl.html" ; 
//		return "www/ftl/f" ; 
//	}
//	
//}
