package com.template.young.callback;

import com.template.young.model.LrcRow;

/**
 * create by XiYoung
 * on 20-1-20
 */
public interface ILrcViewListener {
    /**
     * 当歌词被用户上下拖动的时候回调该方法
     */
    void onLrcSeeked(int newPosition, LrcRow row);
}