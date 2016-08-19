package com.showcast.hvscroll.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.showcast.hvscroll.draw.HorizontalVerticalScrollDraw;
import com.showcast.hvscroll.entity.AbsTableEntity;
import com.showcast.hvscroll.params.GlobalParams;

/**
 * Created by taro on 16/8/17.
 */
public class HorizontalVerticalScrollView extends View {
    private HorizontalVerticalScrollDraw mHvDraw = new HorizontalVerticalScrollDraw(this);

    public HorizontalVerticalScrollView(Context context) {
        super(context);
        this.initial();
    }

    public HorizontalVerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initial();
    }

    public HorizontalVerticalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initial();
    }

    private void initial() {
        mHvDraw.setParams(new GlobalParams());
        mHvDraw.setTable(AbsTableEntity.getExampleTable());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mHvDraw.drawCanvas(canvas);
    }
}
