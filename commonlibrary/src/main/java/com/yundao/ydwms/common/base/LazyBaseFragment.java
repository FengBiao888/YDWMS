package com.yundao.ydwms.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract class LazyBaseFragment extends Fragment {

	protected LayoutInflater inflater;
	private View contentView;
	private Context context;
	private ViewGroup container;
	private Unbinder mUnBinder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		onCreateView(savedInstanceState);
		if (contentView == null)
			contentView = super.onCreateView(inflater, container, savedInstanceState);
		mUnBinder = ButterKnife.bind( contentView );
		return contentView;
	}

	protected void onCreateView(Bundle savedInstanceState) {

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mUnBinder.unbind();
		contentView = null;
		container = null;
		inflater = null;
	}

	public Context getApplicationContext() {
		return context;
	}

	public void setContentView(int layoutResID) {
		setContentView(inflater.inflate(layoutResID, container, false));
	}

	public void setContentView(View view) {
		contentView = view;
	}

	public View getContentView() {
		return contentView;
	}

	public <T extends View> T findViewById(@IdRes int id) {
		if (contentView != null)
			return contentView.findViewById(id);
		return null;
	}
}
