package com.showcast.hvscroll.params;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/17.
 */
public class GlobalParams {

    protected int mCanvasBgColor = Constant.DEFAULT_BACKGROUND_COLOR;
    protected boolean mIsDrawCellStroke = false;
    protected int mStrokeColor = Constant.DEFAULT_STROKE_COLOR;
    protected int mStrokeWidth = Constant.DEFAULT_STROKE_WIDTH;

    public void setCanvasBackgroundColor(@ColorInt int color) {
        mCanvasBgColor = color;
    }

    public void setIsDrawCellStroke(boolean isDraw) {
        mIsDrawCellStroke = isDraw;
    }

    public void setStrokeColor(@ColorInt int color) {
        mStrokeColor = color;
    }

    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
    }

    public int getCanvasBgColor() {
        return mCanvasBgColor;
    }

    public boolean isDrawCellStroke() {
        return mIsDrawCellStroke;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public int getStrokeWidth() {
        return mStrokeWidth;
    }
}
