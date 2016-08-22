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
        return setting.addFrozenMenuIndex(index);
    }

    public MenuSetting getMenuSetting(@Constant.MenuType int whichMenu) {
        MenuSetting setting = mMenuSettings[whichMenu];
        if (setting == null) {
            setting = new MenuSetting();
            mMenuSettings[whichMenu] = setting;
        }
        return setting;
    }

    public static class MenuSetting {
        private boolean mIsMenuFrozenX = false;
        private boolean mIsMenuFrozenY = false;
        private TreeSet<Integer> mFrozenMenus;

        private MenuSetting() {
        }

        public boolean addFrozenMenuIndex(int index) {
            if (index < 0) {
                return false;
            } else {
                if (mFrozenMenus == null) {
                    mFrozenMenus = new TreeSet<>();
                }
                mFrozenMenus.add(index);
                return true;
            }
        }

        public boolean isFrozenMenu(int index) {
            return mFrozenMenus != null && mFrozenMenus.contains(index);
        }

        public int getFrozenMenuSize() {
            return mFrozenMenus == null ? 0 : mFrozenMenus.size();
        }

        public void removeFrzonMenuIndex(int index) {
            if (mFrozenMenus != null) {
                mFrozenMenus.remove(index);
            }
        }

        public void clearFrozenMenuIndex() {
            if (mFrozenMenus != null) {
                mFrozenMenus.clear();
            }
        }

        public int getLastFrozenMenuIndex() {
            return mFrozenMenus == null ? -1 : mFrozenMenus.last();
        }

        public int getFirstFrozenMenuIndex() {
            return mFrozenMenus == null ? -1 : mFrozenMenus.first();
        }

        public SortedSet<Integer> getSetLessThan(int index) {
            if (index < 0) {
                return null;
            } else {
                return mFrozenMenus == null ? null : mFrozenMenus.headSet(index);
            }
        }

        public SortedSet<Integer> getSetGreaterThan(int index) {
            if (index < 0) {
                return null;
            } else {
                return mFrozenMenus == null ? null : mFrozenMenus.tailSet(index);
            }
        }

        @NonNull
        public int[] getValueFrozenMenu() {
            if (mFrozenMenus == null || mFrozenMenus.size() <= 0) {
                return new int[0];
            } else {
                int[] result = new int[mFrozenMenus.size()];
                Iterator<Integer> it = mFrozenMenus.iterator();
                for (int i = 0; i < result.length; i++) {
                    result[i] = it.next();
                }
                return result;
            }
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
