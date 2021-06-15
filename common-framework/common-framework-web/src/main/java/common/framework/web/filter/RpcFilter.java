package common.framework.web.filter;

import common.framework.util.HttpUtil;
import common.framework.util.JsonUtil;
import common.framework.wrapper.ResultWrapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * <p>Description:
 * 通过filter实现对rpc请求的过滤，不经过controller直接到达service的实现类上，支持其他模块引用本模块rpcService实现自动穿透调用
 * 其他模块在调用这些rpcservice的时候，会根据本地是否有实现，优先用本地实现，如果本地没有，则通过远程方式
 * WebFilter这个注解要起作用，必须在springBoot启动类上加 @ServletComponentScan(basePackages = {"xxx"})
 * 要注意这个filter和shiro的互相影响
 * 1: 不允许方法重载
 * 2:参数必须添加注解，注解支持 @RequestParam、@RequestBody
 * </p>
 *
 * @author linan
 * @date 2021-06-03
 */

@Order(-1)
@Component
@ConditionalOnProperty(value = "service.rpc.filter",havingValue = "true",matchIfMissing = true)
public class RpcFilter implements Filter {

    static Logger logger = LoggerFactory.getLogger(RpcFilter.class);

    static final String rpc_url_pre= "/rpc";

    static final String return_type_void = "void";

    /**
     * 每个rpc接口，springCloud已经自动生成了一个代理，所以这里要过滤掉这个自动生成的代理类
     */
    static final String Bean_Proxy = "com.sun.proxy.$Proxy";

    /**
     * 输出格式及字符编码
     */
    static String contentType = "application/json;charset=UTF-8";

    /**
     * 返回数据格式类型map
     */
    static Map<Class, Function<String, Object>> typeFunctionMap = new HashMap();
    /**
     * service名字和spring中bean的对应关系,key为service接口全路径
     */
    static Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    /**
     * url和方法的对应关系,key为url
     */
    static Map<String, Method> methodMap = new ConcurrentHashMap<>();

    static {
        typeFunctionMap.put(String.class, (value) -> {
            return value;
        });
        typeFunctionMap.put(Integer.class, Integer::parseInt);
        typeFunctionMap.put(Float.class, Float::parseFloat);
        typeFunctionMap.put(Double.class, Double::parseDouble);
        typeFunctionMap.put(Long.class, Long::parseLong);
        typeFunctionMap.put(Boolean.class, Boolean::parseBoolean);
    }

    @Autowired
    DefaultListableBeanFactory defaultListableBeanFactory;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String url = ((HttpServletRequest) request).getRequestURI();
        logger.info("request.url={}", url);
        if (!url.startsWith(rpc_url_pre)) {
            chain.doFilter(request, response);
            return;
        }
        //内部rpc调用，任何情况下，都要返回一个ResultWrapper
        String[] serviceNameAndMethod = url.replace(rpc_url_pre, StringUtils.EMPTY).split("/");
        logger.info("serviceNameAndMethod={}::{}", serviceNameAndMethod[1], serviceNameAndMethod[2]);
        response.setContentType(contentType);
        PrintWriter printWriter = response.getWriter();

