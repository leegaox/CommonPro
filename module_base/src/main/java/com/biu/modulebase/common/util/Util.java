package com.biu.modulebase.common.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 */
public class Util {

    // 字符串的非空
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equals(input)) {
            return true;
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static String getString(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        } else {
            return input.toString();
        }
    }

    /**
     * 判断是否是合法的手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isVerificationCodeCorrect(String code) {
        Pattern p = Pattern.compile("^\\d{6}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    public static boolean isPasswordCorrect(String pwd) {
        Pattern p = Pattern.compile("^.{6,15}$");
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    public static boolean isUserNameCorrect(String userName) {
        return !TextUtils.isEmpty(userName.trim());
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param number
     * @return
     */
    public static boolean getBankCardCheckCode(String number) {
        int sumOdd = 0;
        int sumEven = 0;
        int length = number.length();
        int[] wei = new int[length];
        for (int i = 0; i < number.length(); i++) {
            wei[i] = Integer.parseInt(
                    number.substring(length - i - 1, length - i));// 从最末一位开始提取，每一位上的数值
            System.out.println("第" + i + "位数字是：" + wei[i]);
        }
        for (int i = 0; i < length / 2; i++) {
            sumOdd += wei[2 * i];
            if ((wei[2 * i + 1] * 2) > 9) {
                wei[2 * i + 1] = wei[2 * i + 1] * 2 - 9;
            } else {
                wei[2 * i + 1] *= 2;
            }
            sumEven += wei[2 * i + 1];
        }
        System.out.println("奇数位的和是：" + sumOdd);
        System.out.println("偶数位的和是：" + sumEven);
        if ((sumOdd + sumEven) % 10 == 0) {
            return true;
        } else {
            return false;
        }
    }

    // 验证身份证
    public static boolean doAuthentication(String shenfen) {
        Pattern pattern = Pattern.compile(
                "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$");
        Matcher m = pattern.matcher(shenfen);
        return m.matches();
    }

    // 验证只能输入数字和字母
    public static boolean InputFigureLetter(String input) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher m = pattern.matcher(input);
        return m.matches();
    }

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;

    /**
     * new Date(long)
     * 根据给定时间显示 时间字符串：几秒前、几分钟前、今天HH:mm、昨天HH:mm、一年之内(MM-dd HH:mm)、yyyy-MM-dd HH:mm
     *
     * @param date
     * @return String
     */
    public static String getReleaseTime(Date date) {
        Date now = new Date();//获取当前时间
        long delta = now.getTime() - date.getTime();
        if (delta < ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + "秒前";
        }
        if (delta < ONE_HOUR) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + "分钟前";
        }
        if (delta < (now.getTime() - getTodayDateAtZeroAM(now).getTime())) {
            return "今天" + DateToStr(date, "HH:mm");
        }
        if (delta > (now.getTime() - getTodayDateAtZeroAM(now).getTime()) && delta < (now.getTime() - getYestodayDateAtZeroAM(now).getTime())) {
            return "昨天" + DateToStr(date, "HH:mm");
        }
        if (delta > (now.getTime() - getYestodayDateAtZeroAM(now).getTime()) && delta < (now.getTime() - getCurrentYearAtZeroAM(now).getTime())) {
            return DateToStr(date, "MM-dd HH:mm");
        } else {
            return DateToStr(date, "yyyy-MM-dd");
//            return  DateToStr(date, "yyyy-MM-dd HH:mm");
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return 返回值为：{天, 时, 分, 秒}
     * @throws ParseException
     */
    public static String getDistanceTimes(String str1, String str2)
            throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timsString = null;
        if (day > 0) {
            timsString = day + "天";
        } else if (day == 0 && hour > 0) {
            timsString = hour + "小时";
        } else if (day == 0 && hour == 0) {
            timsString = min + "分" + sec + "秒";
        } else if (day == 0 && hour == 0 && min == 0 && sec == 0) {
            timsString = "";
        }
        return timsString;
    }


    /**
     * 字符串转日期()
     */
    public static Date StrToDate2(String str) {
        return StrToDate(str, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 秒数转换成格式化的String时间
     *
     * @param date Stirng型时间戳（秒数）
     * @return
     */
    public static String sec2Date(String datetime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Long time = new Long(datetime);
        String d = sdf.format(time * 1000);
        return d;
    }

    /**
     * 毫秒数转换成格式化的String时间
     *
     * @param date Stirng型时间戳（毫秒数）
     * @return
     */
    public static String hSec2Date(String datetime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Long time = new Long(datetime);
        String d = sdf.format(time);
        return d;
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 得到当前时间之后的几个小时时间
     *
     * @param differhour
     * @return
     */
    public static String getCurrentHourAfter(int differhour) {
        long currenttime = new Date().getTime();
        Date dat = new Date(currenttime + 1000 * 60 * 60 * differhour);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(dat);
    }

    /**
     * 得到当前时间之前的几个小时时间
     *
     * @param differhour
     * @return
     */
    public static String getCurrentHourBefor(int differhour) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        long currenttime = new Date().getTime();
        Date dat = new Date(currenttime - 1000 * 60 * 60 * differhour);
        // format.parse(format.format(dat))
        return format.format(dat);
    }

    /**
     * 字符串转日期
     */
    public static Date StrToDate(String str, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间转化
     *
     * @param time
     * @param type类型
     * @return
     */
    public static String DateToStr(Date time, String type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(type);
        String date = dateFormat.format(time);
        return date;
    }

    /**
     * 获取当前时间，格式为 :yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(now);
        return date;
    }

    public static String getCurreDateFormat(String format) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 获取当前时间，格式为 :yyyy-MM-dd
     *
     * @return
     */
    public static Integer getCurrentDate_MM() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        String date = dateFormat.format(now);
        return isInteger(date);
    }

    /**
     * 获取当前时间，格式为 :yyyy-MM-dd
     *
     * @return
     */
    public static Integer getCurrentDate_dd() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        String date = dateFormat.format(now);
        return isInteger(date);
    }

    /**
     * 获取当前时间，格式为 :yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentDate2() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 获取当前时间，格式为 :yyyy-MM-dd HH:mm
     *
     * @return
     */
    public static String getCurrentDate3() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * str 转换成Date
     *
     * @param date
     * @param format
     * @return
     */
    public static Date str2Date(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取给定Date当天前一天零点时间，格式为 :yyyy-MM-dd HH:mm：ss
     *
     * @return
     */
    public static Date getYestodayDateAtZeroAM(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, -1);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        Date zeroDate = null;
        try {
            zeroDate = formatter.parse(dateString.substring(0, dateString.length() - 8) + "00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
            return zeroDate;
        }
        return zeroDate;
    }

    /**
     * 获取给定Date当天零点时间，格式为 :yyyy-MM-dd HH:mm：ss
     *
     * @return Date
     */
    public static Date getTodayDateAtZeroAM(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        Date zeroDate = null;
        try {
            zeroDate = formatter.parse(dateString.substring(0, dateString.length() - 8) + "00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
            return zeroDate;
        }
        return zeroDate;
    }

    /**
     * 获取给定Date该年份第一天零点时间，格式为 :yyyy-MM-dd HH:mm：ss
     *
     * @return Date
     */
    public static Date getCurrentYearAtZeroAM(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        Date zeroDate = null;
        try {
            zeroDate = formatter.parse(dateString.substring(0, dateString.length() - 14) + "01-01 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
            return zeroDate;
        }
        return zeroDate;
    }

    /**
     * 获取明天零点时间，格式为 :yyyy-MM-dd HH:mm：ss
     *
     * @return
     */
    public static String getTomorrowDateAtZeroAM() {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString.substring(0, dateString.length() - 8) + "00:00:00";
    }

    /**
     * 比较两个 yyyy-MM-dd 格式的日期字符串时间前后
     *
     * @param date1
     * @param date2
     * @return true:"date1在date2后" , false:"date1在date2前"
     */
    public static boolean dateComparator(String date1, String date2) {
        return dateComparator(date1, date2, "yyyy-MM-dd");
    }

    /**
     * 比较两个 yyyy-MM-dd HH:mm:ss 格式的日期字符串时间前后
     *
     * @param date1
     * @param date2
     * @return true:"date1在date2后" , false:"date1在date2前"
     */
    public static boolean dateComparator2(String date1, String date2) {
        return dateComparator(date1, date2, "yyyy-MM-dd HH:mm:ss");
    }

    public static boolean dateComparator(String date1, String date2, String str) {
        DateFormat df = new SimpleDateFormat(str);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return false;
            } else if (dt1.getTime() < dt2.getTime()) {
                return true;
            } else {
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * 获取两个日期的差 yyyy-MM-dd
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long dateDifference1(String date1, String date2) {
        return dateDifference(date1, date2, "yyyy-MM-dd");
    }

    /**
     * 获取两个日期的差 yyyy-MM-dd HH:mm:ss
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long dateDifference2(String date1, String date2) {
        return dateDifference(date1, date2, "yyyy-MM-dd HH:mm:ss");

    }

    /**
     * 获取两个日期的差
     *
     * @param date1
     * @param date2
     * @param str
     * @return
     */
    public static long dateDifference(String date1, String date2, String str) {
        DateFormat df = new SimpleDateFormat(str);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            long temp = dt2.getTime() - dt1.getTime();
            long result = temp / (1000 * 60);
            return result;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    /**
     * 得到两个日期的差
     *
     * @param fDate 秒数
     * @param oDate 秒数
     * @return 天数
     */
    public static int daysOfTwo(long fDate, long oDate) {
        Date dt1 = new Date(fDate * 1000);
        Date dt2 = new Date(oDate * 1000);
        Calendar aCalendar = Calendar.getInstance();

        aCalendar.setTime(dt1);

        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

        aCalendar.setTime(dt2);

        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

        return day2 - day1;
    }

    /**
     * 比较两个数的大小
     *
     * @param num1
     * @param num2
     * @return
     */
    public static boolean numComparator(String num1, String num2) {
        int int1 = Integer.parseInt(num1.trim());
        int int2 = Integer.parseInt(num2.trim());
        return int1 > int2;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param time 需要获取的日期
     * @return 当前日期是星期几，(从0开始，周日、周一.....)
     */
    public static int getWeekOfDate(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dt;
        int week = 0;
        try {
            dt = df.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            week = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (week < 0) {
                week = 0;
            }
        } catch (ParseException e) {
        }
        return week;
    }

    /**
     * 判断是否为double类型
     *
     * @param str
     * @return
     */
    public static boolean isDoubleNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) { // 不是数字
            return false;
        }
    }

    /**
     * 判断是否为float类型
     *
     * @param str
     * @return
     */
    public static boolean isFloatNumeric(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) { // 不是数字
            return false;
        }
    }

    public static boolean isLongNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) { // 不是数字
            return false;
        }
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) { // 不是数字
            return false;
        }
    }

    /**
     * 关于EditText的判断方法
     *
     * @param editText
     * @param yajin    限额大小
     * @param c
     */
    public static void setPricePoint(final EditText editText, final double yajin, final Context c) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(
                                Util.isDouble(s.subSequence(0, s.length()).toString()) + "");
                        editText.setSelection(s.length() + 1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    editText.setText("0");
                    return;
                }
                if (!isDoubleNumeric(s.toString())) {
                    editText.setText("0");
                    Toast.makeText(c, "请输入正确价格", Toast.LENGTH_SHORT).show();
                    return;
                }
                double strcount = Double.parseDouble(s.toString());
                double count = yajin;
                if (strcount > count) {
                    editText.setText("0");
                    Toast.makeText(c, "超出限额", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 时间秒数转换为时间
     */
    public static String getDatesft(Long dates) {
        // long sstime = dates.toString();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dates);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    /**
     * 时间秒数转换为时间  yyyy-MM-dd HH:mm:ss
     */
    public static String getFullDate(Long dates) {
        // long sstime = dates.toString();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dates);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(date);
    }

    /**
     * 发送消息时，获取当前事件
     *
     * @return 当前时间
     */
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }

    public static String checkInt(String num) {
        return (num == null || !Util.isNumeric(num)) ? "0" : num;
    }

    public static String checkDouble(String num) {
        return (num == null || !Util.isDoubleNumeric(num)) ? "0" : num;
    }

    public static String checkFloat(String num) {
        return (num == null || !Util.isFloatNumeric(num)) ? "0" : num;
    }

    public static String checkLong(String num) {
        return (num == null || !Util.isDoubleNumeric(num)) ? "0" : num;
    }

    public static String checkStr(String str) {
        return Util.isEmpty(str) ? "" : str;
    }

    /**
     * 获取前3天时间
     */
    public static String FrontThreeDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3); // 得到前三天
        Date date = calendar.getTime();
        String dates = DateToStrtimeminute(date);
        return dates;
    }

    /**
     * 获取前15天时间
     */
    public static String FroutFifteenFDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -15); // 得到前十五天
        Date date = calendar.getTime();
        String dates = DateToStrtimeminute(date);
        return dates;
    }

    /**
     * 获取前30天时间
     */
    public static String FroutthirtyDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30); // 得到前三十天
        Date date = calendar.getTime();
        String dates = DateToStrtimeminute(date);
        return dates;
    }

    /**
     * 几分钟以后的时间
     *
     * @param after
     * @return
     */
    public static String MinueLaterTime(int after) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, after); // 得到前三十天
        Date date = calendar.getTime();
        String dates = DateToStrtimeminute(date);
        return dates;
    }

    /**
     * 日期转换字符串
     */
    public static String DateToStrtimeminute(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    /**
     * 检查Integer数据
     */
    public static Integer isInteger(Integer num) {
        return isInteger(num + "");
    }

    /**
     * 检查Double数据
     */
    public static Double isDouble(Double num) {
        return isDouble(num + "");
    }

    /**
     * String检查Integer数据
     */
    public static Integer isInteger(String num) {
        return Integer.parseInt(checkInt(num));
    }

    /**
     * String检查Double数据
     */
    public static Double isDouble(String num) {
        return Double.parseDouble(checkDouble(num));
    }

    /**
     * String检查Float数据
     */
    public static Float isFloat(String num) {
        return Float.parseFloat(checkFloat(num));
    }

    /**
     * String检查Long数据
     */
    public static long isLong(String num) {
        return Long.parseLong(checkLong(num));
    }

    /**
     * 检查String数据
     */
    public static String isString(String str) {
        if (null == str) {
            return "";
        }
        return str;

    }

    /**
     * 获取小时和分钟的字符串
     *
     * @param orderDate
     * @return
     */
    public static String getShorDate(Date mDate) {
        if (mDate == null) {
            return "00:00";
        }
        String hoursStr = "";
        String minutesStr = "";
        int hours = mDate.getHours();
        int minutes = mDate.getMinutes();
        if (hours < 10) {
            hoursStr = "0" + hours;
        } else {
            hoursStr = "" + hours;
        }
        if (minutes < 10) {
            minutesStr = "0" + minutes;
        } else {
            minutesStr = "" + minutes;
        }
        return hoursStr + ":" + minutesStr;
    }

    /**
     * 根据剩余秒数计算剩余时间（格式：x天xx小时xx分）
     *
     * @param date 给定的时间
     * @return
     */
    public static String getDifferenceTime(Date date) {
        Date now = new Date();
        int time = (int) (now.getTime() / 1000);
        String timeStr = null;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0) {
            return "00:00";
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + second;
            } else {
                hour = minute / 60;
                if (hour < 24) {
                    minute = minute % 60;
                    second = time - hour * 3600 - minute * 60;
                    timeStr = unitFormat(hour) + "小时" + unitFormat(minute) + "分";
                } else {
                    day = hour / 24;
                    hour = hour % 24;
                    minute = minute % 60;
                    second = time - day * 24 * 3600 - hour * 3600 - minute * 60;
                    timeStr = day + "天" + unitFormat(hour) + "小时" + unitFormat(minute) + "分";
                }

            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }

    public static String getPencent(int num1, int num2) {
        if (num1 == 0) {
            return "0";
        } else {
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(0);

            String result = numberFormat.format((float) num1 / (float) num2 * 100);

            return result;
        }

    }

    /**
     * 两个double相减 返回保留2位小数的字符串
     *
     * @param a
     * @param b
     * @return
     */
    public static String getDoubleMin(double a, double b) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(a - b);
    }

    /**
     * 两个double相减 返回保留2位小数的字符串
     *
     * @param a
     * @param b
     * @return
     */
    public static double getAddDouble(double x, double y) {
        BigDecimal add1 = new BigDecimal(Double.toString(x));
        BigDecimal add2 = new BigDecimal(y + "");

        return add1.add(add2).doubleValue();
    }

    /**
     * 得到小数点后两位
     *
     * @param x
     * @param y
     * @return
     */
    public static double parseDecimalDouble2(double x) {
        BigDecimal bg = new BigDecimal(x);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 两个double相减 返回保留2位小数的字符串
     *
     * @param a
     * @param b
     * @return
     */
    public static String getDoubleMin(String a, String b) {
        double x = isDouble(a);
        double y = isDouble(b);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(x - y);
    }

    /**
     * 两个double相加 返回保留2位小数的字符串
     *
     * @param a
     * @param b
     * @return
     */
    public static String getDoubleAdd(double a, double b) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(a + b);
    }

    /**
     * 两个double相加 返回保留2位小数的字符串
     *
     * @param a
     * @param b
     * @return
     */
    public static String getDoubleAdd(String a, String b) {
        double x = isDouble(a);
        double y = isDouble(b);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(x + y);
    }

    /**
     * double取两位
     *
     * @param a
     * @param b
     * @return
     */
    public static String formatDoubleReturnString(double a) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(a);
    }

    /**
     * double取两位
     *
     * @param a
     * @param b
     * @return
     */
    public static double formatDoubleReturnDouble(double a) {
        DecimalFormat df = new DecimalFormat("0.00");
        return isDouble(df.format(a));
    }

    /**
     * 把电话号码替换成带星号的 例如：182****6742 假如不是电话号码的就不进行替换
     *
     * @param phone
     * @return
     */
    public static String replacePhoneWithAsterisk(String phone) {
        String newphone = phone;
        if (isMobileNO(newphone)) {
            newphone = phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return newphone;
    }

    /**
     * @param 银行卡号 将银行卡号处理为前四后6的格式
     */
    public static String ChangeToBankCard(String bankcard) {
        String bankcardS = "";
        bankcardS = bankcard.substring(0, 6) + "*******" + bankcard.substring(bankcard.length() - 4,
                bankcard.length());
        return bankcardS;
    }

    /**
     * 获取设备屏幕信息
     *
     * @param activity
     * @return
     */
    public static String getScreenInfo(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        StringBuffer sb = new StringBuffer();
        sb.append("=========【屏幕信息】=========");
        sb.append("\n");
        sb.append("widthPixels:" + displayMetrics.widthPixels);
        sb.append("\n");
        sb.append("heightPixels:" + displayMetrics.heightPixels);
        sb.append("\n");
        sb.append("xdpi:" + displayMetrics.xdpi);
        sb.append("\n");
        sb.append("ydpi:" + displayMetrics.ydpi);
        sb.append("\n");
        sb.append("density:" + displayMetrics.density);
        sb.append("\n");
        sb.append("densityDpi:" + displayMetrics.densityDpi);
        sb.append("\n");
        sb.append("scaledDensity:" + displayMetrics.scaledDensity);
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 对外提供getMD5(String)进行md5的加密运算
     */
    public static String encodeMD5(String password) {
        // MessageDigest专门用于加密的类
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] result = messageDigest.digest(password.getBytes()); // 得到加密后的字符组数

            StringBuffer sb = new StringBuffer();

            for (byte b : result) {
                int num = b & 0xff; // 这里的是为了将原本是byte型的数向上提升为int型，从而使得原本的负数转为了正数
                String hex = Integer.toHexString(num); //这里将int型的数直接转换成16进制表示
                //16进制可能是为1的长度，这种情况下，需要在前面补0，
                if (hex.length() == 1) {
                    sb.append(0);
                }
                sb.append(hex);
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encodeBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = Base64.encodeToString(b, Base64.DEFAULT);
        }

        return s;

    }

    /**
     * 获取屏幕分辨率的高度
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context) {
        Display mDisplay = ((Activity) context).getWindowManager().getDefaultDisplay();
        return mDisplay.getHeight();
    }

    /**
     * 获取屏幕分辨率的宽度
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Activity activity) {
        Display mDisplay = activity.getWindowManager().getDefaultDisplay();
        return mDisplay.getWidth();
    }

    /**
     * 获取不同分辨率对应的尺寸
     *
     * @param activity
     * @param size
     * @return
     */
    public static double getCommonDission(Activity activity, int size) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density / 1.5 * size;
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    /**
     * 得到当前应用版本号
     *
     * @return boolean
     * @author gaof
     */
    public static String getAppVersion(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        // info.versionCode
        // info.packageName;
        // info.signatures;

        return info.versionName;
    }

    /**
     * 判断app是否安装
     *
     * @return boolean
     * @author gaof
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    /**
     * 获得AppName
     *
     * @param context
     * @param pID
     * @return
     */
    public static String getAppName(Context context, int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info =
                    (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(
                            pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +" Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * 设置屏幕背景亮度
     *
     * @param activity
     * @param bgAlpha（ 0.0-1.0）
     */
    public static void setBackackgroundAlpha(Activity activity, float bgAlpha) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = bgAlpha;
        window.setAttributes(lp);
    }

    public static void setScreenBright(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
        startAutoBrightness(activity);
    }

    public static void stopAutoBrightness(Activity activity) {

        Settings.System.putInt(activity.getContentResolver(),

                Settings.System.SCREEN_BRIGHTNESS_MODE,

                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * * 开启亮度自动调节 *
     *
     * @param activity
     */

    public static void startAutoBrightness(Activity activity) {

        Settings.System.putInt(activity.getContentResolver(),

                Settings.System.SCREEN_BRIGHTNESS_MODE,

                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

    }

    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
        // StringBuilder sb = new StringBuilder();
        // sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        // sb.append("\nDeviceSoftwareVersion = " +
        // tm.getDeviceSoftwareVersion());
        // sb.append("\nLine1Number = " + tm.getLine1Number());
        // sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        // sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        // sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        // sb.append("\nNetworkType = " + tm.getNetworkType());
        // sb.append("\nPhoneType = " + tm.getPhoneType());
        // sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        // sb.append("\nSimOperator = " + tm.getSimOperator());
        // sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        // sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        // sb.append("\nSimState = " + tm.getSimState());
        // sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        // sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        // Log.e("info", sb.toString());
    }

    public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (Util.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

            if (Util.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                //				vmPolicyBuilder.setClassInstanceLimit(ImageGridActivity.class, 1)
                //						.setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}
