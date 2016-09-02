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
    public @interface FloatPercent {
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
    /**
     * the cell is no a menu
     */
    public static final int MENU_NONE = -1;

    /**
     * default column index of row and column menu
     */
    public static final int FIXED_MENU_INDEX = 0;

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

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {MOVE_DIRECTION_HORIZONTAL, MOVE_DIRECTION_VERTICAL, MOVE_DIRECTION_BOTH, MOVE_DIRECTOIN_NONE})
    public @interface MoveDirection {
    }

    /**
     * horizontal direction
     */
    public static final int MOVE_DIRECTION_HORIZONTAL = -1;
    /**
     * vertical direction
     */
    public static final int MOVE_DIRECTION_VERTICAL = 1;
    /**
     * both direction
     */
    public static final int MOVE_DIRECTION_BOTH = Integer.MAX_VALUE;
    /**
     * both directions are not allowed to move.
     */
    public static final int MOVE_DIRECTOIN_NONE = Integer.MIN_VALUE;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {STYLE_USE_FIXED_SIZE, STYLE_BASE_ON_PARENT_SIZE, STYLE_BASE_ON_PARENT_WIDTH, STYLE_BASE_ON_PARENT_HEIGHT})
    public @interface StyleSize {
    }

    public static final int STYLE_USE_FIXED_SIZE = -1;
    /**
     * width and height percent base on their parent's sizes.
     */
    public static final int STYLE_BASE_ON_PARENT_SIZE = 0;
    /**
     * width and height just base on parent's width
     */
    public static final int STYLE_BASE_ON_PARENT_WIDTH = 1;
    /**
     * width and height just base on parent's height
     */
    public static final int STYLE_BASE_ON_PARENT_HEIGHT = 2;

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
