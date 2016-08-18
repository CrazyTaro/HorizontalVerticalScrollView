package com.showcast.hvscroll.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by taro on 16/8/17.
 */
public class AbsRowEntity {
    private int mColumnCount = 0;
    protected String mTitle;
    protected List<AbsCellEntity> mCellList;

    public AbsRowEntity(int maxColumnCount) {
        mColumnCount = maxColumnCount;
    }

    public AbsRowEntity() {
    }

    public String getTitle() {
        return mTitle;
    }

    public int getColumnCount() {
        return mCellList.size();
    }

    public List<AbsCellEntity> getCellList() {
        return mCellList;
    }

    public AbsCellEntity getCell(int columnIndex) {
        if (mCellList == null || columnIndex < 0 || columnIndex >= mCellList.size()) {
            return null;
        } else {
            return mCellList.get(columnIndex);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
    }

//    public void setCellList(List<AbsCellEntity> cellList) {
//        mCellList = cellList;
//    }

    public void addCell(AbsCellEntity cell) {
        this.checkIfCellListExsit();
        mCellList.add(cell);
    }

    public void addCell(AbsCellEntity cell, int spanRowCount, int spanColumnCount) {
        if (cell != null) {
            cell.setSpanRowCount(spanRowCount);
            cell.setSpanColumnCount(spanColumnCount);
        }
        this.checkIfCellListExsit();
        mCellList.add(cell);
    }

    private void checkIfCellListExsit() {
        if (mCellList == null) {
            if (mColumnCount > 0) {
                mCellList = new ArrayList<>(mColumnCount);
            } else {
                mCellList = new LinkedList<>();
            }
        }
    }
}
