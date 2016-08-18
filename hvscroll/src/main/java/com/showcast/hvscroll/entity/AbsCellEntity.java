package com.showcast.hvscroll.entity;

/**
 * Created by taro on 16/8/17.
 */
public class AbsCellEntity {
    protected String mText;
    protected boolean mIsNeedToDraw;
    protected int mWidth;
    protected int mHeight;
    protected int mSpanRowCount;
    protected int mSpanColumnCount;


    public AbsCellEntity(String text, int spanRowCount, int spanColumnCount) {
        mText = text;
        this.setSpanRowCount(spanRowCount);
        this.setSpanColumnCount(spanColumnCount);
    }

    public AbsCellEntity(String text) {
        this(text, 0, 0);
    }

    public AbsCellEntity() {
        this(null, 0, 0);
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
        if (spanCount >= 0) {
            mSpanRowCount = spanCount;
        }
    }

    public void setSpanColumnCount(int spanCount) {
        if (spanCount >= 0) {
            mSpanColumnCount = spanCount;
        }
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
        return true;
    }
}
