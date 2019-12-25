package com.nf.android.common.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.nf.android.common.utils.ReflectHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 涓撻棬鐢ㄤ簬6.0鏉冮檺鎺堟潈鎺у埗锛屼粠杩欎釜鐣岄潰杩斿洖涔嬪悗锛屾墠缁х画鍘熷厛鐨勪笟鍔★拷?锟�?
 *
 * @author lhy
 *
 */
public class PermissionsGrantActivity extends Activity {
    private final static String TAG = "PermissionsGrant";
    private final static String ACTION_PERMISSION_GRANT_RESULT = "action.permissions.grant.result";
    private final static String EXTRA_REQ_HANDLER_ID = "handler.id"; // 鐢ㄤ簬璁板綍PermissionHandler鐨処D锛屽湪骞挎挱鎺ユ敹鍣ㄤ腑鎵惧洖
    private final static String EXTRA_REQ_PERMISSIONS = "permissions";
    private final static String EXTRA_RET_PERMISSIONS_GRANTED = "permissions.granted";
    private final static String EXTRA_RET_PERMISSIONS_DENIED = "permissions.denied";
    private final static String ACTION_MANAGER_WRITE_SETTINGS = "android.settings.action.MANAGE_WRITE_SETTINGS";

    private boolean	mResumed ;
    private String[] mGrantPermissions; // 寰呮巿鏉冪殑鏉冮檺
    private String mHandlerId;
    private int mReqCode; // 璁板綍璇锋眰锟�?
    private int[] mGrantResults;

    public interface PermissionHandler {
	/**
	 * @param grantedpermissions
	 *            鑾峰緱鎺堟潈鐨勬潈锟�?
	 * @param denied_permissions
	 *            琚嫆鐨勬潈锟�?
	 */
    void onPermissionsResult(String[] grantedpermissions, String[] denied_permissions);
    }

    public static void grantPermissions(Context context, String[] permissions, PermissionHandler handler) {
	int targetSDKVer = context.getApplicationInfo().targetSdkVersion; //鍙湁鐩爣SDK澶т簬绛変簬23鐨勬墠浼氭湁鎺堟潈
	if (targetSDKVer < 23 || permissions == null || permissions.length == 0 ) {
	    handler.onPermissionsResult(permissions, null); // 浣庝簬Android
							    // M鐗堢殑璋冪敤鐩存帴褰撴潈闄愯鎺堜簣
	    return;
	}
	String[] denied_permissions = getGrantPermissions(context, permissions, PackageManager.PERMISSION_DENIED);
	if (denied_permissions == null) { //
	    handler.onPermissionsResult(permissions, null);
	    return;
	}
	permissions = denied_permissions;
	String handlerId = generateHandlerId();
	PermissionGrantReceiver receiver = new PermissionGrantReceiver(context, handler, handlerId);
	IntentFilter filter = new IntentFilter(ACTION_PERMISSION_GRANT_RESULT);
	context.registerReceiver(receiver, filter);
	Intent intent = getGrantPermissionsIntent(context, permissions, handlerId);
	context.startActivity(intent);
	if (containWriteSettingsPermission(denied_permissions)){
	    grantSystemWriteSettings(context, handler); //浠ヤ究璁╂潈闄愮晫闈㈢洊鍦ㄦ渶涓婇潰
	}
    }

    private static Intent getGrantPermissionsIntent(Context context, String[] permissions, String handlerId) {
	Intent intent = new Intent(context, PermissionsGrantActivity.class);
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.putExtra(EXTRA_REQ_PERMISSIONS, permissions);
	intent.putExtra(EXTRA_REQ_HANDLER_ID, handlerId);
	return intent;
    }

    public static void grantSystemWriteSettings(Context context, PermissionHandler handler) {
	int targetSDKVer = context.getApplicationInfo().targetSdkVersion; //鍙湁鐩爣SDK澶т簬绛変簬23鐨勬墠浼氭湁鎺堟潈
	if (targetSDKVer < 23 || systemCanWrite(context)) {
	    handler.onPermissionsResult(new String[]{Manifest.permission.WRITE_SETTINGS}, null);// 浣庝簬Android M鐗堢殑璋冪敤鐩存帴褰撳厑璁歌锟�?
	    return;
	}
	Intent intent = new Intent(context, PermissionsGrantActivity.class);
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setAction(ACTION_MANAGER_WRITE_SETTINGS);
	String handlerId = generateHandlerId();
	intent.putExtra(EXTRA_REQ_HANDLER_ID, handlerId);
//	PermissionGrantReceiver receiver = new PermissionGrantReceiver(context, handler, handlerId);
//	IntentFilter filter = new IntentFilter(ACTION_MANAGER_WRITE_SETTINGS);
//	context.registerReceiver(receiver, filter);
	context.startActivity(intent);
    }

