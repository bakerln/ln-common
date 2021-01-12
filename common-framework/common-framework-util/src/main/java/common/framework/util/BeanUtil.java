package common.framework.util;

import cn.hutool.core.bean.*;
import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.beans.*;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>Description: Object\Bean工具包，包装hutool工具
 *        包装OBJECT\BEAN工具包，后期方便替换</p>
 *
 * @author linan
 * @date 2021-01-11
 */
public class BeanUtil {
    /**
     * 判断是否为可读的Bean对象，判定方法是：
     *
     * <pre>
     *     1、是否存在只有无参数的getXXX方法或者isXXX方法
     *     2、是否存在public类型的字段
     * </pre>
     *
     * @param clazz 待测试类
     * @return 是否为可读的Bean对象
     * @see #hasGetter(Class)
     * @see #hasPublicField(Class)
     */
    public static boolean isReadableBean(Class<?> clazz) {
        return hasGetter(clazz) || hasPublicField(clazz);
    }

    /**
     * 判断是否为Bean对象，判定方法是：
     *
     * <pre>
     *     1、是否存在只有一个参数的setXXX方法
     *     2、是否存在public类型的字段
     * </pre>
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     * @see #hasSetter(Class)
     * @see #hasPublicField(Class)
     */
    public static boolean isBean(Class<?> clazz) {
        return hasSetter(clazz) || hasPublicField(clazz);
    }

