package com.blackteachan.bttools.utils;

/**
 * 日志管理
 * @author blackteachan
 * 创建日期：2019-11-18 09:23
 */
public class BtLog {

    public void info(Object obj){
        System.out.println("INFO: " + obj.toString());
    }
    public void debug(Object obj){
        System.out.println("DEBUG: " + obj.toString());
    }
    public void error(Object obj){
        System.out.println("ERROR: " + obj.toString());
    }

}
