package com.showcast.hvscroll.entity;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;

import com.showcast.hvscroll.params.Constant;

/**
 * Created by taro on 16/8/17.
 */
public class TableEntity {
    protected int mRowCount = 0;
    protected int mColumnCount = 0;
    protected int mMenuCountInRow = 0;
    protected int mMenuCountInColumn = 0;
    protected int mEstimatedRowCount = 20;
    protected int mEstimatedColumnCount = 20;
    protected SparseArrayCompat<CellEntity> mColumnMenuList;
    protected SparseArrayCompat<CellEntity> mRowMenuList;
    protected SparseArrayCompat<SparseArrayCompat<CellEntity>> mCellMap;

    public static final TableEntity getExampleTable() {
        TableEntity table = new TableEntity(35, 11);

//        table.addCellAutoSpan(new CellEntity(0, 0, "text"), new Point(8, 5), new Point(11, 7));

        for (int i = 0; i < 11; i++) {
            table.addMenu(table.newRowMenu(i, "title-" + i), Constant.MENU_ROW);
        }
        for (int i = 0; i < 35; i++) {
            table.addMenu(table.newColumnMenu(i, "title-" + i), Constant.MENU_COLUMN);
        }

        for (int i = 0; i < 35; i++) {
            for (int j = 0; j < 11; j++) {
                table.addCellWithoutSpan(new CellEntity(i, j, "cell " + i + "-" + j));
            }
        }

//        Log.i("count", "row/count = " + table.getRowCount() + "/" + table.getColumnCount());
//        table.autoUpdateRowColumnCount();
//        Log.i("count", "row/count = " + table.getRowCount() + "/" + table.getColumnCount());
//        for (int i = 0; i < 35; i++) {
//            table.removeCell(i, 10);
//        }
//        Log.i("count", "-------------------------------------\n");
//        Log.i("count", "before update\nrow/count = " + table.getRowCount() + "/" + table.getColumnCount());
//        table.autoUpdateRowColumnCount();
//        Log.i("count", "after update\nrow/count = " + table.getRowCount() + "/" + table.getColumnCount());
//        for (int i = 0; i < 10; i++) {
//            table.removeCell(34, i);
//        }
//        Log.i("count", "-------------------------------------\n");
//        Log.i("count", "before update\nrow/count = " + table.getRowCount() + "/" + table.getColumnCount());
//        table.autoUpdateRowColumnCount();
//        Log.i("count", "after update\nrow/count = " + table.getRowCount() + "/" + table.getColumnCount());
        return table;
    }

    public TableEntity() {
        this(20, 20);
    }

    public TableEntity(int estimatedRowCount, int estimatedColumnCount) {
        mEstimatedRowCount = estimatedRowCount;
        mEstimatedColumnCount = estimatedColumnCount;
        mCellMap = new SparseArrayCompat<>(mEstimatedRowCount);
        mRowMenuList = new SparseArrayCompat<>(mEstimatedColumnCount);
        mColumnMenuList = new SparseArrayCompat<>(mEstimatedRowCount);
    }

    public void precreateMenu(@Constant.MenuType int which, int menulCount) {
        switch (which) {
            case Constant.MENU_ROW:
                this.checkIfRowMenuCreate(menulCount);
                break;
            case Constant.MENU_COLUMN:
                this.checkIfColumnMenuCreate(menulCount);
                break;
        }
    }

    public CellEntity newColumnMenu(int row, String text) {
        return new CellEntity(row, Constant.FIXED_MENU_INDEX_COLUMN, text);
    }

    public CellEntity newRowMenu(int column, String text) {
        return new CellEntity(Constant.FIXED_MENU_INDEX_ROW, column, text);
    }

    /**
     * @param menu
     * @param whichMenu
     */
    public void addMenu(@NonNull CellEntity menu, @Constant.MenuType int whichMenu) {
        switch (whichMenu) {
            case Constant.MENU_ROW:
                mRowMenuList.put(menu.getColumnIndex(), menu);
                this.updateMenuCount(menu.getColumnIndex() + 1, 0);
                break;
            case Constant.MENU_COLUMN:
                mColumnMenuList.put(menu.getRowIndex(), menu);
                this.updateMenuCount(0, menu.getRowIndex() + 1);
                break;
        }
    }

