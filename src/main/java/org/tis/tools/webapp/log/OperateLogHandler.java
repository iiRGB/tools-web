package org.tis.tools.webapp.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.tis.tools.base.exception.ToolsRuntimeException;
import org.tis.tools.common.utils.BasicUtil;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.ac.AcOperator;
import org.tis.tools.model.vo.log.LogOperateDetail;
import org.tis.tools.rservice.log.capable.IOperateLogRService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.tis.tools.webapp.util.AjaxUtils.RETMESSAGE;

@Component
@Aspect
public class OperateLogHandler {

    @Autowired
    IOperateLogRService logOperatorRService;

    private final static String CHANGE_DATA = "changeData";
    //日志记录操作延时
    private final int OPERATE_DELAY_TIME = 10;

    //异步操作记录日志的线程池
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JoinPoint point;

    @Pointcut("@annotation(org.tis.tools.webapp.log.OperateLog)")
    public void methodCachePointcut() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestPointcut() {}

    /**
     * 统一处理 LOG4J
     * 进入RequestMapping注解的controller方法前
     */
    @Before("requestPointcut()")
    public void enterController(JoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, Object> inputParamMap = null;
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = point.getArgs();
        String[] paramNames = ((MethodSignature) point.getSignature()).getParameterNames();
        for (int i = 0; i< parameters.length; i++) {
            // 找出使用 @RequestBody 注解参数的请求
            RequestBody reqBodyAnno = parameters[i].getDeclaredAnnotation(RequestBody.class);
            if (paramValues[i] != null) {
                if (reqBodyAnno != null) {
                    inputParamMap = JSON.parseObject(String.valueOf(paramValues[i]));
                    break;
                } else if (!ModelMap.class.isAssignableFrom(paramValues[i].getClass())
                        && !HttpServletRequest.class.isAssignableFrom(paramValues[i].getClass())
                        && !HttpServletResponse.class.isAssignableFrom(paramValues[i].getClass())
                        && !MultipartFile.class.isAssignableFrom(paramValues[i].getClass())) {
                    if (inputParamMap == null) {
                        inputParamMap = new HashMap<>(6);
                    }
                    inputParamMap.put(paramNames[i], String.valueOf(paramValues[i]));
                }
            }
        }
        logger.info(" [请求] Request URI:{}; Request Method:{}; Request Body:{}",
                BasicUtil.wrap(request.getRequestURI(), request.getMethod(), JSON.toJSONString(inputParamMap))) ;

        OperateLog log = method.getAnnotation(OperateLog.class);
        if (log != null) {
            Session session = SecurityUtils.getSubject().getSession();
            AcOperator acOperator = (AcOperator) session.getAttribute("userInfo");
            OperateLogBuilder logBuilder = new OperateLogBuilder();
            logBuilder.start()
                    .setOperateFrom("ABF") // FIXME 从哪设置该值
                    .setUserId(StringUtils.equals(log.operateType(),"login") ? "" : session.getAttribute("userId").toString())
                    .setOperatorName(StringUtils.equals(log.operateType(), "login") ? "" : acOperator.getOperatorName())
                    .setOperateType(log.operateType())
                    .setProcessDesc(log.operateDesc())
                    .setRestfulUrl(request.getPathInfo());
            LogThreadLocal.setLogBuilderLocal(logBuilder);
        }
    }

    @AfterReturning(value = "requestPointcut()", returning = "ret")
    public void exitController(JoinPoint point, Map<String, Object> ret) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String objStr = JSON.toJSONString(ret.get(RETMESSAGE));
        logger.info(" [响应] Request URI:{}; Response Body:{}", BasicUtil.wrap(request.getRequestURI(), objStr)) ;
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        OperateLog logAnt = method.getAnnotation(OperateLog.class);
        if (logAnt != null) {
            LogOperateDetail log = LogThreadLocal.getLogBuilderLocal().getLog();
            log.setOperateResult(JNLConstants.OPERATE_STATUS_SUCCESS);

            // 添加数据变化项（LogAbfChange）到操作日志
            JSONObject reqData = new JSONObject();
            // 从请求参数中获取 changeData
            for(Object arg : point.getArgs()){
                if (arg != null &&  String.class.equals(arg.getClass())) {
                    try{
                        reqData = JSONObject.parseObject(String.valueOf(arg));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if(logAnt.retType() == ReturnType.Object) {
                JSONObject jsonObject = JSONObject.parseObject(objStr);
                JSONObject changeData = reqData.getJSONObject("changeData");
                if(StringUtils.isNotBlank(logAnt.id())) {
                    log.addObj()
                            .setObjGuid(jsonObject.getString(logAnt.id()))
                            .setObjName(StringUtils.isBlank(logAnt.name()) ? null : jsonObject.getString(logAnt.name()))
                            .setObjValue(objStr);
                    for (String key : logAnt.keys()) {
                        log.getObj(0).addKey(key, jsonObject.getString(key));
                    }
                    if (changeData != null) {
                        changeData.keySet().forEach(key ->
                                log.getObj(0).addChangeItem(key, changeData.getString(key)));
                    }
                }

            } else if(logAnt.retType() == ReturnType.List) {
                JSONArray array = JSONObject.parseArray(objStr);
                JSONArray changeData = reqData.getJSONArray("changeData");
                if(StringUtils.isNotBlank(logAnt.id())) {
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        log.addObj().setObjGuid(jsonObject.getString(logAnt.id())).setObjValue(jsonObject.toJSONString());
                        for (String key : logAnt.keys()) {
                            log.getObj(i).addKey(key, jsonObject.getString(key));
                        }
                        int finalI = i;
                        if (changeData != null) {
                            changeData.getJSONObject(i).keySet().forEach(key ->
                                    log.getObj(finalI).addChangeItem(key, changeData.getJSONObject(finalI)
                                            .getString(key)));
                        }
                    }
                }
            }
            saveLogInfo();
        }

    }


    /**
     * controller执行抛出异常完毕后
     *
     * @param point
     */
    @AfterThrowing(value = "requestPointcut()", throwing = "e")
    public void logAfterExecutionThrowException(JoinPoint point, Exception e) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        OperateLogBuilder logBuilder = LogThreadLocal.getLogBuilderLocal();

        if(e instanceof ToolsRuntimeException) {
            logBuilder.getLog().setOperateResult(JNLConstants.OPERATE_STATUS_FAIL);
            logger.warn(" [响应] Request URI:{}; Exception Body:{}",
                    BasicUtil.wrap(request.getRequestURI(), e.getMessage())) ;
        } else {
            logBuilder.getLog().setOperateResult(JNLConstants.OPERATE_STATUS_EXCEPTION);
            logger.error(" [响应] Request URI:" + request.getRequestURI() + "; Exception Body:{}", e) ;
        }
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        OperateLog logAnt = method.getAnnotation(OperateLog.class);
        if (logAnt != null) {
            if (e instanceof ToolsRuntimeException) {
                logBuilder.getLog().setStackTrace(e.getMessage());
                saveLogInfo();
            } else {
                // 获取堆栈String
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                logBuilder.getLog().setStackTrace(sw.toString().toUpperCase());
                saveLogInfo();
                pw.close();
                sw.close();
            }
        }
    }

    private void saveLogInfo() {

        try {
            executor.schedule(bussinessLog(LogThreadLocal.getLogBuilderLocal().getLog())
                    , OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("保存日志出错");
        }
    }

    public TimerTask bussinessLog(LogOperateDetail log) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    logOperatorRService.createOperatorLog(log);
                } catch (Exception e) {
                    logger.error("创建业务日志异常!", e);
                }
            }
        };
    }


}
