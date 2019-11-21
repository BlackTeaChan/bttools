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
