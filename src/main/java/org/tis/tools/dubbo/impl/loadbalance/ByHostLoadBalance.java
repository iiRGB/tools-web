/**
 * 
 */
package org.tis.tools.dubbo.impl.loadbalance;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.LoadBalance;

/**
 * <pre>
 * 根据服务端主机名（IP:PORT）进行负载
 * 间接实现了调用指定主机的服务
 * 消费者： http://springmvc-ip:port/$appName/log/list/{targetProvider}
 * 其中targetProvider为服务端主机名，满足 IP:PORT格式
 * 如： http://springmvc-ip:port/tis/log/list/192.168.224.102:20883
 * 则会选择 dubbo:192.168.224.102:20883 这个服务提供者执行调用 
 * 
 * 但是必须注意，如果此时指定的目标不存在，则dubbo会将请求负载到默认服务提供者。
 * 如果要实现严格的根据主机名调用服务提供者，必须使用路由能力（见 org.tis.tools.dubbo.impl.router）
 * </pre>
 * @author megapro
 *
 */
//FIXME 正确的做法，应该扩展dubbo的router能力来完成服务路由，如此可实现为一台服务器安装多个业务日志代理服务的能力（实现单独一台BS的日志代理集群）
public class ByHostLoadBalance implements LoadBalance {

	/* (non-Javadoc)
	 * @see com.alibaba.dubbo.rpc.cluster.LoadBalance#select(java.util.List, com.alibaba.dubbo.common.URL, com.alibaba.dubbo.rpc.Invocation)
	 */
	@Override
	public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
		
		String targetProvider = invocation.getArguments()[0].toString() ;
		
		for( Invoker i : invokers ){
			String temp = i.getUrl().getHost() + ":" + i.getUrl().getPort() ;
			if( StringUtils.equals(targetProvider, temp)){
				System.out.println("hahahhahah----找到对应的日志代理服务器:" + i.getUrl().toString());
				return i ; 
			}
		}
		
		throw new RpcException("找不到["+targetProvider+"]对应的远程日志代理服务。请确认是否已经启动！") ; 
	}

}
