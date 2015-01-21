package com.chipsguide.app.colorbluetoothlamp.v2.utils;
import android.content.Context;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class WrapImageLoader {
	private static WrapImageLoader loader;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private int imageRes;

	private WrapImageLoader(Context context) {
		this.imageRes = R.drawable.ic_launcher;
		init(context);
	}
	
	private WrapImageLoader(Context context,int imageRes) {
		this(context);
		if(imageRes > 0){
			this.imageRes = imageRes;
		}
	}
	
	public void setDefaultImage(int imageRes) {
		this.imageRes = imageRes;
	}

	private void init(Context context) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(imageRes)
				.showImageForEmptyUri(imageRes)
				.showImageOnFail(imageRes)
				.imageScaleType(ImageScaleType.EXACTLY)
				.cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(false)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}
	
	public static WrapImageLoader getInstance(Context context){
		if(loader == null){
			loader = new WrapImageLoader(context);
		}
		return loader;
	}
	
	public void displayImage(String uri,ImageView iv,int insampleSize,ImageLoadingListener listener) {
		Options o = options.getDecodingOptions();
		o.inSampleSize = insampleSize;
		imageLoader.displayImage(uri, iv, options,listener);
	}
}
