package com.showcast.hvscroll.draw;

import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/17.
 */
public class BaseTableDrawStyle extends BaseDrawStyle{

    protected int mHeight;
    protected int mWidth;

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public boolean setHeight(int height) {
        if (height < 0) {
            return false;
        }
        mHeight = height;
        return true;
    }

    public boolean setWidth(int width) {
        if (width < 0) {
            return false;
        }
        mWidth = width;
        return true;
    }
}
