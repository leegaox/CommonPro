/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.biu.modulebase.common.widget.swiperefreshlayout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;


/**
 * The SwipeRefreshLayout should be used whenever the user can refresh the
 * contents of a view via a vertical swipe gesture. The activity that
 * instantiates this view should add an OnRefreshListener to be notified
 * whenever the swipe to refresh gesture is completed. The SwipeRefreshLayout
 * will notify the listener each and every time the gesture is completed again;
 * the listener is responsible for correctly determining when to actually
 * initiate a refresh of its content. If the listener determines there should
 * not be a refresh, it must call setRefreshing(false) to cancel any visual
 * indication of a refresh. If an activity wishes to show just the progress
 * animation, it should call setRefreshing(true). To disable the gesture and progress
 * animation, call setEnabled(false) on the view.
 * <p/>
 * <p> This layout should be made the parent of the view that will be refreshed as a
 * result of the gesture and can only support one direct child. This view will
 * also be made the target of the gesture and will be forced to match both the
 * width and the height supplied in this layout. The SwipeRefreshLayout does not
 * provide accessibility events; instead, a menu item must be provided to allow
 * refresh of the content wherever this gesture is used.</p>
 * implements android.support.v4.view.NestedScrollingParent,
 * NestedScrollingChild
 */
public class LSwipeRefreshLayout extends ViewGroup {
    private static final String LOG_TAG = LSwipeRefreshLayout.class.getSimpleName();

    /**下拉刷新進度條透明度**/
    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private static final int ALPHA_ANIMATION_DURATION = 300;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    private static final long RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 300;
    private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final float PROGRESS_BAR_HEIGHT = 4;
    private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
    private static final int REFRESH_TRIGGER_DISTANCE = 120;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    private MaterialProgressDrawable mProgress;
    //    private SwipeProgressBar mProgressBar; //the thing that shows progress is going
    private SwipeProgressBar mProgressBarBottom;
    private View mTarget; //the content that gets pulled down
    private int mOriginalOffsetTop;
    private SwipeRefreshListener mListener;

    private CircleImageView mCircleView;
    private int mCircleViewIndex = -1;

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;


    private int mFrom;
    private boolean mRefreshing = false;
    private boolean mLoading = false;
    private int mTouchSlop;
    private float mDistanceToTriggerSync = -1;

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;

    private int mMediumAnimationDuration;
    private float mFromPercentage = 0;
    private float mCurrPercentage = 0;
    private int mProgressBarHeight;
    private int mCurrentTargetOffsetTop;

    private float mInitialMotionY;
    //当子控件移动到尽头时才开始计算初始点的位置
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;
    // Whether this item is scaled up rather than clipped
    private boolean mScale;

    private float mSpinnerFinalOffset;
    // Whether the client has set a custom starting position;
    private boolean mUsingCustomStart;

    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mAlphaStartAnimation;
    private Animation mAlphaMaxAnimation;

    private boolean mNotify;

    private int mCircleWidth;

    private int mCircleHeight;
    // Whether or not the starting offset has been determined.
    private boolean mOriginalOffsetCalculated = false;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private final AccelerateInterpolator mAccelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };
    private Mode mMode = Mode.getDefault();
    //之前手势的方向，为了解决同一个触点前后移动方向不同导致后一个方向会刷新的问题，
    //这里Mode.DISABLED无意义，只是一个初始值，和上拉/下拉方向进行区分
    private Mode mLastDirection = Mode.DISABLED;
    private int mDirection = 0;

    private boolean up;
    private boolean down;
    //数据不足一屏时是否打开上拉加载模式
    private boolean loadNoFull = false;


    private int index =0;
    //对下拉或上拉进行复位
    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            if (mFrom != mOriginalOffsetTop) {
                targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
            }
            int offset = targetTop - mTarget.getTop();
            //注释掉这里，不然上拉后回复原位置会很快，不平滑
