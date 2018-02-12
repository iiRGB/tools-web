/**
 * 
 */
package org.tis.tools.webapp.spi.dubboinfo;

import java.util.List;

/**
 * 
 * Dubbo管理接口，主要获取Dubbo相关的信息
 * 
 * @author megapro
 *
 */
public interface IDubboInfoManager {

	/**
	 * 获取所有biztrace服务提供者信息列表
	 * @return
	 */
	public List<DubboServiceInfo> getBiztraceProviderList() ; 
	
}
