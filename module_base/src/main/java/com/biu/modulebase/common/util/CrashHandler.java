package com.biu.modulebase.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.biu.modulebase.Constant;
import com.biu.modulebase.R;
import com.biu.modulebase.ServiceApi;
import com.biu.modulebase.ServiceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CrashHandler implements UncaughtExceptionHandler {

    /**
     * Debug Log tag
     */
    public static final String TAG = "CrashHandler";

    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;
    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private Map<String, String> infos = new LinkedHashMap<String, String>();
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".cr";

    // private String time;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else { // 如果自己处理了异常，则不会弹出错误对话框，则需要手动退出app
            // Sleep一会后结束程序
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @return true代表处理该异常，不再向上抛异常，
     * false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
     * 简单来说就是true不会弹出那个错误提示框，false就会弹出
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            Log.w(TAG, "handleException --- ex==null");
            return false;
        }
        final String msg = ex.getLocalizedMessage();
        if (msg == null) {
            return false;
        }
        // 收集设备信息
        collectCrashDeviceInfo(mContext);
        // 保存错误报告文件
        String fileName = saveCrashInfo2File(ex, Constant.ERROR_LOG_PATH);
        // 发送错误报告到服务器
        postReport(Constant.ERROR_LOG_PATH + fileName);
        // sendCrashReports2Server(mContext);
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                showToast();
                // Toast toast = Toast.makeText(mContext, "程序出错，即将退出:\r\n" +
                // msg,
                // Toast.LENGTH_LONG);
                // toast.setGravity(Gravity.CENTER, 0, 0);
                // toast.show();
                Looper.loop();
            }
        }.start();

        return false;
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        sendCrashReports2Server();
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     *
     * @param
     */
    private void sendCrashReports2Server() {
        String[] crFiles = getCrashReportFiles();
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));
            for (String fileName : sortedFiles) {
//		File cr = new File(Constant.ERROR_LOG_PATH, fileName);
                postReport(Constant.ERROR_LOG_PATH + fileName);
            }
        }
    }


    private void postReport(String filePath) {
        String paths[] = new String[]{filePath};
        Call<String> call = ServiceUtil.createService(ServiceApi.class).uploadErrorLog2(ServiceUtil.filesToMultipartBody("error_file", paths, MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    /**
     * 自定义弹出toast
     *
     * @param
     */
    @SuppressWarnings("deprecation")
    public void showToast() {
        Toast toast = new Toast(mContext);
        TextView textView = new TextView(mContext);
        textView.setText("抱歉,程序出错,即将关闭!错误报告将发送给后台管理员!");
        textView.setTextSize(18);
        textView.setBackgroundResource(R.drawable.biu_error_toast_bg);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(25, 25, 25, 25);
        toast.setView(textView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * 获取错误报告文件名
     *
     * @return
     */
    private String[] getCrashReportFiles() {
        File filesDir = new File(Constant.ERROR_LOG_PATH);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }

    /**
     * 收集错误信息，写入文件<br>
     *
     * @param ex
     * @return
     */
    private String saveCrashInfo2File(Throwable ex, String path) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append("=========【错误日志】==========\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            String fileName = DateFormat.format("yyyyMMddHHmmss",
                    Calendar.getInstance(Locale.CHINA)) + CRASH_REPORTER_EXTENSION;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    /**
     * 收集程序崩溃的设备信息
     *
     * @param ctx
     */
    public void collectCrashDeviceInfo(Context ctx) {
        infos.put("=========【奔溃时间】=========", "=");
        infos.put("Date", Util.getCurrentDate2());
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                infos.put("=========【版本信息】=========", "=");
                infos.put("版本(versionName)", pi.versionName == null ? "not set"
                        : pi.versionName);
                infos.put("版本号(versionCode)", "" + pi.versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        // 具体信息请参考后面的截图
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put("=========【硬件信息】=========", "=");
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e);
            }
        }
    }

}