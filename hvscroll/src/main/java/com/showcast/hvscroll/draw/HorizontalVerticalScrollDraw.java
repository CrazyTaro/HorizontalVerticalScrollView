package com.showcast.hvscroll.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.showcast.hvscroll.entity.AbsCellEntity;
import com.showcast.hvscroll.entity.AbsTableEntity;
import com.showcast.hvscroll.params.BaseParams;
import com.showcast.hvscroll.params.CellParams;
import com.showcast.hvscroll.params.Constant;
import com.showcast.hvscroll.params.GlobalParams;
import com.showcast.hvscroll.params.MenuParams;
import com.showcast.hvscroll.touchhelper.ClickPointComputeHelper;
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
    private ClickPointComputeHelper mClickHelper;
    private View mDrawView;

    private AbsTableEntity mTable;
    private GlobalParams mGlobalParams;
    private CellParams mCellParams;
    private MenuParams mMenuParams;

    private int mStartDrawX = 0;
    private int mStartDrawY = 0;
    //the direction for scrolling
    private boolean mIsScrollInHorizontal = true;
    //the max width of canvas draws
    private float mCanvasDrawWidth = 0;
    //the max height of canvas draws
    private float mCanvasDrawHeight = 0;
    private int mMaskWidth = 0;

    private boolean mIsCanvasChanged = true;

    private Rect mRecycleRect;
    private Point mViewParams;
    private Point mRecyclePoint;
    private Point mRowPoint;
    private Point mColumnPoint;
    private Paint mPaint;

    public HorizontalVerticalScrollDraw(@NonNull View drawView) {
        this();
        this.setDrawView(drawView);
    }

    private HorizontalVerticalScrollDraw() {
        mTouchHelper = new TouchEventHelper(this);
        mMsActionHelper = new MoveAndScaleTouchHelper(this, this);
        mClickHelper = new ClickPointComputeHelper();

        mMsActionHelper.setNoticationEvent(this);
        mTouchHelper.setIsEnableDoubleClick(false);
        mRecycleRect = new Rect();
        mRecyclePoint = new Point();
        mRowPoint = new Point();
        mColumnPoint = new Point();
        mPaint = new Paint();

        mGlobalParams = new GlobalParams();
    }

    public void setDrawView(@NonNull View drawView) {
        if (mDrawView != null) {
            mDrawView.setOnTouchListener(null);
        }
        mDrawView = drawView;
        mDrawView.setOnTouchListener(mTouchHelper);
    }

    public void setTable(AbsTableEntity table) {
        if (mTable != table) {
            mTable = table;
            mIsCanvasChanged = true;
            mDrawView.invalidate();
        }
    }

    public void setParams(@NonNull GlobalParams global, MenuParams menu, CellParams cell) {
        this.setGlobalParams(global);
        this.setMenuParams(menu);
        this.setCellParams(cell);
    }

    public void setGlobalParams(@NonNull GlobalParams params) {
        mGlobalParams = params;
    }

    public void setMenuParams(MenuParams menu) {
        mMenuParams = menu;
    }

    public void setCellParams(CellParams cell) {
        mCellParams = cell;
        if (cell != null) {
            //TODO:set width/height interval
            mClickHelper.setParams(cell.getWidth(), cell.getHeight(), 0, 0);
        }
    }

    public GlobalParams getGlobalParams() {
        return mGlobalParams;
    }

    public MenuParams getMenuParams() {
        return mMenuParams;
    }

    public CellParams getCellParams() {
        return mCellParams;
    }

    public void setMaskWidth(int maskWidth) {
        mMaskWidth = maskWidth;
    }

    public void drawCanvas(Canvas canvas) {
        if (mTable == null || mCellParams == null) {
            return;
        }
        int offsetX = (int) mMsActionHelper.getDrawOffsetX();
        int offsetY = (int) mMsActionHelper.getDrawOffsetY();

        this.beforeDraw(canvas, offsetX, offsetY);
        if (mMenuParams != null) {
            //draw row menu if necessarily
            if (mMenuParams.isDrawRowMenu()) {
                this.drawRowMenu(mTable, mMenuParams, mRowPoint, 0, 0, offsetX, offsetY, mPaint, canvas);
            }
            //draw column menu if necessarily
            if (mMenuParams.isDrawColumnMenu()) {
                this.drawColumnMenu(mTable, mMenuParams, mColumnPoint, 0, 0, offsetX, offsetY, mPaint, canvas);
            }
        }
        //TODO: return the menu draw width or height to set the cell draw area
        this.drawCellInTable(mTable, mCellParams, mColumnPoint.x, mRowPoint.x, mColumnPoint.y, mRowPoint.y, offsetX, offsetY, mPaint, canvas);
        this.drawAnyWidthMask(mTable, mMenuParams, mCellParams, mMaskWidth, offsetX, offsetY, mPaint, canvas);
    }

    private void beforeDraw(Canvas canvas, int offsetX, int offsetY) {
        if (mViewParams == null) {
            mViewParams = this.getViewWidthHeight(mDrawView, mViewParams);
        }
        mColumnPoint.set(0, 0);
        mRowPoint.set(0, 0);
        canvas.drawColor(mGlobalParams.getCanvasBgColor());
    }

    private void finishDraw() {
    }


    //draw the row menu, menu will fix on the top ,for now
    private void drawRowMenu(AbsTableEntity table, MenuParams params, @NonNull Point outPoint, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        int width, height, drawOffsetX, drawOffsetY = 0;
        MenuParams.MenuSetting setting = params.getSetting(Constant.MENU_ROW);
        width = params.getWidth();
        height = params.getHeight();
        if (table != null && table.getMenuCount(Constant.MENU_ROW) > 0) {
            //ignore offset values if menu is frozen
            //so that menu will not be moved
            if (setting.isFrozenX()) {
                //when frozen,set the original offset to 0
                //this property has priority
                offsetX = 0;
            }
            if (setting.isFrozenY()) {
                offsetY = 0;
            }
            //copy the offset to a new var to make sure the original offset not changed.
            drawOffsetX = offsetX;
            drawOffsetY = offsetY;

            Point[] points = this.calculateSkipUnseenCell(width, height, startDrawX, startDrawY, 1, table.getMenuCount(Constant.MENU_ROW), drawOffsetX, drawOffsetY);
            for (int i = points[0].y; i < points[1].y; i++) {
                //get menu
                AbsCellEntity menu = table.getRowMenu(i);
                this.drawCommonCell(menu, params, Constant.FIXED_MENU_INDEX_ROW, i,
                        drawOffsetX, drawOffsetY,
                        startDrawX, startDrawY, paint, canvas);
            }

            //draw the frozen menu item
            if (setting.getFrozenItemSize() > 0) {
                int[] result = setting.getValueFrozenItems();
                for (int i : result) {
                    //get frozen menu
                    AbsCellEntity menu = table.getRowMenu(i);
                    drawOffsetX = 0;
                    this.drawCommonCell(menu, params, Constant.FIXED_MENU_INDEX_ROW, i,
                            drawOffsetX, drawOffsetY,
                            startDrawX, startDrawY, paint, canvas);
                }
            }
            //when the frozen column draw,the drawStartY must be moved to height
            outPoint.y = height;
        }
        //calculate the length the menu has moved for canvas clipping
        int result = height + drawOffsetY;
        result = result < 0 ? 0 : result;
        outPoint.x = result;
        //calculate the length the cells start to draw(after menu drawing)
        result = outPoint.x - drawOffsetY;
        result = result > 0 ? 0 : result;
        outPoint.y += result;
    }

    private void drawColumnMenu(AbsTableEntity table, MenuParams params, @NonNull Point outPoint, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        int width, height, drawOffsetX = 0, drawOffsetY;
        MenuParams.MenuSetting setting = params.getSetting(Constant.MENU_COLUMN);
        width = params.getWidth();
        height = params.getHeight();
        if (table != null && table.getMenuCount(Constant.MENU_COLUMN) > 0) {
            //ignore offset values if menu is frozen
            //so that menu will not be moved
            if (setting.isFrozenX()) {
                //when frozen,set the original offset to 0
                //this property has priority
                offsetX = 0;
            }
            if (setting.isFrozenY()) {
                offsetY = 0;
            }
            //copy the offset to a new var to make sure the original offset not changed.
            drawOffsetX = offsetX;
            drawOffsetY = offsetY;

            Point[] points = this.calculateSkipUnseenCell(width, height, startDrawX, startDrawY, table.getMenuCount(Constant.MENU_COLUMN), 1, drawOffsetX, drawOffsetY);
            for (int i = points[0].x; i < points[1].x; i++) {
                //get menu
                AbsCellEntity menu = table.getColumnMenu(i);
                //draw column menu
                this.drawCommonCell(menu, params, i, Constant.FIXED_MENU_INDEX_COLUMN,
                        drawOffsetX, drawOffsetY,
                        startDrawX, startDrawY, paint, canvas);
            }

            //draw the frozen menu item
            if (setting.getFrozenItemSize() > 0) {
                int[] result = setting.getValueFrozenItems();
                for (int i : result) {
                    //get frozen menu
                    AbsCellEntity menu = table.getRowMenu(i);
                    drawOffsetX = 0;
                    this.drawCommonCell(menu, params, i, Constant.FIXED_MENU_INDEX_COLUMN,
                            drawOffsetX, drawOffsetY,
                            startDrawX, startDrawY, paint, canvas);
                }
            }
            //when the frozen column draw,the drawStartX must be moved to width
            outPoint.y = width;
        }
        //calculate the length the menu has moved for canvas clipping
        int result = width + drawOffsetX;
        result = result < 0 ? 0 : result;
        outPoint.x = result;
        //calculate the length the cells start to draw(after menu drawing)
        result = outPoint.x - drawOffsetX;
        result = result > 0 ? 0 : result;
        outPoint.y += result;
    }


    //draw all cells on the table
    private void drawCellInTable(AbsTableEntity table, CellParams params, int clipStartX, int clipStartY, int startDrawX, int startDrawY, int canvasOffsetX, int canvasOffsetY, Paint paint, Canvas canvas) {
        if (table != null) {
            int rowCount, columnCount, cellWidth, cellHeight;
            rowCount = table.getRowCount();
            columnCount = table.getColumnCount();
            cellWidth = params.getWidth();
            cellHeight = params.getHeight();
            //clip the rect to make the show area for drawing
            canvas.clipRect(clipStartX, clipStartY, mViewParams.x, mViewParams.y);
            //skip all the unseen cells
            Point[] points = this.calculateSkipUnseenCell(cellWidth, cellHeight, startDrawX, startDrawY, rowCount, columnCount,
                    canvasOffsetX, canvasOffsetY);

            for (int i = points[0].x; i < points[1].x; i++) {
                for (int j = points[0].y; j < points[1].y; j++) {
                    AbsCellEntity cell = table.getCell(i, j);
                    //try draw cell when cell exists or need to draw
                    this.drawCommonCell(cell, params, i, j,
                            canvasOffsetX, canvasOffsetY,
                            startDrawX, startDrawY, paint, canvas);
                }
            }

            //get row and column frozen setting
            CellParams.LineSetting rowSetting = params.getSetting(Constant.LINE_ROW);
            CellParams.LineSetting columnSetting = params.getSetting(Constant.LINE_COLUMN);
            if (rowSetting.getFrozenItemSize() > 0) {
                //get all frozen items' index
                int[] result = rowSetting.getValueFrozenItems();
                for (int i : result) {
                    this.drawFrozenRow(table, params,
                            startDrawX, startDrawY, canvasOffsetX, canvasOffsetY,
                            paint, canvas);
                }
            }
            //the same as frozen row
            if (columnSetting.getFrozenItemSize() > 0) {
                int[] result = columnSetting.getValueFrozenItems();
                for (int i : result) {
                    this.drawFrozenColumn(table, params,
                            startDrawX, startDrawY, canvasOffsetX, canvasOffsetY,
                            paint, canvas);
                }
            }
        }
    }


    //draw frozen column
    //TODO:set textSize/textColor etc.
    private void drawFrozenColumn(AbsTableEntity table, CellParams params, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        int cellWidth;
        CellParams.LineSetting setting = params.getSetting(Constant.LINE_COLUMN);
        cellWidth = params.getWidth();
        //the canvas has been clipped, should not clip again
//        canvas.clipRect(startDrawX, startDrawY, mViewParams.x, mViewParams.y);

        //check if the table exists
        if (table != null) {
            //check if table has any frozen column
            if (setting.getFrozenItemSize() > 0) {
                int offsetWidth = setting.getDrawLength(cellWidth);
                int rowCount = table.getRowCount();
                //get all the frozen columns' index
                int[] result = setting.getValueFrozenItems();
                for (int line : result) {
                    //traverse all row to get the which column
                    for (int i = 0; i < rowCount; i++) {
                        AbsCellEntity cell = table.getCell(i, line);
                        //draw all the columns
                        this.drawCommonCell(cell, params, i, line,
                                offsetWidth, offsetY,
                                startDrawX, startDrawY, paint, canvas);
                    }
                }
            }
        }
    }

    private void drawFrozenRow(AbsTableEntity table, CellParams params, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        int cellHeight;
        CellParams.LineSetting setting = params.getSetting(Constant.LINE_ROW);
        cellHeight = params.getHeight();
        //the canvas has been clipped, should not clip again
//        canvas.clipRect(startDrawX, startDrawY, mViewParams.x, mViewParams.y);

        //check if the table exists
        if (table != null) {
            //check if table has any frozen column
            if (setting.getFrozenItemSize() > 0) {
                int offsetHeight = setting.getDrawLength(cellHeight);
                int columnCount = table.getColumnCount();
                //get all the frozen columns' index
                int[] result = setting.getValueFrozenItems();
                for (int line : result) {
                    //traverse all row to get the which column
                    for (int i = 0; i < columnCount; i++) {
                        AbsCellEntity cell = table.getCell(line, i);
                        //draw all the columns
                        this.drawCommonCell(cell, params, line, i,
                                offsetX, offsetHeight,
                                startDrawX, startDrawY, paint, canvas);
                    }
                }
            }
        }
    }

    private void drawAnyWidthMask(AbsTableEntity table, MenuParams menuParams, CellParams cellParams, int maskWidth, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        if (maskWidth <= 0) {
            return;
        }

        int menuWidth, menuHeight, cellWidth, cellHeight;
        int columnIndex = 2;
        menuWidth = menuParams == null ? 0 : menuParams.getWidth();
        menuHeight = menuParams == null ? 0 : menuParams.getHeight();
        cellWidth = cellParams.getWidth();
        cellHeight = cellParams.getHeight();

        int canvasSeenHeight = menuHeight + cellHeight * table.getRowCount() + offsetY;
        mRecycleRect.left = menuWidth + cellWidth * columnIndex + offsetX;
        mRecycleRect.top = 0;
        mRecycleRect.right = mRecycleRect.left + maskWidth;
        mRecycleRect.bottom = mRecycleRect.top + (canvasSeenHeight > mViewParams.y ? mViewParams.y : canvasSeenHeight);

        if (isDrawRectCanSeen(mRecycleRect)) {
            paint.setColor(Color.RED);
            paint.setAlpha(100);
            canvas.drawRect(mRecycleRect, paint);

            paint.setAlpha(255);
            paint.setColor(Color.LTGRAY);
            canvas.drawLine(mRecycleRect.right, 0, mRecycleRect.right + 2, mViewParams.y, paint);
        }
    }


    /**
     * draw common cells include menus
     *
     * @param cellOrMenu  the cell or menu for drawing
     * @param params      cell params or menu params,both of them are extend from BaseParams
     * @param whichRow    the row index of cell/menu
     * @param whichColumn the column index of cell/menu
     * @param offsetX     the length of canvas has been moved,positive is left to right,negative is right to left
     * @param offsetY     the length of canvas has been moved,positive is top to bottom,negative is bottom to top
     * @param startDrawX  the x coordinate of start drawing
     * @param startDrawY  the y coordinate of start drawing
     * @param paint
     * @param canvas
     * @return return the bigger one between the right of this cell and 0,it is the length from the right of this cell drawn in screen to the 0;
     * if the cell can't be seen,0 will be return
     */
    private void drawCommonCell(AbsCellEntity cellOrMenu, @NonNull BaseParams params, int whichRow, int whichColumn, int offsetX, int offsetY, int startDrawX, int startDrawY, Paint paint, Canvas canvas) {
        if (cellOrMenu != null && cellOrMenu.isNeedToDraw(whichRow, whichColumn)) {
            int width = params.getWidth();
            int height = params.getHeight();
            this.calculateCellWidthAndHeight(mRecyclePoint, cellOrMenu, width, height);
            //calculate every cell
            //we mush use cellWidth for unit here
            //every cell's width maybe changed,but we draw cell one by one
            //when the cell needn't to draw we just ignore it
            mRecycleRect.left = width * whichColumn + offsetX + startDrawX;
            mRecycleRect.right = mRecycleRect.left + mRecyclePoint.x;
            mRecycleRect.top = height * whichRow + offsetY + startDrawY;
            mRecycleRect.bottom = mRecycleRect.top + mRecyclePoint.y;

            BaseDrawStyle style = params.getDefaultDrawStyle();
            //if the draw area cannot be seen,ignore it
            if (isDrawRectCanSeen(mRecycleRect)) {
                int textDrawX, textDrawY;
                textDrawX = mRecycleRect.left;
                textDrawY = mRecycleRect.centerY() + style.getTextSize() * 2 / 3;
                //draw cell
                this.drawCell(cellOrMenu, style, mRecycleRect, textDrawX, textDrawY, paint, canvas);
            }
        }
    }

    /**
     * draw one menu
     *
     * @param menu      menu for draw
     * @param drawStyle the drawStyle for menu,if the menu doesn't request a special drawStyle,the default drawStyle will be offered
     * @param drawRect  the rect for menus drawing,menu should draw int this rect
     * @param textDrawX the x axis for text drawing,default in the center x of rect.
     * @param textDrawY the y axis for text drawing,default under the center y of rect,
     * @param paint
     * @param canvas
     */
    private void drawMenu(AbsCellEntity menu, BaseDrawStyle drawStyle, Rect drawRect, float textDrawX, float textDrawY, Paint paint, Canvas canvas) {
        this.drawCell(menu, drawStyle, drawRect, textDrawX, textDrawY, paint, canvas);
    }

    /**
     * draw one cell
     *
     * @param cell      cell for draw
     * @param drawStyle the drawStyle for cell,if the cell doesn't request a special drawStyle,the default drawStyle will be offered
     * @param drawRect  the rect for cells drawing,cell should draw in this rect
     * @param textDrawX the x axis for text drawing,default in the center x of rect.
     * @param textDrawY the y axis for text drawing,default under the center y of rect,
     * @param paint
     * @param canvas
     */
    private void drawCell(@NonNull AbsCellEntity cell, @NonNull BaseDrawStyle drawStyle, Rect drawRect, float textDrawX, float textDrawY, Paint paint, Canvas canvas) {
        //draw stroke
        paint.setColor(drawStyle.getStrokeColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(drawStyle.getStrokeWidth());
        canvas.drawRect(drawRect, paint);

        //draw background
        paint.setColor(drawStyle.getBackgroundColor());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(drawRect, paint);

        //draw text
        paint.setColor(drawStyle.getTextColor());
        paint.setTextSize(drawStyle.getTextSize());
        this.drawAutofitWidthText(drawRect.width(), cell.getText(), textDrawX, textDrawY, paint, canvas);
    }


    /**
     * calculate cell real width and height,if the cell span other cell (left/right or top/bottom)
     *
     * @param outPoint          a object to save the result
     * @param cell              the cell to calculate its width and height for drawing
     * @param defaultCellWidth  the unit width of cell or menu
     * @param defaultCellHeight the unit height of cell or menu
     */
    private void calculateCellWidthAndHeight(@NonNull Point outPoint, @NonNull AbsCellEntity cell, int defaultCellWidth, int defaultCellHeight) {
        int width = cell.getDrawWidth();
        int height = cell.getDrawHeight();
        width = width <= 0 ?
                defaultCellWidth * cell.getSpanColumnCount() :
                width;
        height = height <= 0 ?
                defaultCellHeight * cell.getSpanRowCount() :
                height;
        outPoint.set(width, height);
    }

    /**
     * return true if the rect in screen,false if the rect out screen
     *
     * @param rect the rect for drawing
     * @return
     */
    private boolean isDrawRectCanSeen(Rect rect) {
        if (rect != null) {
            return !(rect.right < 0 || rect.left > mViewParams.x ||
                    rect.bottom < 0 || rect.top > mViewParams.y);
        } else {
            return false;
        }
    }

    /**
     * calculate the position of cell seen in the screen. try to skip the unseen cells
     *
     * @param unitWidth    default width of cell or menu
     * @param unitHeight   default height of cell or menu
     * @param startDrawX
     * @param startDrawY
     * @param maxRow       the max row count in this line
     * @param maxColumn    the max column count in this line
     * @param offsetWidth  the width has moved
     * @param offsetHeight the height has moved
     * @return
     */
    @NonNull
    private Point[] calculateSkipUnseenCell(int unitWidth, int unitHeight, int startDrawX, int startDrawY, int maxRow, int maxColumn, int offsetWidth, int offsetHeight) {
        Point beginPoint = new Point();
        Point endPoint = new Point();

        beginPoint.y = (Math.abs(offsetWidth) - startDrawX) / unitWidth;
        beginPoint.x = (Math.abs(offsetHeight) - startDrawY) / unitHeight;

        //plus 2 to make sure cells will draw in all canvas
        //if plus only 1 maybe after moving some place will show nothing.
        endPoint.y = beginPoint.y + mViewParams.x / unitWidth + 2;
        endPoint.x = beginPoint.x + mViewParams.y / unitHeight + 2;

        endPoint.y = endPoint.y > maxColumn ? maxColumn : endPoint.y;
        endPoint.x = endPoint.x > maxRow ? maxRow : endPoint.x;
        return new Point[]{beginPoint, endPoint};
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
     * @param drawX        the left axis begin to draw text
     * @param drawY        the bottom axis begin to draw text
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
                //TODO:maybe the max char length less than 3
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
        if (mViewParams == null) {
            return false;
        }
        //TODO: check if out canvas
        float outOfCanvas = mCanvasDrawWidth + 50 - mViewParams.x;
        if (outOfCanvas > 0) {
            if (Math.abs(newOffsetPointF.x) > outOfCanvas) {
                newOffsetPointF.x = outOfCanvas * (newOffsetPointF.x > 0 ? 1 : -1);
            }
        }
        //here to process the situation of moving pass the original position
        if (newOffsetPointF.x > 0) {
            newOffsetPointF.x = 0;
        }
//        return mIsScrollInHorizontal && newOffsetPointF.x <= 0 && outOfCanvas > 0;
        return mIsScrollInHorizontal && newOffsetPointF.x <= 0;
    }

    @Override
    public boolean isCanMovedOnY(PointF moveDistancePointF, PointF newOffsetPointF) {
        //only can move in the positive axis.
        if (mViewParams == null) {
            return false;
        }
        //TODO: check if out canvas
        float outOfCanvas = mCanvasDrawHeight + 50 - mViewParams.y;
        if (outOfCanvas > 0) {
            if (Math.abs(newOffsetPointF.y) > outOfCanvas) {
                newOffsetPointF.y = outOfCanvas * (newOffsetPointF.y > 0 ? 1 : -1);
            }
        }
        //here to process the situation of moving pass the original position
        if (newOffsetPointF.y > 0) {
            newOffsetPointF.y = 0;
        }
//        return !mIsScrollInHorizontal && newOffsetPointF.y <= 0 && outOfCanvas > 0;
        return !mIsScrollInHorizontal && newOffsetPointF.y <= 0;
    }

    @Override
    public void onMove(int suggestEventAction) {
        mDrawView.invalidate();
    }

    @Override
    public void onMoveFail(int suggestEventAction) {
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
    public void onScaleFail(int suggestEventAction) {

    }

    @Override
    public void startMove(float mouseDownX, float mouseDownY) {
        //TODO: set toggle scroll direction
        if (mouseDownX < mCellParams.getWidth()) {
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
//        mMsActionHelper.multiTouchEvent(event, extraMotionEvent);
    }

    @Override
    public void onSingleClickByTime(MotionEvent event) {
        //TODO: set ignore width/height from drawn menu width/height
//        int x = mClickHelper.computeClickXFromFirstLine(event.getX(), mMsActionHelper.getDrawOffsetX(), mCellParams.getWidth());
//        int y = mClickHelper.computeClickYFromFirstLine(event.getY(), mMsActionHelper.getDrawOffsetY(), mCellParams.getHeight());
//        Log.i("click", "x/y=" + x + "/" + y);
        Log.i("offset", "x/y=" + mMsActionHelper.getDrawOffsetX() + "/" + mMsActionHelper.getDrawOffsetY());
    }

    @Override
    public void onSingleClickByDistance(MotionEvent event) {

    }

    @Override
    public void onDoubleClickByTime() {

    }

    public interface ICellSelectListener {
        public void onCellSelected(int row, int column);
    }
}
