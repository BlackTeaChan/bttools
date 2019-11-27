package com.blackteachan.bttools.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Bean工具类
 * @author blackteachan
 * 创建日期：2019-11-18 09:01
 */
public class BtBeanUtil {

    /**
     * Map转为对象类
     * @param map 原始对象
     * @param clazz 实体类
     * @return 充满数据的实体类
     * @throws Exception 抛出异常
     */
    public static Object map2Object(Map<String, Object> map, Class<?> clazz) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = clazz.newInstance();
        List<Field> fieldList = new ArrayList<Field>();
        while(clazz != null){
            //当前类的成员变量
            Field[] thisField = clazz.getDeclaredFields();
            //继续添加成员变量内部的变量到fieldList
            for (Field field : thisField) {
                Class fc = field.getType();
                List<Field> fields = Arrays.asList(fc.getDeclaredFields());
                Object entity = fc.newInstance();
                for (Field tempF : fields) {
                    int mod = tempF.getModifiers();
                    if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                        continue;
                    }
                    tempF.setAccessible(true);
                    tempF.set(entity, map.get(tempF.getName()));
                }
                String className = fc.getSimpleName().substring(0, 1).toLowerCase()
                        + fc.getSimpleName().substring(1);
                map.put(className, entity);
            }
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        for (Field field : fieldList) {
            int mod = field.getModifiers();
            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                continue;
            }
            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }
        return obj;
    }

}
