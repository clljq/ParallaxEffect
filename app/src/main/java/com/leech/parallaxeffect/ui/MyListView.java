package com.leech.parallaxeffect.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 这是一个顶部图片具有视差特效的自定义ListView
 */
public class MyListView extends ListView {

    private int mOriginalHeight;
    private int drawableHeight;
    private ImageView mImage;

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context) {
        super(context);
    }

    /**
     * 拿到ImageView的引用，用来获取image的实际高度
     */
    public void setParallaxImage(ImageView mImage) {
        this.mImage = mImage;
        //获取image在ImageView中的高度
        mOriginalHeight = mImage.getHeight();
        //获取image图源的高度
        drawableHeight = mImage.getDrawable().getIntrinsicHeight();

    }

    /**
     * deltaY : 竖直方向的瞬时偏移量 顶部到头下拉为-, 底部到头上拉为+
     * scrollY : 竖直方向的偏移量
     * scrollRangeY : 竖直方向滑动的范围
     * maxOverScrollY : 竖直方向最大滑动范围
     * isTouchEvent : 是否是手指触摸滑动, true为手指, false为惯性
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY,
                                   int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        /**
         * 手指触摸顶部到头下拉
         * */
        if (isTouchEvent && deltaY < 0) {
            // 把拉动的瞬时变化量的绝对值交给Header, 就可以实现放大效果，高度不超过image的图源高度时才允许继续放大
            if (mImage.getHeight() <= drawableHeight) {
                int newHeight = (int) (mImage.getHeight() + Math.abs(deltaY / 3.0f));
                mImage.getLayoutParams().height = newHeight;
                //当image的布局参数发生变化之后要请求重新布局
                mImage.requestLayout();
                mImage.invalidate();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    /**
     * 当松开手指之后就执行回弹动画，从放大后的高度回弹到初始高度
     * */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                final int startHeight = mImage.getHeight();
                final int endHeight = mOriginalHeight;
                /**
                 * 执行ValueAnimator的动画（属性动画）
                 * */
                startValueAnimator(startHeight, endHeight);

                 /**
                  * 执行回弹动画的第二种方式：自定义Animation
                  * */
//                ResetAnimation animation = new ResetAnimation(mImage, startHeight, endHeight);
//                startAnimation(animation);
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void startValueAnimator(final int startHeight, final int endHeight) {
        ValueAnimator mValueAnim = ValueAnimator.ofInt(startHeight,endHeight);
        //通过设置监听的方式执行伴随动画
        mValueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator mAnim) {
                Integer currentHeight = (Integer) mAnim.getAnimatedValue();
                mImage.getLayoutParams().height = currentHeight;
                invalidate();
                mImage.requestLayout();
            }
        });

        //设置插补器，有回弹效果
        mValueAnim.setInterpolator(new OvershootInterpolator());
        mValueAnim.setDuration(500);
        mValueAnim.start();
    }
}























