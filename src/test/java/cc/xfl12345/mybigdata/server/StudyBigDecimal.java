package cc.xfl12345.mybigdata.server;

import java.math.BigDecimal;

public class StudyBigDecimal {
    public static void main(String[] args) throws Exception {
        judgeNumber(new BigDecimal("0"));
        judgeNumber(new BigDecimal("0.0"));
        judgeNumber(new BigDecimal("1000000000000000.000000"));
        judgeNumber(new BigDecimal("1000000000000000"));
        judgeNumber(new BigDecimal("1E+10"));
        judgeNumber(new BigDecimal("1E-10"));
        judgeNumber(new BigDecimal("1.23456789E+5"));
        judgeNumber(new BigDecimal(Long.MAX_VALUE));
        judgeNumber(new BigDecimal(Long.MIN_VALUE));
        judgeNumber(new BigDecimal(Long.MAX_VALUE + "0"));
    }

    public static void judgeNumber(BigDecimal number) {
        System.out.println("\n\n" + "#".repeat(50));
        System.out.println(number.toPlainString());
        System.out.println("number.precision()=" + number.precision());
        System.out.println("number.scale()=" + number.scale());
        System.out.println("number.signum()=" + number.signum());
        System.out.println("isInteger=" + (number.scale() <= 0));
        System.out.println("isLongInteger=" +
            (number.scale() <= 0 && new BigDecimal(number.longValue()).compareTo(number) == 0)
        );
    }
}
