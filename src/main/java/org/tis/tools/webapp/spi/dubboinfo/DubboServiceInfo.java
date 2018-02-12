/**
 * 
 */
package org.tis.tools.webapp.spi.dubboinfo;

import java.io.Serializable;

import com.alibaba.dubbo.common.URL;

/**
 * <pre>
 * 
 * Dubbo服务信息（VO对象）
 * 
 * 来自对.cache文件的解析
 * 
 * dubbo\://192.168.0.102\:20883/org.tis.tools.service.api.biztrace.IBiztraceRService?
	anyhost\=true&
	application\=tools-service-biztrace-sit&
	dubbo\=2.8.4a&
	generic\=false&
	group\=biztrace&
	interface\=org.tis.tools.service.api.biztrace.IBiztraceRService&
	methods\=analyseBiztrace,listBiztraces,getResolveProcess,resolveBiztraceFixed&
	organization\=org.tis&
	owner\=shiyl&
	pid\=5468&
	revision\=1.0&
	side\=provider&
	timeout\=3000&
	timestamp\=1479345014991&
	version\=1.0
 * </pre>
 * 
 * @author megapro
 *
 */
public class DubboServiceInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6816377331633395709L;

	// (代理)封装dubbo的URL，只提供有用的几个信息
//	private URL dubboProviderUrl;
	private String host = null;
	private int port = 0;
	private String protocol = null;
	private String serviceName = null;

//	public DubboServiceInfo(String url) {
//		dubboProviderUrl = URL.valueOf(url);
//		this.setHost(dubboProviderUrl.getHost());
//		this.setPort(dubboProviderUrl.getPort());
//		this.setProtocol(dubboProviderUrl.getProtocol());
//		this.setServiceName(dubboProviderUrl.getServiceName());
//	}
	
	public DubboServiceInfo(){
		
	}
	
//	public URL getUrl() {
//		return this.dubboProviderUrl;
//	}
//
//	public String getHost() {
//		return this.dubboProviderUrl.getHost();
//	}
//
//	public int getPort() {
//		return this.dubboProviderUrl.getPort();
//	}
//
//	public String getProtocol() {
//		return this.dubboProviderUrl.getProtocol();
//	}
//
//	public String getServiceName() {
//		return this.dubboProviderUrl.getServiceName();
//	}
	
	
	public String getHost() {
		return this.host  ;
	}

	public int getPort() {
		return this.port ; 
	}

	public String getProtocol() {
		return this.protocol ; 
	}

	public String getServiceName() {
		return this.serviceName ; 
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
