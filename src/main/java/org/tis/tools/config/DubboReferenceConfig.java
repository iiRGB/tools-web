package org.tis.tools.config;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tis.tools.rservice.ac.basic.IAcAppRService;
import org.tis.tools.rservice.ac.capable.*;
import org.tis.tools.rservice.jnl.IJnlPrefillRService;
import org.tis.tools.rservice.jnl.IJnlPromotingRService;
import org.tis.tools.rservice.jnl.IJnlRRService;
import org.tis.tools.rservice.log.basic.ILogTxTraceRService;
import org.tis.tools.rservice.log.capable.IOperateLogRService;
import org.tis.tools.rservice.om.capable.*;
import org.tis.tools.rservice.sys.capable.IDictRService;
import org.tis.tools.rservice.sys.capable.IRunConfigRService;
import org.tis.tools.rservice.sys.capable.ISeqnoRService;
import org.tis.tools.service.api.biztrace.IBiztraceRService;
import org.tis.tools.service.api.devmgr.DevMgrRemoteService;
import org.tis.tools.service.api.devmgr.FeatureRegRemoteService;

@Configuration
public class DubboReferenceConfig {

    private static final String GROUP_SYS = "sys";
    private static final String GROUP_AC = "ac";
    private static final String GROUP_OM = "om";
    private static final String GROUP_LOG = "log";
    private static final String GROUP_JNL = "jnl";
    private static final String GROUP_BIZ = "biztrace";
    private static final String GROUP_DEVMGR = "devmgr";

