package com.yundao.ydwms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

import com.google.zxing.WriterException;
import com.nf.android.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.print.PrintJobMonitorService;
import com.yundao.ydwms.protocal.ProductInfo;
import com.yundao.ydwms.protocal.request.PackeResourse;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.BitmapUtil;
import com.yundao.ydwms.util.DateFormatUtils;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 产品打包Activity
 */
public class ProductPackagingActivity extends ProductBaseActivity {

    private boolean isInit = false ; //界面是否初始化

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
                }else{ //其他状态继续轮循
                    if( tempTime ++ < 100 ) {
                        Message message = printJobHandler.obtainMessage();
                        message.obj = msg.obj;
                        printJobHandler.sendMessageDelayed(message, 1000);
                    }else{
                        print.cancel();
                    }
                }
            }
        }
    };


    @Override
    public void initView(Bundle var1) {
        super.initView(var1);
        printFilePath = Environment.getExternalStorageDirectory() + "/print.html";
        mgr = (PrintManager) getSystemService(PRINT_SERVICE);
        submit.setOnClickListener( v->{
            if( productInfos.size() == 0 ){
                ToastUtil.showShortToast( "请先扫条形码" );
                return ;
            }
            if( !TextUtils.isEmpty( printBarCode ) ){
                DialogUtil.showDeclareDialog( getActivity(), "产品已打包，是否打印打包数据", v1 -> {
                    printReport( printBarCode );
                } ).show();
            }else {
                DialogUtil.showDeclareDialog(getActivity(), "确定是否打包", v1 -> {
                    //组装打包接口的数据
                    PackeResourse resourse = new PackeResourse();
                    long timeMillis = System.currentTimeMillis();
                    resourse.barCode = timeMillis + "2"; //时间戳加上2
                    resourse.dateline = timeMillis ;
                    int totalLength = 0;
                    BigDecimal totalWeight = null;
                    for (int i = 0; i < productInfos.size(); i++) {
                        ProductInfo productInfo = productInfos.get(i);
                        if (TextUtils.isEmpty(resourse.trayNumber) && !TextUtils.isEmpty(productInfo.trayNumber)) {
                            resourse.trayNumber = productInfo.trayNumber;
                        }
                        if (TextUtils.isEmpty(resourse.customerId) && !TextUtils.isEmpty(productInfo.customerId)) {
                            resourse.customerId = productInfo.customerId;
                        }
                        if (TextUtils.isEmpty(resourse.customerName) && !TextUtils.isEmpty(productInfo.customerAbbreviation)) {
                            resourse.customerName = productInfo.customerAbbreviation;
                        }
                        if (TextUtils.isEmpty(resourse.materielCode) && !TextUtils.isEmpty(productInfo.materielCode)) {
                            resourse.materielCode = productInfo.materielCode;
                        }
                        if (TextUtils.isEmpty(resourse.materielModel) && !TextUtils.isEmpty(productInfo.materielModel)) {
                            resourse.materielModel = productInfo.materielModel;
                        }
                        if (TextUtils.isEmpty(resourse.materielName) && !TextUtils.isEmpty(productInfo.materielName)) {
                            resourse.materielName = productInfo.materielName;
                        }
                        totalLength += productInfo.length; //米数相加

                        if (productInfo.netWeight != null) {//重量相加
                            if (totalWeight == null) {
                                totalWeight = productInfo.netWeight;
                            } else {
                                totalWeight.add(productInfo.netWeight).setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                        }
                    }
                    resourse.meter = totalLength + "";
                    resourse.number = productInfos.size();
                    resourse.netWeight = totalWeight;
                    resourse.productionLogs = productInfos;
                    baling(getActivity(), true, resourse);

                }).show();
            }
        } );

        submit.setText( "产品打包" );
    }

    @Override
    public void dealwithBarcode(String barcodeStr) {
        productionLog( getActivity(), true, barcodeStr );
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
    public void productionLog(Activity activity, boolean showProgressDialog, String code){

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
                            ProductInfo[] content = body.content ;
                            if(content.length > 0 ){
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    ProductInfo info = content[i];
                                    if( info.state == 1 ){ //产品打包，如果是已打包
                                        ToastUtil.showShortToast( "该产品已打包");
                                        barCode.setText( "" );
                                        continue;
                                    }
                                    if( "半成口".equals( info.materielType ) ){
                                        DialogUtil.showDeclareDialog(getActivity(), "半成品不能打包", false, "我知道了", null).show();
                                        barCode.setText( "" );
                                        continue;
                                    }else if( productInfos.size() > 0 && !productInfos.get(0).isSameType( info ) ){//产品类型不同
                                        declareDialog = DialogUtil.showDeclareDialog(getActivity(), "不是同一个产品规格不可以一起打包", false, "我知道了", null);

                                        declareDialog.show();
                                        barCode.setText( "" );
                                        continue;
                                    }

                                    deleteOperators.add( "delete" );
                                    productInfos.add( info );
                                    if (!isInit) {
                                        pl_root.setAdapter(adapter);
                                        isInit = true;
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                    totalCount.setText("合计：" + productInfos.size() + "件");
                                    setProductInfo( info );
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
    public void baling(Activity activity, boolean showProgressDialog, PackeResourse vo){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.baling( vo )
                .enqueue(new BaseCallBack<PackeResourse>(activity, manager) {
                    @Override
                    public void onResponse(Call<PackeResourse> call, Response<PackeResourse> response) {
                        super.onResponse(call, response);
                        if( response.code() == 201 ){
                            ToastUtil.showShortToast( "打包成功" );
                            writeHtml( vo );
                            printReport( vo.barCode );
                            printBarCode = vo.barCode ;
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
                        }
                    }
                });

    }

    private void printReport(String barcodeStr) {
        WebView print = prepPrintWebView( "packaging", barcodeStr );
        print.loadUrl("file://" + printFilePath);
    }

    private void writeHtml(PackeResourse vo){

        File dataFile = new File(Environment.getExternalStorageDirectory(), "print.html");
        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(dataFile);
            fos.write( htmltext( vo ).getBytes( "utf-8") );
            fos.flush();
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String htmltext(PackeResourse vo){
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
                "<h1>客户名：").append( vo.customerName ).append("</h1>\n" +
                "<div id=\"middle\">\n" +
                "    <div class=\"left\"><text style=\"font-size:20px\">托盘号：").append( vo.trayNumber ).append("</text> </div>\n" +
                "    <div class=\"right\"><text style=\"font-size:20px\">日 期：").append(DateFormatUtils.long2Str( vo.dateline, false ) ).append("</text></div>\n" +
                "</div>\n" +
                "<table width=\"100%\" border=\"1\">\n" +
                "    <tr>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">规格</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">编号</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">重量</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">米数</text></th>\n" +
                "    </tr>\n");
                for( int i = 0 ; i < vo.productionLogs.size() ; i ++ ){
                    ProductInfo productInfo = vo.productionLogs.get(i);
                    buffer.append( "    <tr>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.materielModel ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.materielCode ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.netWeight == null ? "" : productInfo.netWeight.toString() ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.length ).append("</text></th>\n" ) ;
                    buffer.append("    </tr>\n");
                }
                buffer.append( "    <tr>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">合计</text></th>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( vo.productionLogs.size() ).append("件").append("</text></th>\n" )
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
}
