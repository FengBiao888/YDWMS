package com.nf.android.common.utils;

public enum ImageQuality {
	Un_Disposal(-1), // 使用原始图片,不进行处理
	High(520), // 宽或高最大520px,取图大小在0 ~ 1040之间 ---参考ipad平板1024*764
	Middle(430), // 宽或高最大430px,取图大小在0 ~ 860之间 ---参考480*854手机 2.80--300多kb
    Low(700), //宽或高最大330px,取图大小在0 ~ 660之间, 按相机 ---参考320*640手机 2.40mb->75kb

//	Low(365), // 宽或高最大330px,取图大小在0 ~ 660之间, 按相机 ---参考320*640手机 
////				// 由我改成了370

	Adaptive_Net(0); // 自适应网络

	ImageQuality(int size) {
		this.size = size;
	}

	private int size;

	public int getSize() {
		return size;
	}
}
