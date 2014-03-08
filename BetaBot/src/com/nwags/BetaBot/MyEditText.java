package com.nwags.BetaBot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class MyEditText extends EditText{
	private String wtf;
	
	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(myClickListener);
        //setOnTouchListener(otl);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(myClickListener);
        //setOnTouchListener(otl);
    }

    public MyEditText(Context context) {
        super(context);
        setOnClickListener(myClickListener);
        //setOnTouchListener(otl);
    }
    
    private OnTouchListener otl = new OnTouchListener() {
    	public boolean onTouch(View v, MotionEvent event) {
    		v.requestFocusFromTouch();
    		goboom();
    		return true;
    	}
    };
	
    
    private OnClickListener myClickListener = new OnClickListener(){
    	@Override
		public void onClick(View v) {
    		goboom();
		}
    };
    
    public void goboom(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		
		alert.setTitle(getText());
		alert.setMessage("Edit Text");
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(getContext());
		input.setText(wtf);
		alert.setView(input);
		input.setInputType(getInputType());
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			String value = input.getText().toString();
    			// Do something with value!
    			setText(value);
    		}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
    }
    
    
}
