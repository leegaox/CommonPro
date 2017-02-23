package com.biu.modulebase.common.widget.loading;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import com.biu.modulebase.R;


/**
 * @author chenbixin
 * @Title: {标题}
 * @Description:{处理登陆的dialog}
 * @date ${2016/9/28}
 */
public class MyProgressDialog extends Dialog {
    private Context mContext;

    public MyProgressDialog(Context context, int themeResId) {
        super(context, themeResId);

    }
    public MyProgressDialog(Context context){
        super(context);
        mContext=context;

    }

    public  static MyProgressDialog createDialog(Context context){
            MyProgressDialog myProgressDialog=new MyProgressDialog(context, R.style.LoadingDialog);
        myProgressDialog.setContentView(R.layout.loading);
        myProgressDialog.getWindow().getAttributes().gravity= Gravity.CENTER;
        return myProgressDialog;
    }


}
