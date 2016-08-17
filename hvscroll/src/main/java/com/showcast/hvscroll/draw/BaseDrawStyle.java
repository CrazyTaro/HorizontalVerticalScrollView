package com.showcast.hvscroll.draw;

import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/17.
 */
public class BaseDrawStyle {
    protected int mTextColor;
    protected int mTextSize;
    protected int mBackgroundColor;
    protected int mSelectBgColor;

    public int getTextColor() {
        return mTextColor;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getSelectBackgroundColor() {
        return mSelectBgColor;
    }

    public void setBackgroundColor(@ColorInt int color) {
        mBackgroundColor = color;
    }

    public void setSelectBackgroundColor(@ColorInt int color) {
        mSelectBgColor = color;
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
    }

    public boolean setTextSize(int textSize) {
        if (textSize < 0) {
            return false;
        }
        mTextSize = textSize;
        return true;
    }
}
