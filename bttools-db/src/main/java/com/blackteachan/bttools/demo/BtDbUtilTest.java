package com.blackteachan.bttools.demo;

public class BtDbUtilTest {

    public static void main(String[] args) {

        DemoDao demoDao = new DemoDao();
        System.out.println("demoDao.get(): ");
        System.out.println(demoDao.get());


//        for (int i = 0; i < 50; i++) {
//            final int finalI = i;
//            new Thread(new Runnable() {
//                public void run() {
//                    System.out.println("线程" + finalI + "开始");
//                    BtDbUtil btDbUtil = BtDbUtil.getInstance();
//                    btDbUtil.getConnection();
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    btDbUtil.releaseConn();
//                    System.out.println("线程" + finalI + "结束");
//                }
//            }).start();
//        }
    }

}