    private static boolean systemCanWrite(Context context) {
	Object obj = ReflectHelper.callStaticMethod(Settings.System.class, "canWrite",
		new Class<?>[] { Context.class }, new Object[] { context });
	if (obj != null && obj instanceof Boolean) {
	    return ((Boolean) obj).booleanValue();
	} else {
	    return true;// 锟�?锟斤拷鍑虹幇鍙嶅皠缁撴灉涓嶆甯革紝灏卞綋鍙互鍐欙紝浠ュ厤鍑虹幇鍚庣画娴佺▼涓嶆锟�?
	}
    }

    public static boolean containPermission(String []permissions, String perm){
	if (permissions == null){
	    return false;
	}
	for (String s : permissions){
	    if (s.equals(perm)){
		return true;
	    }
	}
	return false;
    }

    public static boolean checkAllPermissionsGranted(Context context, String[] permissions){
	int targetSDK = context.getApplicationInfo().targetSdkVersion;
	if (targetSDK < 23){ //浣庝簬23涓嶇敤锟�?锟斤拷鏉冮檺
	    return true;
	}else if (permissions != null){
	    for (String s: permissions){
		if (isWriteSettingsPermission(s)){
		    if (checkWriteSettingsResult(context, s, PackageManager.PERMISSION_DENIED)){
			return false;
		    }
		}else	if (checkSelfPermissionCompat(context, s, PackageManager.PERMISSION_DENIED)){
		    return false;
		}
	    }
	    return true;
	}else{
	    return true;
	}
    }

    private static boolean isWriteSettingsPermission(String permission){
	return Manifest.permission.WRITE_SETTINGS.equals(permission);
    }

    private static boolean containWriteSettingsPermission(String[] permissions){
	for (String s : permissions){
	    if (isWriteSettingsPermission(s)){
		return true;
	    }
	}
	return false;
    }

    private static boolean checkWriteSettingsResult(Context context, String permission, int permission_result){
	if (!isWriteSettingsPermission(permission)){
	    return false;
	}
	if (permission_result == PackageManager.PERMISSION_GRANTED ){
        return systemCanWrite(context);
	}else{
        return !systemCanWrite(context);
	}
    }


