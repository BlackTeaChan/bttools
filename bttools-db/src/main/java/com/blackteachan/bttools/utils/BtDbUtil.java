package com.blackteachan.bttools.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * 数据库工具类
 * @author blackteachan
 * 创建日期：2019-11-16 16:46
 */
public class BtDbUtil {

    /**
     * 数据库用户名
     */
    private static String user = null;
    /**
     * 数据库密码
     */
    private static String password = null;
    /**
     * 驱动信息
     */
    private static String driver = null;
    /**
     * 数据库地址
     */
    private static String url = null;
    /**
     * 日志
     */
    private static BtLog log = new BtLog();
//    private static Logger log = Logger.getLogger(BtDbUtil.class);
    /**
     * 读取配置
     */
    private static Properties p;
    /**
     * 实例
     */
    private static BtDbUtil db = null;
    /**
     * 连接
     */
    private static Connection connection = null;

    private PreparedStatement pstmt;
    private PreparedStatement calltmt;
    private ResultSet resultSet;

    static{
        //实例化一个properties对象用来解析我们的配置文件
        p = new Properties();
        //通过类加载器来读取我们的配置文件，以字节流的形式读取
        InputStream in = BtDbUtil.class.getClassLoader().getResourceAsStream("dbconfig.properties");
        try {
            //将配置文件自如到Propreties对象，来进行解析
            p.load(in);
            //读取配置文件
            user = p.getProperty("user");
            password = p.getProperty("password");
            driver = p.getProperty("driver");
            url = p.getProperty("url");
            //加载驱动
            Class.forName(driver);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private BtDbUtil() {
        try{
            Class.forName(driver);
        }catch(Exception e){
            log.error("BtDbUtil - 实例化错误: " + e);
        }
    }

    public static BtDbUtil getInstance(){
        if(db == null){
            synchronized (BtDbUtil.class) {
                if(db == null) {
                    db = new BtDbUtil();
                }
            }
        }
        return db;
    }

    /**
     * 获得数据库的连接
     * @return 返回数据库连接
     */
    public Connection getConnection(){
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("BtDbUtil - 连接数据库错误: " + e);
        }
        return connection;
    }


    /**
     * 增加、删除、改
     * @param sql SQL语句
     * @param params 参数
     * @return 是否成功
     * @throws SQLException SQL异常
     */
    public boolean updateByPreparedStatement(String sql, List<Object> params)throws SQLException{
        boolean flag = false;
        int result = -1;
        pstmt = connection.prepareStatement(sql);
        int index = 1;
        if(params != null && !params.isEmpty()){
            for(int i=0; i<params.size(); i++){
                pstmt.setObject(index++, params.get(i));
            }
        }
        try{
            //log.info(pstmt);
            result = pstmt.executeUpdate();
            flag = result > 0 ? true : false;
        }catch(Exception e){
            log.error("BtDbUtil - update错误: " + e);
            return false;
        }
        return flag;
    }

    /**
     * 查询单条记录
     * @param sql SQL语句
     * @param params 参数
     * @return 查询的单条结果
     * @throws SQLException SQL异常
     */
    public Map<String, Object> findSimpleResult(String sql, List<Object> params) throws SQLException{
        Map<String, Object> map = new HashMap<String, Object>();
        int index  = 1;
        pstmt = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i=0; i<params.size(); i++){
                pstmt.setObject(index++, params.get(i));
            }
        }
        //log.info(pstmt);
        resultSet = pstmt.executeQuery();//返回查询结果
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col_len = metaData.getColumnCount();
        while(resultSet.next()){
            for(int i=0; i<col_len; i++ ){
                String cols_name = metaData.getColumnLabel(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
        }
        return map;
    }

    /**
     * 查询单条记录
     * @param sql SQL语句
     * @param params 参数
     * @throws SQLException SQL异常
     */
    public void exeCallDb(String sql, List<Object> params) throws SQLException{
        calltmt = connection.prepareCall("{call oprData(?)}");
        calltmt.setString(1, params.get(1).toString());
        log.info(calltmt);
        calltmt.execute();//返回查询结果
        connection.close();
    }

    /**查询多条记录
     * @param sql SQL语句
     * @param params 参数
     * @return 查询到的多条结果
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> findModeResult(String sql, List<Object> params) throws SQLException{
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int index = 1;
        pstmt = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i = 0; i<params.size(); i++){
                pstmt.setObject(index++, params.get(i));
            }
        }
        //log.info(pstmt);
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while(resultSet.next()){
            Map<String, Object> map = new HashMap<String, Object>();
            for(int i=0; i<cols_len; i++){
                String cols_name = metaData.getColumnLabel(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
            list.add(map);
        }
        return list;
    }


    /**
     * 释放数据库连接
     */
    public void releaseConn(){
        if(resultSet != null){
            try{
                resultSet.close();
            }catch(SQLException e){
                log.error("BtDbUtil - 释放数据库连接错误：" + e);
                e.printStackTrace();
            }
        }try{
            if (connection!=null) {
                connection.close();
            }
        }catch(Exception e){
            log.error("BtDbUtil - 释放数据库连接错误：" + e);
            e.printStackTrace();
        }
    }

}
