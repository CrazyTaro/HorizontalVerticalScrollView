package com.showcast.hvscroll.params;

import android.support.annotation.NonNull;

import com.showcast.hvscroll.draw.BaseDrawStyle;

/**
 * cell params.you can set default cell width and height,frozen columns or rows ect.<br>
 * 单元格的参数对象,可对单元格进行设置其统一的一些参数,包括默认宽高,固定行列等.
 * Created by taro on 16/8/19.
 */
public class CellParams extends BaseParams {
    private boolean mIsDrawFrozenRowFirst = true;
    private LineSetting[] mLineSettings;

    public CellParams() {
        super();
    }

    public CellParams(int width, int height) {
        super(width, height);
    }

    @Override
    protected void initialConstructor() {
        super.initialConstructor();
        mLineSettings = new LineSetting[2];
    }

    @Override
    public LineSetting getSetting(@Constant.LineType int whichLines) {
        LineSetting setting = mLineSettings[whichLines];
        if (setting == null) {
            setting = new LineSetting();
            mLineSettings[whichLines] = setting;
        }
        return setting;
    }

    public boolean isDrawFrozenRowFirst() {
        return mIsDrawFrozenRowFirst;
    }

    /**
     * set if the row is first to draw.<br>
     * 设置是否固定行先绘制
     *
     * @param isRowFirst
     */
    public void setIsDrawFrozenRowFirst(boolean isRowFirst) {
        mIsDrawFrozenRowFirst = isRowFirst;
    }

    /**
     * add the frozen column/row index.<br>
     * 添加固定行/列的索引值,可以添加无效值,但不会起作用.
     *
     * @param index      the index of column/row which need to be frozen.<br>
     *                   需要固定的行/列索引
     * @param whichLines point out the index belongs to column or row.<br>
     *                   指定需要固定的是行或者列
     * @return
     */
    public boolean addFrozenLineIndex(int index, @Constant.LineType int whichLines) {
        LineSetting setting = mLineSettings[whichLines];
        if (setting == null) {
            setting = new LineSetting();
            mLineSettings[whichLines] = setting;
        }
        return setting.addFrozenItemIndex(index);
    }

    /**
     * line setting,a setting for frozen columns or rows<br>
     * 固定行列的设置对象
     */
    public static class LineSetting extends Setting {
        //the count of lines to offset
        //固定位置时跳过的行数(对列来说是跳掉行,对行来说是跳掉列)
        private int mOffsetLines = 0;
        //the length to offset
        //固定位置时跳掉的长度,此值与offsetLines并存,当此值无效(<=0)时,使用行进行动态计算
        private int mOffsetLength = 0;

        protected LineSetting() {
            super();
        }

        public void setOffsetLines(int lines) {
            if (lines >= 0) {
                mOffsetLines = lines;
            }
        }

        public void setOffsetLength(int lineWidthOrHeight) {
            if (lineWidthOrHeight >= 0) {
                mOffsetLength = lineWidthOrHeight;
            }
        }

        public int getOffsetLines() {
            return mOffsetLines;
        }

        public int getOffsetLength() {
            return mOffsetLength;
        }

        /**
         * get the offset length for drawing,return the offsetLength if it is valid(>0),
         * or return the product of offsetLines plus width/height.<br>
         * 返回绘制时需要跳过的长度,若offsetLength有效则返回该值,否则返回offsetLines与width/height的乘积(动态进行计算),
         * 建议使用lines + width/height的形式进行offset
         *
         * @param lineWidthOrHeight
         * @return
         */
        public int getOffsetDrawLength(int lineWidthOrHeight) {
            if (mOffsetLength > 0) {
                return mOffsetLength;
            } else {
                return mOffsetLines * lineWidthOrHeight;
            }
        }
    }
}