    /**
     * 鐢熸垚PermissionHandler鐨処D
     *
     * @return
     */
    private static String generateHandlerId() {
	Random rand = new Random(System.currentTimeMillis());
	long now = System.currentTimeMillis();
	String id = String.format("%x-%x", now, rand.nextLong());
	return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Window window = getWindow() ;
	WindowManager.LayoutParams lp = window.getAttributes();
	lp.alpha = 0.3f; //涓嶈兘鍏拷?锛屽惁浼氫細鍑虹幇onRequestPermissionsResult涓嶅洖锟�?
	window.setAttributes(lp);
	window.setBackgroundDrawable(new ColorDrawable(0));

	View dummyView = new View(this);
	dummyView.setBackgroundResource(android.R.color.transparent);
	setContentView(dummyView);
	final Intent intent = getIntent();
	String action = intent.getAction();
	mHandlerId = intent.getStringExtra(EXTRA_REQ_HANDLER_ID);
	if (ACTION_MANAGER_WRITE_SETTINGS.equals(action)) {
	    requestSystemWriteSettings(this);
	} else {
	    mGrantPermissions = intent.getStringArrayExtra(EXTRA_REQ_PERMISSIONS);
	    if (mGrantPermissions == null || mGrantPermissions.length == 0) {
		finish();
	    } else {
		Random rand = new Random();
		mReqCode = rand.nextInt() & 0x7fffffff;
		String[] req_permissions = getShouldShowRequestPermissionRationale(mGrantPermissions);
		if (req_permissions == null || req_permissions.length == 0) { // 涓嶉渶瑕佹樉绀烘巿鏉冿紝鐩存帴褰撳凡鎺堟潈
		    mGrantResults = new int[mGrantPermissions.length];
		    for (int k = 0; k < mGrantResults.length; k++) {
			mGrantResults[k] = PackageManager.PERMISSION_GRANTED;
		    }
		    finish();
		} else {
		    requestPermissionsCompat(req_permissions, mReqCode);
		}
	    }
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
	final Intent intent = getIntent();
//	String action = intent != null ? intent.getAction() : null;
	if (!mResumed){
	    mResumed = true;
	}else /*if (ACTION_MANAGER_WRITE_SETTINGS.equals(action))*/{
	    finish();
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    @Override
    public void finish() {
	final Intent intent = getIntent();
	if (ACTION_MANAGER_WRITE_SETTINGS.equals(intent.getAction())) {
	    Intent resultIntent = new Intent(ACTION_MANAGER_WRITE_SETTINGS);
	    resultIntent.setPackage(getPackageName());
	    resultIntent.putExtra(EXTRA_REQ_HANDLER_ID, mHandlerId);
	    sendBroadcast(resultIntent);
	} else {
	    String[] granted_permissions = null; // 宸叉巿鏉冪殑
	    String[] denied_permissions = null; // 琚嫆缁濈殑
	    if (mGrantResults == null && mGrantPermissions != null){
		mGrantResults = new int[mGrantPermissions.length];
		String s ;
		for (int k = 0; k < mGrantPermissions.length; k ++) {
		    s = mGrantPermissions[k];
		    if (isWriteSettingsPermission(s)){
			if (checkWriteSettingsResult(this, s, PackageManager.PERMISSION_GRANTED)){
			    mGrantResults[k] = PackageManager.PERMISSION_GRANTED;
			}else{
			    mGrantResults[k] = PackageManager.PERMISSION_DENIED ;
			}
		    }else{
			if (checkSelfPermissionCompat(this, s, PackageManager.PERMISSION_GRANTED)) {
			    mGrantResults[k] = PackageManager.PERMISSION_GRANTED;
			}else{
			    mGrantResults[k] = PackageManager.PERMISSION_DENIED ;
			}
		    }
		}
	    }
	    if (mGrantResults != null) {
		granted_permissions = getGrantPermissions(mGrantPermissions, mGrantResults,
			PackageManager.PERMISSION_GRANTED);
		denied_permissions = getGrantPermissions(mGrantPermissions, mGrantResults,
			PackageManager.PERMISSION_DENIED);
	    } else {
		denied_permissions = mGrantPermissions;
	    }
	    Intent resultIntent = new Intent(ACTION_PERMISSION_GRANT_RESULT);
	    resultIntent.setPackage(getPackageName());
	    if (granted_permissions != null) {
		resultIntent.putExtra(EXTRA_RET_PERMISSIONS_GRANTED, granted_permissions);
	    }
	    if (denied_permissions != null) {
		resultIntent.putExtra(EXTRA_RET_PERMISSIONS_DENIED, denied_permissions);
	    }
	    resultIntent.putExtra(EXTRA_REQ_HANDLER_ID, mHandlerId);
	    sendBroadcast(resultIntent);
	}
	super.finish();
    }

    private String[] getShouldShowRequestPermissionRationale(String[] permissions) {
	List<String> rets = new ArrayList<String>();
	for (String s : permissions) {
	    if (checkSelfPermissionCompat(this, s, PackageManager.PERMISSION_DENIED)) {
//		if (shouldShowRequestPermissionRationaleCompat(s)) {
		rets.add(s);
//		}
	    }
	}
	if (rets.size() > 0) {
	    String[] sa = new String[rets.size()];
	    rets.toArray(sa);
	    return sa;
	} else {
	    return null;
	}
    }

    /**
     * 杩欎釜鏂规硶鐢ㄤ簬鍒ゆ柇鏄惁鍕撅拷?鈥滀笉鍐嶆樉绀猴拷?锛屽湪permission琚玃ackageManager.PERMISSION_DENIED鏃讹紝鑻ヨ繑鍥瀟rue琛ㄧず鐢ㄦ埛涓诲姩鎷掔粷鐨勶紝
     * 鑻ヨ繑鍥瀎alse锛屽垯琛ㄧず鐢辩敤鎴蜂笂娆″嬀閫変簡鈥濅笉鍐嶆樉绀猴拷?锟�?锟斤拷璧风殑
     * @param context
     * @param permission
     * @return
     */
    public static boolean shouldShowRequestPermissionRationaleCompat(Context context, String permission) {
	// boolean ret = shouldShowRequestPermissionRationale(permission);
	Object obj = ReflectHelper.callMethod(context, "shouldShowRequestPermissionRationale",
		new Class<?>[] { String.class }, new Object[] { permission });
	if (obj == null){
	    PackageManager pm = context.getPackageManager();
	    obj = ReflectHelper.callMethod(pm, "shouldShowRequestPermissionRationale",
			new Class<?>[] { String.class }, new Object[] { permission });
	}
	if (obj != null && obj instanceof Boolean) {
	    return ((Boolean) obj).booleanValue();
	} else {
	    return false;
	}
    }

    /**
     * 鍒ゆ柇鎸囧畾鐨勬潈闄愭槸鍚﹀叏閮ㄨ鈥滀笉鍐嶆樉绀猴拷?锟�?锟斤拷缁濈殑
     * @param context
     * @param permissions
     * @return
     */
    public static boolean shouldAllPermissionAutoDenied(Context context, String[] permissions){
	if (permissions == null){
	    return false;
	}
	for (String p: permissions){
	    if (shouldShowRequestPermissionRationaleCompat(context, p)){
		return false;
	    }
	}
	return true;
    }

    private void requestPermissionsCompat(String[] permissions, int reqCode) {
	// this.requestPermissions(permissions, reqCode);
	ReflectHelper.callMethod(this, "requestPermissions", new Class<?>[] { String[].class, int.class },
		new Object[] { permissions, reqCode });
    }

    private static boolean checkSelfPermissionCompat(Context context, String permission, int permission_result) {
	// int ret= checkSelfPermission(permission);
	Object obj = ReflectHelper.callMethod(context, "checkSelfPermission", new Class<?>[] { String.class },
		new Object[] { permission });
	if (obj != null) {
	    if (obj instanceof Integer) {
		return ((Integer) obj).intValue() == permission_result;
	    }
	}
	return false;
    }

    private String[] getGrantPermissions(String[] permissions, int[] grant_results, int check_result) {
	ArrayList<String> rets = new ArrayList<String>();
	int k = 0;
	for (k = 0; k < grant_results.length; k++) {
	    if (isWriteSettingsPermission(permissions[k])){
		if(checkWriteSettingsResult(this, permissions[k], check_result)){
		    rets.add(permissions[k]);
		}
	    }else if (grant_results[k] == check_result) {
		if (k < permissions.length) {
		    rets.add(permissions[k]);
		}
	    }
	}
	if (rets.size() > 0) {
	    String[] sa = new String[rets.size()];
	    rets.toArray(sa);
	    return sa;
	} else {
	    return null;
	}
    }

    private static String[] getGrantPermissions(Context context, String[] permissions, int check_result) {
	ArrayList<String> rets = new ArrayList<String>();
	for (String s : permissions) {
	    if (isWriteSettingsPermission(s)){
		if (checkWriteSettingsResult(context, s, check_result)){
		    rets.add(s);
		}
	    }else{
		if (checkSelfPermissionCompat(context, s, check_result)) {
		    rets.add(s);
		}
	    }
	}
	if (rets.size() > 0) {
	    String[] sa = new String[rets.size()];
	    rets.toArray(sa);
	    return sa;
	} else {
	    return null;
	}
    }

    /**
     * 鏍煎紡鍖栨潈闄愪俊锟�?
     *
     * @param context
     * @param permissions
     * @return
     */
    public static String formatPermissionsInfo(Context context, List<String> permissions) {
	if (permissions == null){
	    return "";
	}
	String [] perms = new String[permissions.size()];
	permissions.toArray(perms);
	return formatPermissionsInfo(context, perms);
    }

    public static String formatPermissionsInfo(Context context, String[] permissions) {
	StringBuffer sb = new StringBuffer();
	PackageManager pm = context.getPackageManager();
	try {
	    for (int i = 0; i < permissions.length; i++) {
		String permName = permissions[i];
		PermissionInfo tmpPermInfo = pm.getPermissionInfo(permName, 0);
		sb.append(permName + "\n");
		sb.append(tmpPermInfo.loadLabel(pm).toString() + "\n");
		sb.append(tmpPermInfo.loadDescription(pm).toString() + "\n");
		sb.append("\n");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return sb.toString();
    }

    /**
     * 鏉冮檺鍥炶皟锛屼笉鑳借娣锋穯
     */
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	if (requestCode != mReqCode) {
	    Log.e(TAG, "onRequestPermissionsResult return, requestCode=" + requestCode + " not equal to " + mReqCode);
	} else {
	    try {
		mGrantResults = grantResults;
	    } catch (Exception e) {
		Log.e(TAG, "onRequestPermissionsResult call error, reason=" + e);
	    }
	}
	if (!isFinishing()) {
	    finish();
	}
    }

    public static void requestSystemWriteSettings(Context context) {
	Uri selfPackageUri = Uri.parse("package:" + context.getPackageName());
	Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS", selfPackageUri);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	context.startActivity(intent);
    }

    private static class PermissionGrantReceiver extends BroadcastReceiver implements Runnable {
	private Context mRegisterContext;
	private PermissionHandler mPermissionHandler;
	private String mHandlerId;

	private PermissionGrantReceiver(Context context, PermissionHandler handler, String handlerId) {
	    mRegisterContext = context;
	    mPermissionHandler = handler;
	    mHandlerId = handlerId;
	    if (context instanceof Activity){
		Application app = ((Activity)context).getApplication();
		app.registerActivityLifecycleCallbacks(new IntentReceiverLeakGuarder((Activity)context, this));
	    }
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (intent == null) {
		unRegisterMe(context);
		return;
	    }
	    String action = intent.getAction();
	    String handlerId = intent.getStringExtra(EXTRA_REQ_HANDLER_ID);
	    if (handlerId == null) { // 涓嶅彲鑳借繘鍒拌繖涓拷?锟�?
		Log.e(TAG, "onReceiver error, call unRegisterMe ");
		unRegisterMe(context);
	    }else if (mHandlerId.equals(handlerId)) {
		if (ACTION_MANAGER_WRITE_SETTINGS.equals(action)) {
//		    Handler handler = new Handler(mRegisterContext.getMainLooper());
//		    handler.post(new Runnable() {
//			@Override
//			public void run() {
//			    mPermissionHandler.onSystemWriteSettings(systemCanWrite(mRegisterContext));
//			}
//		    });
		} else if (ACTION_PERMISSION_GRANT_RESULT.equals(action)) {
		    final String[] granted_permissions = intent.getStringArrayExtra(EXTRA_RET_PERMISSIONS_GRANTED);
		    final String[] denied_permissions = intent.getStringArrayExtra(EXTRA_RET_PERMISSIONS_DENIED);
		    Handler handler = new Handler(mRegisterContext.getMainLooper());
		    handler.post(new Runnable() {
			@Override
			public void run() {
			  if( mPermissionHandler != null ) {
          mPermissionHandler.onPermissionsResult(granted_permissions, denied_permissions);
        }
			}
		    });
		}
		unRegisterMe(context);
 	    }
	}

	private void unRegisterMe(Context context) {
	    Handler handler = new Handler(context.getMainLooper());
	    handler.post(this);
	}

	@Override
	public void run() {
	    try {
		mRegisterContext.unregisterReceiver(this);
	    } catch (Exception e) {
	    }
	}
    }

    private static class IntentReceiverLeakGuarder implements ActivityLifecycleCallbacks{
	private Activity mActivity ;
	private BroadcastReceiver mReceiver ;
	private IntentReceiverLeakGuarder(Activity activity, BroadcastReceiver receiver){
	    mActivity = activity;
	    mReceiver = receiver;
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
	}

	@Override
	public void onActivityStarted(Activity activity) {
	}

	@Override
	public void onActivityResumed(Activity activity) {
	}

	@Override
	public void onActivityPaused(Activity activity) {
	}

	@Override
	public void onActivityStopped(Activity activity) {
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
	    if (activity == mActivity){
		try{
		    mActivity.unregisterReceiver(mReceiver);
		}catch(Exception e){
		}
		Application app = activity.getApplication();
		app.unregisterActivityLifecycleCallbacks(this);
	    }
	}

    }
}
