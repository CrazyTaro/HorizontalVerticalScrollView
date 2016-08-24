package com.showcast.hvscroll.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.showcast.hvscroll.params.Constant;

/**
 * Created by taro on 16/8/17.
 */
public class CellEntity {
    protected int mRowIndex = Constant.DEFAULT_CELL_INDEX;
    protected int mColumnIndex = Constant.DEFAULT_CELL_INDEX;
    protected String mText;
    protected boolean mIsNeedToDraw;
    protected int mWidth;
    protected int mHeight;
    protected int mSpanRowCount = Constant.DEFAULT_SPAN_COUNT;
    protected int mSpanColumnCount = Constant.DEFAULT_SPAN_COUNT;
    protected String mStyleTag = Constant.DEFAULT_STYLE_TAG;

    /**
     * construct a new cell.<br/>
     * 创建一个新的单元格.
     *
     * @param row
     * @param column
     * @param text
     * @param spanRowCount    合并的行单元格数量,包括自己本身.
     * @param spanColumnCount 合并的列单元格数量,包括自己本身.
     */
    public CellEntity(int row, int column, String text, int spanRowCount, int spanColumnCount) {
        mRowIndex = row;
        mColumnIndex = column;
        mText = text;
        this.setSpanRowCount(spanRowCount);
        this.setSpanColumnCount(spanColumnCount);
    }

    /**
     * the span count of row and column is {@link Constant#DEFAULT_SPAN_COUNT};<br/>
     * 合并的单元格数量为默认值.
     *
     * @param row
     * @param column
     * @param text
     */
    public CellEntity(int row, int column, String text) {
        this(row, column, text, Constant.DEFAULT_SPAN_COUNT, Constant.DEFAULT_SPAN_COUNT);
    }

    /**
     * the span count of row and column is {@link Constant#DEFAULT_SPAN_COUNT};<br/>
     * 合并的单元格数量为默认值.
     *
     * @param row
     * @param column
     */
    public CellEntity(int row, int column) {
        this(row, column, null, Constant.DEFAULT_SPAN_COUNT, Constant.DEFAULT_SPAN_COUNT);
    }

    /**
     * set the index of row and column.do not use this method at will.
     * the index decide whether this cell will be drawn or not.{@link #isNeedToDraw(int, int)}<br/>
     * 设置单元格的行列索引,不要随便使用此方法,设置的索引决定了此单元格是否会被绘制.详见链接方法.
     *
     * @param row
     * @param column
     */
    public void setRowAndColumnIndex(int row, int column) {
        if (row < 0 || column < 0) {
            return;
        }
        mRowIndex = row;
        mColumnIndex = column;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setDrawWidth(int fixedWidth) {
        if (fixedWidth >= 0) {
            mWidth = fixedWidth;
        }
    }

    public void setDrawHeight(int fixedHeight) {
        if (fixedHeight >= 0) {
            mHeight = fixedHeight;
        }
    }

    /**
     * set the span count of row.<br/>
     * 设置在行方向上合并的单元格数.
     *
     * @param spanCount
     */
    public void setSpanRowCount(int spanCount) {
        if (spanCount >= Constant.DEFAULT_SPAN_COUNT) {
            mSpanRowCount = spanCount;
        }
    }

    /**
     * set the span count of column.<br/>
     * 设置在列方向上合并的单元格数.
     *
     * @param spanCount
     */
    public void setSpanColumnCount(int spanCount) {
        if (spanCount >= Constant.DEFAULT_SPAN_COUNT) {
            mSpanColumnCount = spanCount;
        }
    }

    /**
     * set the draw style tag for cell.<br/>
     * 设置绘制的样式标志.
     *
     * @param tag
     */
    public void setDrawStyleTag(@NonNull String tag) {
        mStyleTag = tag;
    }

    public int getRowIndex() {
        return mRowIndex;
    }

    public int getColumnIndex() {
        return mColumnIndex;
    }

    @Nullable
    public String getText() {
        return mText;
    }

    public int getDrawWidth() {
        return mWidth;
    }

    public int getDrawHeight() {
        return mHeight;
    }

    public int getSpanRowCount() {
        return mSpanRowCount;
    }

    public int getSpanColumnCount() {
        return mSpanColumnCount;
    }

    /**
     * when the rowIndex and columnIndex are different from the index in cell,cell will not be drawn.<br/>
     * 当指定的index与单元格中的index不同时,此单元格不会进行绘制.
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public boolean isNeedToDraw(int rowIndex, int columnIndex) {
        //when row and column index is the same,draw the cell
        //else maybe the cell merge with other cells
        if (mRowIndex > Constant.DEFAULT_CELL_INDEX && mColumnIndex > Constant.DEFAULT_CELL_INDEX) {
            return mRowIndex == rowIndex && mColumnIndex == columnIndex;
        } else {
            return true;
        }
    }

    public void resetRowAndColumnIndex() {
        this.setRowAndColumnIndex(Constant.DEFAULT_CELL_INDEX, Constant.DEFAULT_CELL_INDEX);
    }

    /**
     * get the cell draw style tag.<br/>
     * 获取单元格绘制的样式标志.
     *
     * @return
     */
    @NonNull
    public String getStyleTag() {
        return mStyleTag;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(100);
        builder.append("{index:[");
        builder.append(mRowIndex);
        builder.append(",");
        builder.append(mColumnIndex);
        builder.append("],span:[");
        builder.append(mSpanRowCount);
        builder.append(",");
        builder.append(mSpanColumnCount);
        builder.append(",text:");
        builder.append(mText);
        builder.append("}");
        return builder.toString();
    }
}
