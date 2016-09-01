package com.grant.horizontalverticalscroll;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.showcast.hvscroll.draw.BaseDrawStyle;
import com.showcast.hvscroll.draw.IHVScrollTable;
import com.showcast.hvscroll.entity.CellEntity;
import com.showcast.hvscroll.entity.TableEntity;
import com.showcast.hvscroll.params.CellParams;
import com.showcast.hvscroll.params.Constant;
import com.showcast.hvscroll.params.GlobalParams;
import com.showcast.hvscroll.params.MenuParams;
import com.showcast.hvscroll.ui.HorizontalVerticalScrollView;

public class MainActivity extends AppCompatActivity implements IHVScrollTable.OnCellClickListener {
    HorizontalVerticalScrollView mHVscroll = null;
    String[] menu = {"Firday,June 3", "9 pm", "9:30 pm", "10:00 pm"};
    String[][] cell = {
            {"2.1 KTVU", "New Girl", "The Big Bang Theory", "2 Broken Sister"},
            {"4.1 KTVU", "DC's Legends of Tomorrow", "", "The First Hello"},
            {"5.1 KTVU", "NCIS", "", "NCIS"},
            {"7.1 KGODT", "Fresh Off The Boat", "Modern Family", "Friends"},
            {"9.1 KQED", "PBS News Hour", "", "Master"},
            {"11.1 KNTVU", "Blue Bloods", "", "Chinca pander"},
            {"20.1 KOFYDT2", "My Girl", "Two and a Half Men", "Angel bird"},
            {"28.1 KTFLCD", "Lauren Lake's Paternity Court", "", "Sally"},
            {"32.1 KMTPDT", "The Parent Hood", "", "American Dependence"},
            {"38.1 KCNSDT3", "Flashpoint", "", "The Children"},
            {"40.3.1 KTLXDT3", "Doom", "", "Doon "},
            {"44.1 KBCWDT", "Walker Texas Ranger", "", "Walker"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHVscroll = (HorizontalVerticalScrollView) findViewById(R.id.hvs_main);

        IHVScrollTable hvs = mHVscroll.getHVScrollTable();
        TableEntity table = new TableEntity(12, 4);
        for (int i = 0; i < menu.length; i++) {
            table.addMenu(table.newRowMenu(i, menu[i]), Constant.MENU_ROW);
        }
        for (int i = 0; i < cell.length; i++) {
            String[] col = cell[i];
            for (int j = 0; j < col.length; j++) {
                table.addCellWithoutSpan(new CellEntity(i, j, col[j]));
            }
        }

        changedCell(table, 1, 1);
        changedCell(table, 2, 1);
        changedCell(table, 4, 1);
        changedCell(table, 5, 1);
        changedCell(table, 7, 1);
        changedCell(table, 8, 1);
        changedCell(table, 9, 1);
        changedCell(table, 10, 1);
        changedCell(table, 11, 1);

        //menu
        MenuParams menuParams = new MenuParams();
        menuParams.setHeight(100);
        menuParams.setWidth(300);
        menuParams.getDefaultDrawStyle().setBackgroundColor(Color.WHITE);
        menuParams.setIsDrawRowMenu(true);
        menuParams.setIsDrawColumn(true);
        menuParams.getSetting(Constant.MENU_ROW).setMenuFrozen(false, false);
        menuParams.getSetting(Constant.MENU_ROW).addFrozenItemIndex(0);
        menuParams.getSetting(Constant.MENU_COLUMN).setMenuFrozen(false, false);

        //cell
        CellParams cellParams = new CellParams();
        cellParams.setHeight(200);
        cellParams.setWidth(300);
        cellParams.getDefaultDrawStyle().setBackgroundColor(Color.BLACK);
        cellParams.getDefaultDrawStyle().setStrokeWidth(5);
        cellParams.getDefaultDrawStyle().setTextColor(Color.WHITE);
        cellParams.getDefaultDrawStyle().setStrokeColor(Color.WHITE);
//        cellParams.getSetting(Constant.LINE_COLUMN).addFrozenItemIndex(0);
//        cellParams.getSetting(Constant.LINE_ROW).addFrozenItemIndex(2);

        //global
        GlobalParams globalParams = new GlobalParams();
        globalParams.setStrokeColor(Color.WHITE);
        globalParams.setStrokeWidth(4);
        globalParams.setIsDrawCellStroke(true);
        globalParams.setIsDrawMask(true);
        globalParams.setMaskWidthPercent(2, 0.8f);
        globalParams.setMaskAlpha(100);
        globalParams.setMaskStartLine(1);
        globalParams.setMaskColor(Color.WHITE);
        globalParams.setMaskSplitLineColor(Color.WHITE);
        hvs.setParams(globalParams, menuParams, cellParams);
//        hvs.setTable(table);
        hvs.setTable(TableEntity.getExampleTable());
        hvs.setOnCellClickListener(this);
    }

    private void changedCell(TableEntity table, int row, int col) {
        CellEntity cell = table.getCell(row, col);
        cell.setSpanColumnCount(2);
        table.addCellAutoSpan(cell);
    }

    @Override
    public void onCellClick(CellEntity cell, int row, int column) {

    }
}
