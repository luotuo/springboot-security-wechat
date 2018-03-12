package com.luotuo.service;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by luotuo on 17-6-7.
 */
@Service
public class NativeSqlServiceImpl {
    @PersistenceUnit
    private EntityManagerFactory emf;

    /**
     * 查询多个属性
     * 返回List<Object[]>数组形式的List，数组中内容按照查询字段先后
     * @param sql   原生SQL语句
     * @return
     */
    public List<Object[]> sqlArrayList(String sql){
        EntityManager em=emf.createEntityManager();
        Query query=em.createNativeQuery(sql);
        List<Object[]> list = query.getResultList();
        em.close();
        return  list;
    }

    /**
     * 查询多个属性
     * 返回List<Object>对象形式的List，Object为Class格式对象
     * @param sql   原生SQL语句
     * @param obj   Class格式对象
     * @return
     */
    public List sqlObjectList(String sql, Object obj){
        EntityManager em=emf.createEntityManager();
        Query query=em.createNativeQuery(sql,obj.getClass());
        List list = query.getResultList();
        em.close();
        return  list;
    }

    /**
     * 查询单个属性
     * 返回List<Object>对象形式的List，Object为对象数据类型
     * @param sql  原生SQL语句
     * @return
     */
    public List sqlSingleList(String sql){
        EntityManager em=emf.createEntityManager();
        Query query=em.createNativeQuery(sql);
        List list = query.getResultList();
        em.close();
        return  list;
    }
}