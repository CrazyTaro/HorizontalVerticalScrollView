package com.showcast.hvscroll.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

import com.showcast.hvscroll.entity.CellEntity;
import com.showcast.hvscroll.params.CellParams;
import com.showcast.hvscroll.params.Constant;

/**
 * Created by taro on 16/8/23.
 */
public class SimpleHVScrollTableDraw extends AbsHorizontalVerticalScrollTableDraw {
    public SimpleHVScrollTableDraw(@NonNull View drawView) {
        super(drawView);
    }

    @Override
    protected void drawCell(@NonNull CellEntity cell, @NonNull BaseDrawStyle drawStyle, Rect drawRect, float textDrawX, float textDrawY, Paint paint, Canvas canvas) {
        //draw background
        paint.setColor(drawStyle.getBackgroundColor());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(drawRect, paint);

        //draw stroke
        paint.setColor(drawStyle.getStrokeColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(drawStyle.getStrokeWidth());
        canvas.drawRect(drawRect, paint);

        //draw text
        paint.setColor(drawStyle.getTextColor());
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(drawStyle.getTextSize());
        this.drawAutofitWidthText(drawRect.width(), cell.getText(), textDrawX, textDrawY, paint, canvas);
    }

    @Override
    protected int getMoveDirection(float mouseDown, float mouseUp) {
        CellParams cellParams = this.getCellParams();
        if (cellParams != null) {
            if (mouseDown < cellParams.getWidth()) {
                return Constant.MOVE_DIRECTION_VERTICAL;
            } else {
                return Constant.MOVE_DIRECTION_HORIZONTAL;
            }
        } else {
            return Constant.MOVE_DIRECTOIN_NONE;
        }
    }

    @Override
    protected void drawMenu(CellEntity menu, BaseDrawStyle drawStyle, Rect drawRect, float textDrawX, float textDrawY, Paint paint, Canvas canvas) {
        //draw menu uses the method draw cell.
        super.drawMenu(menu, drawStyle, drawRect, textDrawX, textDrawY, paint, canvas);
    }
}
