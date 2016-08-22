package com.showcast.hvscroll.draw;

import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/22.
 */
public class CellDrawStyle extends  BaseDrawStyle {
    public CellDrawStyle() {
    }

    public CellDrawStyle(@ColorInt int textColor, int textSize, int strokeWidth, int strokeColor, @ColorInt int bgColor, @ColorInt int selectBgColor) {
        super(textColor, textSize, strokeWidth, strokeColor, bgColor, selectBgColor);
    }
}
