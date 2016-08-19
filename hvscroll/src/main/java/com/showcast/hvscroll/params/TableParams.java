package com.showcast.hvscroll.params;

import android.graphics.Color;
import android.support.annotation.IntDef;

import com.showcast.hvscroll.draw.MenuDrawStyle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by taro on 16/8/17.
 */
public class TableParams {
    public static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final int DEFAULT_TEXT_SIZE = 50;
    public static final int DEFAULT_MENU_WIDTH = 80;
    public static final int DEFAULT_MENU_HEIGHT = 60;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {MENU_ROW, MENU_COLUMN})
    public @interface MenuType {
    }

    public static final int MENU_ROW = 1;
    public static final int MENU_COLUMN = -1;

    protected MenuDrawStyle mRowMenu;
    protected MenuDrawStyle mColumnMenu;

    public TableParams() {
        mRowMenu = new MenuDrawStyle();
        mRowMenu = new MenuDrawStyle();
    }

    public void setIsDrawMenu(boolean isDraw, @MenuType int menuType) {
        MenuDrawStyle menu = getMenu(menuType);
        if (menu != null) {
            menu.setIsDraw(isDraw);
        }
    }

    public void setMenuFrozen(boolean frozenInX, boolean frozenInY, @MenuType int menuType) {
        MenuDrawStyle menu = getMenu(menuType);
        if (menu != null) {
            menu.setMenuFrozen(frozenInX, frozenInY);
        }
    }

    public MenuDrawStyle getMenu(@MenuType int menuType) {
        switch (menuType) {
            case MENU_ROW:
                return mRowMenu;
            case MENU_COLUMN:
                return mColumnMenu;
            default:
                return null;
        }
    }
}
