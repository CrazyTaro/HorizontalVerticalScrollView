package com.showcast.hvscroll.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.showcast.hvscroll.draw.AbsHorizontalVerticalScrollTableDraw;
import com.showcast.hvscroll.draw.IHVScrollTable;
import com.showcast.hvscroll.draw.SimpleHVScrollTableDraw;
import com.showcast.hvscroll.entity.CellEntity;
import com.showcast.hvscroll.entity.TableEntity;
import com.showcast.hvscroll.params.CellParams;

/**
 * Created by taro on 16/8/17.
 */
public class HorizontalVerticalScrollView extends View {
    private AbsHorizontalVerticalScrollTableDraw mHvDraw = new SimpleHVScrollTableDraw(this);

    public HorizontalVerticalScrollView(Context context) {
        super(context);
        this.initial();
    }

    public HorizontalVerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initial();
    }

    public HorizontalVerticalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initial();
    }

    private void initial() {
//
//        //menu
//        MenuParams menuParams = new MenuParams();
//        menuParams.setIsDrawRowMenu(true);
//        menuParams.setIsDrawColumn(false);
//        menuParams.getSetting(Constant.MENU_ROW).setMenuFrozen(false, true);
//        menuParams.getSetting(Constant.MENU_ROW).addFrozenItemIndex(0);
//
//        //cell
//        CellParams cellParams = new CellParams();
////        cellParams.getDefaultDrawStyle().setStrokeColor(Color.TRANSPARENT);
//        cellParams.getSetting(Constant.LINE_COLUMN).addFrozenItemIndex(0);
//        BaseDrawStyle style = new BaseDrawStyle();
//        style.setBackgroundColor(Color.LTGRAY);
//        cellParams.addNewDrawStyle("first", style);
//
//        //global
//        GlobalParams globalParams = new GlobalParams();
//        globalParams.setStrokeColor(Color.WHITE);
//        globalParams.setStrokeWidth(4);
//        globalParams.setIsDrawCellStroke(false);
//        globalParams.setCanvasBackgroundColor(Color.LTGRAY);
//        globalParams.setIsDrawMask(false);
//        globalParams.setMaskWidthPercent(1, 0.8f);
//        globalParams.setMaskAlpha(100);
//        globalParams.setMaskStartLine(3);
//        globalParams.setMaskColor(Color.WHITE);
//        globalParams.setMaskSplitLineColor(Color.WHITE);
//        mHvDraw.setParams(globalParams, menuParams, cellParams);
//        mHvDraw.setTable(TableEntity.getExampleTable());
//        TableEntity table = new TableEntity(10, 8);
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 8; j++) {
//                table.addCellWithoutSpan(new CellEntity(i, j, "cell" + i + "-" + j));
//            }
//        }
//        mHvDraw.setCellParams(new CellParams());
//        mHvDraw.setTable(table);
    }

    public IHVScrollTable getHVScrollTable() {
        return mHvDraw;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if (isInEditMode()) {
//            if (mHvDraw.getTable() == null) {
//                TableEntity table = new TableEntity(10, 8);
//                for (int i = 0; i < 10; i++) {
//                    for (int j = 0; j < 8; j++) {
//                        table.addCellWithoutSpan(new CellEntity(i, j, "cell" + i + "-" + j));
//                    }
//                }
//                mHvDraw.setCellParams(new CellParams());
//                mHvDraw.setTable(table);
//            }
//        }
        mHvDraw.drawCanvas(canvas);
    }
}
