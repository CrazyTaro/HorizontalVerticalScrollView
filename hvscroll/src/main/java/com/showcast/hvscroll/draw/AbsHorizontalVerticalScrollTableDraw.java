package com.showcast.hvscroll.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.showcast.hvscroll.entity.CellEntity;
import com.showcast.hvscroll.entity.TableEntity;
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
public abstract class AbsHorizontalVerticalScrollTableDraw implements IHVScrollTable, TouchEventHelper.OnToucheEventListener,
        MoveAndScaleTouchHelper.IMoveEvent, MoveAndScaleTouchHelper.INotificationEvent {
    private TouchEventHelper mTouchHelper;
    private MoveAndScaleTouchHelper mMsActionHelper;
    private ClickPointComputeHelper mClickHelper;
    private View mDrawView;

    private TableEntity mTable;
    private GlobalParams mGlobalParams;
    private CellParams mCellParams;
    private MenuParams mMenuParams;

    private OnCellClickListener mCellClickListener;

    //the direction for scrolling
    private boolean mIsScrollInHorizontal = false;
    private boolean mIsScrollInVertical = false;
    private boolean mIsConfirmScrollDirection = false;
    private boolean mIsScrollX = false;
    //the max width of canvas draws
    private int mCanvasDrawWidth = 0;
    //the max height of canvas draws
    private int mCanvasDrawHeight = 0;

    protected Rect mRecycleRect;
    protected Rect mSkipRect;
    protected Point mViewParams;
    protected Point mRecyclePoint;
    private Point mRowPoint;
    private Point mColumnPoint;
    private Paint mPaint;

    public AbsHorizontalVerticalScrollTableDraw(@NonNull View drawView) {
        this();
        this.setDrawView(drawView);
    }

    private AbsHorizontalVerticalScrollTableDraw() {
        mTouchHelper = new TouchEventHelper(this);
        mMsActionHelper = new MoveAndScaleTouchHelper(null, this);
        mClickHelper = new ClickPointComputeHelper();

        mMsActionHelper.setNotificationEvent(this);
        mTouchHelper.setIsEnableDoubleClick(false);
        mRecycleRect = new Rect();
        mSkipRect = new Rect();
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

    @Override
    public void setTable(TableEntity table) {
        if (mTable != table) {
            mTable = table;
            mDrawView.invalidate();
        }
    }

    @Override
    public void setParams(@NonNull GlobalParams global, MenuParams menu, CellParams cell) {
        this.setGlobalParams(global);
        this.setMenuParams(menu);
        this.setCellParams(cell);
    }

    @Override
    public void setGlobalParams(@NonNull GlobalParams params) {
        mGlobalParams = params;
    }

    @Override
    public void setMenuParams(MenuParams menu) {
        mMenuParams = menu;
    }

    @Override
    public void setCellParams(CellParams cell) {
        mCellParams = cell;
    }

    @Override
    public void setOnCellClickListener(OnCellClickListener listener) {
        mCellClickListener = listener;
    }

    @Override
    public TableEntity getTable() {
        return mTable;
    }

    @Override
    public GlobalParams getGlobalParams() {
        return mGlobalParams;
    }

    @Override
    public MenuParams getMenuParams() {
        return mMenuParams;
    }

    @Override
    public CellParams getCellParams() {
        return mCellParams;
    }

    /**
     * draw canvas.
     *
     * @param canvas
     */
    public void drawCanvas(Canvas canvas) {
        if (!isNeedToDraw()) {
            return;
        }
        int offsetX = (int) mMsActionHelper.getDrawOffsetX();
        int offsetY = (int) mMsActionHelper.getDrawOffsetY();

        this.beforeDraw(canvas);
        //menu must be draw first to calculate the position where table begins to draw.
        if (mMenuParams != null) {
            //draw menu first
            if (mMenuParams.isDrawRowMenuFirst()) {
                this.drawSingleMenu(mTable, mMenuParams, Constant.MENU_ROW, mRowPoint, offsetX, offsetY, mPaint, canvas);
                this.drawSingleMenu(mTable, mMenuParams, Constant.MENU_COLUMN, mColumnPoint, offsetX, offsetY, mPaint, canvas);
            } else {
                //draw column first
                this.drawSingleMenu(mTable, mMenuParams, Constant.MENU_COLUMN, mColumnPoint, offsetX, offsetY, mPaint, canvas);
                this.drawSingleMenu(mTable, mMenuParams, Constant.MENU_ROW, mRowPoint, offsetX, offsetY, mPaint, canvas);
            }
        }
        if (mGlobalParams.isDrawCellStroke()) {
            this.drawCellBackgroundStroke(mTable, mCellParams, mColumnPoint.x, mRowPoint.x,
                    mColumnPoint.y, mRowPoint.y, offsetX, offsetY, mPaint, canvas);
        }
        //draw cells
        this.drawTable(mTable, mCellParams, mColumnPoint.x, mRowPoint.x,
                mColumnPoint.y, mRowPoint.y, offsetX, offsetY, mPaint, canvas);
        //draw mask
        if (mGlobalParams.isDrawMask()) {
            this.drawAnyWidthMask(mMenuParams, mCellParams, mGlobalParams, mColumnPoint.x, mRowPoint.x, offsetX, offsetY, mPaint, canvas);
        }
        //draw frozen columns/rows, to keep the frozen items up of mask.
        this.drawFrozenLines(mTable, mCellParams, mColumnPoint.x, mRowPoint.x,
                mColumnPoint.y, mRowPoint.y, offsetX, offsetY, mPaint, canvas);
    }


    /**
     * do something before drawing
     *
     * @param canvas
     */
    protected void beforeDraw(Canvas canvas) {
        if (mViewParams == null) {
            mViewParams = this.getViewWidthHeight(mDrawView, mViewParams);
        }
        //nothing would be calculated when table or cellParams is null.
        if (isNeedToDraw()) {
            mCellParams.computeDrawWidthAndHeight(mViewParams.x, mViewParams.y);
            if (mMenuParams != null) {
                mMenuParams.computeDrawWidthAndHeight(mViewParams.x, mViewParams.y);
            }
            //update the canvas width and height.
            this.computeCanvasWidthAndHeight(mTable, mMenuParams, mCellParams);
            //calculate cell/menu width and height
            mClickHelper.setParams(mCellParams.getDrawWidth(), mCellParams.getDrawHeight(), 0, 0);
            //reset clip/startDraw position points.
            mColumnPoint.set(0, 0);
            mRowPoint.set(0, 0);
        }
        //draw background.
        canvas.drawColor(mGlobalParams.getCanvasBgColor());
    }

    /**
     * do something after drawing.
     */
    protected void finishDraw() {
    }

    /**
     * calculate the canvas' width and height.depends on the width and height of menu and cell is fixed.<br>
     * 计算界面的宽高.这个在菜单与单元格之间的宽高不变时有效(若可以动态改变则无效)
     *
     * @param table
     * @param menuParams
     * @param cellParams
     */
    private void computeCanvasWidthAndHeight(TableEntity table, MenuParams menuParams, CellParams cellParams) {
        if (table == null || cellParams == null) {
            mCanvasDrawWidth = 0;
            mCanvasDrawHeight = 0;
            return;
        }
        int width = 0, height = 0;
        int menuWidth = 0, menuHeight = 0;
        //calculate menu draw width and height.
        if (menuParams != null) {
            if (menuParams.isDrawRowMenu()) {
                menuHeight = menuParams.getDrawHeight();
                width = menuParams.getDrawWidth() * table.getMenuCount(Constant.MENU_ROW);
            }
            if (menuParams.isDrawColumnMenu()) {
                menuWidth = menuParams.getDrawWidth();
                height = menuParams.getDrawHeight() * table.getMenuCount(Constant.MENU_COLUMN);
            }
            mCanvasDrawWidth = width + menuWidth;
            mCanvasDrawHeight = height + menuHeight;
        }
        //calculate table cells.
        width = table.getCellColumnCount() * cellParams.getDrawWidth();
        height = table.getCellRowCount() * cellParams.getDrawHeight();
        width += menuWidth;
        height += menuHeight;
        mCanvasDrawWidth = mCanvasDrawWidth < width ? width : mCanvasDrawWidth;
        mCanvasDrawHeight = mCanvasDrawHeight < height ? height : mCanvasDrawHeight;
    }

    /**
     * draw the menu, both row and column menu will draw by this method including their frozen items<br>
     * 绘制菜单,通过指定行列菜单类型决定需要绘制的菜单
     *
     * @param table
     * @param params      menu params
     * @param menuType    menu type,to point out if the menu is row or column
     * @param outPoint    point to save the length for offset after (row or column) menu drawn
     * @param drawOffsetX canvas offset x
     * @param drawOffsetY canvas offset y
     * @param paint
     * @param canvas
     */
    protected void drawSingleMenu(TableEntity table, @NonNull MenuParams params, @Constant.MenuType int menuType, @NonNull Point outPoint, int drawOffsetX, int drawOffsetY, Paint paint, Canvas canvas) {
        int menuCount, width, height, tempX, tempY;
        boolean isRow;
        //get the menu setting
        MenuParams.MenuSetting setting = params.getSetting(menuType);
        width = params.getDrawWidth();
        height = params.getDrawHeight();
        //check if the menu has items to draw
        menuCount = table == null ? 0 : table.getMenuCount(menuType);
        isRow = menuType == Constant.MENU_ROW;
        if (menuCount <= 0 ||
                //is row but needn't to draw
                (isRow && !params.isDrawRowMenu()) ||
                //is column but needn't to draw
                (!isRow && !params.isDrawColumnMenu())) {
            return;
        }
        //record the row or column max count
        //rowCount will be 1 if draw row; and column count will be 1 if draw column
        tempX = isRow ? 1 : menuCount;
        tempY = menuCount - tempX + 1;
        //ignore offset values if menu is frozen
        //so that menu will not be moved
        if (setting.isFrozenX()) {
            //when frozen,set the original offset to 0
            //this property has priority
            drawOffsetX = 0;
        }
        if (setting.isFrozenY()) {
            drawOffsetY = 0;
        }
        this.computeSkipUnseenCell(mSkipRect, width, height, 0, 0, tempX, tempY, drawOffsetX, drawOffsetY);
        tempX = isRow ? mSkipRect.right : mSkipRect.left;
        tempY = isRow ? mSkipRect.bottom : mSkipRect.top;
        for (int i = tempX; i < tempY; i++) {
            //get menu
            CellEntity menu = table.getMenu(i, menuType);
            this.drawCommonCell(menu, params, true, 0, 0,
                    drawOffsetX, drawOffsetY,
                    0, 0, paint, canvas);
        }

        //draw the frozen menu item
        if (setting.getFrozenItemSize() > 0) {
            int[] result = setting.getValueFrozenItems();
            for (int i : result) {
                //get frozen menu
                CellEntity menu = table.getMenu(i, menuType);
                if (isRow) {
                    drawOffsetX = 0;
                } else {
                    drawOffsetY = 0;
                }
                this.drawCommonCell(menu, params, true, 0, 0,
                        drawOffsetX, drawOffsetY,
                        0, 0, paint, canvas);
            }
        }
        //when the frozen menu draw,the startDrawPosition must be moved to height
        outPoint.y = outPoint.x = isRow ? height : width;
        //here the menuCount is just for saving data,it is a temp var.
        menuCount = isRow ? drawOffsetY : drawOffsetX;
        //the same as menuCount
        isRow = isRow ? setting.isFrozenY() : setting.isFrozenX();
        //calculate the length the menu has moved for canvas clipping
        outPoint.x += menuCount;
        //calculate the length the cells start to draw(after menu drawing)
        //changed the startDrawPosition if menu need to be frozen.
        outPoint.y = isRow ? outPoint.y + menuCount : outPoint.y;
    }

    /**
     * draw the cell stroke on the background.when the cell set to draw its stroke,this will be covered.<br>
     * 在背景色上绘制界面单元格的划分线.单元格中所有的绘制操作将会覆盖对应区域的界面(不会影响到单元格的绘制)
     *
     * @param table
     * @param params
     * @param clipStartX    clip the canvas for draw
     * @param clipStartY    clip the canvas for draw
     * @param startDrawX    the x position begins to draw
     * @param startDrawY    the y position begins to draw
     * @param canvasOffsetX canvas offset x
     * @param canvasOffsetY canvas offset y
     * @param paint
     * @param canvas
     */
    protected void drawCellBackgroundStroke(TableEntity table, @NonNull CellParams params, int clipStartX, int clipStartY, int startDrawX, int startDrawY, int canvasOffsetX, int canvasOffsetY, Paint paint, Canvas canvas) {
        if (table != null) {
            int clipEndX, clipEndY, rowCount, columnCount, cellWidth, cellHeight, lineX, lineY, drawX, drawY;
            rowCount = table.getCellRowCount();
            columnCount = table.getCellColumnCount();
            cellWidth = params.getDrawWidth();
            cellHeight = params.getDrawHeight();
            //clip the rect to make the show area for drawing
            clipEndX = mViewParams.x < mCanvasDrawWidth ? mViewParams.x : mCanvasDrawWidth;
            clipEndY = mViewParams.y < mCanvasDrawHeight ? mViewParams.y : mCanvasDrawHeight;
            canvas.clipRect(clipStartX, clipStartY, clipEndX, clipEndY);
            //skip all the unseen cells
            this.computeSkipUnseenCell(mSkipRect, cellWidth, cellHeight, startDrawX, startDrawY, rowCount, columnCount,
                    canvasOffsetX, canvasOffsetY);

            paint.setColor(mGlobalParams.getStrokeColor());
            paint.setStyle(Paint.Style.FILL);


            lineX = cellWidth * mSkipRect.right + canvasOffsetX + startDrawX;
            lineY = cellHeight * mSkipRect.left + canvasOffsetY + startDrawY;
            drawY = lineY;
            drawX = lineX;
            while (drawY < mViewParams.y) {
                canvas.drawLine(0, drawY, mViewParams.x, drawY + mGlobalParams.getStrokeWidth(), paint);
                drawY += cellHeight;
            }
            while (drawX < mViewParams.x) {
                canvas.drawLine(drawX, 0, drawX + mGlobalParams.getStrokeWidth(), mViewParams.y, paint);
                drawX += cellWidth;
            }

            //reset the draw area
            canvas.clipRect(0, 0, mViewParams.x, mViewParams.y);
        }
    }

    //draw all cells on the table
    protected void drawTable(TableEntity table, CellParams params, int clipStartX, int clipStartY, int startDrawX, int startDrawY, int canvasOffsetX, int canvasOffsetY, Paint paint, Canvas canvas) {
        if (table != null) {
            int rowCount, columnCount, cellWidth, cellHeight;
            rowCount = table.getCellRowCount();
            columnCount = table.getCellColumnCount();
            cellWidth = params.getDrawWidth();
            cellHeight = params.getDrawHeight();
            //clip the rect to make the show area for drawing
            canvas.clipRect(clipStartX, clipStartY, mViewParams.x, mViewParams.y);
            //skip all the unseen cells
            this.computeSkipUnseenCell(mSkipRect, cellWidth, cellHeight, startDrawX, startDrawY, rowCount, columnCount,
                    canvasOffsetX, canvasOffsetY);

            for (int i = mSkipRect.left; i < mSkipRect.top; i++) {
                for (int j = mSkipRect.right; j < mSkipRect.bottom; j++) {
                    CellEntity cell = table.getCell(i, j);
                    //try draw cell when cell exists or need to draw
                    this.drawCommonCell(cell, params, false, i, j,
                            canvasOffsetX, canvasOffsetY,
                            startDrawX, startDrawY, paint, canvas);
                }
            }
            //reset the draw area
            canvas.clipRect(0, 0, mViewParams.x, mViewParams.y);
        }
    }

    /**
     * draw frozen columns or rows.<br>
     * 用于绘制固定的行和列.实际绘制某一行或列由对应的绘制方法完成,此方法主要是做绘制的判断及控件绘制顺序
     *
     * @param table
     * @param params
     * @param clipStartX
     * @param clipStartY
     * @param startDrawX
     * @param startDrawY
     * @param canvasOffsetX
     * @param canvasOffsetY
     * @param paint
     * @param canvas
     */
    protected void drawFrozenLines(TableEntity table, CellParams params, int clipStartX, int clipStartY, int startDrawX, int startDrawY, int canvasOffsetX, int canvasOffsetY, Paint paint, Canvas canvas) {
        if (table != null) {
            //clip the rect to make the show area for drawing
            canvas.clipRect(clipStartX, clipStartY, mViewParams.x, mViewParams.y);

            //if draw frozen row first
            if (params.isDrawFrozenRowFirst()) {
                this.drawSingleFrozenLine(mTable, params, Constant.LINE_ROW, startDrawX, startDrawY, canvasOffsetX, canvasOffsetY, paint, canvas);
                this.drawSingleFrozenLine(mTable, params, Constant.LINE_COLUMN, startDrawX, startDrawY, canvasOffsetX, canvasOffsetY, paint, canvas);
            } else {
                this.drawSingleFrozenLine(mTable, params, Constant.LINE_COLUMN, startDrawX, startDrawY, canvasOffsetX, canvasOffsetY, paint, canvas);
                this.drawSingleFrozenLine(mTable, params, Constant.LINE_ROW, startDrawX, startDrawY, canvasOffsetX, canvasOffsetY, paint, canvas);
            }
            //reset the clip area
            canvas.clipRect(0, 0, mViewParams.x, mViewParams.y);
        }
    }


    protected void drawSingleFrozenLine(TableEntity table, CellParams params, @Constant.LineType int lineType, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        int sizeLength, lineCount, offset, rowIndex, columnIndex;
        boolean isRow;
        CellParams.LineSetting setting = params.getSetting(lineType);
        isRow = lineType == Constant.LINE_ROW;
        sizeLength = isRow ? params.getDrawHeight() : params.getDrawWidth();
        //the canvas has been clipped, should not clip again
        //check if the table exists
        if (table != null) {
            //check if table has any frozen column
            if (setting.getFrozenItemSize() > 0) {
                //get the cell offset length
                offset = setting.getOffsetDrawLength(sizeLength);
                lineCount = table.getCellCount(isRow ? Constant.LINE_COLUMN : Constant.LINE_ROW);
                //update the use data according to line type
                if (isRow) {
                    offsetY = 0;
                    startDrawY += offset;
                } else {
                    offsetX = 0;
                    startDrawX += offset;
                }
                //get all the frozen columns or row 's index
                int[] result = setting.getValueFrozenItems();
                for (int line : result) {
                    //traverse all cell at this line(maybe row or column)
                    for (int i = 0; i < lineCount; i++) {
                        //update the rowIndex and columnIndex according to line type
                        if (isRow) {
                            rowIndex = line;
                            columnIndex = i;
                        } else {
                            rowIndex = i;
                            columnIndex = line;
                        }
                        //get cell
                        CellEntity cell = table.getCell(rowIndex, columnIndex);
                        //draw all the cells at this line
                        this.drawCommonCell(cell, params, false, rowIndex, columnIndex,
                                offsetX, offsetY,
                                startDrawX, startDrawY, paint, canvas);
                    }
                }
            }
        }
    }

    //TODO:将固定行列绘制合并为一个方法进行操作(80%的代码相同)
    //draw frozen column
    @Deprecated
    protected void drawFrozenColumn(TableEntity table, CellParams params, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        int cellWidth;
        CellParams.LineSetting setting = params.getSetting(Constant.LINE_COLUMN);
        cellWidth = params.getDrawWidth();
        //the canvas has been clipped, should not clip again

        //check if the table exists
        if (table != null) {
            //check if table has any frozen column
            if (setting.getFrozenItemSize() > 0) {
                int offsetWidth = setting.getOffsetDrawLength(cellWidth);
                int rowCount = table.getCellRowCount();
                //get all the frozen columns' index
                int[] result = setting.getValueFrozenItems();
                for (int line : result) {
                    //traverse all row to get the which column
                    for (int i = 0; i < rowCount; i++) {
                        CellEntity cell = table.getCell(i, line);
                        //draw all the columns
                        this.drawCommonCell(cell, params, false, i, line,
                                offsetWidth, offsetY,
                                startDrawX, startDrawY, paint, canvas);
                    }
                }
            }
        }
    }

    //draw frozen row
    @Deprecated
    protected void drawFrozenRow(TableEntity table, CellParams params, int startDrawX, int startDrawY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        int cellHeight;
        CellParams.LineSetting setting = params.getSetting(Constant.LINE_ROW);
        cellHeight = params.getDrawHeight();
        //the canvas has been clipped, should not clip again

        //check if the table exists
        if (table != null) {
            //check if table has any frozen column
            if (setting.getFrozenItemSize() > 0) {
                int offsetHeight = setting.getOffsetDrawLength(cellHeight);
                int columnCount = table.getCellColumnCount();
                //get all the frozen columns' index
                int[] result = setting.getValueFrozenItems();
                for (int line : result) {
                    //traverse all row to get the which column
                    for (int i = 0; i < columnCount; i++) {
                        CellEntity cell = table.getCell(line, i);
                        //draw all the columns
                        this.drawCommonCell(cell, params, false, line, i,
                                offsetX, offsetHeight,
                                startDrawX, startDrawY, paint, canvas);
                    }
                }
            }
        }
    }

    /**
     * draw mask with any width which given by globalParams.<br>
     * 绘制蒙板界面,界面的相关设置由globalParmas决定
     *
     * @param menuParams   offer the menu width/height if menu need to be drawn.<br>
     *                     提供菜单的宽高当菜单需要被绘制时.
     * @param cellParams   offset the cell width/height.it is about the width of mask to draw.<br>
     *                     提供单元格的宽高.这个与最终绘制的蒙板界面宽度有关.
     * @param globalParams offset the all params about drawing mask.<br>
     *                     提供所有与蒙板界面相关的参数.
     * @param clipX
     * @param clipY
     * @param offsetX
     * @param offsetY
     * @param paint
     * @param canvas
     */
    protected void drawAnyWidthMask(MenuParams menuParams, @NonNull CellParams cellParams, @NonNull GlobalParams globalParams, int clipX, int clipY, int offsetX, int offsetY, Paint paint, Canvas canvas) {
        int maskWidth = (int) globalParams.getDrawMaskWidth(cellParams.getDrawWidth());
        if (maskWidth <= 0) {
            return;
        }

        int menuWidth, cellWidth, startWidth;
        cellWidth = cellParams.getDrawWidth();
        menuWidth = (menuParams == null || !menuParams.isDrawColumnMenu()) ? 0 : cellWidth;
        startWidth = globalParams.getDrawMaskStartWidth(cellWidth);

        int canvasSeenHeight = mCanvasDrawHeight + offsetY;
        mRecycleRect.left = menuWidth + startWidth + offsetX;
        mRecycleRect.top = 0;
        mRecycleRect.right = mRecycleRect.left + maskWidth;
        mRecycleRect.bottom = mRecycleRect.top + (canvasSeenHeight > mViewParams.y ? mViewParams.y : canvasSeenHeight);
        if (mRecycleRect.right - offsetX > mCanvasDrawWidth) {
            mRecycleRect.right = offsetX + mCanvasDrawWidth;
            if (mRecycleRect.left > mRecycleRect.right) {
                mRecycleRect.left = mRecycleRect.right;
            }
        }

        if (isDrawRectCanSeen(mRecycleRect)) {
            canvas.clipRect(clipX, clipY, mViewParams.x, mViewParams.y);
            this.drawMask(globalParams, mRecycleRect, mViewParams, paint, canvas);
            canvas.clipRect(0, 0, mViewParams.x, mViewParams.y);
        }
    }


    /**
     * draw common cells include menus
     *
     * @param cellOrMenu  the cell or menu for drawing
     * @param params      cell params or menu params,both of them are extend from BaseParams
     * @param isMenu      the type for a menu,false if this is a cell
     * @param whichRow    the row index of cell/menu
     * @param whichColumn the column index of cell/menu
     * @param offsetX     the length of canvas has been moved,positive is left to right,negative is right to left
     * @param offsetY     the length of canvas has been moved,positive is top to bottom,negative is bottom to top
     * @param startDrawX  the x coordinate of start drawing
     * @param startDrawY  the y coordinate of start drawing
     * @param paint
     * @param canvas      @return return the bigger one between the right of this cell and 0,it is the length from the right of this cell drawn in screen to the 0;
     */
    protected void drawCommonCell(CellEntity cellOrMenu, @NonNull BaseParams params, boolean isMenu, int whichRow, int whichColumn, int offsetX, int offsetY, int startDrawX, int startDrawY, Paint paint, Canvas canvas) {
        if (cellOrMenu != null && (cellOrMenu.isNeedToDraw(whichRow, whichColumn) || isMenu)) {
            int width, height;
            width = params.getDrawWidth();
            height = params.getDrawHeight();
            if (isMenu) {
                whichRow = cellOrMenu.getRowIndex();
                whichColumn = cellOrMenu.getColumnIndex();
            }
            this.computeCellWidthAndHeight(mRecyclePoint, cellOrMenu, width, height);
            //calculate every cell
            //we mush use cellWidth for unit here
            //every cell's width maybe changed,but we draw cell one by one
            //when the cell needn't to draw we just ignore it
            mRecycleRect.left = width * whichColumn + offsetX + startDrawX;
            mRecycleRect.right = mRecycleRect.left + mRecyclePoint.x;
            mRecycleRect.top = height * whichRow + offsetY + startDrawY;
            mRecycleRect.bottom = mRecycleRect.top + mRecyclePoint.y;

            BaseDrawStyle style = params.getDefaultDrawStyle();
            if (params.isContains(cellOrMenu.getStyleTag())) {
                style = params.getDrawStyle(cellOrMenu.getStyleTag());
            }
            //if the draw area cannot be seen,ignore it
            if (isDrawRectCanSeen(mRecycleRect) && style.isDraw()) {
                int textDrawX, textDrawY;
                textDrawX = mRecycleRect.left;
                textDrawY = mRecycleRect.centerY() + style.getTextSize() * 2 / 3;
                if (isMenu) {
                    this.drawMenu(cellOrMenu, style, mRecycleRect, textDrawX, textDrawY, paint, canvas);
                } else {
                    //draw cell
                    this.drawCell(cellOrMenu, style, mRecycleRect, textDrawX, textDrawY, paint, canvas);
                }
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
    protected void drawMenu(CellEntity menu, BaseDrawStyle drawStyle, Rect drawRect, float textDrawX, float textDrawY, Paint paint, Canvas canvas) {
        this.drawCell(menu, drawStyle, drawRect, textDrawX, textDrawY, paint, canvas);
    }

    /**
     * calculate cell real width and height,if the cell span other cell (left/right or top/bottom)
     *
     * @param outPoint          a object to save the result
     * @param cell              the cell to calculate its width and height for drawing
     * @param defaultCellWidth  the unit width of cell or menu
     * @param defaultCellHeight the unit height of cell or menu
     */
    private void computeCellWidthAndHeight(@NonNull Point outPoint, @NonNull CellEntity cell, int defaultCellWidth, int defaultCellHeight) {
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

    protected boolean isNeedToDraw() {
        return !(mTable == null || mCellParams == null);
    }

    /**
     * calculate the position of cell seen in the screen. try to skip the unseen cells
     *
     * @param outRect
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
    private void computeSkipUnseenCell(@NonNull Rect outRect, int unitWidth, int unitHeight, int startDrawX, int startDrawY, int maxRow, int maxColumn, int offsetWidth, int offsetHeight) {

        outRect.right = (Math.abs(offsetWidth) - startDrawX) / unitWidth;
        outRect.left = (Math.abs(offsetHeight) - startDrawY) / unitHeight;

        //plus 2 to make sure cells will draw in all canvas
        //if plus only 1 maybe after moving some place will show nothing.
        outRect.bottom = outRect.right + mViewParams.x / unitWidth + 2;
        outRect.top = outRect.left + mViewParams.y / unitHeight + 2;

        outRect.bottom = outRect.bottom > maxColumn ? maxColumn : outRect.bottom;
        outRect.top = outRect.top > maxRow ? maxRow : outRect.top;
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
            int maxCharLength = 0;
            if (textWidth > maxDrawWidth) {
                //estimate max char count can be show
                maxCharLength = (int) (drawText.length() * (maxDrawWidth / textWidth));
                //save 3 count for ellipsis
                if (maxCharLength > 3) {
                    maxCharLength -= 3;
                    float drawLength = paint.measureText(drawText, 0, maxCharLength);
                    canvas.drawText(drawText, 0, maxCharLength, drawX, drawY, paint);
                    canvas.drawText("...", drawX + drawLength, drawY, paint);
                    return;
                }
            } else {
                maxCharLength = drawText.length();
            }
            canvas.drawText(drawText, 0, maxCharLength, drawX, drawY, paint);
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

    private void setScrollXEnabled(boolean isScrollXEnabled, boolean isScrollYEnabled) {
        if (!mIsConfirmScrollDirection) {
            if (isScrollXEnabled) {
                mIsConfirmScrollDirection = true;
                mIsScrollX = true;
            } else if (isScrollYEnabled) {
                mIsConfirmScrollDirection = true;
                mIsScrollX = false;
            }
        }
    }

    @Override
    public boolean isCanMovedOnX(PointF moveDistancePointF, PointF newOffsetPointF) {
        //only can move in the positive axis.
        //when move to a big positive axis(for example from 0 to +int),
        //the offset distance will be negative.
        if (mViewParams == null) {
            return false;
        }
        //check if out canvas
        float outOfCanvas = mCanvasDrawWidth + 50 - mViewParams.x;
        if (outOfCanvas > 0) {
            if (Math.abs(newOffsetPointF.x) > outOfCanvas) {
                newOffsetPointF.x = outOfCanvas * (newOffsetPointF.x > 0 ? 1 : -1);
            }
        } else {
            newOffsetPointF.x = 0;
        }
        //here to process the situation of moving pass the original position
        if (newOffsetPointF.x > 0) {
            newOffsetPointF.x = 0;
        }

//        Log.i("draw", "move = " + moveDistancePointF.toString());
//        Log.i("draw", "offset = " + newOffsetPointF.toString());
        boolean isEnabled = newOffsetPointF.x <= 0 && Math.abs(moveDistancePointF.x) > Math.abs(moveDistancePointF.y);
        this.setScrollXEnabled(isEnabled, false);
        return mIsScrollX;
    }

    @Override
    public boolean isCanMovedOnY(PointF moveDistancePointF, PointF newOffsetPointF) {
        //only can move in the positive axis.
        if (mViewParams == null) {
            return false;
        }
        //check if out canvas
        float outOfCanvas = mCanvasDrawHeight + 50 - mViewParams.y;
        if (outOfCanvas > 0) {
            if (Math.abs(newOffsetPointF.y) > outOfCanvas) {
                newOffsetPointF.y = outOfCanvas * (newOffsetPointF.y > 0 ? 1 : -1);
            }
        } else {
            newOffsetPointF.y = 0;
        }
        //here to process the situation of moving pass the original position
        if (newOffsetPointF.y > 0) {
            newOffsetPointF.y = 0;
        }

        boolean isEnabled = newOffsetPointF.y <= 0 && Math.abs(moveDistancePointF.x) < Math.abs(moveDistancePointF.y);
        this.setScrollXEnabled(false, isEnabled);
        return !mIsScrollX;
    }

    @Override
    public void onMove(int suggestEventAction) {
        mDrawView.invalidate();
    }

    @Override
    public void onMoveFail(int suggestEventAction) {
    }

    @Override
    public void startMove(float mouseDownX, float mouseDownY) {
        int direction = this.getMoveDirection(mouseDownX, mouseDownY);
        if (direction == Constant.MOVE_DIRECTION_HORIZONTAL) {
            mIsScrollInHorizontal = true;
            mIsScrollInVertical = false;
        } else if (direction == Constant.MOVE_DIRECTION_VERTICAL) {
            mIsScrollInVertical = true;
            mIsScrollInHorizontal = false;
        } else if (direction == Constant.MOVE_DIRECTION_BOTH) {
            mIsScrollInVertical = true;
            mIsScrollInHorizontal = true;
        } else {
            mIsScrollInHorizontal = false;
            mIsScrollInVertical = false;
        }
    }

    @Override
    public void finishedMove(boolean hasBeenMoved) {
        mIsScrollInVertical = false;
        mIsScrollInHorizontal = false;
        mIsConfirmScrollDirection = false;
        mIsScrollX = false;
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
        if (isNeedToDraw() && mCellClickListener != null) {
            //TODO:菜单单击无效,不会进行检测与响应...
            int y = mClickHelper.computeClickYFromFirstLine(event.getX(), mMsActionHelper.getDrawOffsetX(), mMenuParams.getDrawWidth(), mTable.getCellColumnCount());
            int x = mClickHelper.computeClickXFromFirstLine(event.getY(), mMsActionHelper.getDrawOffsetY(), mMenuParams.getDrawHeight(), mTable.getCellRowCount());
            Log.i("tag", "result x/y=" + x + "/" + y);
            mCellClickListener.onCellClick(mTable.getCell(x, y), x, y);
        }
    }

    @Override
    public void onSingleClickByDistance(MotionEvent event) {

    }

    @Override
    public void onDoubleClickByTime() {

    }

    /**
     * draw one cell.
     *
     * @param cell      cell for draw
     * @param drawStyle the drawStyle for cell,if the cell doesn't request a special drawStyle,the default drawStyle will be offered
     * @param drawRect  the rect for cells drawing,cell should draw in this rect
     * @param textDrawX the x axis for text drawing,default in the center x of rect.
     * @param textDrawY the y axis for text drawing,default under the center y of rect,
     * @param paint
     * @param canvas
     */
    protected abstract void drawCell(@NonNull CellEntity cell, @NonNull BaseDrawStyle drawStyle, Rect drawRect, float textDrawX, float textDrawY, Paint paint, Canvas canvas);

    /**
     * draw mask.it is a choice.you can do nothing here.
     *
     * @param params     offer all the params for drawing mask.
     * @param rect       the area for drawing mask.
     * @param viewParams the width and height of this view.
     * @param paint
     * @param canvas
     */
    protected abstract void drawMask(@NonNull GlobalParams params, @NonNull Rect rect, @NonNull Point viewParams, Paint paint, Canvas canvas);

    /**
     * this method will be called before moving.and the start moving position is given.
     * return the direction which is allowed to move.<br>
     * 此方法会在开始移动之前进行回调.返回值决定了可移动的方向.
     *
     * @param mouseDown
     * @param mouseUp
     * @return
     */
    @Constant.MoveDirection
    protected abstract int getMoveDirection(float mouseDown, float mouseUp);
}