//            final int currentTop = mTarget.getTop();
//            if (offset + currentTop < 0) {
//                offset = 0 - currentTop;
//            }
            setTargetOffsetTopAndBottom(offset, false /* requires update */);
            mProgress.setArrowScale(1 - interpolatedTime);
        }
    };

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mCircleView.getTop();
            setTargetOffsetTopAndBottom(offset, false /* requires update */);
            mProgress.setArrowScale(1 - interpolatedTime);
        }
    };

    private AnimationListener mRefreshAnimListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mProgress.setAlpha(MAX_ALPHA);
                mProgress.start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
            } else {
                stopRefresh();
            }
            mCurrentTargetOffsetTop = mCircleView.getTop();
        }
    };


    //设置下方进度条的完成度百分比
    private Animation mShrinkTriggerBottom = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float percent = mFromPercentage + ((0 - mFromPercentage) * interpolatedTime);
            mProgressBarBottom.setTriggerPercentage(percent);
        }
    };

    //监听，回复初始位置
    private final AnimationListener mReturnToStartPositionListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            // Once the target content has returned to its start position, reset
            // the target offset to 0
            mCurrentTargetOffsetTop = 0;
            mLastDirection = Mode.DISABLED;
        }
    };

    //回复进度条百分比
    private final AnimationListener mShrinkAnimationListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            mCurrPercentage = 0;
        }
    };

    //回复初始位置
    private final Runnable mReturnToStartPosition = new Runnable() {

        @Override
        public void run() {
            mReturningToStart = true;
            animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(),
                    mReturnToStartPositionListener);
        }

    };

//    // Cancel the refresh gesture and animate everything back to its original state.
//    private final Runnable mCancel = new Runnable() {
//
//        @Override
//        public void run() {
//            mReturningToStart = true;
//            // Timeout fired since the user last moved their finger; animate the
//            // trigger to 0 and put the target back at its original position
//            if (mProgressBar != null || mProgressBarBottom != null) {
//                mFromPercentage = mCurrPercentage;
//                if (mDirection > 0 && ((mMode == Mode.PULL_FROM_START) || (mMode == Mode.BOTH))) {
//                    mShrinkTrigger.setDuration(mMediumAnimationDuration);
//                    mShrinkTrigger.setAnimationListener(mShrinkAnimationListener);
//                    mShrinkTrigger.reset();
//                    mShrinkTrigger.setInterpolator(mDecelerateInterpolator);
//                    startAnimation(mShrinkTrigger);
//                } else if (mDirection < 0 && ((mMode == Mode.PULL_FROM_END) || (mMode == Mode.BOTH))) {
//                    mShrinkTriggerBottom.setDuration(mMediumAnimationDuration);
//                    mShrinkTriggerBottom.setAnimationListener(mShrinkAnimationListener);
//                    mShrinkTriggerBottom.reset();
//                    mShrinkTriggerBottom.setInterpolator(mDecelerateInterpolator);
//                    startAnimation(mShrinkTriggerBottom);
//                }
//            }
//            mDirection = 0;
//            animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(),
//                    mReturnToStartPositionListener);
//        }
//
//    };

    /**
     * Simple constructor to use when creating a SwipeRefreshLayout from code.
     *
     * @param context
     */
    public LSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating SwipeRefreshLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public LSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        mProgressBarBottom = new SwipeProgressBar(this);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mProgressBarHeight = (int) (metrics.density * PROGRESS_BAR_HEIGHT);
        mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        mCircleHeight = (int) (CIRCLE_DIAMETER * metrics.density);

        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        mDistanceToTriggerSync = mSpinnerFinalOffset;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        //默认设置进度条颜色
        setColorSchemeResources(new int[]{android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light});
    }


    private void createProgressView() {
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER / 2);
        mProgress = new MaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.GONE);
        addView(mCircleView);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        removeCallbacks(mCancel);
        removeCallbacks(mReturnToStartPosition);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mReturnToStartPosition);
