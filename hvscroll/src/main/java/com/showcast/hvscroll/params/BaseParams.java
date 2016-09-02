package com.showcast.hvscroll.params;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.showcast.hvscroll.draw.BaseDrawStyle;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * base params.all the sub params extend from this class.this class offers the base params for setting.<br>
 * 基础参数类,此类提供了基本的参数设置,包括宽高及绘制样式,数据存储等.
 * Created by taro on 16/8/19.
 */
public abstract class BaseParams {
    protected float mWidthPercent;
    protected float mHeightPercent;
    @Constant.StyleSize
    protected int mStyleSize = Constant.STYLE_USE_FIXED_SIZE;
    protected int mWidth;
    protected int mHeight;
    protected int mDrawWidth;
    protected int mDrawHeight;
    //extended draw style,you can add the custom draw style here
    //扩展的样式存储
    protected Map<String, BaseDrawStyle> mStyleMap;
    //default style,always nonNull,used for every cell which does not request special draw style
    //默认的样式,永不可为null,在没有匹配到单元格的指定样式时都会使用此样式.
    protected BaseDrawStyle mDefaultDrawStyle;

    public BaseParams() {
        this(Constant.DEFAULT_WIDTH, Constant.DEFAULT_HEIGHT);
    }

    public BaseParams(int width, int height) {
        mDefaultDrawStyle = new BaseDrawStyle();
        mStyleMap = new ArrayMap<>(10);

        this.setWidth(width);
        this.setHeight(height);
        this.initialConstructor();
    }

    /**
     * a method will be called after constructing class.<br>
     * 构造方法后一定会被调用的方法,当子类需要初始化某些数据时,可以重写此方法.
     */
    protected void initialConstructor() {
    }

    /**
     * update base params from another params.<br>
     * 将当前的对象状态同步更新到指定对象的状态
     *
     * @param params        nothing is changed when this params is null.<br>
     *                      当此参数为null不会改变任何状态
     * @param isCloneObject clone all fields,including all objects if true,or just clone basic fields if false.<br>
     *                      若该字段为true,复制所有的字段,包括所有对象字段;否则仅复制基本数据类型字段
     */
    public void updateParams(BaseParams params, boolean isCloneObject) {
        if (params != null) {
            this.mWidthPercent = params.mWidthPercent;
            this.mHeightPercent = params.mHeightPercent;
            this.mWidth = params.mWidth;
            this.mHeight = params.mHeight;
            this.mStyleSize = params.mStyleSize;
            if (isCloneObject) {
                this.mDefaultDrawStyle = params.mDefaultDrawStyle.clone();
                ArrayMap<String, BaseDrawStyle> newMap = new ArrayMap<>(params.mStyleMap.size());
                for (String key : params.mStyleMap.keySet()) {
                    newMap.put(key, params.mStyleMap.get(key).clone());
                }
            }
        }
    }

    /**
     * set width,true if successful or false if failed.<br>
     * 设置成功返回true,否则返回false
     *
     * @param width
     * @return
     */
    public boolean setWidth(int width) {
        if (width >= 0) {
            mWidth = width;
            return true;
        } else {
            return false;
        }
    }

    /**
     * set height,true if successful or false if failed.<br>
     * 设置成功返回true,否则返回false.
     *
     * @param height
     * @return
     */
    public boolean setHeight(int height) {
        if (height >= 0) {
            mHeight = height;
            return true;
        } else {
            return false;
        }
    }

    /**
     * set the dynamic width and height which rely on parent view's width or height.once you use this method,
     * when drawing the cells the width will be set the value after calculating.<br>
     * 设置单元格的宽高依赖于view宽高的百分比.
     *
     * @param widthPercent  the percent used for width.notice that the percent maybe any one of the view width and height,it's depended on styleSize<br>
     *                      用于宽度的百分比,该百分比可能是对应view中的宽或者高的任意一个,由下面的style决定.
     * @param heightPercent the percent used for height.<br>
     *                      用于高度的百分比.
     * @param styleSize     用于指定百分比依赖的对象.可分别指定都依赖于view的width或者height;也可以widthPercent依赖于width,heightPercent依赖于height;
     */
    public void setWidthAndHeightPercent(@Constant.FloatPercent float widthPercent, @Constant.FloatPercent float heightPercent, @Constant.StyleSize int styleSize) {
        mWidthPercent = widthPercent;
        mHeightPercent = heightPercent;
        mStyleSize = styleSize;
    }

    /**
     * calculate the cell width and height which used to draw.<br>
     * 计算单元格的宽高用于绘制.
     *
     * @param viewWidth  width of view.
     * @param viewHeight height of view.
     * @return
     */
    @NonNull
    public void computeDrawWidthAndHeight(int viewWidth, int viewHeight) {
        if (isDrawUseSizePercent()) {
            switch (mStyleSize) {
                case Constant.STYLE_RELY_ON_PARENT_SIZE:
                    this.mDrawWidth = (int) (viewWidth * mWidthPercent);
                    this.mDrawHeight = (int) (viewHeight * mHeightPercent);
                    break;
                case Constant.STYLE_RELY_ON_PARENT_WIDTH:
                    this.mDrawWidth = (int) (viewWidth * mWidthPercent);
                    this.mDrawHeight = (int) (viewWidth * mHeightPercent);
                    break;
                case Constant.STYLE_RELY_ON_PARENT_HEIGHT:
                    this.mDrawWidth = (int) (viewHeight * mWidthPercent);
                    this.mDrawHeight = (int) (viewHeight * mHeightPercent);
                    break;
                case Constant.STYLE_USE_FIXED_SIZE:
                default:
                    this.mDrawWidth = mWidth;
                    this.mDrawHeight = mHeight;
                    break;
            }
        }
    }

