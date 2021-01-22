package common.gateway.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Description: SwaggerProvider create and return SwaggerResources object</p>
 *
 * @author linan
 * @date 2020-12-25
 */
@Component
@Profile("dev")
public class SwaggerProvider implements SwaggerResourcesProvider{

    private static final String SWAGGER2URL = "/v2/api-docs";
    private final RouteLocator routeLocator;

    @Value("${spring.application.name}")
    private String self;

    @Autowired
    public SwaggerProvider(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routeHosts = new ArrayList<>();
        routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
                .filter(route -> !self.equals(route.getUri().getHost()))
                .subscribe(route -> routeHosts.add(route.getUri().getHost()));

        Set<String> routeHostsSet = new HashSet<>();
        for (String instance:routeHosts) {
            String url = "/" + instance + SWAGGER2URL;
            if (!routeHostsSet.contains(url)) {
                routeHostsSet.add(url);
                SwaggerResource swaggerResource = swaggerResource(instance,url);
                resources.add(swaggerResource);
            }

        }
        //可手动配置Resource list
        return resources;
    }

    /**
     *  create SwaggerResource object ，2.9.2版本中location为url
     * @param name service name
     * @param location  service json url
     * @return SwaggerResource object
     */
    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("1.0.0");
        return swaggerResource;
    }
}
