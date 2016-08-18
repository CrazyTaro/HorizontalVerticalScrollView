package com.showcast.hvscroll.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by taro on 16/8/17.
 */
public class AbsTableEntity {
    /**
     * default row index of row menu
     */
    public static final int MENU_INDEX_ROW = -1;
    /**
     * default column index of column menu
     */
    public static final int MENU_INDEX_COLUMN = -1;

    protected List<AbsCellEntity> mColumnMenuList;
    protected List<AbsCellEntity> mRowMenuList;
    protected List<AbsRowEntity> mRowList;

    public static final AbsTableEntity getExampleTable() {
        List<AbsCellEntity> rowMenu = new ArrayList<>(10);
        List<AbsCellEntity> columnMenu = new ArrayList<>(5);
        for (int i = 0; i < 10; i++) {
            AbsCellEntity newCell = new AbsCellEntity();
            newCell.setText("title-" + (1 << i));
            rowMenu.add(newCell);
        }
        columnMenu.add(new AbsCellEntity("title-1"));
        AbsTableEntity table = new AbsTableEntity();
        table.setRowMenuList(rowMenu);
        table.setColumnMenuList(columnMenu);

        List<AbsRowEntity> row = new ArrayList<>(40);
        for (int i = 0; i < 35; i++) {
            List<AbsCellEntity> cell = new ArrayList<>(10);
            for (int j = 0; j < 10; j++) {
                AbsCellEntity newCell = new AbsCellEntity();
                newCell.setText("cell");
                cell.add(newCell);
            }
            AbsRowEntity newRow = new AbsRowEntity();
            newRow.setCellList(cell);
            row.add(newRow);
        }

        table.setRowList(row);
        return table;
    }

    public void setRowMenuList(List<AbsCellEntity> menuList) {
        mRowMenuList = menuList;
    }

    public void setColumnMenuList(List<AbsCellEntity> menuList) {
        mColumnMenuList = menuList;
    }

    public void setRowList(List<AbsRowEntity> rowList) {
        mRowList = rowList;
    }

    public int getRowCount() {
        return mRowList == null ? 0 : mRowList.size();
    }

    public int getMenuCount(int whichMenu) {
        switch (whichMenu) {
            case 0:
                return mRowMenuList == null ? 0 : mRowMenuList.size();
            case 1:
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

    public AbsRowEntity getRow(int rowIndex) {
        if (mRowList == null || rowIndex < 0 || rowIndex >= mRowList.size()) {
            return null;
        } else {
            return mRowList.get(rowIndex);
        }
    }

    public AbsCellEntity getCell(int rowIndex, int columnIndex) {
        AbsRowEntity row = this.getRow(rowIndex);
        if (row != null) {
            return row.getCell(columnIndex);
        } else {
            return null;
        }
    }
}