    /**
     * return the width of cell for drawing.
     *
     * @return
     */
    public int getDrawWidth() {
        return mDrawWidth;
    }

    /**
     * return the height of cell for drawing.
     *
     * @return
     */
    public int getDrawHeight() {
        return mDrawHeight;
    }

    /**
     * check if draw the cell by using size percent.
     *
     * @return
     */
    public boolean isDrawUseSizePercent() {
        if (mWidthPercent <= 0 || mHeight <= 0 || mStyleSize == Constant.STYLE_USE_FIXED_SIZE) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * return the percent saved for cell width.
     *
     * @return
     */
    public float getWidthPercent() {
        return mWidthPercent;
    }

    /**
     * return the percent saved for cell height.
     *
     * @return
     */
    public float getHeightPercent() {
        return mHeightPercent;
    }

    /**
     * return the style for percent drawing.
     *
     * @return
     */
    @Constant.StyleSize
    public int getStyleSize() {
        return mStyleSize;
    }

    /**
     * get the stored fixed width.you need to use the method {@link #getDrawWidth()} to get the cell width for drawing.
     *
     * @return
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * get the stored fixed height.you need to use the method {@link #getDrawHeight()} to get the cell height for drawing.
     *
     * @return
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * add new draw style.the style will be matched by tag,so please make sure the tag is unique.<br>
     * 添加新的绘制样式.样式将会通过tag进行匹配,应该确保tag是唯一的.
     *
     * @param tag       the unique tag for a style.
     * @param drawStyle
     * @return
     */
    public BaseDrawStyle addNewDrawStyle(@NonNull String tag, @NonNull BaseDrawStyle drawStyle) {
        return mStyleMap.put(tag, drawStyle);
    }

    public BaseDrawStyle removeDrawStyle(@NonNull String tag) {
        return mStyleMap.remove(tag);
    }

    public boolean isContains(@NonNull String tag) {
        return mStyleMap.containsKey(tag);
    }

    public BaseDrawStyle getDrawStyle(@NonNull String tag) {
        return mStyleMap.get(tag);
    }

    public void clearDrawStyles() {
        mStyleMap.clear();
    }

    /**
     * set default draw style,you can replace default style with your custom style,but the style can't be null.<br>
     * 设置默认的样式,你可以替换掉原始的默认样式.
     *
     * @param style
     */
    public void setDefaultDrawStyle(@NonNull BaseDrawStyle style) {
        mDefaultDrawStyle = style;
    }

    @NonNull
    public BaseDrawStyle getDefaultDrawStyle() {
        return mDefaultDrawStyle;
    }

    /**
     * a abstract method to get setting.which kind of settings is decided by sub class.<br>
     * 获取某个设置,该设置的具体功能由子类决定.父类仅提供此方法的接口.
     *
     * @param which 筛选的参数.
     * @return
     */
    public abstract Setting getSetting(int which);

    /**
     * a common setting.<br>
     * 为子类提供通用的设置对象
     */
    public static class Setting {
        //a set for store frozen columns or rows.
        //用于存储固定行或列的容器
        private TreeSet<Integer> mFrozenSet;

        //not allow other class to construct this class.
        protected Setting() {
        }

        /**
         * add the index of frozen columns or rows.<br>
         * 添加需要固定显示的行或者列
         *
         * @param index
         * @return true if add successfully or false if add unsuccessfully.<br>
         * 添加成功返回true,否则返回false
         */
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

        /**
         * check if a index of column/row need to be frozen.<br>
         * 检测指定index是否需要固定行或列.
         *
         * @param index
         * @return
         */
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

        /**
         * get the last frozen item index.return -1 if not exist.the last index is the biggest index of frozen item.<br>
         * 返回最后一个固定行列的索引,若不存在返回-1,最后一个索引应该是最大的.
         *
         * @return
         */
        public int getLastFrozenItemIndex() {
            return (mFrozenSet == null || mFrozenSet.size() <= 0) ? -1 : mFrozenSet.last();
        }

        /**
         * get the first frozen item index,return -1 if not exist.the first index is the smallest index of frozen item.<br>
         * 返回第一个固定行列的索引,若不存在返回-1,第一个索引应该最小的.
         *
         * @return
         */
        public int getFirstFrozenItemIndex() {
            return (mFrozenSet == null || mFrozenSet.size() <= 0) ? -1 : mFrozenSet.first();
        }

        /***
         * return the set less than the index.<br>
         * 获取小于指定索引的序列.
         *
         * @param index
         * @return
         */
        @Nullable
        public SortedSet<Integer> getSetLessThan(int index) {
            if (index < 0) {
                return null;
            } else {
                return mFrozenSet == null ? null : mFrozenSet.headSet(index);
            }
        }

        /**
         * return the set greater than the index.<br>
         * 获取大于指定索引的序列.
         *
         * @param index
         * @return
         */
        @Nullable
        public SortedSet<Integer> getSetGreaterThan(int index) {
            if (index < 0) {
                return null;
            } else {
                return mFrozenSet == null ? null : mFrozenSet.tailSet(index);
            }
        }

        /**
         * return the sorted index of frozen items.do not return the set to prevent caller to change the data in the set directly.
         * maybe return a array which length is 0 if no data.<br>
         * 返回存储的固定行列的值,此值已经完成排序.无数据时返回长度为0的数组.
         *
         * @return
         */
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
