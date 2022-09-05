package cc.xfl12345.mybigdata.server;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * source code URL=https://blog.csdn.net/sinat_41617212/article/details/100593661
 */
public class TestChineseNumber2 {

    public static final BigInteger CHINESE_WAN = BigInteger.valueOf(10000);
    public static final BigInteger CHINESE_YI = BigInteger.valueOf(100000000);

    public static void main(String[] args) {
        System.out.println(1 << 30);
        System.out.println(ToCH(BigInteger.valueOf(Long.MAX_VALUE)));
        System.out.println(ToCH(CHINESE_YI.multiply(CHINESE_YI)));
        System.out.println(ToCH(new BigInteger(Long.toUnsignedString(Long.MAX_VALUE))));
        System.out.println(ToCH(new BigInteger(Long.toUnsignedString(Long.MAX_VALUE << 1))));
    }

    public static String ToCH(BigInteger intInput) {
        String si = String.valueOf(intInput);
        String sd = "";
        BigInteger remainder;
        String remainderInString;
        if (si.length() <= 4) {// 低于 1 万 不使用中文计数词
            sd += si;
        } else if (si.length() <= 8) {// 万
            remainder = intInput.remainder(CHINESE_WAN);
            if (remainder.compareTo(BigInteger.ZERO) == 0) {
                remainderInString = "";
            } else {
                remainderInString = remainder.toString();
            }

            sd = intInput.divide(CHINESE_WAN) + " 万 " + remainderInString;
        } else if (si.length() <= 12) {// 亿
            remainder = intInput.remainder(CHINESE_YI);
            if (remainder.compareTo(BigInteger.ZERO) == 0) {
                remainderInString = "";
            } else {
                remainderInString = ToCH(remainder);
            }
            sd = intInput.divide(CHINESE_YI) + " 亿 " + remainderInString;
        } else {// 超过万亿
            remainderInString = ToCH(intInput.remainder(CHINESE_YI));
            sd = "(" + ToCH(intInput.divide(CHINESE_YI)) + ") 亿 "
                + (remainderInString.equals("0") ? "" : remainderInString);
        }
        return sd;
    }
}
