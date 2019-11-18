package com.blackteachan.bttools.demo;

import com.blackteachan.bttools.dao.BtBaseDao;

import java.util.Map;

/**
 * @author blackteachan
 * 创建日期：2019-11-18 12:05
 */
public class DemoDao extends BtBaseDao {

    public Map get(){
        return (Map) get("select * from sys_user;", null);
    }

}
