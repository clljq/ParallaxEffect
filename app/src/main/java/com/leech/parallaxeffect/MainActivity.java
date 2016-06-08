package com.leech.parallaxeffect;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.leech.parallaxeffect.ui.MyListView;
import com.leech.parallaxeffect.utils.Cheeses;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MyListView mListView = (MyListView) findViewById(R.id.lv);
        //不显示滑动到顶部的阴影
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //给ListView填充数据
        mListView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, Cheeses.NAMES));
        /**
         * 给ListView加上一个header
         * */
        final View mHeaderView = View.inflate(MainActivity.this, R.layout.view_header, null);
        final ImageView mImage = (ImageView) mHeaderView.findViewById(R.id.iv);
        mListView.addHeaderView(mHeaderView);

        /**
         * 在oncreate中View.getWidth和View.getHeight无法获得一个view的高度和宽度，这是因为View组件布局要在onResume回调后完成
         * 需要使用getViewTreeObserver().addOnGlobalLayoutListener()来获得宽度或者高度
         * OnGlobalLayoutListener可能会被多次触发，因此在得到了高度之后，要将OnGlobalLayoutListener注销掉
         * */
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mListView.setParallaxImage(mImage);
                mHeaderView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

}
