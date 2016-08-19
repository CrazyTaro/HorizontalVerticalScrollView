package com.showcast.hvscroll.entity;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;

/**
 * Created by taro on 16/8/17.
 */
public class AbsTableEntity {
    /**
     * default row index of row menu
     */
    public static final int FIXED_MENU_INDEX_ROW = -1;
    /**
     * default column index of column menu
     */
    public static final int FIXED_MENU_INDEX_COLUMN = -1;

    public static final int MENU_ROW = 0;
    public static final int MENU_COLUMN = 1;

    protected int mRowCount = 0;
    protected int mColumnCount = 0;
    protected int mEstimatedRowCount = 20;
    protected int mEstimatedColumnCount = 20;
    protected SparseArrayCompat<AbsCellEntity> mColumnMenuList;
    protected SparseArrayCompat<AbsCellEntity> mRowMenuList;
    protected SparseArrayCompat<SparseArrayCompat<AbsCellEntity>> mCellMap;

    public static final AbsTableEntity getExampleTable() {
        AbsTableEntity table = new AbsTableEntity(35, 11);

        for (int i = 0; i < 11; i++) {
            AbsCellEntity menu = table.newRowMenu(i, "title-" + (i << i));
            table.addMenu(menu, MENU_ROW);
        }
        table.addMenu(table.newColumnMenu(0, "title-1"), MENU_COLUMN);

        for (int i = 0; i < 35; i++) {
            if ((i & 1) == 1) {
                table.addCellAutoSpan(new AbsCellEntity(i, 0, "cell"));
                for (int k = 1; k < 6; k++) {
                    AbsCellEntity newCell = new AbsCellEntity(i, k * 2 - 1, "long cell");
                    table.addCellAutoSpan(newCell, 2, false);
                }
            } else {
                for (int j = 0; j < 11; j++) {
                    AbsCellEntity newCell = new AbsCellEntity(i, j, "cell");
                    table.addCellWithoutSpan(newCell);
                }
            }
        }

        AbsCellEntity cell = null;
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                cell = table.getCell(i, j);
                if (cell != null) {
                    Log.i("cell", "i=" + i + "/j=" + j + cell.toString());
                }
            }
            Log.i("row", i + "-row --------------------------------");
        }
        return table;
    }

    public AbsTableEntity() {
        this(20, 20);
    }

    public AbsTableEntity(int estimatedRowCount, int estimatedColumnCount) {
        mEstimatedRowCount = estimatedRowCount;
        mEstimatedColumnCount = estimatedColumnCount;
        mCellMap = new SparseArrayCompat<>(mEstimatedRowCount);
        mRowMenuList = new SparseArrayCompat<>(mEstimatedColumnCount);
        mColumnMenuList = new SparseArrayCompat<>(mEstimatedRowCount);
    }

    public void precreateMenu(int which, int menulCount) {
        switch (which) {
            case MENU_ROW:
                this.checkIfRowMenuCreate(menulCount);
                break;
            case MENU_COLUMN:
                this.checkIfColumnMenuCreate(menulCount);
                break;
        }
    }

    public AbsCellEntity newColumnMenu(int row, String text) {
        return new AbsCellEntity(row, FIXED_MENU_INDEX_COLUMN, text);
    }

    public AbsCellEntity newRowMenu(int column, String text) {
        return new AbsCellEntity(FIXED_MENU_INDEX_ROW, column, text);
    }

    public void addMenu(@NonNull AbsCellEntity menu, int whichMenu) {
        switch (whichMenu) {
            case MENU_ROW:
                mRowMenuList.put(menu.getColumnIndex(), menu);
                break;
            case MENU_COLUMN:
                mColumnMenuList.put(menu.getRowIndex(), menu);
                break;
        }
    }

    public void addMenu(AbsCellEntity menu, int rowIndex, int columnIndex, int whichMenu) {
        switch (whichMenu) {
            case MENU_ROW:
                mRowMenuList.put(columnIndex, menu);
                break;
            case MENU_COLUMN:
                mColumnMenuList.put(rowIndex, menu);
                break;
        }
    }

    public void clearMenu(int which) {
        switch (which) {
            case MENU_ROW:
                mRowMenuList.clear();
                break;
            case MENU_COLUMN:
                mColumnMenuList.clear();
                break;
        }
    }

    public void addCellWithoutSpan(@NonNull AbsCellEntity cell) {
        int row = cell.getRowIndex();
        int column = cell.getColumnIndex();
        //update cell span count
        this.updateCellSpanCount(cell, 0, 0);
        this.putCell(row, column, cell);
        //must save row and column max length
        this.updateRowColumnCount(row + 1, column + 1);
    }


    public void addCellAutoSpan(@NonNull AbsCellEntity cell, @NonNull Point from, @NonNull Point to) {
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
        rowCount = startP.x - endP.x;
        columnCount = startP.y - endP.y;
        //update cell span count
        this.updateCellSpanCount(cell, rowCount, columnCount);
        for (int i = 0; i <= rowCount; i++) {
            for (int j = 0; j <= columnCount; j++) {
                this.putCell(startP.x + i, startP.y + j, cell);
            }
        }
        //must save row and column max length
        this.updateRowColumnCount(endP.x + 1, endP.y + 1);
    }

    public void addCellAutoSpan(@NonNull AbsCellEntity cell, int from, int to, boolean isRow) {
        int count = from < to ? to - from : from - to;
        int startRow = cell.getRowIndex();
        int startColumn = cell.getColumnIndex();
        int rowCount = startRow + 1;
        int columnCount = startColumn + 1;
        if (isRow) {
            //update cell span count
            this.updateCellSpanCount(cell, rowCount, 0);
            for (int i = 0; i <= count; i++) {
                this.putCell(startRow + i, startColumn, cell);
            }
            rowCount += count;
        } else {
            this.updateCellSpanCount(cell, 0, columnCount);
            for (int i = 0; i <= count; i++) {
                this.putCell(startRow, startColumn + i, cell);
            }
            columnCount += count;
        }
        //must save row and column max length
        this.updateRowColumnCount(rowCount, columnCount);
    }

    public void addCellAutoSpan(@NonNull AbsCellEntity cell, int spanCount, boolean isRow) {
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

    public void addCellAutoSpan(@NonNull AbsCellEntity cell) {
        int spanRow = cell.getSpanRowCount();
        int spanColumn = cell.getSpanColumnCount();
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        //if the cell not span with any other cells,just put it only
        if (spanRow == AbsCellEntity.DEFAULT_SPAN_COUNT && spanColumn == AbsCellEntity.DEFAULT_SPAN_COUNT) {
            this.addCellWithoutSpan(cell);
        } else if (spanRow == AbsCellEntity.DEFAULT_SPAN_COUNT) {
            //if the cell span with row
            this.addCellAutoSpan(cell, columnIndex, columnIndex + spanColumn, false);
        } else if (spanColumn == AbsCellEntity.DEFAULT_SPAN_COUNT) {
            //if the cell span with column
            this.addCellAutoSpan(cell, rowIndex, rowIndex + spanRow, true);
        } else {
            //if the cell span with row and column
            Point from, to;
            from = new Point(cell.getRowIndex(), cell.getColumnIndex());
            to = new Point(from.x + cell.getSpanRowCount() - 1, from.y + cell.getSpanColumnCount() - 1);
            //already save row and column max length in the next method
            this.addCellAutoSpan(cell, from, to);
        }
    }

    public AbsCellEntity removeCell(AbsCellEntity cell) {
        if (cell == null) {
            return null;
        } else {
            this.removeCell(cell.getRowIndex(), cell.getColumnIndex(), cell);
            return cell;
        }
    }

    public AbsCellEntity removeCell(int rowIndex, int columnIndex) {
        AbsCellEntity oldCell = this.getRow(rowIndex).get(columnIndex);
        if (oldCell != null) {
            this.removeCell(rowIndex, columnIndex, oldCell);
        }
        return oldCell;
    }

    public AbsCellEntity removeCell(AbsCellEntity cell, int rowIndex, int columnIndex) {
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

    public int getRowCount() {
        return mRowCount;
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public int getMenuCount(int whichMenu) {
        switch (whichMenu) {
            case MENU_ROW:
                return mRowMenuList == null ? 0 : mRowMenuList.size();
            case MENU_COLUMN:
                return mColumnMenuList == null ? 0 : mColumnMenuList.size();
            default:
                return 0;
        }
    }

    public AbsCellEntity getRowMenu(int menuIndex) {
        if (mRowMenuList == null || menuIndex < 0 || menuIndex >= mRowMenuList.size()) {
            return null;
        } else {
            return mRowMenuList.get(menuIndex);
        }
    }

    public AbsCellEntity getColumnMenu(int menuIndex) {
        if (mColumnMenuList == null || menuIndex < 0 || menuIndex >= mColumnMenuList.size()) {
            return null;
        } else {
            return mColumnMenuList.get(menuIndex);
        }
    }

    public AbsCellEntity getCell(int rowIndex, int columnIndex) {
        SparseArrayCompat<AbsCellEntity> row = this.getRow(rowIndex);
        if (row != null) {
            return row.get(columnIndex);
        } else {
            return null;
        }
    }

    private void checkIfRowMenuCreate(int initialCount) {
        if (mRowMenuList == null) {
            mRowMenuList = new SparseArrayCompat<>(initialCount);
        }
    }

    private void checkIfColumnMenuCreate(int initialCount) {
        if (mColumnMenuList == null) {
            mColumnMenuList = new SparseArrayCompat<>(initialCount);
        }
    }

    private SparseArrayCompat<AbsCellEntity> getRow(int rowIndex) {
        SparseArrayCompat<AbsCellEntity> row = mCellMap.get(rowIndex);
        if (row == null) {
            row = new SparseArrayCompat<>(mEstimatedColumnCount);
            mCellMap.put(rowIndex, row);
        }
        return row;
    }

    private void removeCell(int rowIndex, int columnIndex, @NonNull AbsCellEntity cell) {
        int spanRow = cell.getSpanRowCount();
        int spanColumn = cell.getSpanColumnCount();
        //if the cell span with no one cell,just remove it only
        if (spanRow == AbsCellEntity.DEFAULT_SPAN_COUNT && spanColumn == AbsCellEntity.DEFAULT_SPAN_COUNT) {
            this.getRow(rowIndex).remove(columnIndex);
        } else if (spanRow == AbsCellEntity.DEFAULT_SPAN_COUNT) {
            //if the cell span with column only
            SparseArrayCompat<AbsCellEntity> row = this.getRow(rowIndex);
            for (int i = 0; i < spanColumn; i++) {
                row.remove(i + columnIndex);
            }
        } else if (spanColumn == AbsCellEntity.DEFAULT_SPAN_COUNT) {
            //if the cell span with row only
            SparseArrayCompat<AbsCellEntity> row = null;
            for (int i = 0; i < spanRow; i++) {
                row = this.getRow(i + rowIndex);
                row.remove(columnIndex);
            }
        } else {
            //if the cell span with row and column,remove all
            SparseArrayCompat<AbsCellEntity> row = null;
            for (int i = 0; i < spanRow; i++) {
                row = this.getRow(i + rowIndex);
                for (int j = 0; j < spanColumn; j++) {
                    row.remove(j + columnIndex);
                }
            }
        }
    }

    private AbsCellEntity putCell(int rowIndex, int columnIndex, AbsCellEntity cell) {
        AbsCellEntity oldCell = this.getRow(rowIndex).get(columnIndex);
        this.getRow(rowIndex).put(columnIndex, cell);
        return oldCell;
    }

    private void updateRowColumnCount(int rowCount, int columnCount) {
        mRowCount = mRowCount < rowCount ? rowCount : mRowCount;
        mColumnCount = mColumnCount < columnCount ? columnCount : mColumnCount;
    }

    private void updateCellSpanCount(@NonNull AbsCellEntity cell, int spanRow, int spanColumn) {
        cell.setSpanRowCount(spanRow + AbsCellEntity.DEFAULT_SPAN_COUNT);
        cell.setSpanColumnCount(spanColumn + AbsCellEntity.DEFAULT_SPAN_COUNT);
    }
}
