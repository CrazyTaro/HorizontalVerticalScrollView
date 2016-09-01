package com.showcast.hvscroll.params;

import android.support.annotation.NonNull;

import com.showcast.hvscroll.draw.BaseDrawStyle;

/**
 * Created by taro on 16/8/19.
 */
public class MenuParams extends BaseParams {
    //是否绘制行菜单
    private boolean mIsDrawRowMenu = false;
    //是否绘制列菜单
    private boolean mIsDrawColumnMenu = false;
    //是否先绘制行菜单,决定行列菜单的绘制顺序.
    private boolean mIsDrawRowMenuFirst = false;
    private MenuSetting[] mMenuSettings;

    public MenuParams() {
        super();
    }

    public MenuParams(int width, int height) {
        super(width, height);
    }

    @Override
    protected void initialConstructor() {
        super.initialConstructor();
        mMenuSettings = new MenuSetting[2];
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

    public void setIsDrawRowMenuFirst(boolean isFirst) {
        mIsDrawRowMenuFirst = isFirst;
    }

    public boolean isDrawRowMenu() {
        return mIsDrawRowMenu;
    }

    public boolean isDrawColumnMenu() {
        return mIsDrawColumnMenu;
    }

    public boolean isDrawRowMenuFirst() {
        return mIsDrawRowMenuFirst;
    }

    /**
     * add the frozen menus' index.<br/>
     * 添加固定行列的索引
     *
     * @param index
     * @param whichMenu tag of row/column menu.<br/>
     *                  行或者列的标志
     * @return true if add successfully or false if add unsuccessfully
     */
    public boolean addFrozenMenuIndex(int index, @Constant.MenuType int whichMenu) {
        MenuSetting setting = mMenuSettings[whichMenu];
        if (setting == null) {
            setting = new MenuSetting();
            mMenuSettings[whichMenu] = setting;
        }
        return setting.addFrozenItemIndex(index);
    }

    /**
     * menu setting.a setting for frozen menus<br/>
     * 菜单设置,固定行列的设置对象
     */
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

        /**
         * menu will be frozen on the top if return true;or can be scrolled by moving if return false;<br/>
         * 如果返回true,菜单将会固定在顶部,否则跟随界面滑动.
         *
         * @return
         */
        public boolean isFrozenX() {
            return mIsMenuFrozenX;
        }

        /**
         * menu will be frozen on left if return true;or can be scrolled by moving if return false;<Br/>
         * 如果返回true,菜单将会固定在左部,否则跟随界面滑动.
         *
         * @return
         */
        public boolean isFrozenY() {
            return mIsMenuFrozenY;
        }
    }
}
