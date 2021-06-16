package common.framework.web.config;

import common.framework.global.ConfigCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * <p>Description:  Actuator 服务状态更新配置</p>
 *
 * @author linan
 * @date 2020-11-26
 */
@Component
public class ServiceStatusConfig implements HealthIndicator {

    private String code;

    public ServiceStatusConfig(@Value("${management.server.status}") String code){
        this.code = code;
    }

    @Override
    public Health health() {
        if (ConfigCode.SERVICE_STATUS_DOWN.getCode().equals(code)){
            return new Health.Builder().down().withDetail("error","handle error client is down").build();
        }else if (ConfigCode.SERVICE_STATUS_OUTOFSERVICE.getCode().equals(code)){
            return new Health.Builder().outOfService().withDetail("error","out of service").build();
        }else {
            return new Health.Builder().up().build();
        }
    }

    //update Server status
    public void setCode(String code) {
        this.code = code;
    }

}