    /**
     * put new menu into table.a row menu will use column index while a column menu will use row index.<br/>
     * 添加新的菜单,行菜单将使用列索引(行索引是固定的),列菜单使用行索引(列索引是固定的).
     *
     * @param menu
     * @param rowIndex
     * @param columnIndex
     * @param whichMenu
     */
    public void addMenu(@Nullable CellEntity menu, int rowIndex, int columnIndex, @Constant.MenuType int whichMenu) {
        switch (whichMenu) {
            case Constant.MENU_ROW:
                mRowMenuList.put(columnIndex, menu);
                this.updateMenuCount(0, columnIndex + 1);
                break;
            case Constant.MENU_COLUMN:
                mColumnMenuList.put(rowIndex, menu);
                this.updateMenuCount(rowIndex + 1, 0);
                break;
        }
    }

    public void clearMenu(@Constant.MenuType int which) {
        switch (which) {
            case Constant.MENU_ROW:
                mRowMenuList.clear();
                break;
            case Constant.MENU_COLUMN:
                mColumnMenuList.clear();
                break;
        }
    }

    /**
     * put new cell into talbe without checking out any span.and the span count of cell will be changed to default value.<br/>
     * 添加新的单元格到表格中,忽略任何可能的跨越行列.同时单元格中的跨越行列值会被替换成默认值.
     *
     * @param cell
     */
    public void addCellWithoutSpan(@NonNull CellEntity cell) {
        int row = cell.getRowIndex();
        int column = cell.getColumnIndex();
        //update cell span count
        this.updateCellSpanCount(cell, 0, 0);
        this.putCell(row, column, cell);
        //must save row and column max length
        this.updateRowColumnCount(row + 1, column + 1);
    }


    /**
     * put new cell into table.this method is the only method you can put a cell which span both row and column.<br/>
     * 添加新单元格,此方法是唯一一个方法可以添加行列都跨越的单元格.
     *
     * @param cell
     * @param from the start position of cell.index in cell will be changed to the index of this position.<br/>
     *             单元格开始的位置,单元格中的index将会被替换为此位置的index,作为起始的位置.
     * @param to   the end position of cell.<br/>
     *             单元格结束的位置(跨行列后)
     */
    public void addCellAutoSpan(@NonNull CellEntity cell, @NonNull Point
            from, @NonNull Point to) {
        boolean isFromFirst = from.x <= to.x && from.y <= to.y;
        int rowCount, columnCount;
        Point startP, endP;
        if (isFromFirst) {
            startP = from;
            endP = to;
        } else {
            startP = to;
            endP = from;
        }
        rowCount = endP.x - startP.x;
        columnCount = endP.y - startP.y;
        //update cell span count
        this.updateCellSpanCount(cell, rowCount, columnCount);
        //update the cell index
        cell.setRowAndColumnIndex(startP.x, startP.y);
        for (int i = 0; i <= rowCount; i++) {
            for (int j = 0; j <= columnCount; j++) {
                this.putCell(startP.x + i, startP.y + j, cell);
            }
        }
        //must save row and column max length
        this.updateRowColumnCount(endP.x + 1, endP.y + 1);
    }

    /**
     * put new cell into table.you can point out the cell span from where(from) to where(to).
     * attention,the cell will be put into the position where row and column index equal to the index in cell.
     * (params from and to just give the span count.)<br/>
     * 添加新单元格,可以通过设置from/to来确定需要合并的单元格数量.from/to参数与实际单元格存放的位置没有关系.
     *
     * @param cell
     * @param from
     * @param to
     * @param isRow
     */
    public void addCellAutoSpan(@NonNull CellEntity cell, int from, int to, boolean isRow) {
        int count = from < to ? to - from : from - to;
        this.addCellAutoSpan(cell, count + 1, isRow);
    }

