package com.showcast.hvscroll.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
        MoveAndScaleTouchHelper.IMoveEvent, MoveAndScaleTouchHelper.IScaleEvent,
        MoveAndScaleTouchHelper.INotificationEvent {
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

    private int mStartDrawX = 0;
    private int mStartDrawY = 0;
    //the direction for scrolling
    private boolean mIsScrollInHorizontal = true;
    //the max width of canvas draws
    private float mCanvasDrawWidth = 0;
    //the max height of canvas draws
    private float mCanvasDrawHeight = 0;

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
        mMsActionHelper.setNoticationEvent(this);
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
        int canvasStartDrawY = 0;
        int canvasStartDrawX = 0;

        this.beforeDraw(canvas, offsetX, offsetY);
        canvasStartDrawY = this.drawRowMenu(mTable, false, mMenuWidth, mMenuHeight, 0, 0, offsetX, 0, mPaint, canvas);
        canvasStartDrawX = this.drawColumnMenu(mTable, true, mMenuWidth, mMenuHeight, 0, 0, offsetX, offsetY, mPaint, canvas);
        canvasStartDrawX = this.drawFrozenColumn(mTable, 0, canvasStartDrawY, offsetX, offsetY, 0, mMenuWidth, mMenuHeight, mPaint, canvas);
        this.drawCellInTable(mTable, canvasStartDrawX, canvasStartDrawY, offsetX, offsetY, mPaint, canvas);
    }

    private void beforeDraw(Canvas canvas, int offsetX, int offsetY) {
        if (mViewParams == null) {
            mViewParams = this.getViewWidthHeight(mDrawView, mViewParams);
        }
        canvas.drawColor(mGlobalParams.getCanvasBgColor());
//
//        //translate the original point(0,0)
//        canvas.translate(offsetX, offsetY);
    }

    private void finishDraw() {
    }


    //draw the row menu, menu will fix on the top ,for now
    //TODO:set textSize/textColor/bgColor etc.
    private int drawRowMenu(AbsTableEntity table, boolean isFrozen, int menuWidth, int menuHeight, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        float textSize = 60f;
        paint.setTextSize(textSize);
        int left, top, right, bottom;

        float textDrawX = 0;
        float textDrawY = 0;
        if (table != null) {
            //ignore offset values if menu is frozen
            //so that menu will not be moved
            if (isFrozen) {
                offsetX = 0;
                offsetY = 0;
            }

            for (int i = 0; i < table.getMenuCount(0); i++) {
                //get menu
                AbsCellEntity menu = table.getRowMenu(i);
                if (menu != null && menu.isNeedToDraw(AbsTableEntity.MENU_INDEX_ROW, i)) {
                    //calculate each menu cell
                    left = menuWidth * i + offsetX + startDrawX;
                    right = left + menuWidth;
                    top = offsetY + startDrawY;
                    bottom = top + menuHeight;
                    mRecycleRect.set(left, top, right, bottom);

                    //if the draw area cannot be seen,ignore it
                    if (isDrawRectCanSeen(mRecycleRect)) {
                        textDrawX = mRecycleRect.left;
                        textDrawY = mRecycleRect.centerY() + textSize * 2 / 3;
                        this.drawCell(menu, mRecycleRect, textDrawX, textDrawY, paint, canvas);
                    }
                }
            }

            //save row menu max draw width
            this.updateCanvasDrawWidth(mRecycleRect.right - offsetX);
            this.updateCanvasDrawHeight(mRecycleRect.bottom - offsetY);
        }

        return menuHeight;
    }

    private int drawColumnMenu(AbsTableEntity table, boolean isFrozen, int menuWidth, int menuHeight, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        float textSize = 60f;
        paint.setTextSize(textSize);
        int left, top, right, bottom;

        float textDrawX = 0;
        float textDrawY = 0;
        if (table != null) {
            //ignore offset values if menu is frozen
            //so that menu will not be moved
            if (isFrozen) {
                offsetX = 0;
                offsetY = 0;
            }

            for (int i = 0; i < table.getMenuCount(1); i++) {
                //get menu
                AbsCellEntity menu = table.getRowMenu(i);
                if (menu != null && menu.isNeedToDraw(i, AbsTableEntity.MENU_INDEX_COLUMN)) {
                    //calculate each menu cell
                    left = offsetX + startDrawX;
                    right = left + menuWidth;
                    top = offsetY + startDrawY + menuHeight * i;
                    bottom = top + menuHeight;
                    mRecycleRect.set(left, top, right, bottom);

                    //if the draw area cannot be seen,ignore it
                    if (isDrawRectCanSeen(mRecycleRect)) {
                        textDrawX = mRecycleRect.left;
                        textDrawY = mRecycleRect.centerY() + textSize * 2 / 3;
                        this.drawCell(menu, mRecycleRect, textDrawX, textDrawY, paint, canvas);
                    }
                }
            }

            //save row menu max draw width
            this.updateCanvasDrawWidth(mRecycleRect.right - offsetX);
            this.updateCanvasDrawHeight(mRecycleRect.bottom - offsetY);
        }

        return menuWidth;
    }

    //draw all cells on the table
    private void drawCellInTable(AbsTableEntity table, int startDrawX, int startDrawY, int canvasOffsetX, int canvasOffsetY, Paint paint, Canvas canvas) {
        if (table != null) {
            int rowCount = table.getRowCount();
            canvas.clipRect(startDrawX, startDrawY, mViewParams.x, mViewParams.y);
            for (int i = 0; i < rowCount; i++) {
                this.drawCellInRow(table.getRow(i), i, mMenuWidth, mMenuHeight, startDrawX, startDrawY, canvasOffsetX, canvasOffsetY, paint, canvas);
                startDrawY += mMenuHeight;
            }
        }
    }


    //draw the cells of every row
    //TODO:need to update textSize/textColor/bgColor etc.
    private void drawCellInRow(AbsRowEntity row, int rowIndex, int cellWidth, int cellHeight, int startDrawX, int startDrawY, int canvasOffsetX, int canvasOffsetY, Paint paint, Canvas canvas) {
        if (row != null) {
            int left, top, right, bottom, columnCount;
            float textDrawX, textDrawY, textSize;

            textSize = 60;
            paint.setTextSize(textSize);
            columnCount = row.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                AbsCellEntity cell = row.getCell(i);
                //try draw cell when cell exists or need to draw
                if (cell == null || cell.isNeedToDraw(rowIndex, i)) {
                    //recyclePoint save the real cell width and height
                    this.calculateCellWidthAndHeight(mRecyclePoint, cell, cellWidth, cellHeight);

                    left = startDrawX + mRecyclePoint.x * i + canvasOffsetX;
                    right = left + mRecyclePoint.x;
                    top = startDrawY + canvasOffsetY;
                    bottom = top + mRecyclePoint.y;
                    mRecycleRect.set(left, top, right, bottom);

                    //draw cell when the cell can be seen
                    if (isDrawRectCanSeen(mRecycleRect)) {
                        textDrawX = mRecycleRect.left;
                        textDrawY = mRecycleRect.centerY() + textSize * 2 / 3;
                        this.drawCell(cell, mRecycleRect, textDrawX, textDrawY, paint, canvas);
                    }
                }
            }

            //save each row max draw width
            this.updateCanvasDrawWidth(mRecycleRect.right - canvasOffsetX);
            this.updateCanvasDrawHeight(mRecycleRect.bottom - canvasOffsetY);
        }
    }

    //draw frozen column
    //TODO:set textSize/textColor etc.
    private int drawFrozenColumn(AbsTableEntity table, int startDrawX, int startDrawY, int offsetX, int offsetY, int whichColumn, int cellWidth, int cellHeight, Paint paint, Canvas canvas) {
        int maxDrawWidth = 0;
        canvas.clipRect(startDrawX, startDrawY, mViewParams.x, mViewParams.y);
        //check if the table exists
        if (table != null && whichColumn >= 0) {
            int left, top, right, bottom, rowCount;
            float textDrawX, textDrawY, textSize;
            rowCount = table.getRowCount();
            boolean isDraw = false;
            textSize = 60;
            paint.setTextSize(textSize);
            //traverse all row to get the which column
            for (int i = 0; i < rowCount; i++) {
                AbsRowEntity row = table.getRow(i);

                if (row != null && whichColumn < row.getColumnCount()) {
                    AbsCellEntity cell = row.getCell(whichColumn);
                    if (cell != null && cell.isNeedToDraw(i, whichColumn)) {
                        this.calculateCellWidthAndHeight(mRecyclePoint, cell, cellWidth, cellHeight);
                        left = startDrawX;
                        right = left + mRecyclePoint.x;
                        top = startDrawY + offsetY;
                        bottom = top + mRecyclePoint.y;
                        mRecycleRect.set(left, top, right, bottom);

                        textDrawX = mRecycleRect.left;
                        textDrawY = mRecycleRect.centerY() + textSize * 2 / 3;
                        this.drawCell(cell, mRecycleRect, textDrawX, textDrawY, paint, canvas);

                        //record the max width of this row
                        maxDrawWidth = maxDrawWidth < mRecyclePoint.x ? mRecyclePoint.x : maxDrawWidth;
                        isDraw = true;
                    }
                }

                //move to next row
                startDrawY += isDraw ? mRecyclePoint.y : cellHeight;
                isDraw = false;
            }

            this.updateCanvasDrawWidth(mRecycleRect.right - offsetX);
            this.updateCanvasDrawHeight(mRecycleRect.bottom - offsetY);
        }
        return maxDrawWidth;
    }

    private void drawFrozenRow() {
    }

    private void drawMenu(AbsCellEntity menu, RectF drawRect, Paint paint, Canvas canvas) {
    }

    private void drawCell(AbsCellEntity cell, Rect drawRect, float textDrawX, float textDrawY, Paint paint, Canvas canvas) {
        //draw stroke
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRect(drawRect, paint);

        //draw background
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(drawRect, paint);

        //draw text
        paint.setColor(Color.WHITE);
        this.drawAutofitWidthText(drawRect.width(), cell.getText(), textDrawX, textDrawY, paint, canvas);
    }


    //TODO: calculate cell real width and height,if the cell span other cell (left/right or top/bottom)
    private void calculateCellWidthAndHeight(Point outPoint, AbsCellEntity cell, int defaultCellWidth, int defaultCellHeight) {
        int width = cell.getDrawWidth();
        int height = cell.getDrawHeight();
        width = width <= 0 ?
                defaultCellWidth * cell.getSpanRowCount() + defaultCellWidth :
                width;
        height = height <= 0 ?
                defaultCellHeight * cell.getSpanColumnCount() + defaultCellHeight :
                height;
        outPoint.set(width, height);
    }

    private boolean isDrawRectCanSeen(Rect rect) {
        if (rect != null) {
            return !(rect.right < 0 || rect.left > mViewParams.x ||
                    rect.bottom < 0 || rect.top > mViewParams.y);
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
    protected void drawAutofitWidthText(float charWidth, float maxDrawWidth, String drawText, float drawY, float drawX, Paint paint, Canvas canvas) {
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
    protected float measureCharWidth(Paint paint, int textSize, boolean isChinese) {
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
    protected void drawAutofitWidthText(float maxDrawWidth, String drawText, float drawX, float drawY, Paint paint, Canvas canvas) {
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

    /**
     * get the view width and height before draw canvas.
     * if get the layout params at the beginning, maybe the view haven't finished its measure and layout,
     * we can't get the real show area of view.
     *
     * @param view
     * @param outPoint point to save the params
     * @return
     */
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

    /**
     * update the canvas width,save the max value
     *
     * @param newWidth
     */
    private void updateCanvasDrawWidth(float newWidth) {
        mCanvasDrawWidth = newWidth > mCanvasDrawWidth ? newWidth : mCanvasDrawWidth;
    }

    /**
     * update the canvas height,save the max value
     *
     * @param newHeight
     */
    private void updateCanvasDrawHeight(float newHeight) {
        mCanvasDrawHeight = newHeight > mCanvasDrawHeight ? newHeight : mCanvasDrawHeight;
    }

    @Override
    public boolean isCanMovedOnX(PointF moveDistancePointF, PointF newOffsetPointF) {
        //only can move in the positive axis.
        //when move to a big positive axis(for example from 0 to +int),
        //the offset distance will be negative.
//        return mIsScrollInHorizontal && newOffsetX <= 0;
        float outOfCanvas = mCanvasDrawWidth + 50 - mViewParams.x;
        if (outOfCanvas > 0) {
            if (Math.abs(newOffsetPointF.x) > outOfCanvas) {
                newOffsetPointF.x = outOfCanvas * (newOffsetPointF.x > 0 ? 1 : -1);
            } else if (newOffsetPointF.x > 0) {
                newOffsetPointF.x = 0;
            }
        }
        return mIsScrollInHorizontal && newOffsetPointF.x <= 0 && outOfCanvas > 0;
    }

    @Override
    public boolean isCanMovedOnY(PointF moveDistancePointF, PointF newOffsetPointF) {
        //only can move in the positive axis.
//        return !mIsScrollInHorizontal && newOffsetY <= 0;
        float outOfCanvas = mCanvasDrawHeight + 50 - mViewParams.y;
        if (outOfCanvas > 0) {
            if (Math.abs(newOffsetPointF.y) > outOfCanvas) {
                newOffsetPointF.y = outOfCanvas * (newOffsetPointF.y > 0 ? 1 : -1);
            } else if (newOffsetPointF.y > 0) {
                newOffsetPointF.y = 0;
            }
        }
        return !mIsScrollInHorizontal && newOffsetPointF.y <= 0 && outOfCanvas > 0;
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
    public void startMove(float mouseDownX, float mouseDownY) {
        if (mouseDownX < mMenuWidth) {
            mIsScrollInHorizontal = false;
        } else {
            mIsScrollInHorizontal = true;
        }
    }

    @Override
    public void finishedMove(boolean hasBeenMoved) {
        mIsScrollInHorizontal = false;
    }

    @Override
    public void startScale(float newScaleRate) {

    }

    @Override
    public void finishedScale(boolean hasBeenScaled) {

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
