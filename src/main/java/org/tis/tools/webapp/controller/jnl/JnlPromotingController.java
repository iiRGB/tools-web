
/**
 * auto generated
 * Copyright (C) 2016 bronsp.com, All rights reserved.
 */
package org.tis.tools.webapp.controller.jnl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tis.tools.base.Page;
import org.tis.tools.base.WhereCondition;
import org.tis.tools.model.po.jnl.JnlPromoting;
import org.tis.tools.rservice.jnl.IJnlPromotingRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.util.AjaxUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author generated by tools:gen-dao
 */
@Controller
@RequestMapping(value = "/jnl")
public class JnlPromotingController extends BaseController {

	//@Reference(group="jnl",version="1.0",interfaceClass=IJnlPromotingRService.class)
	@Autowired
	IJnlPromotingRService jnlPromotingRService;
	
	@RequestMapping(value = "/jnlPromoting/edit")
	public String execute(ModelMap model, @RequestBody String content,
			String age, HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject jsonObj = JSONObject.parseObject(content);
			JnlPromoting p = jsonObj.getObject("item", JnlPromoting.class);
			String id = sequenceService.generateId("JnlPromoting");
			if (StringUtils.isNotEmpty(p.getGuid())) {
				jnlPromotingRService.update(p);
			} else {
				p.setGuid(id);
				//initCreate(p, request);
				jnlPromotingRService.insert(p);
			}
			AjaxUtils.ajaxJson(response, JSON.toJSONString(p));
		} catch (Exception e) {// TODO
			AjaxUtils.ajaxJsonErrorMessage(response, "添加失败!");
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/jnlPromoting/delete")
	public String execute3(ModelMap model, @RequestBody String content,
			String age, HttpServletRequest request, HttpServletResponse response) {
		try {
			List<JnlPromoting> list = JSONArray.parseArray(content, JnlPromoting.class);
//			List<JnlPromoting> list = new ArrayList<JnlPromoting>();
//			for (int i = 0; i < jsonArray.size(); i++) {
//				JnlPromoting p = new JnlPromoting();
//				JSONObject.toBean(JSONObject.fromObject(jsonArray.get(i)),p,jsonConfig);
//				list.add(p);
//			}
			List<String> ids = new ArrayList<String>();
			for (JnlPromoting p : list) {
				ids.add(p.getGuid());
			}
			WhereCondition wc = new WhereCondition();
			wc.andIn("id", ids);
			jnlPromotingRService.deleteByCondition(wc);
			AjaxUtils.ajaxJson(response, "");
		} catch (Exception e) {// TODO
			AjaxUtils.ajaxJsonErrorMessage(response, "删除失败!");
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/jnlPromoting/list")
	public String execute1(ModelMap model, @RequestBody String content,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject jsonObj = JSONObject.parseObject(content);
			// 分页对象
			Page page = getPage(jsonObj);
			// 服务端排序规则
			String orderGuize = getOrderGuize(jsonObj.getString("orderGuize"));
			// 组装查询条件
			WhereCondition wc = new WhereCondition();
			wc.setLength(page.getItemsperpage());
			wc.setOffset((page.getCurrentPage() - 1) * page.getItemsperpage());
			wc.setOrderBy(orderGuize);
			initWanNengChaXun(jsonObj, wc);// 万能查询
			List list = jnlPromotingRService.query(wc);
			JSONArray ja = JSONArray.parseArray(JSON.toJSONString(list));
			page.setTotalItems(jnlPromotingRService.count(wc));
			Map map = new HashMap();
			map.put("page", page);
			map.put("list", ja);
//			String s=JSONObject.fromObject(map,jsonConfig).toString();
			AjaxUtils.ajaxJson(response, JSON.toJSONString(map) );
		} catch (Exception e) {// TODO
			AjaxUtils.ajaxJsonErrorMessage(response, "查询失败!");
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "/jnlPromoting/list/id")
	public String executesd1(ModelMap model, @RequestBody String content,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject jsonObj = JSONObject.parseObject(content);
			JnlPromoting k =  jnlPromotingRService.loadByGuid(jsonObj.getString("id"));
//			JSONObject jo = JSONObject.fromObject(k,jsonConfig);
			AjaxUtils.ajaxJson(response, JSON.toJSONString(k));
		} catch (Exception e) {// TODO
			AjaxUtils.ajaxJsonErrorMessage(response, "查询失败!");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 每个controller定义自己的返回信息变量
	 */
	private Map<String, Object> responseMsg ;
	@Override
	public Map<String, Object> getResponseMessage() {
		if( null == responseMsg ){
			responseMsg = new HashMap<String, Object> () ;
		}
		return responseMsg ;
	}
}
