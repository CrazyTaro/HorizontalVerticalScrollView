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
    public static final int FIXED_MENU_INDEX_ROW = -1;
    /**
     * default column index of column menu
     */
    public static final int FIXED_MENU_INDEX_COLUMN = -1;

    public static final int MENU_ROW = 0;
    public static final int MENU_COLUMN = 1;

    protected List<AbsCellEntity> mColumnMenuList;
    protected List<AbsCellEntity> mRowMenuList;
    protected List<AbsRowEntity> mRowList;

    public static final AbsTableEntity getExampleTable() {
        List<AbsCellEntity> rowMenu = new ArrayList<>(11);
        List<AbsCellEntity> columnMenu = new ArrayList<>(5);
        for (int i = 0; i < 11; i++) {
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
            AbsRowEntity newRow = new AbsRowEntity(11);
            if ((i & 1) == 1) {
                for (int k = 1; k < 6; k++) {
                    AbsCellEntity newCell = new AbsCellEntity(i, k * 2 - 1, "long cell");
                    newRow.addCell(newCell, 0, 2);
                    newRow.addCell(newCell, 0, 2);
                }
            } else {
                for (int j = 0; j < 11; j++) {
                    AbsCellEntity newCell = new AbsCellEntity(i, j, "cell");
                    newRow.addCell(newCell);
                }
            }
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
