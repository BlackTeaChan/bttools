package com.blackteachan.bttools.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author blackteachan
 * 创建日期：2019-11-16 16:46
 */
public class JdbcUtil {

    /**
     * 数据库用户名
     */
    private static final String USERNAME = "wms";
    /**
     * 数据库密码
     */
    private static final String PASSWORD = "wms123456@";
    /**
     * 驱动信息
     */
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    /**
     * 数据库地址
     */
    private static final String URL = "jdbc:mysql://192.168.0.4:3306/wms?useSSL=FALSE&serverTimezone=UTC";

    private static Logger log = Logger.getLogger(JdbcUtil.class);

    private Connection connection;
    private PreparedStatement pstmt;
    private PreparedStatement calltmt;
    private ResultSet resultSet;

    public JdbcUtil() {
        try{
            Class.forName(DRIVER);
        }catch(Exception e){
            log.error("JdbcUtil - 实例化错误: " + e);
        }
    }

    /**
     * 获得数据库的连接
     * @return
     */
    public Connection getConnection(){
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("JdbcUtil - 连接数据库错误: " + e);
        }
        return connection;
    }


    /**
     * 增加、删除、改
     * @param sql
     * @param params
     * @return
     * @throws SQLException
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
            log.error("JdbcUtil - update错误: " + e);
            return false;
        }
        return flag;
    }

    /**
     * 查询单条记录
     * @param sql
     * @param params
     * @return
     * @throws SQLException
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
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public void exeCallDb(String sql, List<Object> params) throws SQLException{
        calltmt = connection.prepareCall("{call oprData(?)}");
        calltmt.setString(1, params.get(1).toString());
        log.info(calltmt);
        calltmt.execute();//返回查询结果
        connection.close();
    }

    /**查询多条记录
     * @param sql
     * @param params
     * @return
     * @throws SQLException
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
                log.error("JdbcUtil - 释放数据库连接错误：" + e);
                e.printStackTrace();
            }
        }try{
            if (connection!=null) {
                connection.close();
            }
        }catch(Exception e){
            log.error("JdbcUtil - 释放数据库连接错误：" + e);
            e.printStackTrace();
        }

    }
}
