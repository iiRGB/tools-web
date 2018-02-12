package org.tis.tools.webapp.controller.old;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tis.tools.model.vo.devmgr.FeatureReg;
import org.tis.tools.service.api.devmgr.FeatureRegRemoteService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.util.AjaxUtils;

@Controller
@RequestMapping("/FeatureRegController")
public class FeatureRegController extends BaseController{
	
	//rebuild to dubbo remote service by shiyl 20161111
	@Autowired
	private FeatureRegRemoteService featureRegRService;
	
	@RequestMapping("/query")
	public String query(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {			
			logger.info("FeatureRegController query request : " + content);
			
			JSONObject jsonObj = JSONObject.fromObject(content);

			String workId = jsonObj.getString("workId");
			String taskId = jsonObj.getString("taskId");
			String featureType = jsonObj.getString("featureType");
			String developer = jsonObj.getString("developer");
			String status = jsonObj.getString("status");
			String releaseDate = jsonObj.getString("releaseDate");
			
			FeatureReg featureReg = new FeatureReg();
			if(workId != null && !"".equals(workId.trim())){
				featureReg.setWorkId(workId);
			}
			if(taskId != null && !"".equals(taskId.trim())){
				featureReg.setArtfId(taskId);
			}
			if(featureType != null && !"".equals(featureType.trim())){
				featureReg.setBranchType(featureType);
			}
			if(developer != null && !"".equals(developer.trim())){
				featureReg.setDeveloper(developer);
			}
			if(status != null && !"".equals(status.trim())){
				featureReg.setStatus(status);
			}
			if(releaseDate != null && !"".equals(releaseDate.trim())){
				featureReg.setReleaseDate(releaseDate);
			}
			
			List<FeatureReg> results = featureRegRService.query(featureReg);			
			
			AjaxUtils.ajaxJson(response, JSONArray.fromObject(results, jsonConfig).toString());
			logger.info("FeatureRegController query response : " + results);
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, e.getMessage());
			logger.error("FeatureRegController query exception : " ,e);
		}
		return null;		
	}
	
	@RequestMapping("/add")
	public String add(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {			
			logger.info("FeatureRegController add request : " + content);
			
			JSONObject jsonObj = JSONObject.fromObject(content);
			//JSONObject addContext =  jsonObj.getJSONObject("addContext");
						
			String featureId = jsonObj.getString("featureId");
			String workId = jsonObj.getString("workId");
			String workDesc = jsonObj.getString("workDesc");
			String taskId = jsonObj.getString("taskId");
			String featureType = jsonObj.getString("featureType");
			String featureMgr = jsonObj.getString("featureMgr");
			String openDate = jsonObj.getString("openDate");
			String listFileName = jsonObj.getString("listFileName");
			String releaser = jsonObj.getString("releaser");
			String release_date = jsonObj.getString("releaseDate");
			String developer = jsonObj.getString("developer");
			String status = jsonObj.getString("status");			
			
			FeatureReg featureReg = new FeatureReg();
			featureReg.setBranchId(featureId);
			featureReg.setWorkId(workId);
			featureReg.setWorkDesc(workDesc);
			featureReg.setArtfId(taskId);
			featureReg.setBranchType(featureType);
			featureReg.setBranchMgr(featureMgr);
			featureReg.setOpenDate(openDate);
			featureReg.setListFileName(listFileName);
			featureReg.setReleaser(releaser);
			featureReg.setReleaseDate(release_date);
			featureReg.setDeveloper(developer);
			featureReg.setStatus(status);
			
			featureRegRService.insert(featureReg);
			
			AjaxUtils.ajaxJson(response, "success");
			logger.info("FeatureRegController add response : ok!");
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, e.getMessage());
			logger.error("FeatureRegController add exception : " ,e);
		}
		return null;		
	}
	
	@RequestMapping("/delete")
	public String delete(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {			
			logger.info("FeatureRegController delete request : " + content);
			
			JSONArray workIdArry =  JSONArray.fromObject(content);
			//JSONObject jsonObj = JSONObject.fromObject(content);
			//JSONArray deletedIdArry =  jsonObj.getJSONArray("deletedIdArry");
			Object[] list = workIdArry.toArray();
 			for(int i=0;i<list.length;i++){
 				String workId = (String) list[i];
 				featureRegRService.delete(workId);
 			}
									
			AjaxUtils.ajaxJson(response, "success");
			logger.info("FeatureRegController delete response : ok!");
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, e.getMessage());
			logger.error("FeatureRegController delete exception : " ,e);
		}
		return null;		
	}
	
	@RequestMapping("/update")
	public String update(ModelMap model,@RequestBody String content,
			HttpServletRequest request,HttpServletResponse response){
		try {			
			logger.info("FeatureRegController update request : " + content);
			
			JSONObject jsonObj = JSONObject.fromObject(content);

			String featureId = jsonObj.getString("featureId");
			String workId = jsonObj.getString("workId");
			String workDesc = jsonObj.getString("workDesc");
			String taskId = jsonObj.getString("taskId");
			String featureType = jsonObj.getString("featureType");
			String featureMgr = jsonObj.getString("featureMgr");
			String openDate = jsonObj.getString("openDate");
			String listFileName = jsonObj.getString("listFileName");
			String releaser = jsonObj.getString("releaser");
			String release_date = jsonObj.getString("releaseDate");
			String developer = jsonObj.getString("developer");
			String status = jsonObj.getString("status");
			
			FeatureReg featureReg = new FeatureReg();
			featureReg.setBranchId(featureId);
			featureReg.setWorkId(workId);
			featureReg.setWorkDesc(workDesc);
			featureReg.setArtfId(taskId);
			featureReg.setBranchType(featureType);
			featureReg.setBranchMgr(featureMgr);
			featureReg.setOpenDate(openDate);
			featureReg.setListFileName(listFileName);
			featureReg.setReleaser(releaser);
			featureReg.setReleaseDate(release_date);
			featureReg.setDeveloper(developer);
			featureReg.setStatus(status);
			
			featureRegRService.update(featureReg);			
			
			AjaxUtils.ajaxJson(response, "success");
			logger.info("FeatureRegController update response : ok!");
		} catch (Exception e) {
			AjaxUtils.ajaxJsonErrorMessage(response, e.getMessage());
			logger.error("FeatureRegController update exception : " ,e);
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
