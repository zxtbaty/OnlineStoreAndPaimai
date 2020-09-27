package org.jinyuanjava.litemall.core.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by HJYIN on 14-11-26.
 */
public class DateUtil {

    public static Date getCurrentDate(){
        return new Date();
    }

    public static String getNowTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now_date = simpleDateFormat.format(new Date());
        return now_date;
    }

    public static String getShortNowTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        String now_date = simpleDateFormat.format(new Date());
        return now_date;
    }
    /**
     *
     * 获得某个时间段内的随机时间
     *
     */
    public static Date randomDate(String beginDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = format.parse(beginDate);// 开始日期
            Date end = format.parse(endDate);// 结束日期
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());

            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long random(long begin, long end) {
        long rtnn = begin + (long) (Math.random() * (end - begin));
        if (rtnn == begin || rtnn == end) {
            return random(begin, end);
        }
        return rtnn;
    }

    /**
     * 获取两个日期相减得到天数
     *
     * */
    public static long getDaySub(String beginDateStr, String endDateStr)
    {
        long day=0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate;
        Date endDate;
        try
        {
            beginDate = format.parse(beginDateStr);
            endDate= format.parse(endDateStr);
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
            //System.out.println("相隔的天数="+day);
        } catch (ParseException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
        return day;
    }

    /**
     * 将字符串转换成日期格式
     */
    public static Date parseString(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将字符串转换成日期格式
     */
    public static Date parseStringHM(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将字符串转换成日期格式
     */
    public static LocalDateTime getLocalDateTimeFromStringHMS(String time) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15:234";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime dateTime=null;
        try {
            dateTime = LocalDateTime.parse(time, df);
        } catch (Exception e) {
            //e.printStackTrace();
            try {
                try
                {
                    df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    dateTime = LocalDateTime.parse(time, df);

                } catch (Exception ye){
                    try {
                        df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        dateTime = LocalDateTime.parse(time, df);;
                    } catch (Exception ze){
                        try
                        {
                            df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            dateTime = LocalDateTime.parse(time, df);
                        } catch (Exception ey) {
                            ey.printStackTrace();
                        }
                    }
                }
            } catch (Exception ex) {
            }
        }
        return dateTime;
    }


    /**
     * 将字符串转换成日期格式
     */
    public static LocalDateTime getLocalDateTimeFromStringHHMM(String time) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime=null;
        try {
            dateTime = LocalDateTime.parse(time, df);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                dateTime = getLocalDateTimeFromStringHM(time);
            } catch (Exception ex) {
                try
                {
                    dateTime = getLocalDateTimeFromStringYYYYMMDD(time);
                } catch (Exception ey) {
                    ey.printStackTrace();
                }

            }
        }
        return dateTime;
    }


    /**
     * 将字符串转换成日期格式
     */
    public static LocalDateTime getLocalDateTimeFromStringHM(String time) {
        if(StringUtils.isEmpty(time)){
            return null;
        }
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
    public static LocalDateTime getLocalDateTimeFromStringYYYYMMDD(String time) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
    public static LocalDate getLocalDateFromStringYYYYMMDD(String time) {
        //字符串转时间
        //String dateTimeStr = "2018-07-28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTime=null;
        try {
            dateTime = LocalDate.parse(time, df);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 将字符串转换成日期格式
     */
    public static Date parseStringYMD(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
    public static String parseDateYMD(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String now_date = simpleDateFormat.format(date);
        return now_date;
    }
    public static String dateToStr(Date dateDate) {
         SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
         String dateString = formatter.format(dateDate);
         return dateString;
       }
    public static Date parseStringYMDTwo(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * 将日期转换成字符串
     */
    public static String parseDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now_date = simpleDateFormat.format(date);
        /*String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);*/
        return now_date;
    }

    public static String parseDateYYYYMMDDHHMM(LocalDateTime localDateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String now_date = simpleDateFormat.format(localDateTime);
        /*String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);*/
        return now_date;
    }

    public static String parseDateTwo(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String now_date = simpleDateFormat.format(date);
        /*String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);*/
        return now_date;
    }
    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String now_date = simpleDateFormat.format(new Date());
        return now_date;
    }

    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String now_date = simpleDateFormat.format(new Date());
        return now_date;
    }

    public static String getShortDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String now_date = simpleDateFormat.format(new Date());
        return now_date;
    }

    public static String getShortDate(LocalDate value) {
        if(value==null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String now_date = simpleDateFormat.format(value);
        return now_date;
    }

    public static String getDateyyyMMddHHmmss(LocalDateTime value) {
        if(value==null){
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = value.atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String now_date = simpleDateFormat.format(date);
        return now_date;
    }
    public static String getDateyyyMMddHHmmss_Format(LocalDateTime value) {
        if(value==null){
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = value.atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now_date = simpleDateFormat.format(date);
        return now_date;
    }

    public static String getDateString(LocalDateTime value) {
        if(value==null){
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = value.atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String now_date = simpleDateFormat.format(date);
        return now_date;
    }


    /**
     * 判断两个时间是否超过24小时
     */
    public static boolean jisuan(String date1, String date2) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse(date1);
        Date end = sdf.parse(date2);
        long cha = end.getTime() - start.getTime();
        double result = cha * 1.0 / (1000 * 60 * 60);
        if (result <= 24) {
            //System.out.println("可用");
            return false;
        } else {
            //System.out.println("已过期");
            return true;
        }
    }

    public static List<Date> findDates(Date dBegin, Date dEnd) {
        List dateList = new ArrayList();
        dateList.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(calBegin.getTime());
        }
        return dateList;
    }

    public static List<Date> findMonths(Date dBegin, Date dEnd) {
        List dateList = new ArrayList();
        dateList.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.MONTH, 1);
            dateList.add(calBegin.getTime());
        }
        return dateList;
    }

    public static List<Date> findHours(Date dBegin, Date dEnd) {
        List dateList = new ArrayList();
        dateList.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.HOUR, 1);
            dateList.add(calBegin.getTime());
        }
        return dateList;
    }

    //根据日期取得星期几

    /**
     * Mon
     * Tue
     * Wed
     * Thu
     * Fri
     * Sat
     * Sun
     *
     * @param date
     * @return
     */
    public static String getWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.US);
        String week = sdf.format(date);
        return week;
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static String getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(now.getTime());
    }

    public static String getDateBefore2(String d, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar now = Calendar.getInstance();//实例化Celendar对象
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return sdf.format(now.getTime());
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param d
     * @return
     */
    public static String getMouBefore(Date d, int mouth) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.MONTH, now.get(Calendar.MONTH) - mouth);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        return simpleDateFormat.format(now.getTime());
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
