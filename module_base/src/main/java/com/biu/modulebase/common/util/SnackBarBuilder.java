package com.biu.modulebase.common.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import com.biu.modulebase.R;

/**
 * @author Lee
 * @Title: {SnackBar构建工具类}
 * @Description:{链式调用，轻松自定义 {@link Snackbar} style
 * eg.:new SnackBarBuilder(this,"联系人权限被拒", Snackbar.LENGTH_LONG).setBackgroundColor(R.color.white).show(); }
 * @date 2017/2/24
 */
public class SnackBarBuilder {

    private Snackbar snackbar;

    private Activity activity;

    //the background color of Snackbar
    private int backgroundColorRes;

    //默认colorAccent
    private int actionTextColor = R.color.colorAccent;

    private CharSequence actionString;

    private View.OnClickListener actionListener;

    /**
     * @param mActivity
     * @param text      The text to show.  Can be formatted text.
     * @param duration  How long to display the message.  Either Snackbar.LENGTH_SHORT or Snackbar.LENGTH_SHORT
     */
    public SnackBarBuilder(Activity mActivity, @NonNull CharSequence text, int duration) {
        activity = mActivity;
        snackbar = Snackbar.make(((ViewGroup) mActivity.getWindow().getDecorView().getRootView().findViewById(
                android.R.id.content)).getChildAt(0), text, duration);
    }


    public SnackBarBuilder setBackgroundColor(int backgroundColorRes) {
        this.backgroundColorRes = backgroundColorRes;
//        snackbar.getView().setBackgroundColor(activity.getResources().getColor(backgroundColorRes));
        return this;
    }

    /**
     * Sets the text color of the action specified in
     * {@link #setAction(CharSequence, View.OnClickListener)}.
     */
    public SnackBarBuilder setActionTextColor(int actionTextColor) {
        this.actionTextColor = actionTextColor;
        return this;
    }

    /**
     * Set the action to be displayed in this {@link Snackbar}.
     *
     * @param actionString   Text to display
     * @param actionListener callback to be invoked when the action is clicked
     */
    public SnackBarBuilder setAction(CharSequence actionString, final View.OnClickListener actionListener) {
        this.actionString = actionString;
        this.actionListener = actionListener;
//        snackbar.setAction(actionString, actionListener);
        return this;
    }

    /**
     * Show the {@link Snackbar}.
     */
    public void show() {
        if (snackbar == null)
            return;
        snackbar.getView().setBackgroundColor(activity.getResources().getColor(backgroundColorRes));
        snackbar.setActionTextColor(activity.getResources().getColor(actionTextColor));
        if (actionListener != null)
            snackbar.setAction(actionString, actionListener);
        snackbar.show();
    }
}
