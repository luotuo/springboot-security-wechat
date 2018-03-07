package com.luotuo.utils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class HttpUtils {

    /**
     *@param request
     *@param Clazz 传入的实体类
     *@function 返回的是格式化后的对象。本函数将request根据传入的类Clazz进行格式化
     *@return dtoObj
     */
    public static Object FormatRequest(HttpServletRequest request, Class Clazz) {
        Object dtoObj = null;
        if ((Clazz == null) || (request == null))
            return dtoObj;
        try {
            //实例化对象
            dtoObj = Clazz.newInstance();
            setDTOValue(request, dtoObj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dtoObj;
    }
    /**
     *@param request
     *@param object 传入的实体对象
     *@function 返回的是格式化后的对象。
     * 本函数将request根据传入的对象object进行格式化，
     * 如果不存在的属性，则不改变原值。适合用于update
     *@return dtoObj
     */
    public static Object FormatRequest(HttpServletRequest request, Object object) {
        if ((object == null) || (request == null))
            return object;
        try {
            setDTOValue(request, object);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return object;
    }
    /**
     * 保存数据
     *@param request
     *@param dto
     *@throws Exception
     */
    public static void setDTOValue(HttpServletRequest request, Object dto) throws Exception {
        if ((dto == null) || (request == null))
            return;
        //得到类中所有的方法 基本上都是set和get方法
        Method[] methods = dto.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            try {
                //方法名
                String methodName = methods[i].getName();
                //方法参数的类型
                Class[] type = methods[i].getParameterTypes();
                //当时set方法时，判断依据：setXxxx类型
                if ((methodName.length() > 3) && (methodName.startsWith("set")) && (type.length == 1)) {
                    //将set后面的大写字母转成小写并截取出来
                    String name = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                    Object objValue = getBindValue(request, name, type[0]);
                    if (objValue != null) {//request中数据不为空时，则赋值
                        Object[] value = { objValue };
                        invokeMothod(dto, methodName, type, value);
                    }
                }
            } catch (Exception ex) {
                throw ex;
            }
        }
    }
    /**
     * 通过request得到相应的值
     *@param request HttpServletRequest
     *@param bindName 属性名
     *@param bindType 属性的类型
     *@return
     */
    public static Object getBindValue(HttpServletRequest request, String bindName, Class bindType) {
        //得到request中的值
        String value = request.getParameter(bindName);
        if (value != null) {
            value = value.trim();//去除值中前后空格
        }
        return getBindValue(value, bindType);
    }
    /**
     * 通过调用方法名（setXxxx）将值设置到属性中
     *@param classObject 实体类对象
     *@param strMethodName 方法名(一般都是setXxxx)
     *@param argsType 属性类型数组
     *@param args 属性值数组
     *@return
     *@throws NoSuchMethodException
     *@throws SecurityException
     *@throws IllegalAccessException
     *@throws IllegalArgumentException
     *@throws InvocationTargetException
     */
    public static Object invokeMothod(Object classObject, String strMethodName, Class[] argsType, Object[] args)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        //得到classObject这个类的方法
        Method concatMethod = classObject.getClass().getMethod(strMethodName, argsType);
        //调用方法将classObject赋值到相应的属性
        return concatMethod.invoke(classObject, args);
    }
    /**
     * 根据bindType类型的不同转成相应的类型值
     *@param value String类型的值，要根据bindType类型的不同转成相应的类型值
     *@param bindType 属性的类型
     *@return
     */
    public static Object getBindValue(String value, Class bindType) {
        if (value == null)
            return null;
        String typeName = bindType.getName();
        if (value.trim().length() == 0) {
            if (typeName.equals("java.lang.String")) return value;
            else return null;
        }

        //依次判断各种类型并转换相应的值
        if (typeName.equals("java.lang.String"))
            return value;
        //如果前端引入的有引号
        value=value.replace("\"","");
        value=value.replace("\'","");
        if (typeName.equals("int"))
            return new Integer(Integer.valueOf(value));
        if (typeName.equals("long"))
            return new Long(Long.valueOf(value));
        if (typeName.equals("boolean"))
            return new Boolean(value);
        if (typeName.equals("float"))
            return new Float(value);
        if (typeName.equals("double"))
            return new Double(value);
        if (typeName.equals("java.math.BigDecimal")) {
            if ("NaN.00".equals(value))
                return new BigDecimal("0");
            return new BigDecimal(value.trim());
        }
        if (typeName.equals("java.util.Date"))
            return Format.formatDate(value);
        if (typeName.equals("java.lang.Integer"))
            return new Integer(Integer.valueOf(value));
        if (typeName.equals("java.lang.Long")) {
            return new Long(Long.valueOf(value));
        }
        if (typeName.equals("java.lang.Boolean")) {
            return new Boolean(Boolean.valueOf(value));
        }
        return value;
    }
}