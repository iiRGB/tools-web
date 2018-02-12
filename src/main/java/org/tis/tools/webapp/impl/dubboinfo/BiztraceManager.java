/**
 * 
 */
package org.tis.tools.webapp.impl.dubboinfo;

import com.alibaba.dubbo.common.URL;
import org.apache.commons.lang.StringEscapeUtils;
import org.tis.tools.common.utils.DirectoryUtil;
import org.tis.tools.webapp.exception.WebAppException;
import org.tis.tools.webapp.spi.dubboinfo.DubboServiceInfo;
import org.tis.tools.webapp.spi.dubboinfo.IDubboInfoManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author megapro
 *
 */
public class BiztraceManager implements IDubboInfoManager {
	
	/** 一个provider服务提供者信息开始标识 */
	private static final String DUBBO_SIGN = "dubbo://";
	/** duboo服务注册缓存文件 */
	private static final String CACHE_TOOLS_WEB_TOOLS_CACHE = "dubbo/cache/tools-web-tools.cache";//fixme 重构 使用disoncf
	/** biztrace 服务的url开头标示字符串 */
	private static final String BIZTRACE_SERVICE_HEADSTR = "biztrace/org.t"
			+ "is.tools.service.api.biztrace.IBiztraceRService";
	
	public static BiztraceManager instance = new BiztraceManager() ; 
	
	private BiztraceManager(){
		
	}
	
	/**
	 * 获取所有biztrace代理服务信息列表
	 * @return
	 */
	public List<DubboServiceInfo> getBiztraceProviderList() {
		
		boolean mocker = false ; //true模拟 
		
		if( !mocker ){
			String cacheFile = DirectoryUtil.getAppMainDirectory() +"/"+CACHE_TOOLS_WEB_TOOLS_CACHE; 
			return resolveCacheFIle(cacheFile) ; 
		}else{
			
			return mockData() ; 
		}
		
	}

	/**
	 * 获取“biztrace/org.tis.tools.service.api.biztrace.IBiztraceRService”服务对应的服务提供者信息
	 * <br>抽取后也好单元测试
	 * @param cacheFile 如果此时注册中心没有日志代理服务提供者，返回null。前端可多次刷新查看当前是否有服务提供者了
	 * @return
	 */
	public List<DubboServiceInfo> resolveCacheFIle(String cacheFile) {
		List<DubboServiceInfo> md = new ArrayList<DubboServiceInfo>() ;
		
		try {
			InputStream in = new FileInputStream(cacheFile);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String temp = null ; 
			
			while( ( temp = br.readLine() ) != null ){
				
				//直解析 biztrace服务 
				if( temp.startsWith(BIZTRACE_SERVICE_HEADSTR)  ){
					String ftemp = StringEscapeUtils.unescapeJava(temp) ;//去掉原始url中的转义字符
					int i = ftemp.indexOf(DUBBO_SIGN) ;// 定位 dubbo\:// 位置
					if( i == -1 ){
						return null ; //此时没有biztrace服务的provider
					}
					String s = ftemp.substring(i, ftemp.length()) ;
					String[] providerUrls = s.split(" ") ;
					for( String e : providerUrls ){
						md.add( covertProviderInfo(e) );
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebAppException("WEB-0001", "获取所有biztrace代理服务信息列表失败！",e) ;
		}
		return md;
	}


	/**
	 * 根据dubbo的url，返回简要dubbo服务提供者信息
	 * @param url dubbo服务url
	 * @return DubboServiceInfo
	 */
	private DubboServiceInfo covertProviderInfo(String url) {
		
		URL du = URL.valueOf(url);
		DubboServiceInfo info = new DubboServiceInfo() ; 
		info.setHost(du.getHost());
		info.setPort(du.getPort());
		info.setProtocol(du.getProtocol());
		info.setServiceName(du.getServiceName());
		return info;
	}
	
	private List<DubboServiceInfo> mockData() {

		List<DubboServiceInfo> md = new ArrayList<DubboServiceInfo>() ;
		String url1 = "dubbo://127.0.0.1:20884/org.tis.tools.service.api.biztrace.IBiztraceRService?anyhost=true&application=tools-service-biztrace&dubbo=2.8.4a&generic=false&group=biztrace&interface=org.tis.tools.service.api.biztrace.IBiztraceRService&methods=listBiztraces,resolveAndAnalyseBiztraceFixed,getResolveProcess&organization=org.tis&owner=shiyl&pid=2054&revision=0.0.1&side=provider&timeout=3000&timestamp=1480295582806&version=1.0" ;
		md.add(covertProviderInfo(url1)) ;

		String url2 = "dubbo://127.0.0.1:20885/org.tis.tools.service.api.biztrace.IBiztraceRService?anyhost=true&application=tools-service-biztrace&dubbo=2.8.4a&generic=false&group=biztrace&interface=org.tis.tools.service.api.biztrace.IBiztraceRService&methods=listBiztraces,resolveAndAnalyseBiztraceFixed,getResolveProcess&organization=org.tis&owner=shiyl&pid=2054&revision=0.0.1&side=provider&timeout=3000&timestamp=1480295582806&version=1.0" ;
		md.add(covertProviderInfo(url2)) ;
		
		String url3 = "dubbo://127.0.0.1:20885/org.tis.tools.service.api.biztrace.IBiztraceRService?anyhost=true&application=tools-service-biztrace&dubbo=2.8.4a&generic=false&group=biztrace&interface=org.tis.tools.service.api.biztrace.IBiztraceRService&methods=listBiztraces,resolveAndAnalyseBiztraceFixed,getResolveProcess&organization=org.tis&owner=shiyl&pid=2054&revision=0.0.1&side=provider&timeout=3000&timestamp=1480295582806&version=1.0" ;
		md.add(covertProviderInfo(url3)) ;
		
		return md;
	}
	
}
