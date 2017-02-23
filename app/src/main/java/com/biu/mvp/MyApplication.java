package com.biu.mvp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.biu.modulebase.common.util.CrashHandler;
import com.biu.modulebase.common.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Lee on 2015/12/14.
 */
public class MyApplication extends Application {
    private ArrayList<Activity> activities = new ArrayList<Activity>();
    /** MyApplication实例 **/
    private static MyApplication mInstance;

    public static boolean isInMobileConnectPlayVideo=false;

    /**Umeng ShareAPI**/
//    private static UMShareAPI mShareAPI;
//    public static PushAgent mPushAgent;
    /**Umeng 获取的token**/
    public static String deviceToken ;

    /** 运行检查更新标志位，如果用户选择稍后更新，则在下次启动应用再检测更新 **/
    public static boolean allow_update = true;

    public static boolean needRefreshUserInfo =false;

//    public static UpdateVO updateVO;
//
//    public static UserInfoBean userInfo;


    /**
     * 界面是否需要刷新 map
     * 如果某个界面需要根据登录状态更改 视图UI ，那么该界面控制器需要注册下供该界面恢复时提供是否刷新判断
     */
    public static Map<String,Boolean> sRefreshMap=new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        if (!BuildConfig.LOG_DEBUG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            // 注册crashHandler
            crashHandler.init(getApplicationContext());
        }
//        initUmeng();
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
//
//    public static UserInfoBean getUserInfo(Context context) {
//        if (userInfo == null) {
//            String userInfoJsonString = PreferencesUtil.getString(context,
//                    PreferencesUtil.KEY_USER_INFO);
//            userInfo = GsonUtil.getGson().fromJson(userInfoJsonString, UserInfoBean.class);
//        }
//        return userInfo;
//    }

