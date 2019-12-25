package com.nf.android.common.listmodule.listitems;

import android.view.View;

import java.io.Serializable;

/**
 * Created by liangjianhua on 2018/5/17.
 */

public class ExtraFileBean implements Serializable{
    private static final long serialVersionUID = 766557613485523194L;

    private String id ;//用于服务器返回id.
    private String fileName ;
    private String filesize ;
    private String url ;
    private String fileNo;
    private String origianFilePath;
    private View.OnClickListener onClickListener ;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public String getOrigianFilePath() {
        return origianFilePath;
    }

    public void setOrigianFilePath(String origianFilePath) {
        this.origianFilePath = origianFilePath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof ExtraFileBean ){
            return url.equals( ((ExtraFileBean) obj).getUrl() ) ;
        }
        return false ;
    }
}
