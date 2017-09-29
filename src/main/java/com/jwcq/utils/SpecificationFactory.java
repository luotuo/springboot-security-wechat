package com.jwcq.utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import java.util.Collection;
import java.util.Date;

/**
 * Created by liuma on 2017/7/24.
 * 支持jdk1.8即以上lambda
 */
public final  class SpecificationFactory {

    /**
     * 模糊查询，匹配对应字段
     * @param attribute
     * @param value
     * @return
     */
    public static Specification containsLike(String attribute, String value) {
        return (root, query, cb) -> cb.like(root.get(attribute), "%" + value + "%");
    }
    /**
     * 某字段的值等于value的查询条件
     * @param attribute
     * @param value
     * @return
     */
    public static Specification equal(String attribute, Object value) {
        return (root, query, cb) -> cb.equal(root.get(attribute),value);
    }

    /**
     *获取对应属性比输入值大的集合涉及int值、long值、double值
     * @param attribute
     * @param value
     * @return
     * */
    public static Specification greater(String attribute, Long value) {
        return (root, query, cb) -> cb.gt(root.get(attribute),value);
    }
    public static Specification greater(String attribute, long value) {
        return (root, query, cb) -> cb.gt(root.get(attribute),value);
    }
    public static Specification greater(String attribute, Integer value) {
        return (root, query, cb) -> cb.gt(root.get(attribute),value);
    }
    public static Specification greater(String attribute, int value) {
        return (root, query, cb) -> cb.gt(root.get(attribute),value);
    }
    public static Specification greater(String attribute, Double value) {
        return (root, query, cb) -> cb.gt(root.get(attribute),value);
    }
    public static Specification greater(String attribute, double value) {
        return (root, query, cb) -> cb.gt(root.get(attribute),value);
    }
     /**
     *获取对应属性比输入值大的集合涉及int值、long值、double值、Date值
     * @param attribute
     * @param value
     * @return
     * */

    /**
     * 获取对应属性的值所在区间,涉及int值、long值、double值、Date值
     * @param attribute
     * @param min
     * @param max
     * @return
     */
    public static Specification isBetween(String attribute, Long min, Long max) {
        return (root, query, cb) -> cb.between(root.get(attribute), min, max);
    }
    public static Specification isBetween(String attribute, long min, long max) {
        return (root, query, cb) -> cb.between(root.get(attribute), min, max);
    }
    public static Specification isBetween(String attribute, int min, int max) {
        return (root, query, cb) -> cb.between(root.get(attribute), min, max);
    }
    public static Specification isBetween(String attribute, Integer min, Integer max) {
        return (root, query, cb) -> cb.between(root.get(attribute), min, max);
    }
    public static Specification isBetween(String attribute, double min, double max) {
        return (root, query, cb) -> cb.between(root.get(attribute), min, max);
    }
    public static Specification isBetween(String attribute, Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get(attribute), min, max);
    }
    public static Specification isBetween(String attribute, Date min, Date max) {
        return (root, query, cb) -> cb.between(root.get(attribute), min, max);
    }
    /**
     * 通过属性名和集合实现in查询
     * @param attribute
     * @param c
     * @return
     */
    public static Specification in(String attribute, Collection c) {
        return (root, query, cb) -> root.get(attribute).in(c);
    }
}