    /**
     * 判断给定的 ID 是否是自己
     * @param context
     * @param userId
     * @return
     */
//    public static boolean isAuthor(Context context, String userId) {
//        UserInfoBean userInfo = MyApplication.getUserInfo(context);
////        LogUtil.LogE(TAG,"userInfo.getId()==========>"+userInfo.getId());
//        return userInfo != null && userInfo.getId().equals(userId);
//    }

//    public void initUmeng() {
//        initUmengPlatform();
//        mPushAgent = PushAgent.getInstance(this);
//        //注册推送服务，每次调用register方法都会回调该接口
//        mPushAgent.register(new IUmengRegisterCallback() {
//
//            @Override
//            public void onSuccess(String deviceToken) {
//                //注册成功会返回device token
//                MyApplication.deviceToken =deviceToken;
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//                LogUtil.LogE(s);
//            }
//        });
//        mPushAgent.onAppStart();
//        mPushAgent.setDebugMode(BuildConfig.LOG_DEBUG);
//        UmengMessageHandler messageHandler = new UmengMessageHandler() {
//            /**
//             * 参考集成文档的1.6.3
//             * http://dev.umeng.com/push/android/integration#1_6_3
//             * */
//            @Override
//            public void dealWithCustomMessage(final Context context, final UMessage msg) {
//                new Handler().post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        // 对自定义消息的处理方式，点击或者忽略
//                        boolean isClickOrDismissed = true;
//                        if (msg.extra != null) {
//                            HashMap<String, String> map = (HashMap<String, String>) msg.extra;
//                            String myType = map.get("myType");
//
//                        }
//                        if (isClickOrDismissed) {
//                            //自定义消息的点击统计
//                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
//                        } else {
//                            //自定义消息的忽略统计
//                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
//                        }
//                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//
//            /**
//             * 参考集成文档的1.6.4
//             * http://dev.umeng.com/push/android/integration#1_6_4
//             * */
//            @Override
//            public Notification getNotification(Context context, UMessage msg) {
////                NotificationCompat.Builder builder = new NotificationCompat.Builder(mInstance);
////                builder.setContentTitle(msg.title).setContentText(msg.text).setTicker(msg.ticker).setAutoCancel(true);
//                UserInfoBean userInfoVO = MyApplication.getUserInfo(context);
//                userInfoVO.setHasMessage("1");
//                PreferencesUtil.putString(context, PreferencesUtil.KEY_USER_INFO,GsonUtil.toJson(userInfoVO).toString());
//                if (msg.extra != null) {
//
//                }
//                HashMap<String, String> map = (HashMap<String, String>) msg.extra;
//                String value = map.get("nSequence");
//                if(value.equals("10")){
//                    PreferencesUtil.putString(getApplicationContext(),PreferencesUtil.KEY_TOKEN,"");
//                }else if(value.equals("3")){
//                    EventBusOrderHelpAllFragment.refreshData();
//
//                }else if(value.equals("4")){
//                    EventBusOrderHelpAllFragment.refreshData();
//                }else if(value.equals("5")){
//                    EventBusOrderHelpAllFragment.refreshData();
//                }else if(value.equals("6")){
//                    EventBusOrderEarnAllFragment.refreshData();
//
//                }else if(value.equals("7")){
//                    EventBusOrderHelpAllFragment.refreshData();
//                }else if(value.equals("8")){
//                    EventBusOrderEarnAllFragment.refreshData();
//                }
////                NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////                NotificationCompat.Builder builder = new NotificationCompat.Builder(mInstance);
//////                notification.setSmallIcon(R.drawable.logo);
////                builder.setContentTitle(msg.title);
////                builder.setContentText(msg.text);
////                builder.setOngoing(true);
////                builder.setAutoCancel(true);	    //点击自动消息
////                builder.setDefaults(Notification.DEFAULT_ALL);	        //铃声,振动,呼吸灯
////                Intent intent = new Intent(context, MainVisitActivity.class);    //点击通知进入的界面
////                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
////                builder.setContentIntent(contentIntent);
//////                builder.notify(0, builder.build());
////                notifyManager.notify("textid", 123, builder.build());
////                return builder.build();
//                return super.getNotification(context, msg);
//            }
//        };
//        mPushAgent.setMessageHandler(messageHandler);
//
//        /**
//         * 该Handler是在BroadcastReceiver中被调用，故
//         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
//         * 参考集成文档的1.6.2
//         * http://dev.umeng.com/push/android/integration#1_6_2
//         * */
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                if (msg.extra != null) {
//                    HashMap<String, String> map = (HashMap<String, String>) msg.extra;
//                    String value = map.get("nSequence");
//                    if(value.startsWith("1-")){
//                        EventBusOrderHelpAllFragment.refreshData();
//
//                        Intent intent=new Intent(getApplicationContext(), OrderDetailNewActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        String id=value.substring(2);
//                        intent.putExtra("id",id);
//                        intent.putExtra("position", 0);
//                        intent.putExtra("orderClass", "OrderHelpAll");
//                        intent.putExtra("type",1);
//                        intent.putExtra("orderType", 1);
//                        startActivity(intent);
//                    }
//
//                    switch (Util.isInteger(value)){
//                        case 1:
//                            /**帮帮抢单成功，推送通知发单人已被抢，发单人点击推送跳转到帮帮订单详情界面；(您发的单子“[帮帮标题]”已被抢，快去看看把！) **/
//                            Intent intent=new Intent(getApplicationContext(), TaskDetailCanActivity.class);
//
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            break;
//                        case 2:
//                            /**我能抢单成功，推送通知发单人已被抢，发单人点击推送跳转到首页的赚钱界面进行选择抢单人。
//                             * (您发布的技能“[我能标题]”已有人下单，快去看看把！)**/
////                            EventBusPageSkip.skipOrderEarnConfirm(EventBusPageSkip.ACTIONG_SKIP_ORDER_EARN_CONFIRM2);
//                            Intent intent2=new Intent(getApplicationContext(), MainActivity.class);
//                            Constant.flag_umPush=2;
//                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent2);
//
//                            break;
//                        case 3:
//                            /**a.发单人选中抢单人，推送通知抢单人,抢单人点击推送跳转到订单-帮忙-待确认；(订单“[我能标题]”已确认接单~) **/
//                            Intent intent3=new Intent(getApplicationContext(), MainActivity.class);
//
//                            Constant.flag_umPush=3;
//                            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent3);
//
//                            break;
//                        case 4:
//                            /**b.发单人拒绝抢单人，推送通知抢单人，抢单人点击推送跳转到订单-帮忙-全部（已失效）。(发布人拒绝了订单“[我能标题]”~)**/
//                            Intent intent4=new Intent(getApplicationContext(), MainActivity.class);
//                            Constant.flag_umPush=4;
//                            intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent4);
//
//                            break;
//                        case 5:
//                            /**a.帮帮抢单人确认完成，推送通知发单人已确认，发单人点击推送跳转到订单-帮忙-待确认 进行确认完成操作；(您发的单子“[帮帮标题]”完成啦~) **/
//                            Intent intent5=new Intent(getApplicationContext(), MainActivity.class);
//                            Constant.flag_umPush=5;
//                            intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent5);
//                            break;
//                        case 6:
//                            /**b.帮帮发单人确认完成，推送通知抢单人订单已完成，抢单人跳转到订单-赚钱-已完成；(订单“[帮帮标题]”已完成~) **/
//                            Intent intent6=new Intent(getApplicationContext(), MainActivity.class);
//                            Constant.flag_umPush=6;
//                            intent6.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent6);
////                            EventBusPageSkip.skipOrderEarnConfirm(EventBusPageSkip.ACTIONG_SKIP_ORDER_EARN_CONFIRM6);
//                            break;
//                        case 7:
//                            /**c. 我能发单人确认完成，推送通知抢单人已完成，抢单人跳转到订单-帮忙待确认界面进行确认操作；(订单“[我能标题]”完成啦~) **/
//                            Intent intent7=new Intent(getApplicationContext(), MainActivity.class);
//                            Constant.flag_umPush=7;
//                            intent7.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent7);
////                            EventBusPageSkip.skipOrderEarnConfirm(EventBusPageSkip.ACTIONG_SKIP_ORDER_EARN_CONFIRM7);
//                            break;
//
//                        case 8:
//                            /**d. 我能接单人确认完成，推送通知发单人已完成，发单人跳转到订单-赚钱-已完成。(订单“[帮帮标题]”已完成~)**/
//                            Intent intent8=new Intent(getApplicationContext(), MainActivity.class);
//                            Constant.flag_umPush=8;
//                            intent8.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent8);
////                            EventBusPageSkip.skipOrderEarnConfirm(EventBusPageSkip.ACTIONG_SKIP_ORDER_EARN_CONFIRM8);
//                            break;
//                        case 9:
//                            Intent intent9=new Intent(getApplicationContext(), CreditsActivity.class);
//                            intent9.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent9);
//                            break;
//                    }
//                    //...
//                }
//            }
//        };
//        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
//        //参考http://bbs.umeng.com/thread-11112-1-1.html
//        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);
////        enablePush();
//
//    }

//    public static void enablePush(){
//        if(PreferencesUtil.putBoolean(mInstance, "allow_push", true)){
//            mPushAgent.enable(new IUmengCallback() {
//                @Override
//                public void onSuccess() {
//                    LogUtil.LogD("SUCCESS:"+mPushAgent.getRegistrationId());
//
//                }
//
//                @Override
//                public void onFailure(String s, String s1) {
//
//                }
//            });
//
//        }else{
//            mPushAgent.disable(new IUmengCallback() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onFailure(String s, String s1) {
//
//                }
//            });
//        }
//    }


//    protected  void initUmengPlatform(){
//        //微信 appid appsecret
//        PlatformConfig.setWeixin(Constant.WEIXIN_AppID, Constant.WEIXIN_AppSecret);
//        //新浪微博 appkey appsecret
//        PlatformConfig.setSinaWeibo(Constant.SINA_AppID, Constant.SINA_AppKEY);
//        // qq qzone appid appkey
//        PlatformConfig.setQQZone(Constant.QQ_AppID, Constant.QQ_AppKEY);
//
//    }

    /**
     * 获取UMShareAPI对象
     * @return
     */
//    public static UMShareAPI getUMShareAPI(){
//        if(mShareAPI ==null){
//            LogUtil.LogI("【初始创建】");
//            mShareAPI  = UMShareAPI.get(getInstance());
//        }
//        return mShareAPI;
//    }


    public static String getDeviceId(){
       return  Util.getDeviceId(mInstance);
    }


    /**
     * 将当前的Activity加入到list中
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 移除activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * finish所有List集合中的Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing())
                activity.finish();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        finishAllActivity();
        System.exit(0);
    }

//    public static UpdateVO getUpdateVO() {
//        return updateVO;
//    }
//
//    public static void setUpdateVO(UpdateVO updateVO) {
//        mInstance.updateVO = updateVO;
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
