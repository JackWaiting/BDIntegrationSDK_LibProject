package com.chipsguide.app.colorbluetoothlamp.v2.brunton.view;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.antistatic.spinnerwheel.AbstractWheel;
import com.antistatic.spinnerwheel.OnWheelChangedListener;
import com.antistatic.spinnerwheel.OnWheelClickedListener;
import com.antistatic.spinnerwheel.OnWheelScrollListener;
import com.antistatic.spinnerwheel.adapter.NumericWheelAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;

public class MyTimePickerView extends FrameLayout {

	private boolean timeScrolled;
	private AbstractWheel hours, mins;
	
	public MyTimePickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MyTimePickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyTimePickerView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.layout_time_picker, this);
		hours = (AbstractWheel) findViewById(R.id.hour);
        hours.setViewAdapter(new NumericWheelAdapter(context, 0, 23, "%02d"));
        hours.setCyclic(true);

        mins = (AbstractWheel) findViewById(R.id.mins);
        mins.setViewAdapter(new NumericWheelAdapter(context, 0, 59, "%02d"));
        mins.setCyclic(true);
     // set current time
        Calendar c = Calendar.getInstance();
        int curHours = c.get(Calendar.HOUR_OF_DAY);
        int curMinutes = c.get(Calendar.MINUTE);

        hours.setCurrentItem(curHours);
        mins.setCurrentItem(curMinutes);

        // add listeners
        addChangingListener(mins, "min");
        addChangingListener(hours, "hour");

        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!timeScrolled) {
                }
            }
        };
        hours.addChangingListener(wheelListener);
        mins.addChangingListener(wheelListener);

        OnWheelClickedListener click = new OnWheelClickedListener() {
            public void onItemClicked(AbstractWheel wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };
        hours.addClickingListener(click);
        mins.addClickingListener(click);

        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                timeScrolled = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                timeScrolled = false;
                if(mListener != null){
                	mListener.onChangeFinished(hours.getCurrentItem(), mins.getCurrentItem());
                }
            }
        };

        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);
	}
	
	/**
     * Adds changing listener for spinnerwheel that updates the spinnerwheel label
     * @param wheel the spinnerwheel
     * @param label the spinnerwheel label
     */
    private void addChangingListener(final AbstractWheel wheel, final String label) {
        wheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
            	
            }
        });
    }
    
    public void setHour(int hour) {
    	hours.setCurrentItem(hour);
	}
    
    public void setMinute(int minute) {
    	mins.setCurrentItem(minute);
	}
    
    public int getHour() {
    	return hours.getCurrentItem();
	}
    
    public int getMinute() {
    	return mins.getCurrentItem();
	}
    
    private OnTimeChangeListener mListener;
    public void setOnTimeChangeListener(OnTimeChangeListener listener) {
    	mListener = listener;
	}
	
    public interface OnTimeChangeListener{
    	void onChangeFinished(int hour, int minute);
    }

}
