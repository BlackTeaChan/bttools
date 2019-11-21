package com.blackteachan.bttools.dao;

import com.blackteachan.bttools.utils.BtBeanUtil;
import com.blackteachan.bttools.utils.BtDbUtil;
import com.blackteachan.bttools.utils.BtLog;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 基础Dao
 * @author blackteachan
 * 创建日期：2019-11-18 09:05
 */
public abstract class BtBaseDao {

    /**
     * 日志
     */
    private BtLog log = new BtLog();

    /**
     * 执行SELECT，查询单个
     * @param clazz 要转换成的实体类
     * @param sql SQL语句
     * @param params 参数（数组）
     * @return 返回查询到的已封装好的数据
     */
    public Object get(Class<?> clazz, String sql, Object ...params){
        long time = System.currentTimeMillis();
        BtDbUtil btDbUtil = BtDbUtil.getInstance();
        btDbUtil.getConnection();

        List<Object> list = params != null ? Arrays.asList(params) : null;
        try {
            Map result = btDbUtil.findSimpleResult(sql, list);
            return clazz != null ? BtBeanUtil.map2Object(result, clazz) : result;
        }catch (Exception e){
            log.debug(e.getMessage());
            return null;
        }finally {
            btDbUtil.releaseConn();
            log.debug("BtBaseDao.get() - " + (System.currentTimeMillis() - time) + "ms");
            log.debug("    SQL - " + sql);
            log.debug("    PAM - " + list);
        }
    }

    /**
     * 执行SELECT，查询单个
     * @param sql SQL语句
     * @param params 参数（数组）
     * @return 返回查询到的数据
     */
    public Object get(String sql, Object ...params){
        return get(null, sql, params);
    }

    /**
     * 执行SELECT，查询多个
     * @param clazz 要转换的实体类
     * @param sql SQL语句
     * @param params 参数（数组）
     * @return 返回查询到的已封装好的数据
     */
    public List getList(Class<?> clazz, String sql, Object ...params){
        long time = System.currentTimeMillis();

        BtDbUtil btDbUtil = BtDbUtil.getInstance();
        btDbUtil.getConnection();

        List<Object> list = params != null ? Arrays.asList(params) : null;
        try {
            List<Map<String, Object>> results = btDbUtil.findModeResult(sql, list);
            if(clazz == null) {
                return results;
            }
            List result = new ArrayList();
            for(Map<String, Object> map : results){
                result.add(BtBeanUtil.map2Object(map, clazz));
            }
            return result;
        }catch (Exception e){
            log.debug(e.getMessage());
            return null;
        }finally {
            log.debug("BtBaseDao.getList() - " + (System.currentTimeMillis() - time) + "ms");
            log.debug("    SQL - " + sql);
            log.debug("    PAM - " + list);
            btDbUtil.releaseConn();
        }
    }

    /**
     * 执行SELECT，查询多个
     * @param sql SQL语句
     * @param params 参数（数组）
     * @return 返回查询到的数据
     */
    public List getList(String sql, Object ...params){
        return getList(null, sql, params);
    }

    /**
     * 执行INSERT、UPDATE、DELETE
     * @param sql SQL语句
     * @param params 参数（数组）（NotNull）
     * @return 是否成功
     */
    public boolean update(String sql, @NotNull Object ...params){
        long time = System.currentTimeMillis();

        BtDbUtil btDbUtil = BtDbUtil.getInstance();
        btDbUtil.getConnection();

        List<Object> list = Arrays.asList(params);
        try {
            return btDbUtil.updateByPreparedStatement(sql, list);
        }catch (Exception e){
            log.debug(e.getMessage());
            return false;
        }finally {
            log.debug("BtBaseDao.update() - " + (System.currentTimeMillis() - time) + "ms");
            log.debug("    SQL - " + sql);
            log.debug("    PAM - " + Arrays.asList(params));
            btDbUtil.releaseConn();
        }
    }

    /**
     * 回调接口
     */
    public interface Callback{

        /**
         * 成功回调
         */
        void success();

        /**
         * 失败回调
         */
        void fail();

    }

}
