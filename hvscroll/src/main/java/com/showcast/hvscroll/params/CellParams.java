package com.showcast.hvscroll.params;

import android.support.annotation.NonNull;

import com.showcast.hvscroll.draw.BaseDrawStyle;
import com.showcast.hvscroll.draw.CellDrawStyle;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by taro on 16/8/19.
 */
public class CellParams extends BaseParams {
    private boolean mIsDrawFrozenRow = false;
    private boolean mIsDrawFrozenColumn = false;
    private LineSetting[] mLineSettings;

    public CellParams() {
        super();
    }

    public CellParams(int width, int height) {
        super(width, height);
    }

    @Override
    protected void initialConstructor() {
        super.initialConstructor();
        this.setDefaultDrawStyle(new CellDrawStyle());
        mLineSettings = new LineSetting[2];
    }

    @Override
    public BaseDrawStyle addNewDrawStyle(String tag, @NonNull BaseDrawStyle drawStyle) {
        if (!(drawStyle instanceof CellDrawStyle)) {
            throw new IllegalArgumentException("the class of drawStyle param must be CellDrawStyle");
        }
        return super.addNewDrawStyle(tag, drawStyle);

    }

    @Override
    public CellDrawStyle getDrawStyle(String tag) {
        return (CellDrawStyle) super.getDrawStyle(tag);
    }

    @Override
    public CellDrawStyle getDefaultDrawStyle() {
        return (CellDrawStyle) super.getDefaultDrawStyle();
    }

    @Override
    public void setDefaultDrawStyle(@NonNull BaseDrawStyle style) {
        if (!(style instanceof CellDrawStyle)) {
            throw new IllegalArgumentException("the class of drawStyle param must be CellDrawStyle");
        }
        super.setDefaultDrawStyle(style);
    }

    @Override
    public LineSetting getSetting(@Constant.LineType int whichLines) {
        LineSetting setting = mLineSettings[whichLines];
        if (setting == null) {
            setting = new LineSetting();
            mLineSettings[whichLines] = setting;
        }
        return setting;
    }

    public boolean addFrozenLineIndex(int index, @Constant.LineType int whichLines) {
        LineSetting setting = mLineSettings[whichLines];
        if (setting == null) {
            setting = new LineSetting();
            mLineSettings[whichLines] = setting;
        }
        return setting.addFrozenItemIndex(index);
    }

    public static class LineSetting extends Setting {
        private int mOffsetLines = 0;
        private int mOffsetLength = 0;

        protected LineSetting() {
            super();
        }

        public void setOffsetLines(int lines) {
            if (lines >= 0) {
                mOffsetLines = lines;
            }
        }

        public void setOffsetLength(int lineWidthOrHeight) {
            if (lineWidthOrHeight >= 0) {
                mOffsetLength = lineWidthOrHeight;
            }
        }

        public int getOffsetLines() {
            return mOffsetLines;
        }

        public int getOffsetLength() {
            return mOffsetLength;
        }

        public int getDrawLength(int lineWidthOrHeight) {
            if (mOffsetLength > 0) {
                return mOffsetLength;
            } else {
                return mOffsetLines * lineWidthOrHeight;
            }
        }
    }
}
