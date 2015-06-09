package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class RenameDialog extends Dialog implements android.view.View.OnClickListener
{
	private EditText mEdittextTag;
	private ImageView mIamgeviewTagBtn;
	private TextView mBack;
	private TextView mCommit;
	Context context;
	private String Stringtag ="";
	private UpdateDialogClickListener dialogClickListener;
	
	public interface UpdateDialogClickListener
	{
		void ItemDialogClickListener();
	}
	
	public void setOnUpdateDialogClickListener(UpdateDialogClickListener dialogClickListener)
	{
	    this.dialogClickListener = dialogClickListener;
	}
	
	public RenameDialog(Context context,String title)
	{
		super(context);
		this.context = context;
		Stringtag = title;
	}

	public RenameDialog(Context context, int theme,String title)
	{
		super(context, theme);
		this.context = context;
		Stringtag = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_rename_layout);
		initView();
	}

	private void initView()
	{
		mEdittextTag = (EditText)this.findViewById(R.id.edittext_tag);
		mIamgeviewTagBtn = (ImageView)this.findViewById(R.id.iamgeview_tag_btn);
		mBack = (TextView)this.findViewById(R.id.mytextview_back);
		mCommit = (TextView)this.findViewById(R.id.mytextview_commit);
		mIamgeviewTagBtn.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mCommit.setOnClickListener(this);
		mEdittextTag.setText(Stringtag);
		if(Stringtag != null)
		{
			if(Stringtag.length() > 10)
			{
				mEdittextTag.setSelection(10);//把光标移到最后
			}else
			{
				mEdittextTag.setSelection(Stringtag.length());//把光标移到最后
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.iamgeview_tag_btn:
			mEdittextTag.setText("");
			break;
		case R.id.mytextview_commit:
			Stringtag = mEdittextTag.getText().toString();
			this.dismiss();
			dialogClickListener.ItemDialogClickListener();
			break;
		case R.id.mytextview_back:
			dismiss();
			break;
		default:
			break;
		}
	}
	
	public String getTag()
	{
		return Stringtag;
	}
}
