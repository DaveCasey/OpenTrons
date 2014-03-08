package com.nwags.BetaBot;

import com.nwags.BetaBot.Support.Recipe;
import com.nwags.BetaBot.Support.Template;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class HomeFragment extends Fragment{
	private HomeFragmentListener parent;
	private int count=0;
	private boolean swich = true;
	private ImageButton imageBtn; 
	private Button shortcutBtn;
	private ImageView logoView;
	private LinearLayout mainLayout;
	
	
	private static Recipe recipe;
	private static Template template;
	private static String rString;
	private static String tString;
	
	private static SharedPreferences settings;
	private static Context mContext;
	private static boolean debug;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mContext = getActivity().getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);

		recipe = new Recipe();
		template = new Template();
		
		try {
			parent = (HomeFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement HomeFragmentListener");
		}
	}
	
	@Override
	public View onCreateView(	LayoutInflater inflater, 
								ViewGroup container, 
								Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.home, container, false);
		
		imageBtn = (ImageButton) v.findViewById(R.id.imageView1);
		imageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if((count++)>3){
					if(swich){
						swich = false;
						parent.unlock();
						count = 0;
						
					}
				}
			}
		});
		logoView = (ImageView) v.findViewById(R.id.imageView2);
		mainLayout = (LinearLayout) v.findViewById(R.id.main_home);
		
		rString = settings.getString("shortcutRecipe", "nothing");
		tString = settings.getString("shortcutTemplate", "nothing");
		int nwsal = Integer.parseInt(settings.getString("nwsal","0"));
		switch(nwsal) {
		case 0:
			imageBtn.setImageDrawable(getResources().getDrawable(R.drawable.open_trons));
			imageBtn.setBackgroundColor(getResources().getColor(R.color.black));
			mainLayout.setBackgroundColor(getResources().getColor(R.color.black));
			logoView.setVisibility(View.GONE);
			break;
		case 1:
			imageBtn.setImageDrawable(getResources().getDrawable(R.drawable.open_white));
			imageBtn.setBackgroundColor(getResources().getColor(R.color.white));
			mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
			logoView.setVisibility(View.VISIBLE);
			break;
		}
		
		
		final boolean rBool = recipe.Inflate(rString);
		boolean tBool = template.Inflate(tString);
		
		shortcutBtn = (Button) v.findViewById(R.id.recipeShortcut);
		if(!rBool){
			shortcutBtn.setText("Recipe Shortcut");
		}else{
			shortcutBtn.setText(recipe.getName());
		}
		shortcutBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view){
				parent.shortcut(rString, tString, rBool);
			}
		});
		
		return v;
	}
	
	public interface HomeFragmentListener {
		void unlock();
		
		void shortcut(String _rString, String _tString, boolean _rBool);
	}
	
}
