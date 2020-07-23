package com.yundao.ydwms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.WriterException;
import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.print.PrintJobMonitorService;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.Baling;
import com.yundao.ydwms.protocal.request.BalingRequest;
import com.yundao.ydwms.protocal.respone.BalingQueryRespone;
import com.yundao.ydwms.protocal.respone.BalingTotalQueryRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.ProductStateEnums;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.BitmapUtil;
import com.yundao.ydwms.util.DateFormatUtils;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.SharedPreferenceUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 产品打包Activity
 */
public class ProductPackagingActivity extends ScanProductBaseActivity {

    private int index = 0 ;
    private String[] codes = new String[]{ "15934283862841" };

    private boolean isPackaged = false ;
    public EditText barCode ; //条码
    public EditText material ; //料号
    public  EditText productName ; //品名
    public EditText specification ; //规格
    public  EditText weightSume ; //总净重
    public  EditText volumeSum ; //总卷数
    public Button printBtn ; //打印

    private BalingRequest resourse ;//打包的数据

    BigDecimal totalWeight = null;

    private Dialog declareDialog; //显示不同规格不能打包的对话框

    private WebView wv = null; //承载打印内容的webview
    private PrintManager mgr = null; //打印Manager
    PrintJob print ; //打印服务，轮询检测打印状态
    Dialog printProgress ;//打印的等待框
    private String printFilePath; //外部保存的生成打印内容的html文件
    private String printBarCode ; //打包生成的1维码。
    private int tempTime = 0 ;//轮循尝试次数
    private Handler printJobHandler = new Handler(){//轮循handler

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if( print != null ){
                System.out.println( "print_state:" + print.getInfo().getState() );
                String barcode = (String) msg.obj;
                if( print.isCancelled() || print.isFailed() ){ //取消或者失败，重新打印
                    if( printProgress != null && printProgress.isShowing() ){
                        printProgress.dismiss();
                    }
                    DialogUtil.showDeclareDialog( getActivity(), "用户取消或打印失败\n是否需要重新打印", v -> {
                        printReport( barcode );
                    } ).show();
                }else if( print.isCompleted() ){ //打印完成
                    if( printProgress != null && printProgress.isShowing() ){
                        printProgress.dismiss();
                    }
                    print = null ;
                    DialogUtil.showDeclareDialog( getActivity(), "打印成功", false, null).show();
                    productInfos.clear();
                    adapter.notifyDataSetChanged();
                    SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
                }else{ //其他状态继续轮循
                    if( tempTime ++ < 100 ) {
                        Message message = printJobHandler.obtainMessage();
                        message.obj = msg.obj;
                        printJobHandler.sendMessageDelayed(message, 1000);
                    }else{
                        if( printProgress != null && printProgress.isShowing() ){
                            printProgress.dismiss();
                        }
                        print.cancel();
                    }
                }
            }
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_product_packaging ;
    }

