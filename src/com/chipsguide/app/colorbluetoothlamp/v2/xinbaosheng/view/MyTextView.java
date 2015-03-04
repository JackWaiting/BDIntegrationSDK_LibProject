package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

	public MyTextView(Context context){
		super(context);
	}
	
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
	}
}