//        removeCallbacks(mCancel);
    }

    //对子控件进行移动
    private void animateOffsetToStartPosition(int from, AnimationListener listener) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(mMediumAnimationDuration);
        mAnimateToStartPosition.setAnimationListener(listener);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mTarget.startAnimation(mAnimateToStartPosition);
    }

    //设置进度条的显示百分比
    private void setTriggerPercentage(float percent) {
        if (percent == 0f) {
            // No-op. A null trigger means it's uninitialized, and setting it to zero-percent
            // means we're trying to reset state, so there's nothing to reset in this case.
            mCurrPercentage = 0;
            return;
        }
        mCurrPercentage = percent;
        if (((mMode == Mode.PULL_FROM_START) || (mMode == Mode.BOTH))
                && mLastDirection != Mode.PULL_FROM_END && !mLoading) {
//            mProgressBar.setTriggerPercentage(percent);
        } else if (((mMode == Mode.PULL_FROM_END) || (mMode == Mode.BOTH))
                && mLastDirection != Mode.PULL_FROM_START && !mRefreshing) {
            mProgressBarBottom.setTriggerPercentage(percent);
        }
    }

    /**
     * 停止下拉刷新
     */
    public void stopRefresh(){
        setRefreshing(false);
        mProgress.stop();
        mCircleView.setVisibility(View.GONE);
        setColorViewAlpha(MAX_ALPHA);
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(0 /* animation complete and view is hidden */);
        } else {
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop,
                    true /* requires update */);
        }
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            ensureTarget();
            mCurrPercentage = 0;
            mRefreshing = refreshing;
            if (!mUsingCustomStart) {
                mCurrPercentage = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            } else {
                mCurrPercentage = (int) mSpinnerFinalOffset;
            }

            setTargetOffsetTopAndBottom((int) mCurrPercentage - mCurrentTargetOffsetTop, true /* requires update */);
            mNotify = false;
            animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshAnimListener);

            if (mRefreshing) {
            } else {
                mLastDirection = Mode.DISABLED;
            }
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshAnimListener);
            } else {
                startScaleDownAnimation(mRefreshAnimListener);
            }
        }
    }

    public void setLoading(boolean loading) {
        if (mLoading != loading) {
            ensureTarget();
            mCurrPercentage = 0;
            mLoading = loading;
            if (mLoading) {
                mProgressBarBottom.start();
            } else {
                mLastDirection = Mode.DISABLED;
                mProgressBarBottom.stop();
            }
        }
    }

    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > mDistanceToTriggerSync) {//下拉刷新
            setRefreshing(true, true /* notify */);
        } else {
            // cancel refresh
            mRefreshing = false;
            mProgress.setStartEndTrim(0f, 0f);
            AnimationListener listener = null;
            if (!mScale) {
                listener = new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!mScale) {
                            startScaleDownAnimation(null);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                };
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
            mProgress.showArrow(false);
        }
    }


    private void startScaleDownAnimation(AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownAnimation);
    }

    private void animateOffsetToCorrectPosition(int from, AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    private void startScaleUpAnimation(AnimationListener listener) {
        mCircleView.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            // Pre API a11, alpha is used in place of scale up to show the
            // progress circle appearing.
            // Don't adjust the alpha during appearance otherwise.
            mProgress.setAlpha(MAX_ALPHA);
        }
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleAnimation);
    }

    /**
     * @deprecated Use {@link #setColorScheme(int, int, int, int)}
     */
    @Deprecated
    private void setColorScheme(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
        setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4);
    }

    /**
     * Set the four colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     */
    public void setTopColor(int colorRes1, int colorRes2, int colorRes3,
                            int colorRes4) {
        setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4);
    }

    public void setBottomColor(int colorRes1, int colorRes2, int colorRes3,
                               int colorRes4) {
        setColorSchemeResourcesBottom(colorRes1, colorRes2, colorRes3, colorRes4);
    }

    public void setColor(int colorRes1, int colorRes2, int colorRes3,
                         int colorRes4) {
        setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4);
        setColorSchemeResourcesBottom(colorRes1, colorRes2, colorRes3, colorRes4);
    }

    public void setColorSchemeResources(@ColorInt int... colorResIds) {
        final Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
        setColorSchemeResourcesBottom(colorResIds[0], colorResIds[1], colorResIds[2], colorResIds[3]);
    }

    private void setColorSchemeResourcesBottom(int colorRes1, int colorRes2, int colorRes3,
                                               int colorRes4) {
        final Resources res = getResources();
        setColorSchemeColorsBottom(res.getColor(colorRes1), res.getColor(colorRes2),
                res.getColor(colorRes3), res.getColor(colorRes4));
    }

    /**
     * Set the four colors used in the progress animation. The first color will
     * also be the color of the bar that grows in response to a user swipe
     * gesture.
     */
    private void setColorSchemeColors(int... colors) {
        ensureTarget();
        mProgress.setColorSchemeColors(colors);
    }

    private void setColorSchemeColorsBottom(int color1, int color2, int color3, int color4) {
        ensureTarget();
        mProgressBarBottom.setColorScheme(color1, color2, color3, color4);
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    public boolean isLoading() {
        return mLoading;
    }

    private void ensureTarget() {
        if (mDistanceToTriggerSync == -1) {
            if (getParent() != null && ((View) getParent()).getHeight() > 0) {
                final DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDistanceToTriggerSync = (int) Math.min(
                        ((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                        REFRESH_TRIGGER_DISTANCE * metrics.density);
            }
        }
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mCircleView)) {
                    mTarget = child;
                    mOriginalOffsetTop = mTarget.getTop() + getPaddingTop();
                    break;
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
//        mProgressBar.draw(canvas);
        mProgressBarBottom.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        mProgressBarBottom.setBounds(0, height - mProgressBarHeight, width, height);
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();
        mCircleView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop,
                (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
//        if (getChildCount() > 1 && !isInEditMode()) {
//            throw new IllegalStateException("SwipeRefreshLayout can host only one direct child");
//        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        mCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleHeight, MeasureSpec.EXACTLY));
        if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true;
            mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleView.getMeasuredHeight();
        }
        mCircleViewIndex = -1;
        // Get the index of the circleview.
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mCircleView) {
                mCircleViewIndex = index;
                break;
            }
        }
//        if (getChildCount() > 0) {
//            getChildAt(0).measure(
//                    MeasureSpec.makeMeasureSpec(
//                            getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
//                            MeasureSpec.EXACTLY),
//                    MeasureSpec.makeMeasureSpec(
//                            getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
//                            MeasureSpec.EXACTLY));
//
//        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    public boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                View lastChild = absListView.getChildAt(absListView.getChildCount() - 1);
                if (lastChild != null) {
                    return (absListView.getLastVisiblePosition() == (absListView.getCount() - 1))
                            && lastChild.getBottom() > absListView.getPaddingBottom();
                } else {
                    return false;
                }
            } else {
                return mTarget.getHeight() - mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        final int action = MotionEventCompat.getActionMasked(ev);

        if(canChildScrollDown()){//解决 ：当数据不满一屏且设置可以上拉模式后，多次快速上拉会激发上拉加载 a
            loadNoFull =true;
        }

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        float mInitialDownX =0;
        switch (action) {

            case MotionEvent.ACTION_DOWN:
                index++;
                mInitialDownX =ev.getX();
                mInitialDownY = mInitialMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
//                final float initialDownY = getMotionEventY(ev, mActivePointerId);
//                if (initialDownY == -1) {
//                    return false;
//                }
                mCurrPercentage = 0;
//                mInitialDownY = initialDownY;

                //这里用up/down记录子控件能否下拉，如果当前子控件不能上下滑动，但当手指按下并移动子控件时，控件就会变得可滑动
                //后面的一些处理不能直接使用canChildScrollUp/canChildScrollDown
                //但仍存在问题：当数据不满一屏且设置可以上拉模式后，多次快速上拉会激发上拉加载
                up = canChildScrollUp();
                down = canChildScrollDown();
                break;

            case MotionEvent.ACTION_MOVE:

                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = getMotionEventY(ev, pointerIndex);
                float x = MotionEventCompat.getX(ev, pointerIndex);
                final float yDiff = y - mInitialDownY;
                float xDiff = x- mInitialDownX;

                if(Math.abs(xDiff)> Math.abs(yDiff) && canChildScrollUp() && canChildScrollDown()){//水平滑动不拦截
                    return false;
                }

                //若上个手势的方向和当前手势方向不一致，返回
                if ((mLastDirection == Mode.PULL_FROM_START && yDiff < 0) ||
                        (mLastDirection == Mode.PULL_FROM_END && yDiff > 0)) {
                    return false;
                }
                //下拉或上拉时，子控件本身能够滑动时，记录当前手指位置，当其滑动到尽头时，
                //mInitialDownY作为下拉刷新或上拉加载的手势起点
                if ((canChildScrollUp() && yDiff > 0) || (canChildScrollDown() && yDiff < 0)) {
                    mInitialDownY = y;
                }

                //下拉
                if (yDiff > mTouchSlop) {
                    //若当前子控件能向下滑动，或者上个手势为上拉，则返回
                    if (canChildScrollUp() || mLastDirection == Mode.PULL_FROM_END) {
                        mIsBeingDragged = false;
                        return false;
                    }
                    if ((mMode == Mode.PULL_FROM_START) || (mMode == Mode.BOTH)) {
//                        mInitialDownY = y;
                        mInitialMotionY = mInitialDownY + mTouchSlop;
                        mIsBeingDragged = true;
                        mLastDirection = Mode.PULL_FROM_START;
                        mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
                    }
                }
                //上拉
                else if (-yDiff > mTouchSlop) {
                    if(!loadNoFull){//解决 ：当数据不满一屏且设置可以上拉模式后，多次快速上拉会激发上拉加载 b
                        return false;
                    }
                    //若当前子控件能向上滑动，或者上个手势为下拉，则返回
                    if (canChildScrollDown() || mLastDirection == Mode.PULL_FROM_START) {
                        mIsBeingDragged = false;
                        return false;
                    }
                    //若子控件不能上下滑动，说明数据不足一屏，若不满屏不加载，返回
                    if (!up && !down && !loadNoFull) {
                        mIsBeingDragged = false;
                        return false;
                    }
                    if ((mMode == Mode.PULL_FROM_END) || (mMode == Mode.BOTH)) {
                        mInitialDownY = y;
                        mIsBeingDragged = true;
                        mLastDirection = Mode.PULL_FROM_END;
//                        startLoad();
                    }
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mCurrPercentage = 0;
                mActivePointerId = INVALID_POINTER;
                mLastDirection = Mode.DISABLED;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex = -1;
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                mInitialDownY = mInitialMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                mCurrPercentage = 0;
//                mInitialDownY = mInitialMotionY;

                up = canChildScrollUp();
                down = canChildScrollDown();
                break;

            case MotionEvent.ACTION_MOVE:
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
//                final float yDiff = y - mInitialMotionY;
                final float yDiff = (y - mInitialMotionY) * DRAG_RATE;

                if ((mLastDirection == Mode.PULL_FROM_START && yDiff < 0) ||
                        (mLastDirection == Mode.PULL_FROM_END && yDiff > 0)) {
                    return true;
                }

                if (!mIsBeingDragged && (yDiff > 0 && mLastDirection == Mode.PULL_FROM_START)
                        || (yDiff < 0 && mLastDirection == Mode.PULL_FROM_END)) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                    mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
                }

                if (mIsBeingDragged) {//控制progressbar随着手指移动而移动
                    if (yDiff > 0) {
                        moveSpinner(yDiff);
                        if (yDiff > mDistanceToTriggerSync) {
                            if (mLastDirection == Mode.PULL_FROM_END) {
                                return true;

                            }
                            if ((mMode == Mode.PULL_FROM_START) || (mMode == Mode.BOTH)) {
                                mLastDirection = Mode.PULL_FROM_START;
//                                startRefresh();
                            }
                        }
                    } else if (-yDiff > mDistanceToTriggerSync) {
                        if ((!up && !down && !loadNoFull) || mLastDirection == Mode.PULL_FROM_START) {
                            return true;
                        }
                        if ((mMode == Mode.PULL_FROM_END) || (mMode == Mode.BOTH)) {
                            mLastDirection = Mode.PULL_FROM_END;
                            startLoad();
                        }
                    }
//                    // User velocity passed min velocity; trigger a refresh
//                    if (yDiff > mDistanceToTriggerSync) {
//                        // User movement passed distance; trigger a refresh
//                        if (mLastDirection == Mode.PULL_FROM_END) {
//                            return true;
//
//                        }
//                        if ((mMode == Mode.PULL_FROM_START) || (mMode == Mode.BOTH)) {
//                            mLastDirection = Mode.PULL_FROM_START;
//                            moveSpinner(yDiff);
//                            startRefresh();
//                        }
//                    } else if (-yDiff > mDistanceToTriggerSync) {
//                        if ((!up && !down && !loadNoFull) || mLastDirection == Mode.PULL_FROM_START) {
//                            return true;
//                        }
//                        if ((mMode == Mode.PULL_FROM_END) || (mMode == Mode.BOTH)) {
//                            mLastDirection = Mode.PULL_FROM_END;
//                            startLoad();
//                        }
//                    } else {
//                        if (!up && !down && yDiff < 0 && !loadNoFull) {
//                            return true;
//                        }
//                        // Just track the user's movement
//                        //根据手指移动距离设置进度条显示的百分比
//                        setTriggerPercentage(
//                                mAccelerateInterpolator.getInterpolation(
//                                        Math.abs(yDiff) / mDistanceToTriggerSync));
//                        updateContentOffsetTop((int) yDiff);
//                        if (mTarget.getTop() == getPaddingTop()) {
//                            // If the user puts the view back at the top, we
//                            // don't need to. This shouldn't be considered
//                            // cancelling the gesture as the user can restart from the top.
////                            removeCallbacks(mCancel);
//                            mLastDirection = Mode.DISABLED;
//                        } else {
//                            mDirection = (yDiff > 0 ? 1 : -1);
////                            updatePositionTimeout();
//                        }
//                    }
//                    mInitialDownY = y;
                }
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                mInitialDownY = MotionEventCompat.getY(ev, index);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                final float y1 = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y1 - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                finishSpinner(overscrollTop);
                mActivePointerId = INVALID_POINTER;
                return false;
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mCurrPercentage = 0;
                mActivePointerId = INVALID_POINTER;
                mLastDirection = Mode.DISABLED;
                return false;
        }

        return true;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void moveSpinner(float overscrollTop) {
        mProgress.showArrow(true);
        float originalDragPercent = overscrollTop / mDistanceToTriggerSync;

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - mDistanceToTriggerSync;
        float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset - mOriginalOffsetTop
                : mSpinnerFinalOffset;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        // where 1.0f is a full circle
        if (mCircleView.getVisibility() != View.VISIBLE) {
            mCircleView.setVisibility(View.VISIBLE);
        }
        if (!mScale) {
            ViewCompat.setScaleX(mCircleView, 1f);
            ViewCompat.setScaleY(mCircleView, 1f);
        }
        if (overscrollTop < mDistanceToTriggerSync) {
            if (mScale) {
                setAnimationProgress(overscrollTop / mDistanceToTriggerSync);
            }
            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA
                    && !isAnimationRunning(mAlphaStartAnimation)) {
                // Animate the alpha
                startProgressAlphaStartAnimation();
            }
            float strokeStart = adjustedPercent * .8f;
            mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
            mProgress.setArrowScale(Math.min(1f, adjustedPercent));
        } else {
            if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation();
            }
        }
        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mProgress.setProgressRotation(rotation);
        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }


    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        // Pre API a11, alpha is used in place of scale. Don't also use it to
        // show the trigger point.
        if (mScale && isAlphaUsedForScale()) {
            return null;
        }
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress
                        .setAlpha((int) (startingAlpha + ((endingAlpha - startingAlpha)
                                * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        mCircleView.setAnimationListener(null);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(alpha);
        return alpha;
    }

    /**
     * Pre API a11, this does an alpha animation.
     *
     * @param progress
     */
    private void setAnimationProgress(float progress) {
        if (isAlphaUsedForScale()) {
            setColorViewAlpha((int) (progress * MAX_ALPHA));
        } else {
            ViewCompat.setScaleX(mCircleView, progress);
            ViewCompat.setScaleY(mCircleView, progress);
        }
    }

    /**
     * Pre API a11, alpha is used to make the progress circle appear instead of scale.
     */
    private boolean isAlphaUsedForScale() {
        return android.os.Build.VERSION.SDK_INT < 11;
    }

    private void setColorViewAlpha(int targetAlpha) {
        mCircleView.getBackground().setAlpha(targetAlpha);
        mProgress.setAlpha(targetAlpha);
    }

    private void startRefresh() {
        if (!mLoading && !mRefreshing) {
//            removeCallbacks(mCancel);
            mReturnToStartPosition.run();
            setRefreshing(true);
            mListener.onRefresh();
        }
    }

    private void startLoad() {
        if (!mLoading && !mRefreshing) {
//            removeCallbacks(mCancel);
            mReturnToStartPosition.run();
            setLoading(true);
            if(mListener !=null)
            mListener.onLoadMore();
        }
    }

    //手指移动时更新子控件的位置
    private void updateContentOffsetTop(int targetTop) {
        final int currentTop = mTarget.getTop();
        if (targetTop > mDistanceToTriggerSync) {
            targetTop = (int) mDistanceToTriggerSync;
        }
        //注释掉，否则上拉的时候子控件会向下移动
//        else if (targetTop < 0) {
//            targetTop = 0;
//        }
        setTargetOffsetTopAndBottom(targetTop - currentTop, true);
    }

    //根据偏移量对子控件进行移动
    private void setTargetOffsetTopAndBottom(int offset) {
        mTarget.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mTarget.getTop();
    }

    //根据偏移量对子控件进行移动
    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mCircleView.bringToFront();
        mCircleView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mCircleView.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

//    private void updatePositionTimeout() {
//        removeCallbacks(mCancel);
//        postDelayed(mCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
//    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mInitialDownY = MotionEventCompat.getY(ev, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }


    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public void setLoadNoFull(boolean load) {
        this.loadNoFull = load;
    }

    public static enum Mode {
        /**
         * Disable all Pull-to-Refresh gesture and Refreshing handling
         */
        DISABLED(0x0),

        /**
         * Only allow the user to Pull from the start of the Refreshable View to
         * refresh. The start is either the Top or Left, depending on the
         * scrolling direction.
         */
        PULL_FROM_START(0x1),

        /**
         * Only allow the user to Pull from the end of the Refreshable View to
         * refresh. The start is either the Bottom or Right, depending on the
         * scrolling direction.
         */
        PULL_FROM_END(0x2),

        /**
         * Allow the user to both Pull from the start, from the end to refresh.
         */
        BOTH(0x3);

        static Mode getDefault() {
            return BOTH;
        }

        boolean permitsPullToRefresh() {
            return !(this == DISABLED);
        }

        boolean permitsPullFromStart() {
            return (this == Mode.BOTH || this == Mode.PULL_FROM_START);
        }

        boolean permitsPullFromEnd() {
            return (this == Mode.BOTH || this == Mode.PULL_FROM_END);
        }

        private int mIntValue;

        // The modeInt values need to match those from attrs.xml
        Mode(int modeInt) {
            mIntValue = modeInt;
        }

        int getIntValue() {
            return mIntValue;
        }


    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }


    /**
     * Simple AnimationListener to avoid having to implement unneeded methods in
     * AnimationListeners.
     */
    private class BaseAnimationListener implements AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * Set the listener to be notified when a refresh or loadMore is triggered via the swipe
     * gesture.
     */
    public void setSwipeRefreshListener(SwipeRefreshListener l){
        mListener =l;
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface SwipeRefreshListener {
        public void onRefresh();

        public void onLoadMore();
    }

}
