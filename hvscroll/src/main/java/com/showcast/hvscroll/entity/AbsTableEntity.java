package com.showcast.hvscroll.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taro on 16/8/17.
 */
public class AbsTableEntity {
    protected List<String> mMenuList;
    protected List<AbsRowEntity> mRowList;

    public static final AbsTableEntity getExampleTable() {
        List<String> menu = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            menu.add("中文例子-" + (1 << i));
        }
        AbsTableEntity table = new AbsTableEntity();
        table.setMenuList(menu);

        List<AbsRowEntity> row = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            List<AbsCellEntity> cell = new ArrayList<>(10);
            for (int j = 0; j < 10; j++) {
                AbsCellEntity newCell = new AbsCellEntity();
                newCell.setText("例子");
                cell.add(newCell);
            }
            AbsRowEntity newRow = new AbsRowEntity();
            newRow.setCellList(cell);
            row.add(newRow);
        }

        table.setRowList(row);
        return table;
    }

    public void setMenuList(List<String> menuList) {
        mMenuList = menuList;
    }

    public void setRowList(List<AbsRowEntity> rowList) {
        mRowList = rowList;
    }

    public int getRowCount() {
        return mRowList == null ? 0 : mRowList.size();
    }

    public int getMenuCount() {
        return mMenuList == null ? 0 : mMenuList.size();
    }

    public String getMenu(int menuIndex) {
        if (mMenuList == null || menuIndex < 0 || menuIndex >= mMenuList.size()) {
            return null;
        } else {
            return mMenuList.get(menuIndex);
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
