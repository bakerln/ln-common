package common.framework.web.healthcheck;

import common.framework.web.config.ServiceStatusConfig;
import common.framework.wrapper.ResultWrapper;
import common.framework.wrapper.ResultWrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: 服务状态修改 ACTUATOR</p>
 *
 * @author linan
 * @date 2020-11-04
 */
@RestController
@RequestMapping(value = "/healthcheck")
public class MyHealthCheck {


    @Autowired
    private ServiceStatusConfig serviceStatusConfig;

    @PostMapping("/status/update")
    public ResultWrapper update(@RequestBody String code) {
        serviceStatusConfig.setCode(code);
        return ResultWrapperUtil.success();
    }
}
