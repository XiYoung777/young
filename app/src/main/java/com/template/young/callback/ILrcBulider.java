package com.template.young.callback;

import com.template.young.model.LrcRow;

import java.util.List;

public interface ILrcBulider {
    List<LrcRow> getLrcRows(String rawLrc);
}