    /**
     * put new cell into table with span count and indicating on row or column.when you use this method,
     * make sure your cell just span in one direction.<br/>
     * 根据给定的跨越行/列数及跨越方向添加单元格.确定该单元格只需要跨越一行/列的情况下再使用此方法.
     *
     * @param cell
     * @param spanCount the span count of row or column.
     * @param isRow     true when span on row, false when span on column.<br/>
     *                  在行方向上跨越时为true,否则为false
     */
    public void addCellAutoSpan(@NonNull CellEntity cell, int spanCount, boolean isRow) {
        if (spanCount < 0) {
            throw new IllegalArgumentException("span count can not be negative integer");
        }
        int startRow = cell.getRowIndex();
        int startColumn = cell.getColumnIndex();
        int rowCount = startRow + 1;
        int columnCount = startColumn + 1;
        if (isRow) {
            this.updateCellSpanCount(cell, spanCount - 1, 0);
            for (int i = 0; i < spanCount; i++) {
                this.putCell(startRow + i, startColumn, cell);
            }
            rowCount += spanCount - 1;
        } else {
            this.updateCellSpanCount(cell, 0, spanCount - 1);
            for (int i = 0; i < spanCount; i++) {
                this.putCell(startRow, startColumn + i, cell);
            }
            columnCount += spanCount - 1;
        }
        //must save row and column max length
        this.updateRowColumnCount(rowCount, columnCount);
    }

    /**
     * put new cell into table and auto calculate the cell span.you needn't to anything else.<br/>
     * 自动计算跨行列并添加单元格到表格中.
     *
     * @param cell the cell to add.the span count should be set before,
     *             or not set if the cell doesn't span any row or column.<br/>
     *             若单元格需要跨越其它行列,必须先设置.否则则不需要.
     */
    public void addCellAutoSpan(@NonNull CellEntity cell) {
        int spanRow = cell.getSpanRowCount();
        int spanColumn = cell.getSpanColumnCount();
        //if the cell not span with any other cells,just put it only
        if (spanRow == Constant.DEFAULT_SPAN_COUNT && spanColumn == Constant.DEFAULT_SPAN_COUNT) {
            this.addCellWithoutSpan(cell);
        } else if (spanRow == Constant.DEFAULT_SPAN_COUNT) {
            //if the cell span with row
            this.addCellAutoSpan(cell, spanColumn, false);
        } else if (spanColumn == Constant.DEFAULT_SPAN_COUNT) {
            //if the cell span with column
            this.addCellAutoSpan(cell, spanRow, true);
        } else {
            //if the cell span with row and column
            Point from, to;
            from = new Point(cell.getRowIndex(), cell.getColumnIndex());
            to = new Point(from.x + cell.getSpanRowCount() - 1, from.y + cell.getSpanColumnCount() - 1);
            //already save row and column max length in the next method
            this.addCellAutoSpan(cell, from, to);
        }
    }

    /**
     * remove cell.in ordinary cases,this method is the first choice.<br/>
     * 移除单元格.正常情况下,首选此方法.
     *
     * @param cell
     * @return
     */
    public CellEntity removeCell(CellEntity cell) {
        if (cell == null) {
            return null;
        } else {
            this.removeCell(cell.getRowIndex(), cell.getColumnIndex(), cell);
            return cell;
        }
    }

    /**
     * remove cell.<br/>
     * 移除单元格.
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public CellEntity removeCell(int rowIndex, int columnIndex) {
        CellEntity oldCell = this.getCell(rowIndex, columnIndex);
        if (oldCell != null) {
            this.removeCell(rowIndex, columnIndex, oldCell);
        }
        return oldCell;
    }

    /**
     * remove cell. the row index and column index should be the same as index in cell.<br/>
     * 移除单元格,rowIndex与columnIndex必须与cell中的index相同,此处是一个保证措施.
     *
     * @param cell        cell to remove.<br/>
     *                    移除的cell,可以为null,null时无效果.
     * @param rowIndex
     * @param columnIndex
     * @return
     * @throws IllegalArgumentException when row index and column index are not equal to the index in cell.
     */
    public CellEntity removeCell(@Nullable CellEntity cell, int rowIndex, int columnIndex) throws IllegalArgumentException {
        if (cell == null) {
            return null;
        } else {
            if (cell.getRowIndex() != rowIndex || cell.getColumnIndex() != columnIndex) {
                throw new IllegalArgumentException("the rowIndex or columnIndex inside cell are different from the values offered." +
                        "\nif you want to remove a cell ignore its inside params,please use removeCell(rowIndex,columnIndex)");
            } else {
                return this.removeCell(cell);
            }
        }
    }

