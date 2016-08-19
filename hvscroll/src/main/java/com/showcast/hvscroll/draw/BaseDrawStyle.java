package com.showcast.hvscroll.draw;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/17.
 */
public class BaseDrawStyle {
    public static final int UNDRAW_COLOR = Color.TRANSPARENT;

    public static final int DEFAULT_COLOR = Color.WHITE;
    public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final int DEFAULT_STROKE_WIDTH = 2;
    public static final int DEFAULT_TEXT_SIZE = 50;

    protected int mStrokeColor = DEFAULT_COLOR;
    protected int mStrokeWidth = DEFAULT_STROKE_WIDTH;
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mTextSize = DEFAULT_TEXT_SIZE;
    protected int mBackgroundColor = DEFAULT_COLOR;
    protected int mSelectBgColor = DEFAULT_COLOR;

    public BaseDrawStyle() {
    }

    public BaseDrawStyle(@ColorInt int textColor, int textSize, int strokeWidth, int strokeColor, @ColorInt int bgColor, @ColorInt int selectBgColor) {
        super();
        this.setTextColor(textColor);
        this.setTextSize(textSize);
        this.setStrokeWidth(strokeWidth);
        this.setStrokeColor(strokeColor);
        this.setBackgroundColor(bgColor);
        this.setSelectBackgroundColor(selectBgColor);
    }

    public int getStrokeWidth() {
        return mStrokeColor;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

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

    public boolean setStrokeWidth(int width) {
        if (width < 0) {
            return false;
        } else {
            mStrokeWidth = width;
            return true;
        }
    }

    public void setStrokeColor(@ColorInt int color) {
        mStrokeColor = color;
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
