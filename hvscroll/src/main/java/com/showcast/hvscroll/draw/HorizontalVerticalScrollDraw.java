package com.showcast.hvscroll.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.showcast.hvscroll.entity.AbsCellEntity;
import com.showcast.hvscroll.entity.AbsRowEntity;
import com.showcast.hvscroll.entity.AbsTableEntity;
import com.showcast.hvscroll.params.GlobalParams;
import com.showcast.hvscroll.params.TableParams;
import com.showcast.hvscroll.touchhelper.MoveAndScaleTouchHelper;
import com.showcast.hvscroll.touchhelper.TouchEventHelper;

/**
 * Created by taro on 16/8/17.
 */
public class HorizontalVerticalScrollDraw implements TouchEventHelper.OnToucheEventListener,
        MoveAndScaleTouchHelper.IMoveEvent, MoveAndScaleTouchHelper.IScaleEvent {

    private TouchEventHelper mTouchHelper;
    private MoveAndScaleTouchHelper mMsActionHelper;
    private View mDrawView;

    private AbsTableEntity mTable;
    private BaseTableDrawStyle mRowStyle;
    private BaseTableDrawStyle mCellStyle;
    private TableParams mTableParams;
    private GlobalParams mGlobalParams;

    //test fields
    private int mMenuHeight = 80;
    private int mMenuWidth = 200;

    private int startDrawX = 0;
    private int startDrawY = 0;

    private RectF mRecycleRectf;
    private Rect mRecycleRect;
    private Point mViewParams;
    private Point mRecyclePoint;
    private Paint mPaint;

    public HorizontalVerticalScrollDraw(@NonNull View drawView) {
        this();
        this.setDrawView(drawView);
    }

    private HorizontalVerticalScrollDraw() {
        mTouchHelper = new TouchEventHelper(this);
        mMsActionHelper = new MoveAndScaleTouchHelper(this, this);
        mRecycleRectf = new RectF();
        mRecycleRect = new Rect();
        mRecyclePoint = new Point();
        mPaint = new Paint();
    }

    public void setDrawView(@NonNull View drawView) {
        if (mDrawView != null) {
            mDrawView.setOnTouchListener(null);
        }
        mDrawView = drawView;
        mDrawView.setOnTouchListener(mTouchHelper);
    }

    public void setTable(AbsTableEntity table) {
        mTable = table;
    }

    public void setCellDrawStyle(BaseTableDrawStyle style) {
        mCellStyle = style;
    }

    public void setParams(GlobalParams global, TableParams table) {
        mGlobalParams = global;
        mTableParams = table;
    }

    public void drawCanvas(Canvas canvas) {
        if (mTable == null) {
            return;
        }
        int offsetX = (int) mMsActionHelper.getDrawOffsetX();
        int offsetY = (int) mMsActionHelper.getDrawOffsetY();
        int drawY = 0;

        this.beforeDraw(canvas, offsetX, offsetY);
        drawY = this.drawRowMenu(mTable, offsetX, offsetY, mPaint, canvas);
        this.drawCell(mTable, 0, drawY, offsetX, offsetY, mPaint, canvas);
    }

    private void beforeDraw(Canvas canvas, int offsetX, int offsetY) {
        if (mViewParams == null) {
            mViewParams = this.getViewWidthHeight(mDrawView, mViewParams);
        }
        canvas.drawColor(mGlobalParams.getCanvasBgColor());

        //translate the original point(0,0)
        canvas.translate(offsetX, offsetY);
    }

    private void finishDraw() {
    }


    //draw the row menu, menu will fix on the top ,for now
    private int drawRowMenu(AbsTableEntity table, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        paint.setTextSize(60);
        int left, top, right, bottom;

        float charLength = paint.measureText("0");
        float textDrawX = 0;
        float textDrawY = 0;
        if (table != null) {
            for (int i = 0; i < table.getMenuCount(); i++) {
                //calculate each menu cell
                left = mMenuWidth * i;
                right = left + mMenuWidth;
                top = 0;
                bottom = top + mMenuHeight;
                mRecycleRect.set(left, top, right, bottom);

                //if the draw area cannot be seen,ignore it
                if (mRecycleRect.right < offsetX * -1 || mRecycleRect.left > mViewParams.x + offsetX * -1) {
                    Log.i("optimistic", "jump to continue");
                    continue;
                }

                //draw menu cell bg color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawRect(mRecycleRect, mPaint);

                //draw menu cell stroke
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.LTGRAY);
                canvas.drawRect(mRecycleRect, mPaint);

                //draw menu text
                String menu = table.getMenu(i);
                textDrawX = mRecycleRect.left;
                textDrawY = mRecycleRect.top + mRecycleRect.height() * 2f / 3f;
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.FILL);
                //draw text auto fit to cell with
//            this.drawAutofitWidthText(charLength, mMenuWidth, menu, textDrawY, textDrawX, mPaint, canvas);
                this.drawAutofitWidthText(mMenuWidth, menu, textDrawX, textDrawY, paint, canvas);
            }
        }

        return mMenuHeight;
    }

    private void drawColumnMenu() {

    }

    //draw all cells on the table
    private void drawCell(AbsTableEntity table, int startDrawX, int startDrawY, int canvasOffsetX, int canvasOffsetY, Paint paint, Canvas canvas) {
        int rowCount = table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            this.drawCellInRow(table.getRow(i), mMenuWidth, mMenuHeight, startDrawX, startDrawY, canvasOffsetX, canvasOffsetY, paint, canvas);
            startDrawY += mMenuHeight;
        }
    }


    //draw the cells of every row
    //TODO:need to update textSize/textColor/bgColor etc.
    private void drawCellInRow(AbsRowEntity row, int cellWidth, int cellHeight, int startDrawX, int startDrawY, int canvasOffsetX, int canvasOffsetY, Paint paint, Canvas canvas) {
        if (row != null) {
            int left, top, right, bottom, columnCount;
            float drawTextX, drawTextY, textSize;

            textSize = 60;
            paint.setTextSize(textSize);

            columnCount = row.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                AbsCellEntity cell = row.getCell(i);
                //try draw cell when cell exists or need to draw
                if (cell == null || cell.isNeedToDraw()) {
                    //recyclePoint save the real cell width and height
                    this.calculateCellWidthAndHeight(mRecyclePoint, cell, cellWidth, cellHeight);

                    left = startDrawX + mRecyclePoint.x * i;
                    right = left + mRecyclePoint.x;
                    top = startDrawY;
                    bottom = startDrawY + mRecyclePoint.y;
                    mRecycleRect.set(left, top, right, bottom);

                    //draw cell when the cell can be seen
                    if (isDrawRectCanSeen(mRecycleRect, canvasOffsetX, canvasOffsetY)) {
                        //draw background color
                        paint.setColor(Color.BLACK);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawRect(mRecycleRect, paint);
                        paint.setStrokeWidth(1);
                        paint.setColor(Color.LTGRAY);
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(mRecycleRect, paint);

                        //draw text
                        paint.setColor(Color.WHITE);
                        paint.setStyle(Paint.Style.FILL);
                        drawTextX = mRecycleRect.left;
                        drawTextY = mRecycleRect.centerY() + textSize * 2 / 3;
                        this.drawAutofitWidthText(mRecyclePoint.x, cell.getText(), drawTextX, drawTextY, paint, canvas);
                    }
                }
            }
        }
    }

    private void drawFrozenColumn() {
        
    }

    private void drawFrozenRow() {
    }

    private RectF calculateCellDrawArea() {
        return null;
    }

    //TODO: calculate cell real width and height,if the cell span other cell (left/right or top/bottom)
    private void calculateCellWidthAndHeight(Point outPoint, AbsCellEntity cell, int defaultCellWidth, int defaultCellHeight) {
        outPoint.set(defaultCellWidth, defaultCellHeight);
    }


    private boolean isDrawRectCanSeen(Rect rect, int canvasOffsetX, int canvasOffsetY) {
        if (rect != null) {
            return !(rect.right < canvasOffsetX * -1 ||
                    rect.left > (mViewParams.x + canvasOffsetX * -1) ||
                    rect.bottom < canvasOffsetY * -1 ||
                    rect.top > (mViewParams.y + canvasOffsetY * -1));
        } else {
            return false;
        }
    }

    /**
     * draw text to auto fit the width,when the length is too long,ellipsis will replace the unshown text
     *
     * @param charWidth    the width of each char at the text,pay attention to english text is different from chinese text
     * @param maxDrawWidth max width for draw
     * @param drawText     text for draw
     * @param drawY        the bottom axis to begin to draw text
     * @param drawX        the axis begin to draw text
     * @param paint        the paint should set textSize/textColor already
     * @param canvas
     */
    private void drawAutofitWidthText(float charWidth, float maxDrawWidth, String drawText, float drawY, float drawX, Paint paint, Canvas canvas) {
        if (!TextUtils.isEmpty(drawText)) {
            int maxCharLength = (int) (maxDrawWidth / charWidth);
            if (drawText.length() > maxCharLength) {
                float drawLength = paint.measureText(drawText, 0, maxCharLength - 3);
                canvas.drawText(drawText, 0, maxCharLength - 3, drawX, drawY, paint);
                canvas.drawText("...", drawX + drawLength, drawY, paint);
            } else {
                canvas.drawText(drawText, drawX, drawY, paint);
            }
        }
    }

    /**
     * measure one char width
     *
     * @param paint
     * @param textSize  textSize for draw
     * @param isChinese true if the char contains chinese,false otherwise
     * @return
     */
    private float measureCharWidth(Paint paint, int textSize, boolean isChinese) {
        paint.setTextSize(textSize);
        String ch = isChinese ? "e" : "中";
        return paint.measureText(ch);
    }

    /**
     * draw text to auto fit the width,when the length is too long,ellipsis will replace the unshown text
     *
     * @param maxDrawWidth max width for draw
     * @param drawText     text for draw
     * @param drawX        the bottom axis to begin to draw text
     * @param drawY        the axis begin to draw text
     * @param paint        the paint should set textSize/textColor already
     * @param canvas
     */
    private void drawAutofitWidthText(float maxDrawWidth, String drawText, float drawX, float drawY, Paint paint, Canvas canvas) {
        if (!TextUtils.isEmpty(drawText)) {
            //measure the text width if all char draw out
            float textWidth = mPaint.measureText(drawText);
            if (textWidth > maxDrawWidth) {
                //estimate max char count can be show
                int maxCharLength = (int) (drawText.length() * (maxDrawWidth / textWidth));
                //save 3 count for ellipsis
                float drawLength = paint.measureText(drawText, 0, maxCharLength - 3);
                canvas.drawText(drawText, 0, maxCharLength - 3, drawX, drawY, paint);
                canvas.drawText("...", drawX + drawLength, drawY, paint);
            } else {
                canvas.drawText(drawText, drawX, drawY, paint);
            }
        }
    }

    private Point getViewWidthHeight(View view, Point outPoint) {
        if (view != null) {
            if (outPoint == null) {
                outPoint = new Point();
            }
            outPoint.set(view.getWidth(), view.getHeight());
            return outPoint;
        } else {
            return null;
        }
    }

    @Override
    public boolean isCanMovedOnX(float moveDistanceX, float newOffsetX) {
        //only can move in the positive axis.
        //when move to a big positive axis(for example from 0 to +int),
        //the offset distance will be negative.
        return newOffsetX <= 0;
    }

    @Override
    public boolean isCanMovedOnY(float moveDistacneY, float newOffsetY) {
        return false;
    }

    @Override
    public void onMove(int suggestEventAction) {
        mDrawView.invalidate();
    }

    @Override
    public void onMoveFail(int suggetEventAction) {

    }

    @Override
    public boolean isCanScale(float newScaleRate) {
        return false;
    }

    @Override
    public void setScaleRate(float newScaleRate, boolean isNeedStoreValue) {

    }

    @Override
    public void onScale(int suggestEventAction) {
        mDrawView.invalidate();
    }

    @Override
    public void onScaleFail(int suggetEventAction) {

    }

    @Override
    public void onSingleTouchEventHandle(MotionEvent event, int extraMotionEvent) {
        mMsActionHelper.singleTouchEvent(event, extraMotionEvent);
    }

    @Override
    public void onMultiTouchEventHandle(MotionEvent event, int extraMotionEvent) {
        mMsActionHelper.multiTouchEvent(event, extraMotionEvent);
    }

    @Override
    public void onSingleClickByTime(MotionEvent event) {

    }

    @Override
    public void onSingleClickByDistance(MotionEvent event) {

    }

    @Override
    public void onDoubleClickByTime() {

    }
}
