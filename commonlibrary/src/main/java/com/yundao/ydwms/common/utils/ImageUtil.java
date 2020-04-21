package com.yundao.ydwms.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageUtil {
	private ImageUtil() {
	}

	public static final String TAG = ImageUtil.class.getSimpleName();


	/*
	 * 进行图片缩放
	 */
	public static Bitmap scaleImageFile(File imageFile, int size) {
		if (imageFile == null || !imageFile.isFile() || !imageFile.exists()
				|| size <= 0) {
			return null;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();

		// 先指定原始大小
		options.inSampleSize = 1;
		// 只进行大小判断
		options.inJustDecodeBounds = true;
		// 调用此方法得到options得到图片的大小
		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
		// 获得缩放比例
		options.inSampleSize = getScaleSampleSize(options, size);
		// OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		// 根据options参数，减少所需要的内存
		Bitmap sourceBitmap = null;
		sourceBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),
				options);

		return sourceBitmap;
	}

	/*
	 * 进行图片缩放
	 */
	public static boolean scaleImageFile(File source, File dest, int size) {
		boolean isSuccess = false;
		if (source == null || !source.isFile() || !source.exists()
				|| dest == null || size <= 0) {
			return isSuccess;
		}

		Bitmap bitmap = scaleImageFile(source, size);
		if (bitmap == null) {
			return isSuccess;
		}

		FileOutputStream fos = null;
		try {
			if (!dest.exists()) {
				dest.createNewFile();
			}
			fos = new FileOutputStream(dest);
			bitmap.compress(getCompressFormat(dest), 70, fos);
			isSuccess = true;
			
			if (source.getAbsolutePath().equals( dest.getAbsolutePath())) {
				source.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		bitmap.recycle();
		return isSuccess;
	}

	/*
	 * 这个函数会对图片的大小进行判断，并得到合适的缩放比例，比如2即1/2,3即1/3
	 * 
	 * @target: 要缩放成的宽或高
	 * 
	 * @return: 缩放比例
	 */
	public static int getScaleSampleSize(BitmapFactory.Options options,
			int target) {
		if (options == null || target <= 0) {
			return 1;
		}

		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = Math.round(w / target);
		int candidateH = Math.round(h / target);
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;

		return candidate;
	}

	/*
	 * 图片处理成圆角
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final int minBound = Math.min(bitmap.getWidth(), bitmap.getHeight());
		final float roundPx = minBound * 4 / 50;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}


	/**
	 * 旋转图片
	 * 
	 * @param bitmap
	 *            图片
	 * @param degrees
	 *            角度
	 * @return 旋转后的图片Bitmap对象
	 */
	public static Bitmap rotate(Bitmap bitmap, int degrees) {
		if (degrees != 0 && bitmap != null) {
			Matrix matrix = new Matrix();
			matrix.setRotate(degrees, (float) bitmap.getWidth() / 2,
					(float) bitmap.getHeight() / 2);
			try {
				Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				if (bitmap != rotated) {
					bitmap.recycle();
					bitmap = rotated;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return bitmap;
	}

	/**
	 * 旋转图片文件
	 * 
	 * @param imageFile
	 *            图片文件
	 * @param degrees
	 *            角度
	 * @return 缩放并旋转后的图片文件
	 * @throws IOException
	 */
	public static boolean rotateImageFile(File imageFile, File dest, int degrees) {
		boolean isSuccess = false;
		if (imageFile == null || !imageFile.isFile() || !imageFile.exists()
				|| dest == null) {
			return isSuccess;
		}

//		Bitmap bitmap = scaleImageFile(imageFile, ROTATE_MAX_SIZE);
		//用最低的
		Bitmap bitmap = scaleImageFile(imageFile, ImageQuality.Low.getSize());

		Bitmap rotated = rotate(bitmap, degrees); // 翻转图片

		FileOutputStream fileOutputStream = null;
		try {
			if (!dest.exists()) {
				dest.createNewFile();
			}

			fileOutputStream = new FileOutputStream(dest);

			rotated.compress(getCompressFormat(dest), 70, fileOutputStream);

			isSuccess = true;
			if (imageFile.getAbsolutePath().equals( dest.getAbsolutePath())) {
				imageFile.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		bitmap.recycle();
		rotated.recycle();

		return isSuccess;
	}

	private static Bitmap.CompressFormat getCompressFormat(File file) {
		Bitmap.CompressFormat format = null;
		try {
			String fileExtension = FileUtil.getFileExtensionFromName(file
					.getName());
			format = Bitmap.CompressFormat.valueOf(fileExtension);
		} catch (Exception e) {
			format = Bitmap.CompressFormat.JPEG;
		}
		return format;
	}

	public static Uri getImageUri(String path) {
		return Uri.fromFile(new File(path));
	}

	public static void recycleAllImageView (ViewGroup parent){
		if (parent == null){
			return ;
		}
		final int N = parent.getChildCount();
		View child = null;
		for (int k = 0; k < N; k ++){
			child = parent.getChildAt(k);
			if (child instanceof ImageView){
//		AspLog.i(TAG, "recycleAllImageView child["+k +"]="+child);
				((ImageView)child).setImageResource(0); //若该ImageView为RecycledImageView的话，则会释放Bitmap
				child.setTag(null);
			}else if (child instanceof ViewGroup){
//		AspLog.i(TAG, "recycleAllImageView child["+k +"]="+child +",go ahead for its children");
				recycleAllImageView((ViewGroup)child);
			}
		}
	}

	public static void recycleImage(View v){
		if (v instanceof ImageView){
			Drawable dw = ((ImageView)v).getDrawable();
			if (dw != null && dw instanceof BitmapDrawable){
				Bitmap bmp = ((BitmapDrawable)dw).getBitmap();
				if (bmp != null){
					((ImageView)v).setImageResource(0);
//		    bmp.recycle(); //2013.12.9 Mic:直接调用可能会引起其他使用该图片绘制的视图引发异常
//		    ((ImageView)v).setImageDrawable(new ColorDrawable(0));
				}
			}
		}
	}

	static File compressImage(File imageFile, int reqWidth, int reqHeight, Bitmap.CompressFormat compressFormat, int quality, String destinationPath) throws IOException {
		FileOutputStream fileOutputStream = null;
		File file = new File(destinationPath).getParentFile();
		if (!file.exists()) {
			file.mkdirs();
		}
		if( imageFile.getPath().equals(destinationPath) ){
			return imageFile ;
		}
		try {
			fileOutputStream = new FileOutputStream(destinationPath);
			// write the compressed bitmap at the destination specified by destinationPath.
			decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(compressFormat, quality, fileOutputStream);
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		return new File(destinationPath);
	}

	static Bitmap decodeSampledBitmapFromFile(File imageFile, int reqWidth, int reqHeight) throws IOException {
		// First decode with inJustDecodeBounds=true to check dimensions
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

		//check the rotation of the image and display it properly
		ExifInterface exif;
		exif = new ExifInterface(imageFile.getAbsolutePath());
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
		Matrix matrix = new Matrix();
		if (orientation == 6) {
			matrix.postRotate(90);
		} else if (orientation == 3) {
			matrix.postRotate(180);
		} else if (orientation == 8) {
			matrix.postRotate(270);
		}
		scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		return scaledBitmap;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static boolean isContaintPostfixOfPic(String path) {
		List<String> params = new ArrayList<String>();
		params.add(".png");
		params.add(".jpg");
		params.add(".bmp");
		params.add(".jpeg");
		params.add(".gif");

		if (!TextUtils.isEmpty(path)) {
			for (int i = 0; i < params.size(); i++) {
				if (path.endsWith(params.get(i))) {
					return true;
				}
			}
		}

		return false;
	}
}
