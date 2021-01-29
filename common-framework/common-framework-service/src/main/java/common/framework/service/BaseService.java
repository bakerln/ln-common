//package common.framework.service;
//
//import common.framework.pojo.entity.BaseEntity;
//import common.framework.wrapper.ResultWrapper;
//import org.springframework.data.repository.NoRepositoryBean;
//
//import java.util.List;
//
///**
// * <p>Description:  公共JPA接口</p>
// * @author linan
// * @date 2020-07-23 16:24
// */
//@NoRepositoryBean
//public interface BaseService <T extends BaseEntity> {
//
//    ResultWrapper existsById(Integer var1);
//
//    ResultWrapper beforeSave(T var1);
//
//    ResultWrapper save(T var1);
//
//    ResultWrapper afterSave(T var1);
//
//    ResultWrapper beforeDelete(Integer id);
//
//    ResultWrapper deleteById(Integer var1);
//
//    ResultWrapper beforeModify(T var1);
//
//    ResultWrapper modify(Integer var1, T var2);
//
//    ResultWrapper afterModify(T var1);
//
//    ResultWrapper<T> findById(Integer var1);
//
//    ResultWrapper beforeFind(T var1);
//
//    ResultWrapper<T> findOne(T var1);
//
//    ResultWrapper<List<T>> findListAll();
//
//    ResultWrapper afterFind(T var1);
//}
