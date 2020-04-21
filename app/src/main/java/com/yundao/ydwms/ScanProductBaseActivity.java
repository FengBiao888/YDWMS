package com.yundao.ydwms;

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

import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class ScanProductBaseActivity extends ProductBaseActivity {

    boolean isHandsetTest = false ; //当前是不手持设备

    private Vibrator mVibrator; //打扫成功后的震动器
    private ScanManager mScanManager; //扫码manager
    private SoundPool soundpool = null;//打包成功后bee一声
    private int soundid; //声音文件id

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

    /**
     * 扫一维码功能的初始化
     */
    private void initScan() {

        mScanManager = new ScanManager();
        mScanManager.openScanner();

        mScanManager.switchOutputMode( 0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //扫一维码功能的初始化
        if( isHandsetTest ) {
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
        if( isHandsetTest ) {
            //反注册广播
            if (mScanManager != null) {
                mScanManager.stopDecode();
            }
            unregisterReceiver(mScanReceiver);
        }
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

}
