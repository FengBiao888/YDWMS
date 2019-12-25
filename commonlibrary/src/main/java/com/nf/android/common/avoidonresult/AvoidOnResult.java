package com.nf.android.common.avoidonresult;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;


/**
 * Created by jack on 2017/12/27.
 * modify by https://guofeng007.github.io remove request code ,instead use callback.hashcode as requestcode 2017/1/10
 */

public class AvoidOnResult {
    private static final String TAG = "AvoidOnResult";
    private AvoidOnResultFragment mAvoidOnResultFragment;

    public AvoidOnResult(Activity activity) {
        mAvoidOnResultFragment = getAvoidOnResultFragment(activity);
    }

    public AvoidOnResult(Fragment fragment){
        this(fragment.getActivity());
    }

    private AvoidOnResultFragment getAvoidOnResultFragment(Activity activity) {
        AvoidOnResultFragment avoidOnResultFragment = findAvoidOnResultFragment(activity);
        if (avoidOnResultFragment == null) {
            avoidOnResultFragment = new AvoidOnResultFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(avoidOnResultFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return avoidOnResultFragment;
    }

    private AvoidOnResultFragment findAvoidOnResultFragment(Activity activity) {
        return (AvoidOnResultFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public void startForResult(Intent intent, Callback callback) {
        mAvoidOnResultFragment.startForResult(intent, callback);
    }

    public void startForResult(Class<?> clazz, Callback callback) {
        Intent intent = new Intent(mAvoidOnResultFragment.getActivity(), clazz);
        startForResult(intent, callback);
    }

    public abstract static class Callback {
      protected int requestCode ;
      public Callback( int requestCode ){
        this.requestCode = requestCode ;
      }
      public Callback( ){
        this.requestCode = -1 ;
      }
      public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
