package com.yundao.ydwms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.ProductArrayLogRequest;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.ProductStateEnums;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.SharedPreferenceUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public abstract class ScanProductBaseActivity extends ProductBaseActivity {

    public String SHARE_PREFERENCE_KEY ;//缓存的key

    private Vibrator mVibrator; //打扫成功后的震动器
    private ScanManager mScanManager; //扫码manager
    private SoundPool soundpool = null;//打包成功后bee一声
    private int soundid; //声音文件id

    protected boolean isInit;

    ProductQueryListener listener ;

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() { //扫码结果广播监听

        @Override
        public void onReceive(Context context, Intent intent) {

            soundpool.play(soundid, 1, 1, 0, 0, 1);
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            String barcodeStr = new String(barcode, 0, barcodelen);
//            if( foucusEditText != null && "仓位".equals( remark.getText().toString() )){
//                foucusEditText.setText( barcodeStr );
//                foucusEditText.clearFocus();
//                foucusEditText = null ;
//            }else{
                ProductionLogDto ProductionLogDto = new ProductionLogDto();
                ProductionLogDto.barCode = barcodeStr ;

                if( productInfos.contains( ProductionLogDto ) ){
                    ToastUtil.showShortToast( "该产品已在列表中" );
                    return ;
                }

                if( !barcodeHasSpecialCondition() ){ //子类实现判断对于结果是否有特殊情况操作
//                    barCode.setText( barcodeStr );
                    dealwithBarcode( barcodeStr );//子类实现方法，对于扫码结果如何操作
                }
//            }

        }

    };

    /**
     * 子类实现判断对于结果是否有特殊情况操作
     * @param barcodeStr
     */
    public abstract void dealwithBarcode(String barcodeStr);

    /**
     * 子类实现方法，对于扫码结果如何操作
     * @return
     */
    public abstract boolean barcodeHasSpecialCondition();

    /**
     * 设置顶部标题栏
     */
    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( ( (ScanTypeEnum)anEnum ).getCodeName() );

    }

    /**
     * 返回布局文件id，子类实现可对展示内容作修改
     * @return
     */
    @Override
    protected int getLayout() {
        return R.layout.activity_product_normal;
    }

    /**
     * 父类的初始化方法，这里初始化大部分设置，子类可重写方法作特殊处理
     * @param var1
     */
    @Override
    public void initView(Bundle var1) {
        super.initView(var1);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    protected void loadFromCache(ProductStateEnums... state) {
        Object object = SharedPreferenceUtil.getObject( SHARE_PREFERENCE_KEY );
        if( object != null ) {
            if (object instanceof ArrayList) {
                ArrayList<String> codesArray = (ArrayList<String>) object;
//            cachedBarcodes.addAll( codesArray );
//            String[] codes = codesArray.toArray(new String[codesArray.size()]);
                if( codesArray != null && codesArray.size() > 0 ) {
                    productionLog(getActivity(), true, codesArray, state);
                }
            }
        }
    }

    /**
     * 扫一维码功能的初始化
     */
    private void initScan() {
        if( mScanManager == null ) {
            mScanManager = new ScanManager();
        }
        mScanManager.openScanner();

        mScanManager.switchOutputMode( 0 );
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //扫一维码功能的初始化
        if( ! YDWMSApplication.getInstance().isPhoneTest() ) {
            initScan();
            IntentFilter filter = new IntentFilter();
            int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
            String[] value_buf = mScanManager.getParameterString(idbuf);
            if (value_buf != null && value_buf[0] != null && !value_buf[0].equals("")) {
                filter.addAction(value_buf[0]);
            } else {
                filter.addAction(SCAN_ACTION);
            }
            //注册接收广播
            registerReceiver(mScanReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if( ! YDWMSApplication.getInstance().isPhoneTest() ) {
            //反注册广播
            if (mScanManager != null) {
                mScanManager.stopDecode();
            }
            unregisterReceiver(mScanReceiver);
        }
    }

    /**
     * 产品信息
     * @param activity
     * @param showProgressDialog
     * @param code
     */
    public void productionLog(Activity activity, boolean showProgressDialog, String code, ProductStateEnums... state){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        Call<ProductQueryRespone> productQueryResponeCall = postRequestInterface.productionLog(code);
        productQueryResponeCall
                .enqueue(new BaseCallBack<ProductQueryRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<ProductQueryRespone> call, Response<ProductQueryRespone> response) {
                        super.onResponse(call, response);
                        ProductQueryRespone body = response.body();
                        if( body != null && response.code() == 200 ){
//                            int totalElements = body.totalElements;
                            ProductionLogDto[] content = body.content;
                            if(/* totalElements == content.length && */content.length > 0 ){
                                boolean containOutgoing = false ;
                                boolean containInComing = false ;

                                for( int i = 0 ; i < content.length ; i ++ ){
                                    ProductionLogDto info = content[i];
//                                    info.state = 1 ;
                                    if( info == null ) continue;
                                    for( int j = 0 ; j < state.length ; j ++ ) {
                                        if (state[j] == ProductStateEnums.INCOMING && info.productionState == 1) { //产品进仓
//                                        ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已进仓");
                                            containInComing = true;
                                        } else if (state[j] == ProductStateEnums.OUTGOING && info.productionState == 2) {
//                                        ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已出仓");
                                            containOutgoing = true;
                                        }
                                    }
                                    if( containInComing || containOutgoing ){
                                        continue;
                                    }

                                    deleteOperators.add( "delete" );
//                                    cachedBarcodes.add( code );
                                    productInfos.add( info );
                                    if( listener != null ){
                                        listener.onSuccessed( info );
                                    }
                                    if( productInfos.size() > 0 ) {
                                        if (!isInit) {
                                            pl_root.setAdapter(adapter);
                                            isInit = true;
                                        }
                                        adapter.notifyDataSetChanged();

                                        totalCount.setText("合计：" + productInfos.size() + "件");
                                        setProductionLogDto(info);
                                    }
                                }
                                if( containOutgoing && containInComing ){
                                    ToastUtil.showShortToast( "包含已进仓和已出仓的产品" );
                                }else if( containOutgoing ){
                                    ToastUtil.showShortToast( "包含已出仓的产品" );
                                }else if( containInComing ){
                                    ToastUtil.showShortToast( "包含已进仓的产品" );
                                }
                            }else{
                                ToastUtil.showShortToast( "不能识别该产品" );
                            }
                        }else if( response.code() == 400 ){
                            ToastUtil.showShortToast( "不能识别该产品" );
                        }else if( response.code() == 401 ){
                            ToastUtil.showShortToast( "登录过期，请重新登录" );
                            AvoidOnResult avoidOnResult = new AvoidOnResult( getActivity() );
                            Intent intent = new Intent( getActivity(), LoginActivity.class );
                            avoidOnResult.startForResult(intent, new AvoidOnResult.Callback() {
                                @Override
                                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                                    if( resultCode == Activity.RESULT_OK ){
                                        User user = YDWMSApplication.getInstance().getUser();
                                        if( user != null ){
                                            operator.setText( "操作员：" + user.username );
                                        }
                                    }
                                }
                            });
                        }else{
                            try {
                                ToastUtil.showShortToast( response.errorBody().string() );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 产品信息
     * @param activity
     * @param showProgressDialog
     * @param code
     */
    public void productionLog(Activity activity, boolean showProgressDialog, ArrayList<String> code, ProductStateEnums... state){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        ProductArrayLogRequest request = new ProductArrayLogRequest();
        request.barcodes = code ;

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        Call<ProductionLogDto[]> productQueryResponeCall = postRequestInterface.productionLog( request );
        productQueryResponeCall
                .enqueue(new BaseCallBack<ProductionLogDto[]>(activity, manager) {
                    @Override
                    public void onResponse(Call<ProductionLogDto[]> call, Response<ProductionLogDto[]> response) {
                        super.onResponse(call, response);
                        ProductionLogDto[] body = response.body();
                        if( body != null && response.code() == 200 ){
//                            int totalElements = body.totalElements;
                            ProductionLogDto[] content = body;
                            if(/* totalElements == content.length && */content.length > 0 ){
                                boolean containOutgoing = false ;
                                boolean containInComing = false ;
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    ProductionLogDto info = content[i];
//                                    info.state = 1 ;
//                                    if( info != null && info.state == 1 ){ //产品打包，如果是已打包
//                                        ToastUtil.showShortToast( "该产品已打包");
//                                        continue;
//                                    }
                                    if( info == null ) continue;
                                    for( int j = 0 ; j < state.length ; j ++ ) {
                                        if (state[j] == ProductStateEnums.INCOMING && info.productionState == 1) { //产品进仓
//                                        ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已进仓");
                                            containInComing = true;
                                        } else if (state[j] == ProductStateEnums.OUTGOING && info.productionState == 2) {
//                                        ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已出仓");
                                            containOutgoing = true;
                                        }
                                    }
                                    if( containInComing || containOutgoing ){
                                        continue;
                                    }

                                    deleteOperators.add( "delete" );
                                    productInfos.add( info );
                                    setProductionLogDto( info );
                                }
                                if( productInfos.size() > 0 ) {
                                    if (!isInit) {
                                        pl_root.setAdapter(adapter);
                                        isInit = true;
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                    totalCount.setText("合计：" + productInfos.size() + "件");
                                }
                                if( containOutgoing && containInComing ){
                                    ToastUtil.showShortToast( "包含已进仓和已出仓的产品" );
                                }else if( containOutgoing ){
                                    ToastUtil.showShortToast( "包含已出仓的产品" );
                                }else if( containInComing ){
                                    ToastUtil.showShortToast( "包含已进仓的产品" );
                                }

                            }else{
                                ToastUtil.showShortToast( "不能识别该产品" );
                            }
                        }else if( response.code() == 400 ){
                            ToastUtil.showShortToast( "不能识别该产品" );
                        }else if( response.code() == 401 ){
                            ToastUtil.showShortToast( "登录过期，请重新登录" );
                            AvoidOnResult avoidOnResult = new AvoidOnResult( getActivity() );
                            Intent intent = new Intent( getActivity(), LoginActivity.class );
                            avoidOnResult.startForResult(intent, new AvoidOnResult.Callback() {
                                @Override
                                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                                    if( resultCode == Activity.RESULT_OK ){
                                        User user = YDWMSApplication.getInstance().getUser();
                                        if( user != null ){
                                            operator.setText( "操作员：" + user.username );
                                        }
                                    }
                                }
                            });
                        }else{
                            try {
                                ToastUtil.showShortToast( response.errorBody().string() );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 根据产品列表，获取一维码集合
     * @return
     */
    public List<Long> genCodes(){
        List<Long> list = new ArrayList<>();
        for(int i = 0; i < productInfos.size() ; i ++ ){
            list.add( productInfos.get(i).id );
        }
        return list ;
    }

    /**
     * 根据产品列表，获取条码集合
     * @return
     */
    public List<String> genBarCodes(){
        List<String> list = new ArrayList<>();
        for(int i = 0; i < productInfos.size() ; i ++ ){
            list.add( productInfos.get(i).barCode );
        }
        return list ;
    }


    public void setListener(ProductQueryListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDestroy() {

//        List<String> codesArray = new ArrayList<>();
//        codesArray.addAll(Arrays.asList(codes));
//        System.out.println( "kdkdkdk " + cachedBarcodes);
        if( !TextUtils.isEmpty( SHARE_PREFERENCE_KEY ) ) {
            List<String> barcodes = genBarCodes();
            System.out.println( "kdkdkdk " + " SHARE_PREFERENCE_KEY: " + SHARE_PREFERENCE_KEY + barcodes);
            if( barcodes.size() > 0 ) {
                SharedPreferenceUtil.putObject(SHARE_PREFERENCE_KEY, barcodes );
            }else{
                SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
            }
        }
        super.onDestroy();
    }

    public interface ProductQueryListener{


        void onQueryFailed() ;

        void onSuccessed( ProductionLogDto productionLogDto );
    }

}
