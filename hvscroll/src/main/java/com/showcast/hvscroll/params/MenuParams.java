package com.showcast.hvscroll.params;

import android.support.annotation.NonNull;

import com.showcast.hvscroll.draw.BaseDrawStyle;
import com.showcast.hvscroll.draw.MenuDrawStyle;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by taro on 16/8/19.
 */
public class MenuParams extends BaseParams {
    private boolean mIsDrawRowMenu = false;
    private boolean mIsDrawColumnMenu = false;
    private MenuSetting[] mMenuSettings;

    public MenuParams() {
        super();
    }

    public MenuParams(int width, int height) {
        super(width, height);
    }

    @Override
    protected void initialConstructor() {
        mMenuSettings = new MenuSetting[2];
        this.setDefaultDrawStyle(new MenuDrawStyle());
    }

    @Override
    public BaseDrawStyle addNewDrawStyle(String tag, @NonNull BaseDrawStyle drawStyle) {
        if (!(drawStyle instanceof MenuDrawStyle)) {
            throw new IllegalArgumentException("the class of drawStyle param must be MenuDrawStyle");
        }
        return super.addNewDrawStyle(tag, drawStyle);

    }

    @Override
    public MenuDrawStyle getDrawStyle(String tag) {
        return (MenuDrawStyle) super.getDrawStyle(tag);
    }

    @Override
    public MenuDrawStyle getDefaultDrawStyle() {
        return (MenuDrawStyle) super.getDefaultDrawStyle();
    }

    @Override
    public void setDefaultDrawStyle(@NonNull BaseDrawStyle style) {
        if (!(style instanceof MenuDrawStyle)) {
            throw new IllegalArgumentException("the class of drawStyle param must be MenuDrawStyle");
        }
        super.setDefaultDrawStyle(style);
    }

    @Override
    public MenuSetting getSetting(@Constant.MenuType int whichMenu) {
        MenuSetting setting = mMenuSettings[whichMenu];
        if (setting == null) {
            setting = new MenuSetting();
            mMenuSettings[whichMenu] = setting;
        }
        return setting;
    }

    public void setIsDrawRowMenu(boolean isDraw) {
        mIsDrawRowMenu = isDraw;
    }

    public void setIsDrawColumn(boolean isDraw) {
        mIsDrawColumnMenu = isDraw;
    }

    public boolean isDrawRowMenu() {
        return mIsDrawRowMenu;
    }

    public boolean isDrawColumnMenu() {
        return mIsDrawColumnMenu;
    }

    public boolean addFrozenMenuIndex(int index, @Constant.MenuType int whichMenu) {
        MenuSetting setting = mMenuSettings[whichMenu];
        if (setting == null) {
            setting = new MenuSetting();
            mMenuSettings[whichMenu] = setting;
        }
        return setting.addFrozenItemIndex(index);
    }

    public static class MenuSetting extends Setting {
        private boolean mIsMenuFrozenX = false;
        private boolean mIsMenuFrozenY = false;

        protected MenuSetting() {
            super();
        }

        public void setMenuFrozen(boolean frozenInX, boolean frozenInY) {
            mIsMenuFrozenX = frozenInX;
            mIsMenuFrozenY = frozenInY;
        }

        public boolean isFrozenX() {
            return mIsMenuFrozenX;
        }

        public boolean isFrozenY() {
            return mIsMenuFrozenY;
        }
    }
}
