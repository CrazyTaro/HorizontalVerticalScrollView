package com.showcast.hvscroll.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.showcast.hvscroll.draw.BaseDrawStyle;
import com.showcast.hvscroll.draw.HorizontalVerticalScrollDraw;
import com.showcast.hvscroll.entity.TableEntity;
import com.showcast.hvscroll.params.CellParams;
import com.showcast.hvscroll.params.Constant;
import com.showcast.hvscroll.params.GlobalParams;
import com.showcast.hvscroll.params.MenuParams;

/**
 * Created by taro on 16/8/17.
 */
public class HorizontalVerticalScrollView extends View {
    private HorizontalVerticalScrollDraw mHvDraw = new HorizontalVerticalScrollDraw(this);

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
        //menu
        MenuParams menuParams = new MenuParams();
        menuParams.setIsDrawRowMenu(true);
        menuParams.setIsDrawColumn(false);
        menuParams.getSetting(Constant.MENU_ROW).setMenuFrozen(false, true);
        menuParams.getSetting(Constant.MENU_ROW).addFrozenItemIndex(0);

        //cell
        CellParams cellParams = new CellParams();
//        cellParams.getDefaultDrawStyle().setStrokeColor(Color.TRANSPARENT);
        cellParams.getSetting(Constant.LINE_COLUMN).addFrozenItemIndex(0);
        BaseDrawStyle style = new BaseDrawStyle();
        style.setBackgroundColor(Color.LTGRAY);
        cellParams.addNewDrawStyle("first", style);

        //global
        GlobalParams globalParams = new GlobalParams();
        globalParams.setStrokeColor(Color.WHITE);
        globalParams.setStrokeWidth(4);
        globalParams.setIsDrawCellStroke(false);
        globalParams.setCanvasBackgroundColor(Color.LTGRAY);
        globalParams.setIsDrawMask(false);
        globalParams.setMaskWidthPercent(1, 0.8f);
        globalParams.setMaskAlpha(100);
        globalParams.setMaskStartLine(3);
        globalParams.setMaskColor(Color.WHITE);
        globalParams.setMaskSplitLineColor(Color.WHITE);
        mHvDraw.setParams(globalParams, menuParams, cellParams);
        mHvDraw.setTable(TableEntity.getExampleTable());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mHvDraw.drawCanvas(canvas);
    }
}
