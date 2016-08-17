package com.showcast.hvscroll.entity;

import java.util.List;

/**
 * Created by taro on 16/8/17.
 */
public class AbsRowEntity {
    protected String mTitle;
    protected List<AbsCellEntity> mCellList;

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

    public void setCellList(List<AbsCellEntity> cellList) {
        mCellList = cellList;
    }
}
