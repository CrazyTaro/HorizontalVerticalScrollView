package com.showcast.hvscroll.touchhelper;

/**
 * Created by taro on 16/8/21.
 */
public class ClickPointComputeHelper {
    private float mCellWidth;
    private float mCellHeight;
    private float mIntervalInWidth;
    private float mIntervalInHeight;
    private float mPaddingLeft;
    private float mPaddingRight;
    private float mPaddingTop;
    private float mPaddingBottom;

    public ClickPointComputeHelper() {
    }

    public ClickPointComputeHelper(float width, float height) {
        this();
        if (this.setParams(width, height, 0, 0)) {
            throw new IllegalArgumentException("argument illegal,width and height must not be 0 or negative integer");
        }
    }

    public boolean setPadding(float left, float top, float right, float bottom) {
        if (left < 0 || top < 0 || right < 0 || bottom < 0) {
            return false;
        } else {
            this.mPaddingLeft = left;
            this.mPaddingTop = top;
            this.mPaddingRight = right;
            this.mPaddingBottom = bottom;
            return true;
        }
    }

    public boolean setParams(float cellWidth, float cellHeight, float intervalWidth, float intervalHeight) {
        if (cellWidth <= 0 || cellHeight <= 0 || intervalWidth < 0 || intervalHeight < 0) {
            return false;
        }
        this.mCellWidth = cellWidth;
        this.mCellHeight = cellHeight;
        this.mIntervalInWidth = intervalWidth;
        this.mIntervalInHeight = intervalHeight;
        return true;
    }

    public int computeClickXFromFirstLine(float y, float offsetY, float ignoreY, int maxRowCount) {
        float remainX = y - mPaddingTop - ignoreY - offsetY;
        int cellX = 0;
        //out of click area
        if (remainX < 0) {
            return -1;
        }
        cellX = (int) (remainX / (mCellHeight + mIntervalInHeight));
        return cellX >= maxRowCount ? -1 : cellX;
    }

    public int computeClickYFromFirstLine(float x, float offsetX, float ignoreX, int maxColumnCount) {
        float remainY = x - mPaddingLeft - ignoreX - offsetX;
        int cellY = 0;
        if (remainY < 0) {
            return -1;
        }
        cellY = (int) (remainY / (mCellWidth + mIntervalInWidth));
        return cellY >= maxColumnCount ? -1 : cellY;
    }

    public int computeClickXFromMiddle(float x, float middleX, float leftStart, float rightStart) {
        float remainX = Math.abs(x - middleX);
        float start = 0;
        int offsetMiddle = 0;
        if (remainX <= 0) {
            return offsetMiddle;
        }
        start = x - middleX < 0 ? leftStart : rightStart;
        remainX -= start;
        while (remainX > mPaddingLeft) {
            remainX -= mCellWidth;
            remainX -= mIntervalInWidth;
            offsetMiddle++;
        }
        if (remainX + mIntervalInWidth > 0) {
            return -1;
        } else {
            return offsetMiddle - 1;
        }
    }
}
