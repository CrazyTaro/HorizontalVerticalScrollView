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


    public boolean addFrozenLineIndex(int index, @Constant.LineType int whichLines) {
        LineSetting setting = mLineSettings[whichLines];
        if (setting == null) {
            setting = new LineSetting();
            mLineSettings[whichLines] = setting;
        }
        return setting.addFrozenLineIndex(index);
    }

    public LineSetting getLineSetting(@Constant.LineType int whichLines) {
        LineSetting setting = mLineSettings[whichLines];
        if (setting == null) {
            setting = new LineSetting();
            mLineSettings[whichLines] = setting;
        }
        return setting;
    }

    public static class LineSetting {
        private int mOffsetLines = 0;
        private int mOffsetLength = 0;
        private TreeSet<Integer> mFrozenLines;

        private LineSetting() {
        }

        public boolean addFrozenLineIndex(int index) {
            if (index < 0) {
                return false;
            } else {
                if (mFrozenLines == null) {
                    mFrozenLines = new TreeSet<>();
                }
                mFrozenLines.add(index);
                return true;
            }
        }

        public boolean isFrozenLine(int index) {
            return mFrozenLines != null && mFrozenLines.contains(index);
        }

        public int getFrozenLineSize() {
            return mFrozenLines == null ? 0 : mFrozenLines.size();
        }

        public void removeFrzonLineIndex(int index) {
            if (mFrozenLines != null) {
                mFrozenLines.remove(index);
            }
        }

        public void clearFrozenLineIndex() {
            if (mFrozenLines != null) {
                mFrozenLines.clear();
            }
        }

        public int getLastFrozenLineIndex() {
            return mFrozenLines == null ? -1 : mFrozenLines.last();
        }

        public int getFirstFrozenLineIndex() {
            return mFrozenLines == null ? -1 : mFrozenLines.first();
        }

        public SortedSet<Integer> getSetLessThan(int index) {
            if (index < 0) {
                return null;
            } else {
                return mFrozenLines == null ? null : mFrozenLines.headSet(index);
            }
        }

        public SortedSet<Integer> getSetGreaterThan(int index) {
            if (index < 0) {
                return null;
            } else {
                return mFrozenLines == null ? null : mFrozenLines.tailSet(index);
            }
        }

        @NonNull
        public int[] getValueFrozenLine() {
            if (mFrozenLines == null || mFrozenLines.size() <= 0) {
                return new int[0];
            } else {
                int[] result = new int[mFrozenLines.size()];
                Iterator<Integer> it = mFrozenLines.iterator();
                for (int i = 0; i < result.length; i++) {
                    result[i] = it.next();
                }
                return result;
            }
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
