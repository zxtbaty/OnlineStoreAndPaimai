package org.jinyuanjava.litemall.db.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by HJYIN on 14-11-26.
 */
public class DateUtil {


    /**
     * 将字符串转换成日期格式
     */
    public static LocalDateTime getLocalDateTimeFromStringHMS(String time) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime dateTime=null;
        try {
            dateTime = LocalDateTime.parse(time, df);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                dateTime = getLocalDateTimeFromStringHM(time);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return dateTime;
    }

    /**
     * 将字符串转换成日期格式
     */
    public static LocalDateTime getLocalDateFromString(String date) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime=null;
        try {
            dateTime = LocalDateTime.parse(date, df);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 将字符串转换成日期格式
     */
    public static LocalDate getLocalShortDateFromString(String date) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTime=null;
        try {
            dateTime = LocalDate.parse(date, df);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 将字符串转换成日期格式
     */
    public static LocalDateTime getLocalDateTimeFromStringHM(String time) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime=null;
        try {
            dateTime = LocalDateTime.parse(time, df);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }
    /**
     * 将字符串转换成日期格式
     */
    public static LocalDateTime getLocalDateTimeFromString(String time) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15";
        if(time.length()==10){
            time+=" 00:00:00";
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime=null;
        try {
            dateTime = LocalDateTime.parse(time, df);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }


    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static String getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(now.getTime());
    }

    /**
     * 得到几天后的时间2
     *
     * @param d
     * @param day
     * @return
     */
    public static String getDateAfter2(String d, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar now = Calendar.getInstance();//实例化Celendar对象
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return sdf.format(now.getTime());
    }

    /**
     * 比较时间的大小（按照日期比较）
     */
    public static int compareDate(String date1, String date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = simpleDateFormat.parse(date1);
            Date dt2 = simpleDateFormat.parse(date2);
            if (dt1.getTime() >= dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 比较时间的大小 按照完整的时间
     *
     * @return
     */
    public static int compateAppointDate(String date1, String date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = simpleDateFormat.parse(date1);
            Date dt2 = simpleDateFormat.parse(date2);
            if (dt1.getTime() >= dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 得到某年某月的某一周的第一天的日期
     *
     * @return
     */
    public static String getWeekFirstday(String year, String month, String week) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c = Calendar.getInstance();
        c.set(c.YEAR, Integer.parseInt(year));//设置年
        c.set(c.MONTH, Integer.parseInt(month) - 1);//设置月
        c.set(c.WEEK_OF_MONTH, Integer.parseInt(week));//设置星期
        c.set(Calendar.DAY_OF_WEEK, 1);//本周第一天，以星期日开始
        return (sdf.format(c.getTime()));
    }

    /**
     * 获得起始月份和结束月份之间的月份
     *
     * @return
     */
    public static List getYearMonth(Date date1, Date date2) {
        Calendar dd = Calendar.getInstance();//定义日期实例
        dd.setTime(date1);//设置日期起始时间
        String str = "";
        List<Object> datelist = new ArrayList<Object>();
        while (dd.getTime().before(date2)) {//判断是否到结束日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            str = sdf.format(dd.getTime());
            dd.add(Calendar.MONTH, 1);//进行当前日期月份加1
            datelist.add(str);
        }
        return datelist;
    }

    //获取本月的第一天
    public static String getfirstday(String year, String month) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //获取前月的第一天
        Calendar cal_1 = Calendar.getInstance();//获取当前日期

        cal_1.set(Calendar.YEAR, Integer.parseInt(year));
        cal_1.set(Calendar.MONTH, Integer.parseInt(month));

        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        return format.format(cal_1.getTime());
    }

    /*
    * 获取本月的最后一天
    *
    * */
    public static String getLastDay(String year, String month) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.YEAR, Integer.parseInt(year));
        ca.set(Calendar.MONTH, Integer.parseInt(month));

        ca.add(Calendar.MONTH, -1);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = format.format(ca.getTime());
        return last;
    }

    /*判断所给日期是星期几*/
    public static int dayForWeek(String pTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /*将String类型的格林威治时间转成Date类型的格林威治时间*/
    public static Date stringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    public static String getFormatDate(long date, String formate) {
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        return sdf.format(new Date(date));
    }
    /**
     * 13、10位时间戳转成String类型的日期
     *
     * @param str_num
     * @return 转换异常返回 0
     */
    public static String timestamp2Date(String str_num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (str_num.length() == 13) {
            String date = sdf.format(new Date(toLong(str_num)));
            return date;
        } else {
            String date = sdf.format(new Date(toInt(str_num) * 1000L));
            return date;
        }
    }

    /**
     * 将日期转成时间戳
     *
     * @param date
     * @return 转换异常返回 0
     */
    public static long getTime(Date date){
        long lSysTime1 = date.getTime();
        return lSysTime1;
    }

    /**
     * String转long
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }
    /**
     * 将字符串转换成日期格式(毫秒)
     */
    public static Date getDateTimeHM() {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 对象转整
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString());
    }

    public  static void main(String[] args){
        System.out.println(getTime(new Date())/1000/(60*60*24));
    }



}
