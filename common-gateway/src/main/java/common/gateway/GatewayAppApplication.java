package common.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>Description:  网关启动</p>
 *
 * @author linan
 * @date 2020-11-03
 */
@EnableEurekaClient
@SpringBootApplication
public class GatewayAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayAppApplication.class, args);
    }

}
