package com.showcast.hvscroll.entity;

import com.showcast.hvscroll.draw.BaseDrawStyle;

/**
 * Created by taro on 16/8/17.
 */
public class AbsCellEntity extends BaseDrawStyle {
    protected String mText;

    public AbsCellEntity(String text) {
        mText = text;
    }

    public AbsCellEntity() {
    }

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public boolean isNeedToDraw() {
        return true;
    }
}
