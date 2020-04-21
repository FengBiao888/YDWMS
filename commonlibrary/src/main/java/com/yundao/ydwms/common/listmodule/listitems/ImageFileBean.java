package com.yundao.ydwms.common.listmodule.listitems;

import android.view.View;

import java.io.Serializable;

/**
 * Created by yangdl on 2019/9/18.
 */

public class ImageFileBean implements Serializable{

  private static final long serialVersionUID = 8037775152738018146L;

  private String fileName ;
  private String fileNameSuffix ;
  private String fileNo;
  private String fileSize;
  private String fileUrl;
  private String id;
  private String file;

  private View.OnClickListener onClickListener ;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileNameSuffix() {
    return fileNameSuffix;
  }

  public void setFileNameSuffix(String fileNameSuffix) {
    this.fileNameSuffix = fileNameSuffix;
  }

  public String getFileNo() {
    return fileNo;
  }

  public void setFileNo(String fileNo) {
    this.fileNo = fileNo;
  }

  public String getFileSize() {
    return fileSize;
  }

  public void setFileSize(String fileSize) {
    this.fileSize = fileSize;
  }

  public String getFileUrl() {
    return fileUrl;
  }

  public void setFileUrl(String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public void setOnClickListener(View.OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof ImageFileBean ){
            return fileUrl.equals( ((ImageFileBean) obj).getFileUrl() ) ;
        }
        return false ;
    }
}
