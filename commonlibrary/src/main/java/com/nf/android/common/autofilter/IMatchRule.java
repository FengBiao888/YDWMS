package com.nf.android.common.autofilter;

/**
 * Created by liangjianhua on 2018/5/25.
 */

public interface IMatchRule {

    boolean isMatch(String prefix);

    boolean isFirstCapIndex(int asscii);
}