    /**
     * 判断是否有Setter方法<br>
     * 判定方法是是否存在只有一个参数的setXXX方法
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     * @since 4.2.2
     */
    public static boolean hasSetter(Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            final Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length == 1 && method.getName().startsWith("set")) {
                    // 检测包含标准的setXXX方法即视为标准的JavaBean
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为Bean对象<br>
     * 判定方法是是否存在只有一个参数的setXXX方法
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     * @since 4.2.2
     */
    public static boolean hasGetter(Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (Method method : clazz.getMethods()) {
                if (method.getParameterTypes().length == 0) {
                    if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 指定类中是否有public类型字段(static字段除外)
     *
     * @param clazz 待测试类
     * @return 是否有public类型字段
     * @since 5.1.0
     */
    public static boolean hasPublicField(Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (Field field : clazz.getFields()) {
                if (ModifierUtil.isPublic(field) && false == ModifierUtil.isStatic(field)) {
                    //非static的public字段
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 创建动态Bean
     *
     * @param bean 普通Bean或Map
     * @return {@link DynaBean}
     * @since 3.0.7
     */
    public static DynaBean createDynaBean(Object bean) {
        return new DynaBean(bean);
    }

    /**
     * 查找类型转换器 {@link PropertyEditor}
     *
     * @param type 需要转换的目标类型
     * @return {@link PropertyEditor}
     */
    public static PropertyEditor findEditor(Class<?> type) {
        return PropertyEditorManager.findEditor(type);
    }

    /**
     * 获取{@link BeanDesc} Bean描述信息
     *
     * @param clazz Bean类
     * @return {@link BeanDesc}
     * @since 3.1.2
     */
    public static BeanDesc getBeanDesc(Class<?> clazz) {
        return BeanDescCache.INSTANCE.getBeanDesc(clazz, () -> new BeanDesc(clazz));
    }

    /**
     * 遍历Bean的属性
     *
     * @param clazz  Bean类
     * @param action 每个元素的处理类
     * @since 5.4.2
     */
    public static void descForEach(Class<?> clazz, Consumer<? super PropDesc> action) {
        getBeanDesc(clazz).getProps().forEach(action);
    }

    // --------------------------------------------------------------------------------------------------------- PropertyDescriptor

    /**
     * 获得Bean字段描述数组
     *
     * @param clazz Bean类
     * @return 字段描述数组
     * @throws BeanException 获取属性异常
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeanException {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            throw new BeanException(e);
        }
        return ArrayUtil.filter(beanInfo.getPropertyDescriptors(), (Filter<PropertyDescriptor>) t -> {
            // 过滤掉getClass方法
            return false == "class".equals(t.getName());
        });
    }

    /**
     * 获得字段名和字段描述Map，获得的结果会缓存在 {@link BeanInfoCache}中
     *
     * @param clazz      Bean类
     * @param ignoreCase 是否忽略大小写
     * @return 字段名和字段描述Map
     * @throws BeanException 获取属性异常
     */
    public static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws BeanException {
        return BeanInfoCache.INSTANCE.getPropertyDescriptorMap(clazz, ignoreCase, () -> internalGetPropertyDescriptorMap(clazz, ignoreCase));
    }

    /**
     * 获得字段名和字段描述Map。内部使用，直接获取Bean类的PropertyDescriptor
     *
     * @param clazz      Bean类
     * @param ignoreCase 是否忽略大小写
     * @return 字段名和字段描述Map
     * @throws BeanException 获取属性异常
     */
    private static Map<String, PropertyDescriptor> internalGetPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws BeanException {
        final PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
        final Map<String, PropertyDescriptor> map = ignoreCase ? new CaseInsensitiveMap<>(propertyDescriptors.length, 1)
                : new HashMap<>((int) (propertyDescriptors.length), 1);

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            map.put(propertyDescriptor.getName(), propertyDescriptor);
        }
        return map;
    }

    /**
     * 获得Bean类属性描述，大小写敏感
     *
     * @param clazz     Bean类
     * @param fieldName 字段名
     * @return PropertyDescriptor
     * @throws BeanException 获取属性异常
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, final String fieldName) throws BeanException {
        return getPropertyDescriptor(clazz, fieldName, false);
    }

    /**
     * 获得Bean类属性描述
     *
     * @param clazz      Bean类
     * @param fieldName  字段名
     * @param ignoreCase 是否忽略大小写
     * @return PropertyDescriptor
     * @throws BeanException 获取属性异常
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, final String fieldName, boolean ignoreCase) throws BeanException {
        final Map<String, PropertyDescriptor> map = getPropertyDescriptorMap(clazz, ignoreCase);
        return (null == map) ? null : map.get(fieldName);
    }

    /**
     * 获得字段值，通过反射直接获得字段值，并不调用getXXX方法<br>
     * 对象同样支持Map类型，fieldNameOrIndex即为key
     *
     * @param bean             Bean对象
     * @param fieldNameOrIndex 字段名或序号，序号支持负数
     * @return 字段值
     */
    public static Object getFieldValue(Object bean, String fieldNameOrIndex) {
        if (null == bean || null == fieldNameOrIndex) {
            return null;
        }

        if (bean instanceof Map) {
            return ((Map<?, ?>) bean).get(fieldNameOrIndex);
        } else if (bean instanceof Collection) {
            return CollUtil.get((Collection<?>) bean, Integer.parseInt(fieldNameOrIndex));
        } else if (ArrayUtil.isArray(bean)) {
            return ArrayUtil.get(bean, Integer.parseInt(fieldNameOrIndex));
        } else {// 普通Bean对象
            return ReflectUtil.getFieldValue(bean, fieldNameOrIndex);
        }
    }

    /**
     * 设置字段值，，通过反射设置字段值，并不调用setXXX方法<br>
     * 对象同样支持Map类型，fieldNameOrIndex即为key
     *
     * @param bean             Bean
     * @param fieldNameOrIndex 字段名或序号，序号支持负数
     * @param value            值
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void setFieldValue(Object bean, String fieldNameOrIndex, Object value) {
        if (bean instanceof Map) {
            ((Map) bean).put(fieldNameOrIndex, value);
        } else if (bean instanceof List) {
            CollUtil.setOrAppend((List) bean, Convert.toInt(fieldNameOrIndex), value);
        } else if (ArrayUtil.isArray(bean)) {
            ArrayUtil.setOrAppend(bean, Convert.toInt(fieldNameOrIndex), value);
        } else {
            // 普通Bean对象
            ReflectUtil.setFieldValue(bean, fieldNameOrIndex, value);
        }
    }

    /**
     * 解析Bean中的属性值
     *
     * @param <T>        属性值类型
     * @param bean       Bean对象，支持Map、List、Collection、Array
     * @param expression 表达式，例如：person.friend[5].name
     * @return Bean属性值
     * @see BeanPath#get(Object)
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(Object bean, String expression) {
        return (T) BeanPath.create(expression).get(bean);
    }

    /**
     * 解析Bean中的属性值
     *
     * @param bean       Bean对象，支持Map、List、Collection、Array
     * @param expression 表达式，例如：person.friend[5].name
     * @param value      属性值
     * @see BeanPath#get(Object)
     * @since 4.0.6
     */
    public static void setProperty(Object bean, String expression, Object value) {
        BeanPath.create(expression).set(bean, value);
    }

    // --------------------------------------------------------------------------------------------- mapToBean

    /**
     * Map转换为Bean对象
     *
     * @param <T>           Bean类型
     * @param map           {@link Map}
     * @param beanClass     Bean Class
     * @param isIgnoreError 是否忽略注入错误
     * @return Bean
     * @deprecated 请使用 {@link #toBean(Object, Class)} 或 {@link #toBeanIgnoreError(Object, Class)}
     */
    @Deprecated
    public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, boolean isIgnoreError) {
        return fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
    }

    /**
     * Map转换为Bean对象<br>
     * 忽略大小写
     *
     * @param <T>           Bean类型
     * @param map           Map
     * @param beanClass     Bean Class
     * @param isIgnoreError 是否忽略注入错误
     * @return Bean
     * @deprecated 请使用 {@link #toBeanIgnoreCase(Object, Class, boolean)}
     */
    @Deprecated
    public static <T> T mapToBeanIgnoreCase(Map<?, ?> map, Class<T> beanClass, boolean isIgnoreError) {
        return fillBeanWithMapIgnoreCase(map, ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
    }

    /**
     * Map转换为Bean对象
     *
     * @param <T>         Bean类型
     * @param map         {@link Map}
     * @param beanClass   Bean Class
     * @param copyOptions 转Bean选项
     * @return Bean
     * @deprecated 请使用 {@link #toBean(Object, Class, CopyOptions)}
     */
    @Deprecated
    public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, CopyOptions copyOptions) {
        return fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(beanClass), copyOptions);
    }

    /**
     * Map转换为Bean对象
     *
     * @param <T>           Bean类型
     * @param map           {@link Map}
     * @param beanClass     Bean Class
     * @param isToCamelCase 是否将Map中的下划线风格key转换为驼峰风格
     * @param copyOptions   转Bean选项
     * @return Bean
     */
    public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, boolean isToCamelCase, CopyOptions copyOptions) {
        return fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(beanClass), isToCamelCase, copyOptions);
    }

    // --------------------------------------------------------------------------------------------- fillBeanWithMap

    /**
     * 使用Map填充Bean对象
     *
     * @param <T>           Bean类型
     * @param map           Map
     * @param bean          Bean
     * @param isIgnoreError 是否忽略注入错误
     * @return Bean
     */
    public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isIgnoreError) {
        return fillBeanWithMap(map, bean, false, isIgnoreError);
    }

    /**
     * 使用Map填充Bean对象，可配置将下划线转换为驼峰
     *
     * @param <T>           Bean类型
     * @param map           Map
     * @param bean          Bean
     * @param isToCamelCase 是否将下划线模式转换为驼峰模式
     * @param isIgnoreError 是否忽略注入错误
     * @return Bean
     */
    public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isToCamelCase, boolean isIgnoreError) {
        return fillBeanWithMap(map, bean, isToCamelCase, CopyOptions.create().setIgnoreError(isIgnoreError));
    }

