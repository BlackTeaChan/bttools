package com.blackteachan.bttools.utils;

import java.math.BigDecimal;

/**
 * 数字工具类
 * @author blackteachan
 * @since 2019-11-14 16:25
 * @version 1.0
 */
public class NumberUtil {
    
    private static final char[] CHARS_BASE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String STRING_BASE = "0123456789ABCDEF";

    /**
     * 模拟量转数字量
     * @param rMin 模拟量最大值
     * @param rMax 模拟量最小值
     * @param aMin 量程最大值
     * @param aMax 量程最小值
     * @param v 模拟量
     * @return 数字量double
     */
    public static double analog2Digital(double rMin, double rMax, double aMin, double aMax, double v){
        //return (rMax-rMin) * ((v-aMin)/(aMax-aMin)) + rMin;
        //PLC 工程量范围为(最小值为Amin 最大值为 Amax); 变送器量程为 (最小量程为Rmin 最大量程为 Rmax) 当前PLC采集工程量值为X; 当前读取变送器值为Y; 解析式为： 
        return ((rMax-rMin)*(v-aMin))/(aMax-aMin)+rMin;
        //例：S7-300/S7-400 
        //PLC 工程量范围为0~27648; 变送器量程为-20~70; 
        //当前PLC采集工程量值为13824; 求前读取变送器值为Y; 
        //Y=((70-(-20))* (13824-0))/ (27648-0)+ (-20); Y=25; 例：S7-200 
        //PLC 工程量范围为6400~32000; 变送器量程为-20~70; 
        //当前PLC采集工程量值为19200; 求前读取变送器值为Y; 
        //Y=((70-(-20))*( 19200-6400))/ (32000-6400)+ (-20); Y=25
    }

    /**
     * 十进制int转十六进制String
     * <p>输入：257</p>
     * <p>输出："01 01"</p>
     * @param dec 十进制数
     * @return 十六进制字符串
     */
    public static String dec2Hex(int dec){
        int h1 = dec / 256;
        int h2 = dec - (h1 * 256);
        return String.format("%02x", h1) + " " + String.format("%02x", h2);
    }

    /**
     * 十六进制String转十进制int
     * <p>输入："0101"</p>
     * <p>输出：257</p>
     * @param hex 十六进制String
     * @return 十进制int
     */
    public static int hex2Dec(String hex) {
        try {
            return Integer.parseInt(hex, 16);
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * byte数组转十六进制String
     * <p>输入：[-86, 1, 1]</p>
     * <p>输出："AA0101"</p>
     * @param bytes 字节数组
     * @return 十六进制String
     */
    public static String bytes2Hex(byte[] bytes) {
        //此方法耗效率高
        StringBuffer sb = new StringBuffer(bytes.length);
        String str;
        for (int i = 0; i < bytes.length; i++) {
            str = Integer.toHexString(0xFF & bytes[i]);
            if (str.length() < 2) {
                sb.append(0);
            }
            sb.append(str.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * byte数组转十六进制String数组
     * <p><b>效率较低，建议使用{@link #bytes2Hex(byte[])}</b></p>
     * <p>输入：[-86, 1, 1]</p>
     * <p>输出：["AA", "01", "01"]</p>
     * @param bytes 字节数组
     * @return 十六进制String
     */
    public static String[] bytes2Hex2(byte[] bytes) {
        return bytes2Hex(bytes).replaceAll("(.{2})", "$1 ").split(" ");
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * <p>输入："425443"</p>
     * <p>输出："BTC"</p>
     * @param hex 十六进制String
     * @return ASCII String
     */
    public static String hex2Str(String hex) {
        char[] hexs = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = STRING_BASE.indexOf(hexs[2 * i]) * 16;
            n += STRING_BASE.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     * @param str 字符串
     * @return 十六进制String
     */
    public static String str2Hex(String str) {
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(CHARS_BASE[bit]);
            bit = bs[i] & 0x0f;
            sb.append(CHARS_BASE[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * IEEE-754浮点数转换
     * <p>推荐使用</p>
     * @param hex 十六进制
     * @return 小数BigDecimal
     */
    public static BigDecimal hex2BigDecimal(String hex){
        Float value = Float.intBitsToFloat((int)Long.parseLong(hex, 16));
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * IEEE32格式，十六进制转double
     * <p>输入："40 48 F5 C2"</p>
     * <p>输出：3.14</p>
     * @param hex 四字节十六进制字符串(40 48 F5 C2)
     * @return 浮点数
     */
    public static double hex2Double(String hex) {
        String[] hexarray = hex.split( " " );
        // 高地位互换
        hex = hexarray[ 0 ] + hexarray[ 1 ] + hexarray[ 2 ] + hexarray[ 3 ];
        // System.out.println( hexstr );
        // 先将16进制数转成二进制数0 10000001 00000000000000000000000 <br>
        String binarystr = hex2Bin( hex );
        // 1位符号位(SIGN)=0 表示正数
        String sign = binarystr.substring( 0, 1 );
        // 8位指数位(EXPONENT)=10000001=129[10进制]
        String exponent = binarystr.substring( 1, 9 );
        int expint = Integer.parseInt( exponent, 2 );
        // 23位尾数位(MANTISSA)=00000000000000000000000
        String last = binarystr.substring( 9 );
        // 小数点移动位数
        int mobit = expint - 127;
        // 小数点右移18位后得10101001 01101001 110.00000
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 23; i++) {
            if (i == mobit) {
                sb.append(".");
            }
            char b = last.charAt( i );
            sb.append( b );
        }
        String valstr = "1" + sb.toString();
        // 指数
        int s = valstr.indexOf( "." ) - 1;
        double dval = 0d;
        for (int i = 0; i < valstr.length(); i++) {
            if (valstr.charAt( i ) == '.') {
                continue;
            }
            double d = Math.pow( 2, s );
            int f = Integer.valueOf( valstr.charAt( i ) + "" );
            d = d * f;
            s = s - 1;
            dval = dval + d;
        }
        if ("1".equals(sign)) {
            dval = 0 - dval;
        }
        return  dval;
    }

    /**
     * 十六进制字符串转二进制字符串
     * @param hexString 十六进制字符串
     * @return 二进制String
     */
    public static String hex2Bin(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            return null;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString( Integer.parseInt( hexString.substring( i, i + 1 ), 16 ) );
            bString += tmp.substring( tmp.length() - 4 );
        }
        return bString;
    }

}