        try{
            /**
             *   获取对象类及方法
             */
            Object serviceBean = serviceMap.get(serviceNameAndMethod[1]);
            Method targetMethod = methodMap.get(url);
            //从缓存里如果没有取到，则取一次
            if (targetMethod == null) {
                Class serviceClass = Class.forName(serviceNameAndMethod[1]);
                Map<String, Object> beanMap = defaultListableBeanFactory.getBeansOfType(serviceClass);
                AtomicReference<Object> service = new AtomicReference<>();
                for (Map.Entry entry:  beanMap.entrySet()) {
                    logger.error(entry.getValue().getClass().getName());
                    //SpringCloud已经自动生成了一个代理，所以这里要过滤掉这个自动生成的代理类
                    if (!entry.getValue().getClass().getName().contains(Bean_Proxy)) {
                        service.set(entry.getValue());
                    }
                }
                serviceBean = service.get();
                if (serviceBean == null) {
                    logger.error("没有找到class={}", serviceNameAndMethod[1]);
                    printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("没有找到class=" + serviceNameAndMethod[1])));
                    return;
                }
                Method[] methods = serviceClass.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getName().equals(serviceNameAndMethod[2])) {
                        targetMethod = methods[i];
                        break;
                    }
                }
                if (targetMethod == null) {
                    logger.error("没有找到方法:{}.{}", serviceNameAndMethod[1], serviceNameAndMethod[2]);
                    printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("没有找到方法:" + serviceNameAndMethod[1] + "." + serviceNameAndMethod[2])));
                    return;
                }
                //放入map缓存
                serviceMap.put(serviceNameAndMethod[1], serviceBean);
                methodMap.put(url, targetMethod);
            }

            /**
             *   获取请求参数
             */
            //参数类型
            Class[] parameterTypes = targetMethod.getParameterTypes();
            //参数的枚举
            Type[] genericParameterTypes = targetMethod.getGenericParameterTypes();
            //参数的注解
            Annotation[][] parameterAnnotations = targetMethod.getParameterAnnotations();

            //读取request的参数信息
            Map<String, String[]> parameterMap = request.getParameterMap();
            //读body体里的内容
            String bodyParam = HttpUtil.read(request.getReader());

            //建一个空数组，用来存参数，反射调用方法的时候使用的参数
            Object[] parameters4Method = new Object[parameterTypes.length];

            /**
             *  处理请求参数
             */
            for (int index = 0; index < parameterAnnotations.length; index++) {
                //判断是否为requestBody
                boolean useRequestBody = false;
                String name = null;
                Annotation[] parameterAnnotation4OneParam = parameterAnnotations[index];
                for (Annotation annotation : parameterAnnotation4OneParam) {
                    if (annotation.annotationType().isAssignableFrom(RequestParam.class)) {
                        RequestParam requestParam = (RequestParam) annotation;
                        name = requestParam.value();
                        break;
                    } else if (annotation.annotationType().isAssignableFrom(RequestBody.class)) {
                        useRequestBody = true;
                        break;
                    }
                }
                //接口参数无注解，即如果没有参数名称，也不是requestBody，则接口定义有问题，无法正确处理
                if (name == null && !useRequestBody) {
                    logger.error("参数至少包含一个注解RequestParam或RequestBody:{}", targetMethod.getName());
                    printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("参数至少包含一个注解RequestParam或RequestBody:" + targetMethod.getName())));
                    return;
                }
                //参数值
                String param;
                if (useRequestBody){
                    param = bodyParam;
                }else{
                    param =  parameterMap.get(name)==null? null:parameterMap.get(name)[0];
                }
                Class paramClassType = parameterTypes[index];
                Function<String, Object> dataChangeTypeFunction = typeFunctionMap.get(paramClassType);

                /**
                 * 参数类型：普通参数类型
                 */
                if (dataChangeTypeFunction != null) {
                    parameters4Method[index] = dataChangeTypeFunction.apply(param);
                    continue;
                }
                /**
                 * 参数类型：List类型
                 */
                if (List.class.isAssignableFrom(paramClassType)){
                    //List没有指定泛型类型 无法解析则直接报错
                    if (Class.class.isAssignableFrom(genericParameterTypes[index].getClass())) {
                        logger.error("List类型参数没有指定泛型类型,{},{}", serviceNameAndMethod[1], serviceNameAndMethod[2]);
                        printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("List类型参数没有指定泛型类型:" + serviceNameAndMethod[1] + "." + serviceNameAndMethod[2])));
                        return;
                    }
                    List paramList = this.doList(genericParameterTypes, param, index);
                    parameters4Method[index] = paramList;
                    continue;
                }
                /**
                 * 参数类型：Map
                 */
                if (Map.class.isAssignableFrom(paramClassType)) {
                    if (useRequestBody) {
                        Map paramMap = JsonUtil.toBean(param, genericParameterTypes[index],false);
                        parameters4Method[index] = paramMap;
                    } else {
                        logger.error("Map类型参数只支持reqestBody,{}.{}", serviceNameAndMethod[1], serviceNameAndMethod[2]);
                        printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("Map类型参数只支持reqestBody," + serviceNameAndMethod[0] + "." + serviceNameAndMethod[1])));
                        return;
                    }
                    continue;
                }
                /**
                 * 参数类型：Set
                 */
                if (Set.class.isAssignableFrom(paramClassType)) {
                    //List没有指定泛型类型 无法解析则直接报错
                    if (Class.class.isAssignableFrom(genericParameterTypes[index].getClass())) {
                        logger.error("Set类型参数没有指定泛型类型,{},{}", serviceNameAndMethod[0], serviceNameAndMethod[1]);
                        printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("Set类型参数没有指定泛型类型:" + serviceNameAndMethod[0] + "." + serviceNameAndMethod[1])));
                        return;
                    }
                    //先转成List然后把List转成Set
                    List paramList = this.doList(genericParameterTypes, param, index);
                    parameters4Method[index] = new HashSet<>(paramList);
                    continue;
                }
                /**
                 * 参数类型：JSON
                 */
                if (param.startsWith("{")){
                    //内容包含json的认为是对象，从json反序列化对象
                    parameters4Method[index] = JsonUtil.toBean(param, parameterTypes[index]);
                    continue;
                }

                logger.error("没有找到类型转换器,{}", parameterTypes[index].getName());
                printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("没有找到类型转换器," + parameterTypes[index].getName())));
                return;
            }

            /**
             *   请求RPC方法
             */
            Object result = targetMethod.invoke(serviceBean, parameters4Method);
            Class returnType = targetMethod.getReturnType();
            //返回类型为void，则返回空json，联调的时候要留意测试一下
            if (return_type_void.equals(returnType.getName())) {
                printWriter.write("{}");
            } else {
                printWriter.write(JsonUtil.toJsonStr(result));
            }

        }catch (ClassNotFoundException e){
            logger.error("没有找到class={}", serviceNameAndMethod[0], e);
            printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("PRC没有找到对应class")));
        } catch (IllegalAccessException e) {
            logger.error("方法签名对不上method={},class={}", serviceNameAndMethod[1], serviceNameAndMethod[0], e);
            printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("RPC方法签名对不上")));
        } catch (InvocationTargetException e) {
            logger.error("方法内部异常method={},class={}", serviceNameAndMethod[1], serviceNameAndMethod[0], e);
            printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("RPC方法内部异常")));
        }  catch (BeanCreationException e) {
            logger.error("方法参数异常method={},class={}", serviceNameAndMethod[1], serviceNameAndMethod[0], e);
            printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("RPC方法参数容器绑定异常")));
        }  catch (ClassCastException e) {
            logger.error("方法参数异常method={},class={}", serviceNameAndMethod[1], serviceNameAndMethod[0], e);
            printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("RPC方法参数容器绑定异常")));
        }  catch (Exception e) {
            logger.error("发生异常,url={},message={}", url, e);
            printWriter.write(JsonUtil.toJsonStr(ResultWrapperUtil.serverError("RPC接口异常")));
        } finally {
            printWriter.flush();
            printWriter.close();
            return;
        }
    }

    private List doList(Type[] genericParameterTypes, String param, int index) {
        ParameterizedType parameterizedType = (ParameterizedType) genericParameterTypes[index];
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        List paramList  = JsonUtil.toList(param, (Class) actualTypeArguments[0]);
        return paramList;
    }


}
