package com.showcast.hvscroll.params;

import android.support.annotation.ColorInt;

/**
 * global params, you can set the canvas background color and mask params etc.<br/>
 * 全局参数对象,可设置全局性的参数值,如背景色,界面蒙板参数等.
 * Created by taro on 16/8/17.
 */
public class GlobalParams {
    private boolean mIsDrawMask = false;
    private float mMaskWidthPercent = 0;
    private float mMaskWidth = 0;
    private int mMaskAlpha = 255;
    private int mMaskColor = Constant.DEFAULT_TEXT_COLOR;
    private int mMaskSplitLineColor = Constant.DEFAULT_COLOR;
    private int mMaskSplitLineWidth = Constant.DEFAULT_STROKE_WIDTH;
    private int mMaskWidthLines = 0;
    private int mMaskStartLineCount = 0;
    private int mMaskStartWidth = 0;

    protected int mCanvasBgColor = Constant.DEFAULT_BACKGROUND_COLOR;
    protected boolean mIsDrawCellStroke = false;
    protected int mStrokeColor = Constant.DEFAULT_STROKE_COLOR;
    protected int mStrokeWidth = Constant.DEFAULT_STROKE_WIDTH;

    public void setCanvasBackgroundColor(@ColorInt int color) {
        mCanvasBgColor = color;
    }

    public void setIsDrawCellStroke(boolean isDraw) {
        mIsDrawCellStroke = isDraw;
    }

    public void setStrokeColor(@ColorInt int color) {
        mStrokeColor = color;
    }

    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
    }

    public int getCanvasBgColor() {
        return mCanvasBgColor;
    }

    public boolean isDrawCellStroke() {
        return mIsDrawCellStroke;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public int getStrokeWidth() {
        return mStrokeWidth;
    }


    public void setIsDrawMask(boolean isDraw) {
        mIsDrawMask = isDraw;
    }

    /**
     * set mask width.if the value>0,this value will use for drawing mask but not percent and lines.<br/>
     * 设置蒙板的宽度,可以任意值,大于0时该值有效,绘制蒙板界面时将使用此值而不是按列数及百分比计算.
     *
     * @param width this value is valid if it is greater than 0.<br/>
     *              大于0时此值有效.
     */
    public void setMaskWidth(float width) {
        mMaskWidth = width;
    }

    /**
     * set the mask percent bases on mask lines.the mask width is the product of mask lines,
     * mask percent and the width of each line.<br/>
     * 设置蒙板的列数及蒙板的百分比.当percent=1时,蒙板的宽度为maskLines * eachWidth(每行的宽度)
     *
     * @param maskLines count of lines to mask(if percent = 1)<br/>
     *                  蒙板的列数
     * @param percent   percent to mask on the total width of mask lines.<br/>
     *                  蒙板的百分比,基于指定列数的总宽
     */
    public void setMaskWidthPercent(int maskLines, @Constant.FloatPercent float percent) {
        if (maskLines >= 0) {
            mMaskWidthLines = maskLines;
            mMaskWidthPercent = percent;
        }
    }

    /**
     * set the mask alpha.<br/>
     * 设置蒙板界面的透明度.
     * @param alpha
     */
    public void setMaskAlpha(@Constant.Alpha int alpha) {
        mMaskAlpha = alpha;
    }

    /**
     * set the mask color.<br/>
     * 设置蒙板界面颜色
     * @param color
     */
    public void setMaskColor(@ColorInt int color) {
        mMaskColor = color;
    }

    /**
     * set the split mask line color.<br/>
     * 设置蒙板界面最右边的边界线颜色
     * @param color
     */
    public void setMaskSplitLineColor(@ColorInt int color) {
        mMaskSplitLineColor = color;
    }

    /**
     * set the split mask line width.<br/>
     * 设置蒙板界面最右边的边界线宽度.
     * @param width
     */
    public void setMaskSplitLineWidth(int width) {
        if (width >= 0) {
            mMaskSplitLineWidth = width;
        }
    }

    /**
     * set the count of lines the mask skips over.similar to the maskStartWidth,
     * when maskStartWidth is valid(>0),this value will be ignored.<br/>
     * 设置蒙板需要跳过的列数,当maskStartWidth有效时,此参数值无效.否则偏移的长度为 列数*列宽.(动态计算跳过的列数)
     *
     * @param lineCount
     */
    public void setMaskStartLine(int lineCount) {
        if (lineCount >= 0) {
            mMaskStartLineCount = lineCount;
        }
    }

    /**
     * set the x coordinate for mask starting to draw.<br/>
     * 设置蒙板界面开始绘制的坐标.蒙板只在cell界面进行绘制.不涉及menu
     *
     * @param startWidth
     */
    public void setMaskStartWidth(int startWidth) {
        mMaskStartWidth = startWidth;
    }

    public float getMaskWidth() {
        return mMaskWidth;
    }

    public float getMaskPercent() {
        return mMaskWidthPercent;
    }

    public int getMaskAlpha() {
        return mMaskAlpha;
    }

    public int getMaskColor() {
        return mMaskColor;
    }

    public int getMaskSplitLineColor() {
        return mMaskSplitLineColor;
    }

    public int getMaskSplitLineWidth() {
        return mMaskSplitLineWidth;
    }

    public int getMaskStartWidth() {
        return mMaskStartWidth;
    }

    public int getDrawMaskStartWidth(int cellWidth) {
        return mMaskStartWidth > 0 ? mMaskStartWidth : mMaskStartLineCount * cellWidth;
    }

    /**
     * return true if draw mask or false if not.the mask for drawing will base the canvas height,
     * you can only set the mask width,the height will always equal to canvas height.<br/>
     * 返回是否绘制蒙板界面,蒙板界面只能设置宽度,高度永远与绘制界面的高度一致
     *
     * @return
     */
    public boolean isDrawMask() {
        return mIsDrawMask;
    }

    /**
     * if you don't plan to extend this class,you can ignore this method.<br/>
     * 获取绘制蒙板界面时的宽度,蒙板只会满高绘制,只能设置覆盖的宽度,高度与当前绘制的界面高度相同.
     * 当maskWidth>0时返回maskWidth,否则返回以maskLines列宽度为基础的百分比maskPercent宽度
     *
     * @param cellWidth 每个单位的默认宽度.
     * @return
     */
    public float getDrawMaskWidth(int cellWidth) {
        return mMaskWidth <= 0 ? (mMaskWidthLines * cellWidth * mMaskWidthPercent) : mMaskWidth;
    }
}