    @Override
    public void initView(Bundle var1) {
        super.initView(var1);
        SHARE_PREFERENCE_KEY = "PRODUCT_PACKAGING_KEY" ;
        barCode = findViewById( R.id.bar_code_value ); //条码
        material = findViewById( R.id.material_value ); //料号
        productName = findViewById( R.id.product_name_value ); //品名
        specification = findViewById( R.id.specification_value) ; //规格
        weightSume = findViewById( R.id.weight_sum_value ); //总净重
        volumeSum = findViewById( R.id.volume_sum_value ); //总卷数
        printBtn = findViewById( R.id.print ); //打印

        printFilePath = Environment.getExternalStorageDirectory() + "/print.html";
        mgr = (PrintManager) getSystemService(PRINT_SERVICE);
        submit.setOnClickListener( v->{

            if( YDWMSApplication.getInstance().isUseLocalData() ) {
                if (index < codes.length) {
                    dealwithBarcode(codes[index]);
                    index++;
                    return;
                }
            }


            if( productInfos.size() == 0 ){
                ToastUtil.showShortToast( "请先扫条形码" );
                return ;
            }

//            if( !TextUtils.isEmpty( printBarCode ) ){
//                DialogUtil.showDeclareDialog( getActivity(), "产品已打包，是否打印打包数据", v1 -> {
//                    printReport( printBarCode );
//                } ).show();
//            }else {
                DialogUtil.showDeclareDialog(getActivity(), "确定是否打包", v1 -> {
                    //组装打包接口的数据
                    if( resourse == null ) {
                        resourse = new BalingRequest();
                        resourse.baling = new Baling();
                    }
                    long timeMillis = System.currentTimeMillis();
                    if( TextUtils.isEmpty( resourse.baling.barCode ) ){//没有打包过，才要生成打包码
                        resourse.baling.barCode = timeMillis + "2"; //时间戳加上2
                    }
                    resourse.baling.balingDate = timeMillis ;
                    BigDecimal totalWeight = null;
                    BigDecimal totalLength = null;
                    List<Long> ids = new ArrayList<>();
                    for (int i = 0; i < productInfos.size(); i++) {
                        ProductionLogDto productInfo = productInfos.get(i);
                        if (TextUtils.isEmpty(resourse.baling.trayNumber) && !TextUtils.isEmpty(productInfo.trayNumber)) {
                            resourse.baling.trayNumber = productInfo.trayNumber;
                        }
//                        if (TextUtils.isEmpty(resourse.customerId) && !TextUtils.isEmpty(productInfo.customerId)) {
                        resourse.baling.customerId = productInfo.customerId;
//                        }
                        if (TextUtils.isEmpty(resourse.baling.customerAbbreviation) && !TextUtils.isEmpty(productInfo.customerAbbreviation)) {
                            resourse.baling.customerAbbreviation = productInfo.customerAbbreviation;
                        }
                        if (TextUtils.isEmpty(resourse.baling.productModel) && !TextUtils.isEmpty(productInfo.productModel)) {
                            resourse.baling.productModel = productInfo.productModel;
                        }

                        resourse.baling.productId = productInfo.productId;

                        if (TextUtils.isEmpty(resourse.baling.productCode) && !TextUtils.isEmpty(productInfo.productCode)) {
                            resourse.baling.productCode = productInfo.productCode;
                        }

                        if (TextUtils.isEmpty(resourse.baling.productType) && !TextUtils.isEmpty(productInfo.productType)) {
                            resourse.baling.productType = productInfo.productType;
                        }

                        if (productInfo.netWeight != null) {//重量相加
                            if (totalWeight == null) {
                                totalWeight = productInfo.netWeight;
                            } else {
                                totalWeight = totalWeight.add(productInfo.netWeight).setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                        }
                        if (productInfo.length != null) {//米数相加
                            if (totalLength == null) {
                                totalLength = productInfo.length;
                            } else {
                                totalLength = totalLength.add(productInfo.length).setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                        }

                        ids.add( productInfo.id );
                    }
                    resourse.baling.meter = totalLength ;
                    resourse.baling.amount = productInfos.size()  ;
                    resourse.baling.netWeight = totalWeight;
                    resourse.ids = ids;

                    baling(getActivity(), true, resourse.baling);

                }).show();
//            }
        } );

        printBtn.setOnClickListener( v -> {
            if( resourse == null || resourse.baling == null ){
                ToastUtil.showShortToast( "请先上传打包数据" );
                return ;
            }
            String[] array = new String[]{"明细", "汇总"};
            DialogUtil.showTypeDialog(getActivity(), "请选择", array, new DialogUtil.OnItemSelectListener() {
                @Override
                public void onItemSelect(Dialog dialog, String type, int position) {
                    dialog.cancel();
                    boolean sum = false ;
                    if( position == 0 ){
                        sum = false ;
                    }else if( position == 1 ){
                        sum = true ;
                    }

                    writeHtml( resourse.baling, sum );
                    printReport( resourse.baling.barCode );
                    printBarCode = resourse.baling.barCode ;
                }
            });


        } );

        barCode.setOnClickListener(v -> DialogUtil.showInputDialog(getActivity(), barCode.getText().toString(), (dialog, type, position) -> {
            barCode.setText( type );
            dealwithBarcode( type );
            dialog.dismiss();
        }));

        loadFromCache(ProductStateEnums.NONE);
    }

    @Override
    protected void setProductionLogDto(ProductionLogDto productInfo) {

        totalWeight = null ;
        if( productInfo != null ) {
            barCode.setText(productInfo.barCode);
            material.setText(productInfo.productModel);
            productName.setText(productInfo.productName);
            specification.setText(productInfo.productModel);
        }
        if( productInfos.size() > 0 ) {
            volumeSum.setText(productInfos.size() + "");

            for (int i = 0; i < productInfos.size(); i++) {
                ProductionLogDto info = productInfos.get(i);
//            firstInfo.volume = ;
                if (info.netWeight != null) {//重量相加
                    if (totalWeight == null) {
                        totalWeight = info.netWeight;
                    } else {
                        totalWeight = totalWeight.add(info.netWeight).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                }
            }
            if( totalWeight != null ) {
                weightSume.setText(totalWeight.toString());
            }
        }else{
            barCode.setText( "" );
            material.setText( "" );
            productName.setText( "" );
            specification.setText( "" );
            volumeSum.setText( "" );
            weightSume.setText( "" );
        }
    }

    @Override
    protected void clearProductionLogDto() {
//        barCode.setText( "" );
//        material.setText( "" );
//        productName.setText( "" );
//        specification.setText( "" );
//        volumeSum.setText( "" );
//        weightSume.setText( "" );
        setProductionLogDto( null );

    }

    @Override
    public void dealwithBarcode(String barcodeStr) {

        if( barcodeStr.endsWith("2") ){
//            productInfos.clear();
//            clearProductionLogDto();
            balingProductionLog( getActivity(), true, barcodeStr, ProductStateEnums.OUTGOING );
        } else {

            ProductionLogDto ProductionLogDto = new ProductionLogDto();
            ProductionLogDto.barCode = barcodeStr ;

            if( productInfos.contains( ProductionLogDto ) ){
                ToastUtil.showShortToast( "该产品已在列表中" );
                return ;
            }

            productionLog(getActivity(), true, barcodeStr, ProductStateEnums.OUTGOING );
        }
    }

    @Override
    public boolean barcodeHasSpecialCondition() {
        return false;
    }

    /**
     * 产品信息
     * @param activity
     * @param showProgressDialog
     * @param code
     */
    @Override
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
                            ProductionLogDto[] content = body.content ;
                            if(content.length > 0 ){
                                boolean containOutgoing = false ;
                                boolean containPackaged = false ;
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    ProductionLogDto info = content[i];
                                    if( info == null ) continue;
                                    if( info.balingId != 0 ){ //产品打包，如果是已打包
                                        containPackaged = true ;
//                                        ToastUtil.showShortToast( "该产品已打包");
                                        continue;
                                    }
                                    for( int j = 0 ; j < state.length ; j ++ ) {
                                        if (state[j] == ProductStateEnums.OUTGOING && info.productionState == 2) {
//                                        ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已出仓");
                                            containOutgoing = true;
                                        }
                                    }
                                    if( containOutgoing ){
                                        continue;
                                    }

                                    if( "半成品".equals( info.productType ) ){
                                        DialogUtil.showDeclareDialog(getActivity(), "半成品不能打包", false, "我知道了", null).show();
                                        continue;
                                    }else if( productInfos.size() > 0 && !productInfos.get(0).isSameOrderId( info ) ){//产品类型不同
                                        declareDialog = DialogUtil.showDeclareDialog(getActivity(), "销售单号不相同，不能同时打包", false, "我知道了", null);
                                        declareDialog.show();
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
                                if( containOutgoing && containPackaged ){
                                    ToastUtil.showShortToast( "包含已打包和已出仓的产品" );
                                }else if( containOutgoing ){
                                    ToastUtil.showShortToast( "包含已出仓的产品" );
                                }else if( containPackaged ){
                                    ToastUtil.showShortToast( "包含已打包的产品" );
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
     * 打包的产品信息
     * @param activity
     * @param showProgressDialog
     * @param code
     */
    public void balingProductionLog(Activity activity, boolean showProgressDialog, String code, ProductStateEnums state){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        Call<BalingQueryRespone> productQueryResponeCall = postRequestInterface.balingProductionLog(code);
        productQueryResponeCall
                .enqueue(new BaseCallBack<BalingQueryRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BalingQueryRespone> call, Response<BalingQueryRespone> response) {
                        super.onResponse(call, response);
                        BalingQueryRespone body = response.body();
                        if( body != null && response.code() == 200 ){
                            ProductionLogDto[] content = body.productionLogs ;

                            resourse = new BalingRequest();
                            resourse.baling = new Baling();
                            resourse.baling = body.baling ;
                            boolean containPackaged = false ;
                            boolean containOutgoing = false ;
                            if(content.length > 0 ){
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    ProductionLogDto info = content[i];
                                    if( info == null ) continue;
                                    if( info.balingId != 0 ){ //产品打包，如果是已打包
//                                        ToastUtil.showShortToast( "该产品已打包");
                                        containPackaged = true ;
                                        continue;
                                    }
                                    if(state == ProductStateEnums.OUTGOING && info.productionState == 2 ){
//                                        ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已出仓");
                                        containOutgoing = true ;
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
                                if( containOutgoing && containPackaged ){
                                    ToastUtil.showShortToast( "包含已打包和已出仓的产品" );
                                }else if( containOutgoing ){
                                    ToastUtil.showShortToast( "包含已出仓的产品" );
                                }else if( containPackaged ){
                                    ToastUtil.showShortToast( "包含已打包的产品" );
                                }
                            }else{
                                ToastUtil.showShortToast( "不能识别该产品" );
                            }
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
                        }else if( response.code() == 400 ){
                            try {
                                ToastUtil.showShortToast( response.errorBody().string() );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            ToastUtil.showShortToast( "错误码:" + response.code()+ ",不能识别该产品" );
                        }
                    }
                });
    }

    /**
     * 产品打包接口
     * @param activity
     * @param showProgressDialog
     * @param vo
     */
    public void baling(Activity activity, boolean showProgressDialog, Baling vo){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.baling( resourse )
                .enqueue(new BaseCallBack<Baling>(activity, manager) {
                    @Override
                    public void onResponse(Call<Baling> call, Response<Baling> response) {
                        super.onResponse(call, response);
                        if( response.code() == 201 || response.code() == 204 ){
                            ToastUtil.showShortToast( "打包成功" );
//                            barCode.setText( vo.barCode );
                            isPackaged = true ;
//                            SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
                            printBarCode = vo.barCode ;
                            if( response.body() != null ) {
                                resourse.baling = response.body();
                            }
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

    private void printReport(String barcodeStr) {
        WebView print = prepPrintWebView( "packaging", barcodeStr );
        print.loadUrl("file://" + printFilePath);
    }

    private void writeHtml(Baling vo, boolean sum){

        File dataFile = new File(Environment.getExternalStorageDirectory(), "print.html");
        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(dataFile);
            fos.write( ( sum ? htmltextSum(vo) : htmltext( vo ) ).getBytes( "utf-8") );
            fos.flush();
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String htmltext(Baling vo){
        StringBuffer buffer = new StringBuffer( );
        buffer.append("<style type=\"text/css\">\n" +
                "    table{ border-collapse: collapse; }\n" +
                "    table,table tr td { border:1px solid #000; }\n" +
                "    table tr td{ padding: 5px 10px; }\n" +
                "    .left { width: 50%; height: 40px; float: left; text-align:left; }\n" +
                "    .right { width: 50%; height: 40px; float: right; text-align:right; }\n" +
                "    .center { margin: auto; }\n" +
                "</style>\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <script type=\"text/javascript\">\n" +
                "\t\tfunction onSaveCallback(src){ document.getElementById(\"test\").src = src; }\n" +
                "\t</script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>客户名：").append( vo.customerAbbreviation ).append("</h1>\n" +
                "<div id=\"middle\">\n" +
                "    <div class=\"left\"><text style=\"font-size:20px\">托盘号：").append( vo.trayNumber ).append("</text> </div>\n" +
                "    <div class=\"right\"><text style=\"font-size:20px\">日 期：").append(DateFormatUtils.long2Str( vo.balingDate, false ) ).append("</text></div>\n" +
                "</div>\n" +
                "<table width=\"100%\" border=\"1\">\n" +
                "    <tr>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">规格</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">编号</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">重量</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">米数</text></th>\n" +
                "    </tr>\n");
                int numberSum = vo.list.size() ;
                for( int i = 0 ; i < vo.list.size() ; i ++ ){
                    ProductionLogDto productInfo = vo.list.get(i);
                    buffer.append( "    <tr>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.productModel ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.volume ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.netWeight == null ? "" : productInfo.netWeight.toString() ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.length ).append("</text></th>\n" ) ;
                    buffer.append("    </tr>\n");
                }
                buffer.append( "    <tr>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">合计</text></th>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( numberSum ).append("件").append("</text></th>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( vo.netWeight == null ? "" : vo.netWeight.toString() ).append("KG").append("</text></th>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( vo.meter ).append("M").append("</text></th>\n" )
                      .append( "    </tr>\n");
                buffer.append("</table>\n" +
                "<div style=\"margin-top:5px\" align=\"center\" ><img id=\"test\" src=\"\" onclick=\"\"/></div>\n" +
                "<div style=\"margin-top:5px\" align=\"center\">").append( vo.barCode ).append("</div>\n" +
                "</body>\n" +
                "</html>").append("\n");
        return buffer.toString() ;
    }

    public String htmltextSum(Baling vo){
        StringBuffer buffer = new StringBuffer( );
        buffer.append("<style type=\"text/css\">\n" +
                "    table{ border-collapse: collapse; }\n" +
                "    table,table tr td { border:1px solid #000; }\n" +
                "    table tr td{ padding: 5px 10px; }\n" +
                "    .left { width: 50%; height: 40px; float: left; text-align:left; }\n" +
                "    .right { width: 50%; height: 40px; float: right; text-align:right; }\n" +
                "    .center { margin: auto; }\n" +
                "</style>\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <script type=\"text/javascript\">\n" +
                "\t\tfunction onSaveCallback(src){ document.getElementById(\"test\").src = src; }\n" +
                "\t</script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>客户名：").append( vo.customerAbbreviation ).append("</h1>\n" +
                "<div id=\"middle\">\n" +
                "    <div class=\"left\"><text style=\"font-size:20px\">托盘号：").append( vo.trayNumber ).append("</text> </div>\n" +
                "    <div class=\"right\"><text style=\"font-size:20px\">日 期：").append(DateFormatUtils.long2Str( vo.balingDate, false ) ).append("</text></div>\n" +
                "</div>\n" +
                "<table width=\"100%\" border=\"1\">\n" +
                "    <tr>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">规格</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">件数</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">重量</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">米数</text></th>\n" +
                "    </tr>\n");

        int numberSum = 0 ;
        for( int i = 0 ; i < vo.modelList.size() ; i ++ ){
            Baling.Model productInfo = vo.modelList.get(i);
            buffer.append( "    <tr>\n" )
                    .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.productModel ).append("</text></th>\n" )
                    .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.number ).append("</text></th>\n" )
                    .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.netWeight == null ? "" : productInfo.netWeight.toString() ).append("</text></th>\n" )
                    .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.length ).append("</text></th>\n" ) ;
            buffer.append("    </tr>\n");
            numberSum += productInfo.number ;
        }
        buffer.append( "    <tr>\n" )
                .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">合计</text></th>\n" )
                .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( numberSum ).append("件").append("</text></th>\n" )
                .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( vo.netWeight == null ? "" : vo.netWeight.toString() ).append("KG").append("</text></th>\n" )
                .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( vo.meter ).append("M").append("</text></th>\n" )
                .append( "    </tr>\n");
        buffer.append("</table>\n" +
                "<div style=\"margin-top:5px\" align=\"center\" ><img id=\"test\" src=\"\" onclick=\"\"/></div>\n" +
                "<div style=\"margin-top:5px\" align=\"center\">").append( vo.barCode ).append("</div>\n" +
                "</body>\n" +
                "</html>").append("\n");
        return buffer.toString() ;
    }

    private WebView prepPrintWebView(final String name, String barcodeStr) {
        WebView result = getWebView();

        result.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if( url.equals( "file://" + printFilePath )  ) {
                    try {
                        //生成1维码，显示到网页中。
                        String url1 = "javascript:onSaveCallback('data:image/png;base64," + BitmapUtil.bitmaptoString(BitmapUtil.CreateOneDCode(barcodeStr)) + "')";
                        view.loadUrl(url1);
                        print = print(name, view.createPrintDocumentAdapter(),
                                new PrintAttributes.Builder().build());
                        printProgress = DialogUtil.getProgressDialog( getActivity(), "正在打印，请稍候") ;
                        printProgress.setCancelable( false );
                        printProgress.show();
                        Message msg = printJobHandler.obtainMessage();
                        msg.obj = barcodeStr ;
                        printJobHandler.sendMessageDelayed(msg, 1000);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return (result);
    }

    private WebView getWebView() {
        if (wv == null) {
            wv = new WebView(this);
            WebSettings settings = wv.getSettings();
            //支持JavaScript
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(false); //支持通过JS打开新窗口
        }
        return (wv);
    }

    private PrintJob print(String name, PrintDocumentAdapter adapter,
                           PrintAttributes attrs) {
        startService(new Intent(this, PrintJobMonitorService.class));
        return (mgr.print(name, adapter, attrs));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( isPackaged ){
            SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
        }
    }
}
