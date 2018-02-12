package org.tis.tools.webapp;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 获取项目全局配置信息实例
 * 继承于spring的PropertyPlaceholderConfigurer，在资源加载同时暴露出所有的key-value信息接口
 * @author zhangsu
 *
 */
public class CustomizedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	//存放全局的key-value信息
    private static Map<String, String> ctxPropertiesMap;
  
    @Override  
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,  
            Properties props)throws BeansException {  

    	super.processProperties(beanFactory, props);
        
        //加载配置文件信息到实例map中 
        ctxPropertiesMap = new HashMap<String, String>();  
        for (Object key : props.keySet()) {  
            String keyStr = key.toString();  
            String value = props.getProperty(keyStr);  
            ctxPropertiesMap.put(keyStr, value);  
        }
    }  
  
    //静态全局方法访问项目全局变量
    public static String getContextProperty(String name) {  
        return ctxPropertiesMap.get(name);  
    }  
}  
