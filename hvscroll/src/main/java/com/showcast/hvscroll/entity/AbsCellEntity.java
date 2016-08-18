package com.showcast.hvscroll.entity;

/**
 * Created by taro on 16/8/17.
 */
public class AbsCellEntity {
    public static final int DEFAULT_SPAN_COUNT = 1;
    public static final int DEFAULT_CELL_INDEX = -1;

    protected int mRowIndex = DEFAULT_CELL_INDEX;
    protected int mColumnIndex = DEFAULT_CELL_INDEX;
    protected String mText;
    protected boolean mIsNeedToDraw;
    protected int mWidth;
    protected int mHeight;
    protected int mSpanRowCount = DEFAULT_SPAN_COUNT;
    protected int mSpanColumnCount = DEFAULT_SPAN_COUNT;

    public AbsCellEntity(int row, int column, String text, int spanRowCount, int spanColumnCount) {
        mRowIndex = row;
        mColumnIndex = column;
        mText = text;
        this.setSpanRowCount(spanRowCount);
        this.setSpanColumnCount(spanColumnCount);
    }

    public AbsCellEntity(int row, int column, String text) {
        this(row, column, text, DEFAULT_SPAN_COUNT, DEFAULT_SPAN_COUNT);
    }

    public AbsCellEntity(String text, int spanRowCount, int spanColumnCount) {
        this(DEFAULT_CELL_INDEX, DEFAULT_CELL_INDEX, text, spanRowCount, spanColumnCount);
    }

    public AbsCellEntity(String text) {
        this(DEFAULT_CELL_INDEX, DEFAULT_CELL_INDEX, text, DEFAULT_SPAN_COUNT, DEFAULT_SPAN_COUNT);
    }

    public AbsCellEntity() {
        this(DEFAULT_CELL_INDEX, DEFAULT_CELL_INDEX, null, DEFAULT_SPAN_COUNT, DEFAULT_SPAN_COUNT);
    }

    public void setRowAndColumnIndex(int row, int column) {
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

    public void setSpanRowCount(int spanCount) {
        if (spanCount >= DEFAULT_SPAN_COUNT) {
            mSpanRowCount = spanCount;
        }
    }

    public void setSpanColumnCount(int spanCount) {
        if (spanCount >= DEFAULT_SPAN_COUNT) {
            mSpanColumnCount = spanCount;
        }
    }

    public int getRowIndex() {
        return mRowIndex;
    }

    public int getColumnIndex() {
        return mColumnIndex;
    }

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

    public boolean isNeedToDraw(int rowIndex, int columnIndex) {
        //when row and column index is the same,draw the cell
        //else maybe the cell merge with other cells
        if (mRowIndex > DEFAULT_CELL_INDEX && mColumnIndex > DEFAULT_CELL_INDEX) {
            return mRowIndex == rowIndex && mColumnIndex == columnIndex;
        } else {
            return true;
        }
    }

    public void resetRowAndColumnIndex() {
        this.setRowAndColumnIndex(DEFAULT_CELL_INDEX, DEFAULT_CELL_INDEX);
    }
}
