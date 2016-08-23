package com.showcast.hvscroll.params;

import android.graphics.Color;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by taro on 16/8/22.
 */
public class Constant {
    @Retention(RetentionPolicy.SOURCE)
    @FloatRange(from = 0, to = 255)
    public @interface Alpha {
    }

    @Retention(RetentionPolicy.SOURCE)
    @FloatRange(from = 0, to = 1)
    public @interface MaskPercent {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {MENU_ROW, MENU_COLUMN})
    public @interface MenuType {
    }

    /**
     * menu type of row menu
     */
    public static final int MENU_ROW = 0;
    /**
     * menu type of column menu
     */
    public static final int MENU_COLUMN = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {FIXED_MENU_INDEX_ROW, FIXED_MENU_INDEX_COLUMN})
    public @interface FixedMenuIndex {
    }

    /**
     * default row index of row menu
     */
    public static final int FIXED_MENU_INDEX_ROW = 0;
    /**
     * default column index of column menu
     */
    public static final int FIXED_MENU_INDEX_COLUMN = 0;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {LINE_ROW, LINE_COLUMN})
    public @interface LineType {
    }

    /**
     * line type of row line
     */
    public static final int LINE_ROW = 0;
    /**
     * line type of column line
     */
    public static final int LINE_COLUMN = 1;


    /**
     * default style tag
     */
    public static final String DEFAULT_STYLE_TAG = "hvv_default_style_tag";

    /**
     * default width for cell or menu.
     */
    public static final int DEFAULT_WIDTH = 200;
    /**
     * default height for cell or menu
     */
    public static final int DEFAULT_HEIGHT = 80;

    /**
     * default span count,value = {@value DEFAULT_SPAN_COUNT}(the own cell)
     */
    public static final int DEFAULT_SPAN_COUNT = 1;
    /**
     * default cell index,value = {@value DEFAULT_CELL_INDEX}
     */
    public static final int DEFAULT_CELL_INDEX = -1;

    /**
     * transparent color,see {@link Color#TRANSPARENT}
     */
    public static final int COLOR_TRANSPARENT = Color.TRANSPARENT;

    /**
     * default color,white
     */
    public static final int DEFAULT_COLOR = Color.WHITE;
    /**
     * default background color,transparent
     */
    public static final int DEFAULT_BACKGROUND_COLOR = COLOR_TRANSPARENT;
    /**
     * default text color,black
     */
    public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    /**
     * default stroke color,light gray
     */
    public static final int DEFAULT_STROKE_COLOR = Color.LTGRAY;
    /**
     * default stroke width,2px
     */
    public static final int DEFAULT_STROKE_WIDTH = 2;
    /**
     * default text size,50px
     */
    public static final int DEFAULT_TEXT_SIZE = 50;
}
