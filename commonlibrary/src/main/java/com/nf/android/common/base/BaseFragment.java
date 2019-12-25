package com.nf.android.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

  private Unbinder mUnBinder;
  protected FragmentActivity fragmentActivity ;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fragmentActivity = getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = onCreateView(inflater, container);
    //返回一个Unbinder值（进行解绑），注意这里的this不能使用getActivity()
    mUnBinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
  }

  public void onResume() {
	    super.onResume();
//	    MobclickAgent.onPageStart(getClass().getSimpleName()); //统计页面
	}

	public void onPageRestore(){//ljh 部分机器不能在OnResume里进行view更新，报空指针，设置此方法供主动调动

    }

    public boolean onBackPressed(){
      return false ;
    }

	public void onPause() {
	    super.onPause();
//	    MobclickAgent.onPageEnd(getClass().getSimpleName());
	}

	protected abstract View onCreateView(LayoutInflater inflater, ViewGroup container);

}