    @Bean
    public ReferenceBean<IDictRService> dictRService() {
        ReferenceBean<IDictRService> ref = new ReferenceBean<>();
        ref.setId("dictRService");
        ref.setInterface(IDictRService.class);
        ref.setGroup(GROUP_SYS);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<ISeqnoRService> seqnoRService() {
        ReferenceBean<ISeqnoRService> ref = new ReferenceBean<>();
        ref.setInterface(ISeqnoRService.class);
        ref.setGroup(GROUP_SYS);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IRunConfigRService> runConfigRService() {
        ReferenceBean<IRunConfigRService> ref = new ReferenceBean<>();
        ref.setInterface(IRunConfigRService.class);
        ref.setGroup(GROUP_SYS);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IAcAppRService> appRService() {
        ReferenceBean<IAcAppRService> ref = new ReferenceBean<>();
        ref.setInterface(IAcAppRService.class);
        ref.setGroup(GROUP_AC);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IApplicationRService> applicationRService() {
        ReferenceBean<IApplicationRService> ref = new ReferenceBean<>();
        ref.setInterface(IApplicationRService.class);
        ref.setGroup(GROUP_AC);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IAuthenticationRService> authenticationRService() {
        ReferenceBean<IAuthenticationRService> ref = new ReferenceBean<>();
        ref.setInterface(IAuthenticationRService.class);
        ref.setGroup(GROUP_AC);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IMenuRService> menuRService() {
        ReferenceBean<IMenuRService> ref = new ReferenceBean<>();
        ref.setInterface(IMenuRService.class);
        ref.setGroup(GROUP_AC);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IOperatorRService> operatorRService() {
        ReferenceBean<IOperatorRService> ref = new ReferenceBean<>();
        ref.setInterface(IOperatorRService.class);
        ref.setGroup(GROUP_AC);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IRoleRService> roleRService() {
        ReferenceBean<IRoleRService> ref = new ReferenceBean<>();
        ref.setInterface(IRoleRService.class);
        ref.setGroup(GROUP_AC);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IEntityRService> entityRService() {
        ReferenceBean<IEntityRService> ref = new ReferenceBean<>();
        ref.setInterface(IEntityRService.class);
        ref.setGroup(GROUP_AC);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IOrgRService> orgRService() {
        ReferenceBean<IOrgRService> ref = new ReferenceBean<>();
        ref.setInterface(IOrgRService.class);
        ref.setGroup(GROUP_OM);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IGroupRService> groupRService() {
        ReferenceBean<IGroupRService> ref = new ReferenceBean<>();
        ref.setInterface(IGroupRService.class);
        ref.setGroup(GROUP_OM);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IPositionRService> positionRService() {
        ReferenceBean<IPositionRService> ref = new ReferenceBean<>();
        ref.setInterface(IPositionRService.class);
        ref.setGroup(GROUP_OM);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IEmployeeRService> employeeRService() {
        ReferenceBean<IEmployeeRService> ref = new ReferenceBean<>();
        ref.setInterface(IEmployeeRService.class);
        ref.setGroup(GROUP_OM);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IBusiOrgRService> busiOrgRService() {
        ReferenceBean<IBusiOrgRService> ref = new ReferenceBean<>();
        ref.setInterface(IBusiOrgRService.class);
        ref.setGroup(GROUP_OM);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IDutyRService> dutyRService() {
        ReferenceBean<IDutyRService> ref = new ReferenceBean<>();
        ref.setInterface(IDutyRService.class);
        ref.setGroup(GROUP_OM);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<IOperateLogRService> operateLogRService() {
        ReferenceBean<IOperateLogRService> ref = new ReferenceBean<>();
        ref.setInterface(IOperateLogRService.class);
        ref.setGroup(GROUP_LOG);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }






    @Bean
    public ReferenceBean<IBiztraceRService> biztraceRService() {
        ReferenceBean<IBiztraceRService> ref = new ReferenceBean<>();
        ref.setInterface(IBiztraceRService.class);
        ref.setGroup(GROUP_BIZ);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<DevMgrRemoteService> devMgrRService() {
        ReferenceBean<DevMgrRemoteService> ref = new ReferenceBean<>();
        ref.setInterface(DevMgrRemoteService.class);
        ref.setGroup(GROUP_DEVMGR);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

    @Bean
    public ReferenceBean<FeatureRegRemoteService> featureRegRService() {
        ReferenceBean<FeatureRegRemoteService> ref = new ReferenceBean<>();
        ref.setInterface(FeatureRegRemoteService.class);
        ref.setGroup(GROUP_DEVMGR);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }




    @Bean
    public ReferenceBean<IJnlPrefillRService> jnlPrefillRService() {
        ReferenceBean<IJnlPrefillRService> ref = new ReferenceBean<>();
        ref.setInterface(IJnlPrefillRService.class);
        ref.setGroup(GROUP_DEVMGR);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }


    @Bean
    public ReferenceBean<IJnlPromotingRService> jnlPromotingRService() {
        ReferenceBean<IJnlPromotingRService> ref = new ReferenceBean<>();
        ref.setInterface(IJnlPromotingRService.class);
        ref.setGroup(GROUP_DEVMGR);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }


    @Bean
    public ReferenceBean<ILogTxTraceRService> logTxTraceRService() {
        ReferenceBean<ILogTxTraceRService> ref = new ReferenceBean<>();
        ref.setInterface(ILogTxTraceRService.class);
        ref.setGroup(GROUP_DEVMGR);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }

//    @Bean
//    public ReferenceBean<IJnlTellerTraceRService> jnlTellerTraceRService() {
//        ReferenceBean<IJnlTellerTraceRService> ref = new ReferenceBean<>();
//        ref.setInterface(IJnlTellerTraceRService.class);
//        ref.setGroup(GROUP_DEVMGR);
//        ref.setVersion("0.9");
//        ref.setCheck(false);
//        return ref;
//    }
    @Bean
    public ReferenceBean<IJnlRRService> jnlRRService() {
        ReferenceBean<IJnlRRService> ref = new ReferenceBean<>();
        ref.setInterface(IJnlRRService.class);
        ref.setGroup(GROUP_DEVMGR);
        ref.setVersion("0.9");
        ref.setCheck(false);
        return ref;
    }
}