    public void clearTable() {
        mCellMap.clear();
    }

    /**
     * get the cell row count in table.exclude menu.<br/>
     * 返回单元格中的总行数,不包含菜单.
     *
     * @return
     */
    public int getRowCount() {
        return mRowCount;
    }

    /**
     * get the cell column count int table.exclude menu.<br/>
     * 返回单元格中的总列数,不包含菜单.
     *
     * @return
     */
    public int getColumnCount() {
        return mColumnCount;
    }

    /**
     * get count of row/column menu.<br/>
     * 获取行/列菜单中的数量. 行菜单中只有列数,列菜单中只有行数.
     *
     * @param whichMenu
     * @return
     */
    public int getMenuCount(@Constant.MenuType int whichMenu) {
        switch (whichMenu) {
            case Constant.MENU_ROW:
                return mRowMenuList == null ? 0 : mMenuCountInRow;
            case Constant.MENU_COLUMN:
                return mColumnMenuList == null ? 0 : mMenuCountInColumn;
            default:
                return 0;
        }
    }

    /**
     * get row menu,maybe return null if the menu doesn't exist.<br/>
     * 获取行的菜单,可能返回null(菜单本质也是一个单元格.)
     *
     * @param menuIndex
     * @return
     */
    @Nullable
    public CellEntity getRowMenu(int menuIndex) {
        if (mRowMenuList == null || menuIndex < 0 || menuIndex >= mRowMenuList.size()) {
            return null;
        } else {
            return mRowMenuList.get(menuIndex);
        }
    }

    /**
     * get column menu,maybe return null if the menu doesn't exist.<br/>
     * 获取列的菜单,可能返回null(菜单本质也一个单元格)
     *
     * @param menuIndex
     * @return
     */
    @Nullable
    public CellEntity getColumnMenu(int menuIndex) {
        if (mColumnMenuList == null || menuIndex < 0 || menuIndex >= mColumnMenuList.size()) {
            return null;
        } else {
            return mColumnMenuList.get(menuIndex);
        }
    }

    /**
     * get a cell from the position.remember,the index in the cell maybe different from given row index and row index.
     * And the row index and column index are the true real position.<br/>
     * 获取指定位置的cell,cell中的index可能与获取的index不同,但获取的index才是正确的index.(一般情况下不会有这种问题.但是存在这种可能.
     * 在调添加cell的方法某些操作不正确时可能造成这种情况)
     *
     * @param rowIndex
     * @param columnIndex
     * @return null will be return if nothing in the position.<br/>
     * 该位置无任何单元格时,返回null
     */
    @Nullable
    public CellEntity getCell(int rowIndex, int columnIndex) {
        SparseArrayCompat<CellEntity> row = this.getRow(rowIndex);
        if (row != null) {
            return row.get(columnIndex);
        } else {
            return null;
        }
    }

    /**
     * update count of row menu and column menu.true if one of them changed or false nothing changed.<br/>
     * 更新菜单的数量.
     *
     * @return
     */
    public boolean autoUpdateMenuCount() {
        boolean isChanged = false;
        int newRowCount = -1;
        int newColumnCount = -1;
        CellEntity menu = null;
        for (int i = mRowCount - 1; i >= 0; i--) {
            menu = mRowMenuList.get(i);
            if (menu != null) {
                newRowCount = i;
                break;
            }
        }
        for (int i = mColumnCount - 1; i >= 0; i--) {
            menu = mColumnMenuList.get(i);
            if (menu != null) {
                newColumnCount = i;
            }
        }
        isChanged = newRowCount != mMenuCountInRow || newColumnCount != mMenuCountInColumn;
        mMenuCountInRow = newRowCount + 1;
        mMenuCountInColumn = newColumnCount + 1;
        return isChanged;
    }