    /**
     * 使用Map填充Bean对象，忽略大小写
     *
     * @param <T>           Bean类型
     * @param map           Map
     * @param bean          Bean
     * @param isIgnoreError 是否忽略注入错误
     * @return Bean
     */
    public static <T> T fillBeanWithMapIgnoreCase(Map<?, ?> map, T bean, boolean isIgnoreError) {
        return fillBeanWithMap(map, bean, CopyOptions.create().setIgnoreCase(true).setIgnoreError(isIgnoreError));
    }

    /**
     * 使用Map填充Bean对象
     *
     * @param <T>         Bean类型
     * @param map         Map
     * @param bean        Bean
     * @param copyOptions 属性复制选项 {@link CopyOptions}
     * @return Bean
     */
    public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, CopyOptions copyOptions) {
        return fillBeanWithMap(map, bean, false, copyOptions);
    }

    /**
     * 使用Map填充Bean对象
     *
     * @param <T>           Bean类型
     * @param map           Map
     * @param bean          Bean
     * @param isToCamelCase 是否将Map中的下划线风格key转换为驼峰风格
     * @param copyOptions   属性复制选项 {@link CopyOptions}
     * @return Bean
     * @since 3.3.1
     */
    public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isToCamelCase, CopyOptions copyOptions) {
        if (MapUtil.isEmpty(map)) {
            return bean;
        }
        if (isToCamelCase) {
            map = MapUtil.toCamelCaseMap(map);
        }
        copyProperties(map, bean, copyOptions);
        return bean;
    }

    // --------------------------------------------------------------------------------------------- fillBean

    /**
     * 对象或Map转Bean
     *
     * @param <T>    转换的Bean类型
     * @param source Bean对象或Map
     * @param clazz  目标的Bean类型
     * @return Bean对象
     * @since 4.1.20
     */
    public static <T> T toBean(Object source, Class<T> clazz) {
        return toBean(source, clazz, null);
    }

    /**
     * 对象或Map转Bean，忽略字段转换时发生的异常
     *
     * @param <T>    转换的Bean类型
     * @param source Bean对象或Map
     * @param clazz  目标的Bean类型
     * @return Bean对象
     * @since 5.4.0
     */
    public static <T> T toBeanIgnoreError(Object source, Class<T> clazz) {
        return toBean(source, clazz, CopyOptions.create().setIgnoreError(true));
    }

    /**
     * 对象或Map转Bean，忽略字段转换时发生的异常
     *
     * @param <T>         转换的Bean类型
     * @param source      Bean对象或Map
     * @param clazz       目标的Bean类型
     * @param ignoreError 是否忽略注入错误
     * @return Bean对象
     * @since 5.4.0
     */
    public static <T> T toBeanIgnoreCase(Object source, Class<T> clazz, boolean ignoreError) {
        return toBean(source, clazz,
                CopyOptions.create()
                        .setIgnoreCase(true)
                        .setIgnoreError(ignoreError));
    }

    /**
     * 对象或Map转Bean
     *
     * @param <T>     转换的Bean类型
     * @param source  Bean对象或Map
     * @param clazz   目标的Bean类型
     * @param options 属性拷贝选项
     * @return Bean对象
     * @since 5.2.4
     */
    public static <T> T toBean(Object source, Class<T> clazz, CopyOptions options) {
        final T target = ReflectUtil.newInstanceIfPossible(clazz);
        copyProperties(source, target, options);
        return target;
    }

    /**
     * ServletRequest 参数转Bean
     *
     * @param <T>           Bean类型
     * @param beanClass     Bean Class
     * @param valueProvider 值提供者
     * @param copyOptions   拷贝选项，见 {@link CopyOptions}
     * @return Bean
     */
    public static <T> T toBean(Class<T> beanClass, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
        return fillBean(ReflectUtil.newInstanceIfPossible(beanClass), valueProvider, copyOptions);
    }

    /**
     * 填充Bean的核心方法
     *
     * @param <T>           Bean类型
     * @param bean          Bean
     * @param valueProvider 值提供者
     * @param copyOptions   拷贝选项，见 {@link CopyOptions}
     * @return Bean
     */
    public static <T> T fillBean(T bean, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
        if (null == valueProvider) {
            return bean;
        }

        return BeanCopier.create(valueProvider, bean, copyOptions).copy();
    }

    // --------------------------------------------------------------------------------------------- beanToMap

    /**
     * 对象转Map，不进行驼峰转下划线，不忽略值为空的字段
     *
     * @param bean bean对象
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object bean) {
        return beanToMap(bean, false, false);
    }

    /**
     * 对象转Map
     *
     * @param bean              bean对象
     * @param isToUnderlineCase 是否转换为下划线模式
     * @param ignoreNullValue   是否忽略值为空的字段
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
        return beanToMap(bean, new LinkedHashMap<>(), isToUnderlineCase, ignoreNullValue);
    }

    /**
     * 对象转Map
     *
     * @param bean              bean对象
     * @param targetMap         目标的Map
     * @param isToUnderlineCase 是否转换为下划线模式
     * @param ignoreNullValue   是否忽略值为空的字段
     * @return Map
     * @since 3.2.3
     */
    public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, final boolean isToUnderlineCase, boolean ignoreNullValue) {
        if (bean == null) {
            return null;
        }

        return beanToMap(bean, targetMap, ignoreNullValue, key -> isToUnderlineCase ? StrUtil.toUnderlineCase(key) : key);
    }

    /**
     * 对象转Map<br>
     * 通过实现{@link Editor} 可以自定义字段值，如果这个Editor返回null则忽略这个字段，以便实现：
     *
     * <pre>
     * 1. 字段筛选，可以去除不需要的字段
     * 2. 字段变换，例如实现驼峰转下划线
     * 3. 自定义字段前缀或后缀等等
     * </pre>
     *
     * @param bean            bean对象
     * @param targetMap       目标的Map
     * @param ignoreNullValue 是否忽略值为空的字段
     * @param keyEditor       属性字段（Map的key）编辑器，用于筛选、编辑key，如果这个Editor返回null则忽略这个字段
     * @return Map
     * @since 4.0.5
     */
    public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean ignoreNullValue, Editor<String> keyEditor) {
        if (bean == null) {
            return null;
        }

        return BeanCopier.create(bean, targetMap,
                CopyOptions.create()
                        .setIgnoreNullValue(ignoreNullValue)
                        .setFieldNameEditor(keyEditor)
        ).copy();
    }

    // --------------------------------------------------------------------------------------------- copyProperties

    /**
     * 按照Bean对象属性创建对应的Class对象，并忽略某些属性
     *
     * @param <T>              对象类型
     * @param source           源Bean对象
     * @param tClass           目标Class
     * @param ignoreProperties 不拷贝的的属性列表
     * @return 目标对象
     */
    public static <T> T copyProperties(Object source, Class<T> tClass, String... ignoreProperties) {
        T target = ReflectUtil.newInstanceIfPossible(tClass);
        copyProperties(source, target, CopyOptions.create().setIgnoreProperties(ignoreProperties));
        return target;
    }

    /**
     * 复制Bean对象属性<br>
     * 限制类用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类
     *
     * @param source           源Bean对象
     * @param target           目标Bean对象
     * @param ignoreProperties 不拷贝的的属性列表
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        copyProperties(source, target, CopyOptions.create().setIgnoreProperties(ignoreProperties));
    }

    /**
     * 复制Bean对象属性<br>
     *
     * @param source     源Bean对象
     * @param target     目标Bean对象
     * @param ignoreCase 是否忽略大小写
     */
    public static void copyProperties(Object source, Object target, boolean ignoreCase) {
        BeanCopier.create(source, target, CopyOptions.create().setIgnoreCase(ignoreCase)).copy();
    }

    /**
     * 复制Bean对象属性<br>
     * 限制类用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类
     *
     * @param source      源Bean对象
     * @param target      目标Bean对象
     * @param copyOptions 拷贝选项，见 {@link CopyOptions}
     */
    public static void copyProperties(Object source, Object target, CopyOptions copyOptions) {
        if (null == copyOptions) {
            copyOptions = new CopyOptions();
        }
        BeanCopier.create(source, target, copyOptions).copy();
    }

    /**
     * 给定的Bean的类名是否匹配指定类名字符串<br>
     * 如果isSimple为{@code false}，则只匹配类名而忽略包名，例如：cn.hutool.TestEntity只匹配TestEntity<br>
     * 如果isSimple为{@code true}，则匹配包括包名的全类名，例如：cn.hutool.TestEntity匹配cn.hutool.TestEntity
     *
     * @param bean          Bean
     * @param beanClassName Bean的类名
     * @param isSimple      是否只匹配类名而忽略包名，true表示忽略包名
     * @return 是否匹配
     * @since 4.0.6
     */
    public static boolean isMatchName(Object bean, String beanClassName, boolean isSimple) {
        return ClassUtil.getClassName(bean, isSimple).equals(isSimple ? StrUtil.upperFirst(beanClassName) : beanClassName);
    }

    /**
     * 把Bean里面的String属性做trim操作。此方法直接对传入的Bean做修改。
     * <p>
     * 通常bean直接用来绑定页面的input，用户的输入可能首尾存在空格，通常保存数据库前需要把首尾空格去掉
     *
     * @param <T>          Bean类型
     * @param bean         Bean对象
     * @param ignoreFields 不需要trim的Field名称列表（不区分大小写）
     * @return 处理后的Bean对象
     */
    public static <T> T trimStrFields(T bean, String... ignoreFields) {
        if (bean == null) {
            return null;
        }

        final Field[] fields = ReflectUtil.getFields(bean.getClass());
        for (Field field : fields) {
            if (ModifierUtil.isStatic(field)) {
                continue;
            }
            if (ignoreFields != null && ArrayUtil.containsIgnoreCase(ignoreFields, field.getName())) {
                // 不处理忽略的Fields
                continue;
            }
            if (String.class.equals(field.getType())) {
                // 只有String的Field才处理
                final String val = (String) ReflectUtil.getFieldValue(bean, field);
                if (null != val) {
                    final String trimVal = StrUtil.trim(val);
                    if (false == val.equals(trimVal)) {
                        // Field Value不为null，且首尾有空格才处理
                        ReflectUtil.setFieldValue(bean, field, trimVal);
                    }
                }
            }
        }

        return bean;
    }

    /**
     * 判断Bean是否为非空对象，非空对象表示本身不为{@code null}或者含有非{@code null}属性的对象
     *
     * @param bean             Bean对象
     * @param ignoreFiledNames 忽略检查的字段名
     * @return 是否为空，{@code true} - 空 / {@code false} - 非空
     * @since 5.0.7
     */
    public static boolean isNotEmpty(Object bean, String... ignoreFiledNames) {
        return false == isEmpty(bean, ignoreFiledNames);
    }

    /**
     * 判断Bean是否为空对象，空对象表示本身为{@code null}或者所有属性都为{@code null}<br>
     * 此方法不判断static属性
     *
     * @param bean             Bean对象
     * @param ignoreFiledNames 忽略检查的字段名
     * @return 是否为空，{@code true} - 空 / {@code false} - 非空
     * @since 4.1.10
     */
    public static boolean isEmpty(Object bean, String... ignoreFiledNames) {
        if (null != bean) {
            for (Field field : ReflectUtil.getFields(bean.getClass())) {
                if (ModifierUtil.isStatic(field)) {
                    continue;
                }
                if ((false == ArrayUtil.contains(ignoreFiledNames, field.getName()))
                        && null != ReflectUtil.getFieldValue(bean, field)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断Bean是否包含值为{@code null}的属性<br>
     * 对象本身为{@code null}也返回true
     *
     * @param bean             Bean对象
     * @param ignoreFiledNames 忽略检查的字段名
     * @return 是否包含值为<code>null</code>的属性，{@code true} - 包含 / {@code false} - 不包含
     * @since 4.1.10
     */
    public static boolean hasNullField(Object bean, String... ignoreFiledNames) {
        if (null == bean) {
            return true;
        }
        for (Field field : ReflectUtil.getFields(bean.getClass())) {
            if (ModifierUtil.isStatic(field)) {
                continue;
            }
            if ((false == ArrayUtil.contains(ignoreFiledNames, field.getName()))
                    && null == ReflectUtil.getFieldValue(bean, field)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 拷贝属性
     * @param source 源对象
     * @param target  目标对象
     * @throws BeansException
     */
    public static void copyPropertiesBySpring(Object source, Object target) throws BeansException {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 拷贝属性
     * @param source 源对象
     * @param target  目标对象
     *@param ignoreProperties 忽略属性
     * @throws BeansException
     */
    public static void copyPropertiesBySpring(Object source, Object target, String... ignoreProperties) throws BeansException {
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 比较两个对象是否相等，此方法是 {@link #equal(Object, Object)}的别名方法。<br>
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
     * </ol>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     * @see #equal(Object, Object)
     * @since 5.4.3
     */
    public static boolean equals(Object obj1, Object obj2) {
        return equal(obj1, obj2);
    }

    /**
     * 比较两个对象是否相等。<br>
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
     * </ol>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     * @see Objects#equals(Object, Object)
     */
    public static boolean equal(Object obj1, Object obj2) {
        if (obj1 instanceof BigDecimal && obj2 instanceof BigDecimal) {
            return NumberUtil.equals((BigDecimal) obj1, (BigDecimal) obj2);
        }
        return Objects.equals(obj1, obj2);
    }

    /**
     * 比较两个对象是否不相等。<br>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否不等
     * @since 3.0.7
     */
    public static boolean notEqual(Object obj1, Object obj2) {
        return false == equal(obj1, obj2);
    }


    /**
     * 对象中是否包含元素<br>
     * 支持的对象类型包括：
     * <ul>
     * <li>String</li>
     * <li>Collection</li>
     * <li>Map</li>
     * <li>Iterator</li>
     * <li>Enumeration</li>
     * <li>Array</li>
     * </ul>
     *
     * @param obj     对象
     * @param element 元素
     * @return 是否包含
     */
    public static boolean contains(Object obj, Object element) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            if (element == null) {
                return false;
            }
            return ((String) obj).contains(element.toString());
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).contains(element);
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).containsValue(element);
        }

        if (obj instanceof Iterator) {
            Iterator<?> iter = (Iterator<?>) obj;
            while (iter.hasNext()) {
                Object o = iter.next();
                if (equal(o, element)) {
                    return true;
                }
            }
            return false;
        }
        if (obj instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration<?>) obj;
            while (enumeration.hasMoreElements()) {
                Object o = enumeration.nextElement();
                if (equal(o, element)) {
                    return true;
                }
            }
            return false;
        }
        if (obj.getClass().isArray() == true) {
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                Object o = Array.get(obj, i);
                if (equal(o, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查对象是否为null<br>
     * 判断标准为：
     *
     * <pre>
     * 1. == null
     * 2. equals(null)
     * </pre>
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNull(Object obj) {
        //noinspection ConstantConditions
        return null == obj || obj.equals(null);
    }

    /**
     * 检查对象是否不为null
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNotNull(Object obj) {
        //noinspection ConstantConditions
        return null != obj && false == obj.equals(null);
    }

    /**
     * 判断指定对象是否为空，支持：
     *
     * <pre>
     * 1. CharSequence
     * 2. Map
     * 3. Iterable
     * 4. Iterator
     * 5. Array
     * </pre>
     *
     * @param obj 被判断的对象
     * @return 是否为空，如果类型不支持，返回false
     * @since 4.5.7
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }

        if (obj instanceof CharSequence) {
            return StrUtil.isEmpty((CharSequence) obj);
        } else if (obj instanceof Map) {
            return MapUtil.isEmpty((Map) obj);
        } else if (obj instanceof Iterable) {
            return IterUtil.isEmpty((Iterable) obj);
        } else if (obj instanceof Iterator) {
            return IterUtil.isEmpty((Iterator) obj);
        } else if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.isEmpty(obj);
        }

        return false;
    }

    /**
     * 判断指定对象是否为非空，支持：
     *
     * <pre>
     * 1. CharSequence
     * 2. Map
     * 3. Iterable
     * 4. Iterator
     * 5. Array
     * </pre>
     *
     * @param obj 被判断的对象
     * @return 是否为空，如果类型不支持，返回true
     * @since 4.5.7
     */
    public static boolean isNotEmpty(Object obj) {
        return false == isEmpty(obj);
    }

    /**
     * 如果给定对象为{@code null}返回默认值
     *
     * <pre>
     * BeanUtil.defaultIfNull(null, null)      = null
     * BeanUtil.defaultIfNull(null, "")        = ""
     * BeanUtil.defaultIfNull(null, "zz")      = "zz"
     * BeanUtil.defaultIfNull("abc", *)        = "abc"
     * BeanUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param <T>          对象类型
     * @param object       被检查对象，可能为{@code null}
     * @param defaultValue 被检查对象为{@code null}返回的默认值，可以为{@code null}
     * @return 被检查对象为{@code null}返回默认值，否则返回原值
     * @since 3.0.7
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return (null != object) ? object : defaultValue;
    }


    /**
     * 如果给定对象为{@code null} 返回默认值, 如果不为null 返回自定义handle处理后的返回值
     *
     * @param source       Object 类型对象
     * @param handle       自定义的处理方法
     * @param defaultValue 默认为空的返回值
     * @param <T>          被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
     * @return 处理后的返回值
     * @since 5.4.6
     */
    public static <T> T defaultIfNull(Object source, Supplier<? extends T> handle, final T defaultValue) {
        if (Objects.nonNull(source)) {
            return handle.get();
        }
        return defaultValue;
    }

    /**
     * 如果给定对象为{@code null}或者""返回默认值, 否则返回自定义handle处理后的返回值
     *
     * @param str          String 类型
     * @param handle       自定义的处理方法
     * @param defaultValue 默认为空的返回值
     * @param <T>          被检查对象为{@code null}或者 ""返回默认值，否则返回自定义handle处理后的返回值
     * @return 处理后的返回值
     * @since 5.4.6
     */
    public static <T> T defaultIfEmpty(String str, Supplier<? extends T> handle, final T defaultValue) {
        if (StrUtil.isNotEmpty(str)) {
            return handle.get();
        }
        return defaultValue;
    }

    /**
     * 如果给定对象为{@code null}或者 "" 返回默认值
     *
     * <pre>
     * BeanUtil.defaultIfEmpty(null, null)      = null
     * BeanUtil.defaultIfEmpty(null, "")        = ""
     * BeanUtil.defaultIfEmpty("", "zz")      = "zz"
     * BeanUtil.defaultIfEmpty(" ", "zz")      = " "
     * BeanUtil.defaultIfEmpty("abc", *)        = "abc"
     * </pre>
     *
     * @param <T>          对象类型（必须实现CharSequence接口）
     * @param str          被检查对象，可能为{@code null}
     * @param defaultValue 被检查对象为{@code null}或者 ""返回的默认值，可以为{@code null}或者 ""
     * @return 被检查对象为{@code null}或者 ""返回默认值，否则返回原值
     * @since 5.0.4
     */
    public static <T extends CharSequence> T defaultIfEmpty(final T str, final T defaultValue) {
        return StrUtil.isEmpty(str) ? defaultValue : str;
    }

    /**
     * 如果给定对象为{@code null}或者""或者空白符返回默认值
     *
     * <pre>
     * BeanUtil.defaultIfEmpty(null, null)      = null
     * BeanUtil.defaultIfEmpty(null, "")        = ""
     * BeanUtil.defaultIfEmpty("", "zz")      = "zz"
     * BeanUtil.defaultIfEmpty(" ", "zz")      = "zz"
     * BeanUtil.defaultIfEmpty("abc", *)        = "abc"
     * </pre>
     *
     * @param <T>          对象类型（必须实现CharSequence接口）
     * @param str          被检查对象，可能为{@code null}
     * @param defaultValue 被检查对象为{@code null}或者 ""或者空白符返回的默认值，可以为{@code null}或者 ""或者空白符
     * @return 被检查对象为{@code null}或者 ""或者空白符返回默认值，否则返回原值
     * @since 5.0.4
     */
    public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultValue) {
        return StrUtil.isBlank(str) ? defaultValue : str;
    }

    /**
     * 克隆对象<br>
     * 如果对象实现Cloneable接口，调用其clone方法<br>
     * 如果实现Serializable接口，执行深度克隆<br>
     * 否则返回{@code null}
     *
     * @param <T> 对象类型
     * @param obj 被克隆对象
     * @return 克隆后的对象
     */
    public static <T> T clone(T obj) {
        T result = ArrayUtil.clone(obj);
        if (null == result) {
            if (obj instanceof Cloneable) {
                result = ReflectUtil.invoke(obj, "clone");
            } else {
                result = cloneByStream(obj);
            }
        }
        return result;
    }

    /**
     * 返回克隆后的对象，如果克隆失败，返回原对象
     *
     * @param <T> 对象类型
     * @param obj 对象
     * @return 克隆后或原对象
     */
    public static <T> T cloneIfPossible(final T obj) {
        T clone = null;
        try {
            clone = clone(obj);
        } catch (Exception e) {
            // pass
        }
        return clone == null ? obj : clone;
    }

    /**
     * 序列化后拷贝流的方式克隆<br>
     * 对象必须实现Serializable接口
     *
     * @param <T> 对象类型
     * @param obj 被克隆对象
     * @return 克隆后的对象
     * @throws UtilException IO异常和ClassNotFoundException封装
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneByStream(T obj) {
        if (false == (obj instanceof Serializable)) {
            return null;
        }
        final FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            out.flush();
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
            return (T) in.readObject();
        } catch (Exception e) {
            throw new UtilException(e);
        } finally {
            IoUtil.close(out);
        }
    }

    /**
     * 序列化<br>
     * 对象必须实现Serializable接口
     *
     * @param <T> 对象类型
     * @param obj 要被序列化的对象
     * @return 序列化后的字节码
     */
    public static <T> byte[] serialize(T obj) {
        if (false == (obj instanceof Serializable)) {
            return null;
        }
        final FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
        IoUtil.writeObjects(byteOut, false, (Serializable) obj);
        return byteOut.toByteArray();
    }

    /**
     * 反序列化<br>
     * 对象必须实现Serializable接口
     *
     * <p>
     * 注意！！！ 此方法不会检查反序列化安全，可能存在反序列化漏洞风险！！！
     * </p>
     *
     * @param <T>   对象类型
     * @param bytes 反序列化的字节码
     * @return 反序列化后的对象
     */
    public static <T> T deserialize(byte[] bytes) {
        return IoUtil.readObj(new ByteArrayInputStream(bytes));
    }

    /**
     * 反序列化<br>
     * 对象必须实现Serializable接口
     *
     * @param <T>   对象类型
     * @param bytes 反序列化的字节码
     * @return 反序列化后的对象
     * @see #deserialize(byte[])
     * @deprecated 请使用 {@link #deserialize(byte[])}
     */
    @Deprecated
    public static <T> T unserialize(byte[] bytes) {
        return deserialize(bytes);
    }

    /**
     * 是否为基本类型，包括包装类型和非包装类型
     *
     * @param object 被检查对象
     * @return 是否为基本类型
     * @see ClassUtil#isBasicType(Class)
     */
    public static boolean isBasicType(Object object) {
        return ClassUtil.isBasicType(object.getClass());
    }

    /**
     * 检查是否为有效的数字<br>
     * 检查Double和Float是否为无限大，或者Not a Number<br>
     * 非数字类型和Null将返回true
     *
     * @param obj 被检查类型
     * @return 检查结果，非数字类型和Null将返回true
     */
    public static boolean isValidIfNumber(Object obj) {
        if (obj instanceof Number) {
            return NumberUtil.isValidNumber((Number) obj);
        }
        return true;
    }

    /**
     * {@code null}安全的对象比较，{@code null}对象排在末尾
     *
     * @param <T> 被比较对象类型
     * @param c1  对象1，可以为{@code null}
     * @param c2  对象2，可以为{@code null}
     * @return 比较结果，如果c1 &lt; c2，返回数小于0，c1==c2返回0，c1 &gt; c2 大于0
     * @see java.util.Comparator#compare(Object, Object)
     * @since 3.0.7
     */
    public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
        return CompareUtil.compare(c1, c2);
    }

    /**
     * {@code null}安全的对象比较
     *
     * @param <T>         被比较对象类型
     * @param c1          对象1，可以为{@code null}
     * @param c2          对象2，可以为{@code null}
     * @param nullGreater 当被比较对象为null时是否排在前面
     * @return 比较结果，如果c1 &lt; c2，返回数小于0，c1==c2返回0，c1 &gt; c2 大于0
     * @see java.util.Comparator#compare(Object, Object)
     * @since 3.0.7
     */
    public static <T extends Comparable<? super T>> int compare(T c1, T c2, boolean nullGreater) {
        return CompareUtil.compare(c1, c2, nullGreater);
    }

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param obj 被检查的对象
     * @return {@link Class}
     * @since 3.0.8
     */
    public static Class<?> getTypeArgument(Object obj) {
        return getTypeArgument(obj, 0);
    }

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param obj   被检查的对象
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Class}
     * @since 3.0.8
     */
    public static Class<?> getTypeArgument(Object obj, int index) {
        return ClassUtil.getTypeArgument(obj.getClass(), index);
    }

    /**
     * 将Object转为String<br>
     * 策略为：
     * <pre>
     *  1、null转为"null"
     *  2、调用Convert.toStr(Object)转换
     * </pre>
     *
     * @param obj Bean对象
     * @return Bean所有字段转为Map后的字符串
     * @since 3.2.0
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return StrUtil.NULL;
        }
        if (obj instanceof Map) {
            return obj.toString();
        }

        return Convert.toStr(obj);
    }

    /**
     * 存在多少个{@code null}或空对象，通过{@link cn.hutool.core.util.ObjectUtil#isEmpty(Object)} 判断元素
     *
     * @param objs 被检查的对象,一个或者多个
     * @return 存在{@code null}的数量
     */
    public static int emptyCount(Object... objs) {
        return ArrayUtil.emptyCount(objs);
    }

    /**
     * 是否存在{@code null}对象，通过{@link cn.hutool.core.util.ObjectUtil#isNull(Object)} 判断元素
     *
     * @param objs 被检查对象
     * @return 是否存在
     * @since 5.5.3
     * @see ArrayUtil#hasNull(Object[])
     */
    public static boolean hasNull(Object... objs) {
        return ArrayUtil.hasNull(objs);
    }

    /**
     * 是否存在{@code null}或空对象，通过{@link cn.hutool.core.util.ObjectUtil#isEmpty(Object)} 判断元素
     *
     * @param objs 被检查对象
     * @return 是否存在
     * @see ArrayUtil#hasEmpty(Object...)
     */
    public static boolean hasEmpty(Object... objs) {
        return ArrayUtil.hasEmpty(objs);
    }

    /**
     * 是否全都为{@code null}或空对象，通过{@link cn.hutool.core.util.ObjectUtil#isEmpty(Object)} 判断元素
     *
     * @param objs 被检查的对象,一个或者多个
     * @return 是否都为空
     */
    public static boolean isAllEmpty(Object... objs) {
        return ArrayUtil.isAllEmpty(objs);
    }

    /**
     * 是否全都不为{@code null}或空对象，通过{@link cn.hutool.core.util.ObjectUtil#isEmpty(Object)} 判断元素
     *
     * @param objs 被检查的对象,一个或者多个
     * @return 是否都不为空
     */
    public static boolean isAllNotEmpty(Object... objs) {
        return ArrayUtil.isAllNotEmpty(objs);
    }
}
