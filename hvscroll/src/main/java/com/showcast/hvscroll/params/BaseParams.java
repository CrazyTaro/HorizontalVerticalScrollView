package com.showcast.hvscroll.params;

import android.support.annotation.NonNull;

import com.showcast.hvscroll.draw.BaseDrawStyle;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by taro on 16/8/19.
 */
public abstract class BaseParams {
    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 80;

    protected int mWidth;
    protected int mHeight;
    protected Map<String, BaseDrawStyle> mStyleMap;
    protected BaseDrawStyle mDefaultDrawStyle;

    public BaseParams() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public BaseParams(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        this.initialConstructor();
    }

    protected void initialConstructor() {
    }

    public boolean setWidth(int width) {
        if (width >= 0) {
            mWidth = width;
            return true;
        } else {
            return false;
        }
    }

    public boolean setHeight(int height) {
        if (height >= 0) {
            mHeight = height;
            return true;
        } else {
            return false;
        }
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public BaseDrawStyle addNewDrawStyle(@NonNull String tag, @NonNull BaseDrawStyle drawStyle) {
        return mStyleMap.put(tag, drawStyle);
    }

    public BaseDrawStyle removeDrawStyle(String tag) {
        return mStyleMap.remove(tag);
    }

    public boolean isContains(String tag) {
        return mStyleMap.containsKey(tag);
    }

    public BaseDrawStyle getDrawStyle(String tag) {
        return mStyleMap.get(tag);
    }

    public void clearDrawStyles() {
        mStyleMap.clear();
    }

    public void setDefaultDrawStyle(@NonNull BaseDrawStyle style) {
        mDefaultDrawStyle = style;
    }

    public BaseDrawStyle getDefaultDrawStyle() {
        return mDefaultDrawStyle;
    }

    public abstract Setting getSetting(int which);

    public static class Setting {
        private TreeSet<Integer> mFrozenSet;

        protected Setting() {
        }

        public boolean addFrozenItemIndex(int index) {
            if (index < 0) {
                return false;
            } else {
                if (mFrozenSet == null) {
                    mFrozenSet = new TreeSet<>();
                }
                mFrozenSet.add(index);
                return true;
            }
        }

        public boolean isFrozenItem(int index) {
            return mFrozenSet != null && mFrozenSet.contains(index);
        }

        public int getFrozenItemSize() {
            return mFrozenSet == null ? 0 : mFrozenSet.size();
        }

        public void removeFrzonItemIndex(int index) {
            if (mFrozenSet != null) {
                mFrozenSet.remove(index);
            }
        }

        public void clearFrozenItemIndex() {
            if (mFrozenSet != null) {
                mFrozenSet.clear();
            }
        }

        public int getLastFrozenItemIndex() {
            return mFrozenSet == null ? -1 : mFrozenSet.last();
        }

        public int getFirstFrozenItemIndex() {
            return mFrozenSet == null ? -1 : mFrozenSet.first();
        }

        public SortedSet<Integer> getSetLessThan(int index) {
            if (index < 0) {
                return null;
            } else {
                return mFrozenSet == null ? null : mFrozenSet.headSet(index);
            }
        }

        public SortedSet<Integer> getSetGreaterThan(int index) {
            if (index < 0) {
                return null;
            } else {
                return mFrozenSet == null ? null : mFrozenSet.tailSet(index);
            }
        }

        @NonNull
        public int[] getValueFrozenItems() {
            if (mFrozenSet == null || mFrozenSet.size() <= 0) {
                return new int[0];
            } else {
                int[] result = new int[mFrozenSet.size()];
                Iterator<Integer> it = mFrozenSet.iterator();
                for (int i = 0; i < result.length; i++) {
                    result[i] = it.next();
                }
                return result;
            }
        }
    }
}
