package common.framework.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 基础DAO，用于封装dao操作
 *
 * @param <T> 数据库泛型参数
 * @param <S> 请求泛型参数
 */
@Component
@ConditionalOnProperty("spring.datasource")
public class BaseDao<T, S> {
    /**
     * 注入jdbctemp
     */
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * 获取  NamedParameterJdbcTemplate
     *
     * @return NamedParameterJdbcTemplate
     */
    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    /**
     * 新增
     *
     * @param sql sql
     * @param t   新增对象
     * @return 新增记录值
     */
    protected int insert(String sql, T t) {
        return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(t));
    }

    protected int[] bachInsert(String sql, List<T> list) {
        return getNamedParameterJdbcTemplate().batchUpdate(sql, SqlParameterSourceUtils.createBatch(list));
    }

    /**
     * 新增并返回主键
     *
     * @param sql     sql
     * @param t       新增对象
     * @param pkField 主键字段
     * @return 新增记录值
     */
    protected int insertForId(String sql, T t, String pkField) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rc = getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(t), keyHolder, new String[]{pkField});
        if (rc > 0) {
            return keyHolder.getKey().intValue();
        } else {
            return 0;
        }
    }

    /**
     * 修改，参数为model类
     *
     * @param sql sql
     * @param t   修改对象
     * @return 修改记录值
     */
    protected int update(String sql, T t) {
        return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(t));
    }

    /**
     * 按照参数修改
     *
     * @param sql     sql
     * @param objects 多参数
     * @return 修改记录值
     */
    protected int update(String sql, Object... objects) {
        return jdbcTemplate.update(sql, objects);
    }

    /**
     * 逻辑删除
     *
     * @param sql     sql
     * @param objects 多参数
     * @return 删除记录值
     */
    protected int delete(String sql, Object... objects) {
        return jdbcTemplate.update(sql, objects);
    }

    /**
     * 根据参数获取model
     *
     * @param sql     sql
     * @param objects 多参数
     * @return 返回对象
     */
    protected T get(String sql, Object... objects) {
        List<T> list = jdbcTemplate.query(sql, objects, BeanPropertyRowMapper.newInstance(getClazz()));
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据查询条件获取列表
     *
     * @param sql sql
     * @param s   对象
     * @return 列表
     */
    protected T get(String sql, S s) {
        List<T> list = getNamedParameterJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(s), BeanPropertyRowMapper.newInstance(getClazz()));
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 直接根据sql获取列表
     *
     * @param sql sql
     * @return 列表
     */
    protected List<T> list(String sql) {
        List<T> list = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(getClazz()));
        return list;
    }

    /**
     * 根据sql和多参数获取列表
     *
     * @param sql     列表
     * @param objects 多参数
     * @return 列表
     */
    protected List<T> list(String sql, Object... objects) {
        List<T> list = jdbcTemplate.query(sql, objects, BeanPropertyRowMapper.newInstance(getClazz()));
        return list;
    }

    /**
     * 根据查询条件获取列表
     *
     * @param sql sql
     * @param s   对象
     * @return 列表
     */
    protected List<T> list(String sql, S s) {
        List<T> list = getNamedParameterJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(s), BeanPropertyRowMapper.newInstance(getClazz()));
        return list;
    }

    /**
     * 查询总数，无参数
     *
     * @param sql sql
     * @return 总数
     */
    protected int count(String sql) {
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * 查询总数，带参数
     *
     * @param sql     sql
     * @param objects 多参数
     * @return 总数
     */
    protected int count(String sql, Object... objects) {
        return jdbcTemplate.queryForObject(sql, objects, Integer.class);
    }

    /**
     * 查询总数，带查询对象
     *
     * @param sql sql
     * @param s   对象
     * @return 总数
     */
    protected int count(String sql, S s) {
        return getNamedParameterJdbcTemplate().queryForObject(sql, new BeanPropertySqlParameterSource(s), Integer.class);
    }

    /**
     * 取得当前泛型的实际class名
     *
     * @return 类名
     */
    private Class<T> getClazz() {
        return ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * 根据查询条件获取列表
     *
     * @param sql   sql
     * @param s     对象
     * @param clazz DTO对象
     * @return 列表
     */
    protected List listDTO(String sql, S s, Class clazz) {
        List list = getNamedParameterJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(s), BeanPropertyRowMapper.newInstance(clazz));
        return list;
    }

    /**
     * 根据查询条件获取对象
     *
     * @param sql   sql
     * @param s     对象
     * @param clazz DTO对象
     * @return 列表
     */
    protected Object getDTO(String sql, S s, Class clazz) {
        List list = getNamedParameterJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(s), BeanPropertyRowMapper.newInstance(clazz));
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
