package org.tis.tools.webapp.controller.old;
//package d.webapp.controller;
//
//
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import d.dao.model.WhereCondition;
//import d.dao.model.Yuangong;
//import d.service.biz.YuangongService;
//
//
//@Controller
//public class M_loginController {
//	
//	@Autowired
//	private YuangongService yuangongService;
//	
//	@RequestMapping(value = "/login")
//	public String execute(ModelMap model, String loginname,
//			String passwd, HttpServletRequest request, HttpServletResponse response) {
//		Object loginObj = request.getSession().getAttribute("login");
//		if(loginObj!=null){
//			return "redirect:/ng/index.html";
//		}
//		if(StringUtils.isEmpty(loginname)){
////			model.put("loginnameErr", "用户名不能为空");
//			return "login";
//		}
//		if(StringUtils.isEmpty(passwd)){
////			model.put("passwdErr", "密码不能为空");
//			return "login";
//		}
//		WhereCondition wc = new WhereCondition();
//		wc.andEquals("dengluming", loginname);
//		List<Yuangong> list = yuangongService.query(wc);
//		if(CollectionUtils.isEmpty(list)){
//			//用户名不存在
//			model.put("errorMeg", "用户名不存在");
//			return "login";
//		}else{
//			Yuangong r = list.get(0);
//			if(StringUtils.equals(r.getMima(), passwd)){ //登陆成功
//				
//				return "redirect:/ng/index.html";
//			}else{
//				//密码错误
//				model.put("errorMeg", "密码错误");
//				return "login";
//			}
//		}
//	}
//
//}
