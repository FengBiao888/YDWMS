package com.nf.android.common.avoidonresult;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jack on 2017/12/27.
 * modify by https://guofeng007.github.io remove request code ,instead use callback.hashcode as requestcode 2017/1/10
 */

public class AvoidOnResultFragment extends Fragment {
    private Map<Integer, AvoidOnResult.Callback> mCallbacks = new HashMap<>();

    public AvoidOnResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    public void startForResult(Intent intent, AvoidOnResult.Callback callback) {
        int key = callback.requestCode == -1 ? callback.hashCode() : callback.requestCode ;
        mCallbacks.put(key, callback);
        startActivityForResult(intent, key);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //callback方式的处理
        AvoidOnResult.Callback callback = mCallbacks.remove(requestCode);
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
        }
    }

}