    /**
     * update row and column count.if the table changed,maybe need to check the row and column count.
     * but try to not use this method frequently.we have to traverse the table.<br/>
     * 更新行列总数.当table数据变动比较大时应该考虑是否需要更新行列总数.但如果确定table的行列值不变的情况下或者影响不大时,
     * 尽量不要频繁使用此方法,此方法需要遍历表格(尽管往往不需要全部遍历.)
     *
     * @return
     */
    public boolean autoUpdateRowColumnCount() {
        boolean isChanged = false;
        int newRowCount = -1;
        int newColumnCount = -1;
        SparseArrayCompat<CellEntity> row = null;
        CellEntity cell = null;
        //traverse from back to beginning.
        //从后往前遍历
        for (int i = mRowCount - 1; i >= 0; i--) {
            row = this.getRow(i);
            if (row == null) {
                continue;
            }
            if (newRowCount != mRowCount - 1) {
                if (row.size() > 0) {
                    newRowCount = i > newRowCount ? i : newRowCount;
                } else {
                    //if the row no data,ignore it.
                    //<=0说明该行不存在数据,跳过.
                    continue;
                }
            }
            //traverse all the row which contains data.record the max column count.
            //遍历有数据的每一行,取到最大列值.从后面开始遍历.
            if (newColumnCount != mColumnCount - 1) {
                for (int j = mColumnCount - 1; j >= 0; j--) {
                    cell = row.get(j);
                    if (cell != null) {
                        newColumnCount = newColumnCount < j ? j : newColumnCount;
                        break;
                    }
                }
            }
        }
        isChanged = mRowCount != newRowCount || mColumnCount != newColumnCount;
        mRowCount = newRowCount + 1;
        mColumnCount = newColumnCount + 1;
        return isChanged;
    }

    private void checkIfRowMenuCreate(int initialCount) {
        if (mRowMenuList == null) {
            mRowMenuList = new SparseArrayCompat<>(initialCount);
        }
    }

    /**
     * check if column menu has been created.<br/>
     * 检测列菜单是否已经被创建了.默认未创建.
     *
     * @param initialCount the count of menu.it is just a rough number for creating a container.
     *                     you can also offer a exact number.<br/>
     *                     此处只是一个初始值的数量,容器可以进行扩展.但是最好还是提供一个精确的数量(或稍大于预期的数量).
     */
    private void checkIfColumnMenuCreate(int initialCount) {
        if (mColumnMenuList == null) {
            mColumnMenuList = new SparseArrayCompat<>(initialCount);
        }
    }

    /**
     * get row from table.never return null.<br/>
     * 获取表格中的某行,永远也不会返回null.
     *
     * @param rowIndex
     * @return
     */
    @Nullable
    private SparseArrayCompat<CellEntity> getRow(int rowIndex) {
        return mCellMap.get(rowIndex);
    }

    /**
     * return the row in the rowIndex.the row will be created if the row is not exist.<br/>
     * 返回指定位置的行,若该位置的行不存在,则创建一个新的行.
     *
     * @param rowIndex
     * @return
     */
    @NonNull
    private SparseArrayCompat<CellEntity> getRowAndCreateIfNotExist(int rowIndex) {
        SparseArrayCompat<CellEntity> row = mCellMap.get(rowIndex);
        if (row == null) {
            row = new SparseArrayCompat<>(mEstimatedColumnCount);
            mCellMap.put(rowIndex, row);
        }
        return row;
    }

