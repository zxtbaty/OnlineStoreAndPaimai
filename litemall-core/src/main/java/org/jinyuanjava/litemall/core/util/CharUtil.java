package org.jinyuanjava.litemall.core.util;

import java.math.BigDecimal;
import java.util.Random;

public class CharUtil {

    public static String getRandomString(Integer num) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String objectConverToString(Object object) {
         if(object==null){
             return "";
         } else
         {
             return object.toString();
         }
    }
    public static Double objectConverToDouble(Object object) {
        if(object==null){
            return null;
        } else
        {
            Double bonusValue= Double.parseDouble(object.toString());

            return bonusValue;
        }
    }
    public static Integer objectConverToInteger(Object object) {
        if(object==null){
            return 0;
        } else
        {
            Integer result= Integer.parseInt(object.toString());

            return result;
        }
    }

    public static Boolean objectConverToBoolean(Integer value) {
        if(value==null){
            return null;
        } else
        {
            if(value==0){
                return false;
            } else
            {
                return true;
            }
        }
    }
    public static Byte objectConverToByte(Integer value) {
        if(value==null){
            return null;
        } else
        {
            return Byte.valueOf(value.toString());
        }
    }

    public static Double objectConverToDoubleDefault0(Object object) {
        if(object==null){
            return Double.parseDouble("0") ;
        } else
        {
            Double bonusValue= Double.parseDouble(object.toString());

            return bonusValue;
        }
    }
    public static BigDecimal objectConverToBigdecimalDefault0(Object object) {
        if(object==null){
            return BigDecimal.valueOf(Double.parseDouble("0"));
        } else
        {
            Double bonusValue= Double.parseDouble(object.toString());

            return BigDecimal.valueOf(bonusValue);
        }
    }
}
