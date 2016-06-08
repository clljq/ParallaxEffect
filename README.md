##简介
通过自定义ListView，重写其onScrollBy方法可以实现顶部下拉图片放大，松手回弹的效果，涉及到ValueAnimator动画
##核心代码如下
###下拉放大
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
###松手回弹
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
                 * 执行ValueAnimator动画
                 * */
                startValueAnimator(startHeight, endHeight);
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