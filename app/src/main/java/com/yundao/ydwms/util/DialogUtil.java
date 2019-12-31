package com.yundao.ydwms.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yundao.ydwms.R;


/**
 * 得到ProgressDialog实例的工具类
 *
 * @author coder
 */
public class DialogUtil {

    public interface OnDialogButtonClickListener {
        void onClick(Dialog dialog, View view, String inputContent);
    }

    public interface OnItemSelectListener {
        void onItemSelect(Dialog dialog, String type, int position);
    }

    public interface OnTempItemSelectListener {
        void onItemSelect(Dialog dialog, String name, String value, int position);
    }


    /**
     * 得到ProgressDialog实例
     *
     * @param context
     * @param msgResId 对应的string.xml文件中的resId
     * @return ProgressDialog实例
     */
    public static Dialog getProgressDialog(Context context, int msgResId) {

        return getProgressDialog(context, context.getString(msgResId));
    }

    /**
     * 得到ProgressDialog实例
     *
     * @param context
     * @param message 消息字符串
     * @return ProgressDialog实例
     */
    public static Dialog getProgressDialog(Context context, String message) {

        return getProgressDialog(context, null, message);
    }

    /**
     * 得到ProgressDialog实例
     *
     * @param context
     * @param titleResId 标题对应的string.xml文件中的resId
     * @param msgResId   对应的string.xml文件中的resId
     * @return ProgressDialog实例
     */
    public static Dialog getProgressDialog(Context context, int titleResId,
                                           int msgResId) {

        return getProgressDialog(context, context.getString(titleResId),
                context.getString(msgResId));
    }

    /**
     * @param context
     * @param title   标题字符串
     * @param message 消息字符串
     * @return ProgressDialog实例
     */
    public static Dialog getProgressDialog(Context context, String title,
                                           String message) {

        // ProgressDialog progressDialog = new ProgressDialog(context);
        // progressDialog.setTitle(title);
        // progressDialog.setMessage(message);
        //
        // return progressDialog;

        return createLoadingDialog(context, message);

    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msgId
     * @return
     */
    public static Dialog createLoadingDialog(Context context, int msgId) {
        return createLoadingDialog(context, context.getString(msgId));

    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.load_layout, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);

        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局

        loadingDialog.setCanceledOnTouchOutside(false);

        loadingDialog.setCancelable(true);
        return loadingDialog;

    }

    private static ProgressDialog progressDialog;

    /**
     * 显示进度框
     *
     * @param context
     * @param msg     显示内容
     */
    public static void openProgressDialog(Context context, String msg) {
        // Log.i(LogConfig.CODER,
        // "openProgressDialog::"+((Activity)context).getLocalClassName());
        progressDialog = new ProgressDialog(context);

        progressDialog.setMessage(msg);
        if (null != progressDialog && !progressDialog.isShowing()) {
            progressDialog.show();
        }

    }

    /**
     * 显示进度框
     *
     * @param context
     * @param msgId   显示内容
     */
    public static void openProgressDialog(Context context, int msgId) {
        // Log.i(LogConfig.CODER,
        // "openProgressDialog::"+((Activity)context).getLocalClassName());
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(msgId));
        if (null != progressDialog && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 显示进度框
     *
     * @param context
     * @param title   标题
     * @param msg     显示内容
     */
    public static void openProgressDialog(Context context, String title,
                                          String msg) {
        // Log.i(LogConfig.CODER,
        // "openProgressDialog::"+((Activity)context).getLocalClassName());
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        if (null != progressDialog && !progressDialog.isShowing()) {
            progressDialog.show();
        }

    }

    /**
     * 显示进度框
     *
     * @param context
     * @param titleId 标题
     * @param msgId   显示内容
     */
    public static void openProgressDialog(Context context, int titleId,
                                          int msgId) {
        // Log.i(LogConfig.CODER,
        // "openProgressDialog::"+((Activity)context).getLocalClassName());
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(titleId));
        progressDialog.setMessage(context.getString(msgId));
        if (null != progressDialog && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 关闭进度框
     */
    public static void closeProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }
    }

    public static Dialog showDeclareDialog(Activity context, String tips, View.OnClickListener rightClickListener ){
        return showDeclareDialog( context, tips, true, rightClickListener );
    }

    public static Dialog showDeclareDialog(Activity context, String tips, boolean rightVisiable, View.OnClickListener rightClickListener ){
        Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setCancelable( false );
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewDialog = inflater.inflate(R.layout.dialog_declare, null);
        TextView comment = viewDialog.findViewById( R.id.content );
        TextView left = viewDialog.findViewById( R.id.btn_positive );
        TextView right = viewDialog.findViewById( R.id.btn_cancel );
        right.setVisibility( rightVisiable ? View.VISIBLE : View.GONE );
        viewDialog.findViewById( R.id.middle ).setVisibility(  rightVisiable ? View.VISIBLE : View.GONE );
        comment.setText( tips );
        right.setOnClickListener(v -> dialog.cancel());
        left.setOnClickListener(v -> {
            v.setTag( comment.getText().toString() );
            rightClickListener.onClick( v );
            dialog.cancel();
        });

        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels ;
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM );
        dialogWindow.setDimAmount( 0.45f );
//        dialogWindow.setBackgroundDrawableResource( R.color.black );
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
        dialog.setContentView(viewDialog, layoutParams);

        return dialog ;
    }


}
