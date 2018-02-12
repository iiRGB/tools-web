/**
 * 
 */
package org.tis.tools.dubbo.impl.router;

import java.util.List;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Router;

/**
 * @author megapro
 *
 */
public class ByHostRouter implements Router {

	URL url = null ; 
	
	ByHostRouter(URL u ){
		this.url = u ; 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Router o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.alibaba.dubbo.rpc.cluster.Router#getUrl()
	 */
	@Override
	public URL getUrl() {
		return this.url;
	}

	/* (non-Javadoc)
	 * @see com.alibaba.dubbo.rpc.cluster.Router#route(java.util.List, com.alibaba.dubbo.common.URL, com.alibaba.dubbo.rpc.Invocation)
	 */
	@Override
	public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
		// TODO Auto-generated method stub
		System.out.println("hahaha..... 自由的扩展路由规则吧");
		
		return null;
	}

}
