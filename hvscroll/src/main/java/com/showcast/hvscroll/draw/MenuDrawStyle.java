package com.showcast.hvscroll.draw;

import android.support.annotation.ColorInt;

/**
 * Created by taro on 16/8/19.
 */
public class MenuDrawStyle extends BaseDrawStyle {
    public MenuDrawStyle() {
        super();
    }

    public MenuDrawStyle(@ColorInt int textColor, int textSize, int strokeWidth, int strokeColor, @ColorInt int bgColor, @ColorInt int selectBgColor) {
        super(textColor, textSize, strokeWidth, strokeColor, bgColor, selectBgColor);
    }

}
