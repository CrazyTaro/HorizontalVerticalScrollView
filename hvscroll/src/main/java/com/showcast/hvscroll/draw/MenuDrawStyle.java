package com.showcast.hvscroll.draw;

import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/19.
 */
public class MenuDrawStyle extends BaseDrawStyle {
    protected boolean mIsDrawMenu = false;
    protected boolean mIsMenuFrozenX = false;
    protected boolean mIsMenuFrozenY = false;

    public MenuDrawStyle() {
        super();
    }

    public MenuDrawStyle(@ColorInt int textColor, int textSize, int strokeWidth, int strokeColor, @ColorInt int bgColor, @ColorInt int selectBgColor) {
        super(textColor, textSize, strokeWidth, strokeColor, bgColor, selectBgColor);
    }

    public void setIsDraw(boolean isDraw) {
        mIsDrawMenu = isDraw;
    }

    public void setMenuFrozen(boolean frozenInX, boolean frozenInY) {
        mIsMenuFrozenX = frozenInX;
        mIsMenuFrozenY = frozenInY;
    }

    public boolean isDraw() {
        return mIsDrawMenu;
    }

    public boolean isFrozenX() {
        return mIsMenuFrozenX;
    }

    public boolean isFrozenY() {
        return mIsMenuFrozenY;
    }
}
