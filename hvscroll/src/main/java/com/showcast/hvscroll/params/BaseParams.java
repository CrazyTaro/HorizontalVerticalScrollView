package com.showcast.hvscroll.params;

/**
 * Created by taro on 16/8/19.
 */
public class BaseParams {
    public BaseParams() {
    }

    public BaseParams(int width, int height) {
        super();
        this.setWidth(width);
        this.setHeight(height);
    }

    public void setIsDraw(boolean isDraw) {
        mIsDraw = isDraw;
    }

    public boolean setWidth(int width) {
        if (width >= 0) {
            mWidth = width;
            return true;
        } else {
            return false;
        }
    }

    public boolean setHeight(int height) {
        if (height >= 0) {
            mHeight = height;
            return true;
        } else {
            return false;
        }
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean isDraw() {
        return mIsDraw;
    }

    protected int mWidth;
    protected int mHeight;

    protected boolean mIsDraw;
}
