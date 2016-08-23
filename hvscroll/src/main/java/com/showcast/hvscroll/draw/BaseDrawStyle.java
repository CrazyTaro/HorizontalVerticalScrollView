package com.showcast.hvscroll.draw;

import android.support.annotation.ColorInt;

import com.showcast.hvscroll.params.Constant;

/**
 * Created by taro on 16/8/17.
 */
public class BaseDrawStyle {
    protected int mStrokeColor = Constant.DEFAULT_COLOR;
    protected int mStrokeWidth = Constant.DEFAULT_STROKE_WIDTH;
    protected int mTextColor = Constant.DEFAULT_TEXT_COLOR;
    protected int mTextSize = Constant.DEFAULT_TEXT_SIZE;
    protected int mBackgroundColor = Constant.COLOR_TRANSPARENT;
    protected int mSelectBgColor = Constant.DEFAULT_COLOR;
    protected boolean mIsDraw = true;

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

    public void setIsDraw(boolean isDraw) {
        mIsDraw = isDraw;
    }

    public boolean isDraw() {
        return mIsDraw;
    }
}
