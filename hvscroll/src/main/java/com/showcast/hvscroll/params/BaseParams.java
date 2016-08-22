package com.showcast.hvscroll.params;

import android.support.annotation.NonNull;

import com.showcast.hvscroll.draw.BaseDrawStyle;

import java.util.Map;

/**
 * Created by taro on 16/8/19.
 */
public class BaseParams {
    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 80;

    protected int mWidth;
    protected int mHeight;
    protected Map<String, BaseDrawStyle> mStyleMap;
    protected BaseDrawStyle mDefaultDrawStyle;

    public BaseParams() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public BaseParams(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        this.initialConstructor();
    }

    protected void initialConstructor() {

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

    public BaseDrawStyle addNewDrawStyle(@NonNull String tag, @NonNull BaseDrawStyle drawStyle) {
        return mStyleMap.put(tag, drawStyle);
    }

    public BaseDrawStyle removeDrawStyle(String tag) {
        return mStyleMap.remove(tag);
    }

    public boolean isContains(String tag) {
        return mStyleMap.containsKey(tag);
    }

    public BaseDrawStyle getDrawStyle(String tag) {
        return mStyleMap.get(tag);
    }

    public void clearDrawStyles() {
        mStyleMap.clear();
    }

    public void setDefaultDrawStyle(@NonNull BaseDrawStyle style) {
        mDefaultDrawStyle = style;
    }

    public BaseDrawStyle getDefaultDrawStyle() {
        return mDefaultDrawStyle;
    }
}
