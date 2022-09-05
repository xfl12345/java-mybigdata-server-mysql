package cc.xfl12345.mybigdata.server;

/**
 * source code URL=https://blog.csdn.net/sinat_41617212/article/details/100593661
 */
public class TestChineseNumber {

    public static void main(String[] args) {
        System.out.println(ToCH(Integer.MAX_VALUE));
    }

    public static String ToCH(int intInput) {
        String si = String.valueOf(intInput);
        String sd = "";
        if (si.length() == 1) // 個
        {
            sd += GetCH(intInput);
            return sd;
        } else if (si.length() == 2)// 十
        {
            if (si.substring(0, 1).equals("1"))
                sd += "十";
            else
                sd += (GetCH(intInput / 10) + "十");
            sd += ToCH(intInput % 10);
        } else if (si.length() == 3)// 百
        {
            sd += (GetCH(intInput / 100) + "百");
            if (String.valueOf(intInput % 100).length() < 2 && intInput % 100 != 0)
                sd += "零";
            sd += ToCH(intInput % 100);
        } else if (si.length() == 4)// 千
        {
            sd += (GetCH(intInput / 1000) + "千");
            if (String.valueOf(intInput % 1000).length() < 3 && intInput % 1000 != 0)
                sd += "零";
            sd += ToCH(intInput % 1000);
        } else if (si.length() == 5 || si.length() == 6 ||si.length() == 7 || si.length() == 8 )// 万
        {
            String getCH = GetCH(intInput / 10000);

            if(getCH.length()==1) {
                sd += (GetCH(intInput / 10000) + "万");
            }else {
                sd += (ToCH(intInput / 10000) + "万");
            }
            if (String.valueOf(intInput % 10000).length() < 4 && intInput % 10000 != 0)
                sd += "零";
            sd += ToCH(intInput % 10000);
        }else if (si.length() == 9 || si.length() == 10 ||si.length() == 11 || si.length() == 12 )// 亿
        {
            String getCH = GetCH(intInput / 100000000);

            if(getCH.length()==1) {
                sd += (GetCH(intInput / 100000000) + "亿");
            }else {
                sd += (ToCH(intInput / 100000000) + "亿");
            }
            if (String.valueOf(intInput % 100000000).length() < 8 && intInput % 100000000 != 0)
                sd += "零";
            sd += ToCH(intInput % 100000000);
        }


        return sd;
    }

    private static String GetCH(int input) {
        String sd = "";
        switch (input) {
            case 1:
                sd = "一";
                break;
            case 2:
                sd = "二";
                break;
            case 3:
                sd = "三";
                break;
            case 4:
                sd = "四";
                break;
            case 5:
                sd = "五";
                break;
            case 6:
                sd = "六";
                break;
            case 7:
                sd = "七";
                break;
            case 8:
                sd = "八";
                break;
            case 9:
                sd = "九";
                break;
            default:
                break;
        }
        return sd;
    }
}
