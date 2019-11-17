package com.blackteachan.bttools.utils;

public class JdbcUtilTest {

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("线程" + finalI + "开始");
                    DbUtil dbUtil = DbUtil.getInstance();
                    dbUtil.getConnection();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dbUtil.releaseConn();
                    System.out.println("线程" + finalI + "结束");
                }
            }).start();
        }
    }

}