    /**
     * remove the cell where the position equals to row index and column index.
     * the index in the cell will not be used.<br/>
     * 从表格中移除给定位置的单元格,单元格中的行列值不会使用到.
     *
     * @param rowIndex
     * @param columnIndex
     * @param cell
     */
    private void removeCell(int rowIndex, int columnIndex, @NonNull CellEntity cell) {
        int spanRow = cell.getSpanRowCount();
        int spanColumn = cell.getSpanColumnCount();
        SparseArrayCompat<CellEntity> row = this.getRow(rowIndex);
        //if the cell span with no one cell,just remove it only
        if (spanRow == Constant.DEFAULT_SPAN_COUNT && spanColumn == Constant.DEFAULT_SPAN_COUNT) {
            if (row != null) {
                row.remove(columnIndex);
            }
        } else if (spanRow == Constant.DEFAULT_SPAN_COUNT) {
            //if the cell span with column only
            if (row != null) {
                for (int i = 0; i < spanColumn; i++) {
                    row.remove(i + columnIndex);
                }
            }
        } else if (spanColumn == Constant.DEFAULT_SPAN_COUNT) {
            //if the cell span with row only
            for (int i = 0; i < spanRow; i++) {
                row = this.getRow(i + rowIndex);
                if (row != null) {
                    row.remove(columnIndex);
                }
            }
        } else {
            //if the cell span with row and column,remove all
            for (int i = 0; i < spanRow; i++) {
                row = this.getRow(i + rowIndex);
                if (row != null) {
                    for (int j = 0; j < spanColumn; j++) {
                        row.remove(j + columnIndex);
                    }
                }
            }
        }
    }

    /**
     * put new cell.the row index and column index in the new cell will not be used.
     * the new cell will be put into the position where given by row index and column index.<br/>
     * 添加新单元格.单元格本身的行列索引不会被使用,此方法会将单元格添加到给定的行列索引中.
     *
     * @param rowIndex    row index of the new cell.<br/>
     *                    新单元格的行索引.是索引!
     * @param columnIndex column index of the new cell.<br/>
     *                    新单元格的列索引.
     * @param cell        the cell to put into table.it can be null.<br/>
     *                    新添加的cell,可以是null
     * @return return the old cell.maybe null;<br/>
     * 返回旧的单元格对象,可能是null
     */

    private CellEntity putCell(int rowIndex, int columnIndex, @Nullable CellEntity cell) {
        SparseArrayCompat<CellEntity> row = this.getRow(rowIndex);
        CellEntity oldCell = null;
        if (cell != null && row == null) {
            row = this.getRowAndCreateIfNotExist(rowIndex);
            row.put(columnIndex, cell);
        } else if (cell != null) {
            oldCell = row.get(columnIndex);
            row.put(columnIndex, cell);
        } else if (row != null) {
            oldCell = row.get(columnIndex);
        }
        //other situations: row null so the old cell is null too;
        //or cell is null so we needn't put the cell into table.
        return oldCell;
    }

    /**
     * update table row count and column count.<br/>
     * 更新表格的行列总数.由单元格所在的位置决定.
     *
     * @param rowCount
     * @param columnCount
     */
    private void updateRowColumnCount(int rowCount, int columnCount) {
        mRowCount = mRowCount < rowCount ? rowCount : mRowCount;
        mColumnCount = mColumnCount < columnCount ? columnCount : mColumnCount;
    }

    /**
     * update menu count of row menu or column menu.like the table count.<br/>
     * 更新行列菜单的数量.
     *
     * @param rowMenuCount
     * @param columnMenuCount
     */
    private void updateMenuCount(int rowMenuCount, int columnMenuCount) {
        mMenuCountInRow = mMenuCountInRow < rowMenuCount ? rowMenuCount : mMenuCountInRow;
        mMenuCountInColumn = mMenuCountInColumn < columnMenuCount ? columnMenuCount : mMenuCountInColumn;
    }

    /**
     * update cell span count when adding new cells.<br/>
     * 更新单元格跨越的行列数
     *
     * @param cell       the new cell to add.
     * @param spanRow    the row count of cell spans(do not include itself.)<br/>
     *                   单元格跨越的行数,不包括本身所在的行.如该单元格一共跨了2行,则此值应该为1.
     * @param spanColumn the column count of cell spans(do not include itself.)<br/>
     *                   单元格跨越的列数,不包括本身所有的列,如该单元格一共跨了2列,则此值应该为1.
     */
    private void updateCellSpanCount(@NonNull CellEntity cell, int spanRow, int spanColumn) {
        cell.setSpanRowCount(spanRow + Constant.DEFAULT_SPAN_COUNT);
        cell.setSpanColumnCount(spanColumn + Constant.DEFAULT_SPAN_COUNT);
    }
}
