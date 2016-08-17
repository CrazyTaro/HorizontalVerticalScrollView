package com.showcast.hvscroll.params;

import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/17.
 */
public class GlobalParams {
    protected int mCanvasBgColor;

    public void setCanvasBackgroundColor(@ColorInt int color) {
        mCanvasBgColor = color;
    }

    public int getCanvasBgColor() {
        return mCanvasBgColor;
    }
}
