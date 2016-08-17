package com.showcast.hvscroll.params;

import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/17.
 */
public class TableParams {
    protected int mRowMenuHeight;
    protected int mRowMenuTextColor;
    protected int mRowMenuBgColor;
    protected int mRowMenuTextSize;
    protected int mColumnMenuWidth;
    protected int mColumnMenuTextColor;
    protected int mColumnMenuBgColor;
    protected int mColumnMenuTextSize;

    public boolean setRowMenuHeight(int rowMenuHeight) {
        if (rowMenuHeight < 0) {
            return false;
        }
        this.mRowMenuHeight = rowMenuHeight;
        return true;
    }

    public void setRowMenuTextColor(@ColorInt int rowMenuTextColor) {
        this.mRowMenuTextColor = rowMenuTextColor;
    }

    public void setRowMenuBackgroundColor(@ColorInt int rowMenuBgColor) {
        this.mRowMenuBgColor = rowMenuBgColor;
    }

    public boolean setRowMenuTextSize(int rowMenuTextSize) {
        if (rowMenuTextSize < 0) {
            return false;
        }
        this.mRowMenuTextSize = rowMenuTextSize;
        return true;
    }

    public boolean setColumnMenuWidth(int columnMenuWidth) {
        if (columnMenuWidth < 0) {
            return false;
        }
        this.mColumnMenuWidth = columnMenuWidth;
        return true;
    }

    public void setColumnMenuTextColor(@ColorInt int columnMenuTextColor) {
        this.mColumnMenuTextColor = columnMenuTextColor;
    }

    public void setColumnMenuBackgroundColor(@ColorInt int columnMenuBgColor) {
        this.mColumnMenuBgColor = columnMenuBgColor;
    }

    public boolean setColumnMenuTextSize(int columnMenuTextSize) {
        if (columnMenuTextSize < 0) {
            return false;
        }
        this.mColumnMenuTextSize = columnMenuTextSize;
        return true;
    }

}
