/**
 * 
 */
package org.tis.tools.dubbo.impl.router;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.cluster.Router;
import com.alibaba.dubbo.rpc.cluster.RouterFactory;

/**
 * 
 * 根据服务提供者的主机名路由
 * 
 * @author megapro
 *
 */
public class ByHostRouterFactory implements RouterFactory {

	/* (non-Javadoc)
	 * @see com.alibaba.dubbo.rpc.cluster.RouterFactory#getRouter(com.alibaba.dubbo.common.URL)
	 */
	@Override
	public Router getRouter(URL url) {
		return new ByHostRouter(url);
	}

}
