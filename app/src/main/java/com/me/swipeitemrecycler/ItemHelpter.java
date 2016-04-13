package com.me.swipeitemrecycler;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;


/**
 * Created by jhon on 2016/4/11.
 */
public class ItemHelpter implements RecyclerView.OnItemTouchListener,GestureDetector.OnGestureListener {
    /**
     * 动画执行的时间
     */
    private static  int DURATION = 200;

    /**
     * item是否子拖动
     */
    private boolean mIsDragging;
    /**
     * 手势类用来判断手势滑动
     */
    private GestureDetectorCompat mGestureDetector;
    private Callback mCallback;
    private Context mContext;

    private Animator mAnimator;
    /**
     * 触发scroll滑动距离
     */
    private int mTouchSlop;
    /**
     * 触发scroll滑动的速度
     */

    private int mMaxVelocity ;
    private int mMinVelocity ;
    /**
     * 点击button后是否button是否会自动关闭
     */
    private boolean isCloseButton = false;

    private float mLastx;
    private float mLasty;

    private SwipeLayout mSwipeLayout;

    public ItemHelpter(Context context,Callback callback) {
        mContext = context;
        mCallback = callback;
        mGestureDetector = new GestureDetectorCompat(mContext,this);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop()/2;
        mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinVelocity = configuration.getScaledMinimumFlingVelocity();
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(Math.abs(velocityX) > mMinVelocity && Math.abs(velocityX) < mMaxVelocity) {
            if(!smoothView(false) ) {
                if(isClosed())
                    mSwipeLayout = null;
                return true;
            }
                return true;
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        int acton = MotionEventCompat.getActionMasked(e);
        int x = (int) e.getX();
        int y = (int) e.getY();
        /**
         * 如果recycler滑动就关闭item
         */
        if(rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE){
               if(mSwipeLayout != null){
                  smoothView(true);
                   mSwipeLayout = null;
               }
            return false;
        }
        if(mAnimator != null && mAnimator.isRunning()){
            return true;
        }

        boolean needIntercept =  false;
        switch (acton){
            case MotionEvent.ACTION_DOWN:
                mLastx = e.getX();
                mLasty = e.getY();
                if(mSwipeLayout != null){
                    return inView(x,y);
                }
                mSwipeLayout = mCallback.getSwipLayout(mLastx,mLasty);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (x -(int) mLastx);
                int deltaY = (y - (int) mLasty);
                if(Math.abs(deltaY) > Math.abs(deltaX))
                    return false;
                //如果移动距离达到要求，则拦截
                needIntercept = mIsDragging = ( mSwipeLayout!= null && Math.abs(deltaX) >= mTouchSlop);


            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(isExpanded()){

                    if (inView(x, y)) {

                    }else{

                       if(isCloseButton) {
//                        needIntercept = false; //这里可以直接做结束动画
                       } else {
                           return false; //这样不会将界面直接关闭
                       }
                    }
                    smoothView(true);
                }
                break;
        }
        return needIntercept;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if(mAnimator != null && mAnimator.isRunning() || mSwipeLayout == null)
            return;

        if(mGestureDetector.onTouchEvent(e)){
            mIsDragging = true;
            return;
        }

        int x = (int)e.getX();
        int action = MotionEventCompat.getActionMasked(e);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int offset = (int)(mLastx - x);
                if(mIsDragging){
                    horizontalDrag(offset);
                }
                mLastx = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if(mIsDragging){
                    if(!smoothView(false) && isClosed())
                        mSwipeLayout = null;
                    mIsDragging = false;
                }
                break;

        }

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * 是否是开启状态
     * @return
     */
    public boolean isExpanded(){
        return mSwipeLayout != null && mSwipeLayout.mCenterView.getScrollX() == mSwipeLayout.mRightWidth;
    }

    /**
     * 私有方法判读是不是关闭状态
     * @return
     */
    private boolean isClosed(){
        return mSwipeLayout != null && mSwipeLayout.mCenterView.getScrollX() == 0;
    }

    /**
     * 执行滑动动画
     * 传true会关闭按钮位置
     * @return
     */
    private boolean smoothView(Boolean isClose){
        int scrollX = mSwipeLayout.mCenterView.getScrollX();
        int to = 0;
        int width = mSwipeLayout.mRightWidth / 2;
        int duration = DURATION;
         System.out.println(scrollX);
        if(mAnimator != null){
            return false;
        }

        if(!isClose) {
            if (scrollX > width) {
                to = mSwipeLayout.mRightWidth;
                duration = (int) (DURATION * (float) (scrollX - width) / width);
            } else {
                to = 0;
                duration = (int) (duration * (float) (scrollX) / width);
            }
        } else {
            to = 0;
            duration = (int) (duration * (float) (scrollX) / width);
        }
        if(to == scrollX){
            return  false;
        }
        mAnimator = ObjectAnimator.ofInt(mSwipeLayout.mCenterView,"scrollX",to);
        mAnimator.setDuration(duration);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator = null;
                if(isClosed()){
                    mSwipeLayout = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimator = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();

        return true;
    }

    public  interface Callback {
         SwipeLayout getSwipLayout(float x,float y);
    }

    /**
     * 点击位置是否是已经打开的view
     * @param x
     * @param y
     * @return
     */
    private boolean inView(int x, int y) {

        if (mSwipeLayout == null)
            return false;

        int scrollX = mSwipeLayout.mCenterView.getScrollX();
        int left = mSwipeLayout.mCenterView.getWidth() - scrollX;
        int top = mSwipeLayout.mCenterView.getTop();
        int right = left + mSwipeLayout.mRightWidth ;
        int bottom = mSwipeLayout.mCenterView.getBottom();
        Rect rect = new Rect(left, top, right, bottom);
        return rect.contains(x, y);
    }

    /**
     *
     * 根据touch事件来滚动View的scrollX
     *
     * @param delta
     */
    private void horizontalDrag(int delta) {
        int scrollX = mSwipeLayout.mCenterView.getScrollX();
        int scrollY = mSwipeLayout.mCenterView.getScrollY();
        if ((scrollX + delta) <= 0) {
            mSwipeLayout.mCenterView.scrollTo(0, scrollY);
            return;
        }
        int horRange = mSwipeLayout.mRightWidth;
        scrollX += delta;
        if (Math.abs(scrollX) < horRange) {
            mSwipeLayout.mCenterView.scrollTo(scrollX, scrollY);
        } else {
            mSwipeLayout.mCenterView.scrollTo(horRange, scrollY);
        }
    }

}
