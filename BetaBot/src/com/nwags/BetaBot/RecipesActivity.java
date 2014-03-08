package com.nwags.BetaBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.nwags.BetaBot.Support.Command;
import com.nwags.BetaBot.Support.MixBook;
import com.nwags.BetaBot.Support.Recipe;
import com.nwags.BetaBot.Support.Template;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecipesActivity extends Activity {
	private static final String TAG = "RecipesActivity";
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private NoDefaultSpinner headerSpinner;
    private Button recipeButton;
    
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    
    private String mString=null;
    private String fString=null;
    
    static MixBook mixbook;
    static Recipe recipe;
    static Template template;
    static Command command;
    
    private static final int REQUEST_EDITRECIPE = 10;
    private static final int REQUEST_BOOM = 11;
    
    private Typeface tf;
    
    SharedPreferences settings;
    Context mContext;
    private boolean debug;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setResult(RESULT_CANCELED, getIntent());
        setContentView(R.layout.recipes);
        
        mContext = getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);
		
        tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
        
        command = new Command();
        
        mString = getIntent().getStringExtra("mixbook");
        fString = getIntent().getStringExtra("file");
       
        if(debug){
        	Log.d(TAG,"A mString:"+mString);
        	Log.d(TAG,"A mString:"+fString);
        	Log.d(TAG,"A mString.trim:"+mString.trim());
        	Log.d(TAG,"A mString.trim:"+fString.trim());
        }
        
        mixbook = new MixBook();
        mixbook.Inflate(mString);
		if(mixbook.getRecipeSet().size()==0){
			recipe = new Recipe();
			Command command = new Command(
					0,					//	Number
					"no ingredients",	//	Ingredient
					0,					//	Speed
					0f,					//	Depth
					0,					//	ZSpeed
					0f,					//	Aspirate
					0,					//	AspSpeed
					false,				//	Blowout
					false,				//	DropTip
					false,				//	Suction
					false,				//	Echo
					false,				//	Grip
					false,				//	AutoReturnX
					false,				//	AutoReturnY
					true,				//	AutoReturnZ
					false,				//	Home
					0f,					//	OffsetX
					0f,					//	OffsetY
					0f,					//	OffsetZ
					0,					//	RowA
					0,					//	RowB
					0f,					//	Delay
					0,					//	Times
					0,					//	Sensor
					0f,					//	Condition
					0,					//	Criterion
					0,					//	Negative
					true,
					0,
					0,
					0f,
					0f
				);
			recipe.setName("NO RECIPES!");
			recipe.add(command);
		}
    }
    
    /*
     * Preparing the list data
     */
    private void prepareListData(MixBook mxbk) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        // Adding child data
        int count=0;
        String sample = " No. | Ingredient      | Speed   | Depth   |   ZSpeed | Aspirate | AspSpeed | Blowout | Droptip | Suction | Grip  | A.R. X | A.R. Y | A.R. Z | Home   | Offset X | Offset Y | Offset Z | Row A | Row B | Delay   | Times | Sensor    | Condition | Criterion | Negative     | Trace  ";
        for(String key:mxbk.getRecipeSet()){
        	listDataHeader.add(mxbk.get(key).getName());
        	List<String>ingredientlist = new ArrayList<String>();
        	Iterator<Command> iterator = mxbk.get(key).iterator();
    		ingredientlist.add(sample);
        	while (iterator.hasNext()) {
    			Command command = new Command();
    			command = iterator.next();
    			String speedo = null;
    			switch(command.Speed){
    			case 0:
    				speedo = "Full";
    				break;
    			case 1:
    				speedo = "3/4";
    				break;
    			case 2:
    				speedo = "Half";
    				break;
    			case 3:
    				speedo = "1/4";
    				break;
    			case 4:
    				speedo = "1/10";
    				break;
    			case 5:
    				speedo = "Full";
    				break;
    			default:
    				speedo = "Full";
    				break;
    			}
    			
    			String zspeedo = null;
    			switch(command.ZSpeed){
    			case 0:
    				zspeedo = "Full";
    				break;
    			case 1:
    				zspeedo = "3/4";
    				break;
    			case 2:
    				zspeedo = "Half";
    				break;
    			case 3:
    				zspeedo = "1/4";
    				break;
    			case 4:
    				zspeedo = "1/10";
    				break;
    			case 5:
    				zspeedo = "Full";
    				break;
    			default:
    				zspeedo = "Full";
    				break;
    			}
    			
    			String aspspeedo = null;
    			switch(command.AspSpeed){
    			case 0:
    				aspspeedo = "Full";
    				break;
    			case 1:
    				aspspeedo = "3/4";
    				break;
    			case 2:
    				aspspeedo = "Half";
    				break;
    			case 3:
    				aspspeedo = "1/4";
    				break;
    			case 4:
    				aspspeedo = "1/10";
    				break;
    			case 5:
    				aspspeedo = "Full";
    				break;
    			default:
    				aspspeedo = "Full";
    				break;
    			}		
    			String senso = null;
    			switch(command.Sensor){
    			case 0:
    				senso = "Off";
    				break;
    			case 1:
    				senso = "All Mvnt.";
    				break;
    			case 2:
    				senso = "Ingredients";
    				break;
    			case 3:
    				senso = "Modals";
    				break;
    			case 4:
    				senso = "Auto Rtrns.";
    				break;
    			case 5:
    				senso = "Homing";
    				break;
    			default:
    				senso = "Off";
    				break;
    			}
    			String criterio = null;
    			switch(command.Criterion){
    			case 0:
    				criterio = "Off";
    				break;
    			case 1:
    				criterio = "Greater";
    				break;
    			case 2:
    				criterio = "Less";
    				break;
    			default:
    				criterio = "Off";
    				break;
    			}
    			String nego = null;
    			switch(command.Negative){
    			case 0:
    				nego = "Continue";
    				break;
    			case 1:
    				nego = "Hold";
    				break;
    			case 2:
    				nego = "Home";
    				break;
    			case 3:
    				nego = "End";
    				break;
    			default:
    				if(command.Negative>3)
    				nego = String.valueOf(command.Negative-3);
    				break;
    			}
    			
    										//    NO. : Ingr. : Sped : Dpth : Zspd : Aspr : Aspd : Blwt : Drpt : Sctn : Grip : AR x : AR Y : AR Z : Home : OS X : OS Y : OS Z : RowA : RowB : Dlay : Tmes : Sens : Cond : Crit : Nega  : Trace
    			String fromage = String.format(" %-3s : %-15s : %-7s : %-7s : %-8s : %-8s : %-8s : %-7s : %-7s : %-7s : %-5s : %-6s : %-6s : %-6s : %-6s : %-8s : %-8s : %-8s : %-5s : %-5s : %-7s : %-5s : %-9s : %-9s : %-9s : %-12s : %-6s ", 
    					command.NumberS,
    					command.Ingredient, 
    					speedo,
    					command.DepthS,
    					zspeedo,
    					command.AspirateS, 
    					aspspeedo,
    					command.BlowoutS,
    					command.DropTipS,
    					command.SuctionS,    					
    					command.GripS,
    					command.AutoReturnXS,
    					command.AutoReturnYS,
    					command.AutoReturnZS,
    					command.HomeS,
    					command.OffsetXS,
    					command.OffsetYS,
    					command.OffsetZS,
    					command.RowAS,
    					command.RowBS,
    					command.DelayS,
    					command.Times,
    					senso,
    					command.ConditionS,
    					criterio,
    					nego,
    					command.TraceS).toUpperCase();
    			ingredientlist.add(fromage);
    		}
    		listDataChild.put(listDataHeader.get(count++), ingredientlist);
        }
    }
    
    @Override
    public void onActivityResult(	int requestCode,
    								int resultCode,
    								Intent data	){
    	if(debug)
    		Log.d(TAG,"onActivityResult");
    	
    	if(requestCode==REQUEST_EDITRECIPE)
    	{
    		if(debug)
    			Log.d(TAG,"REQUEST_EDITRECIPE");
    		
    		if(resultCode==Activity.RESULT_OK && data!=null)
    		{
    			setContentView(R.layout.recipes);
    	        
    	        command = new Command();
    	        
    	        mString = data.getStringExtra("mixbook");
    	        if(debug){
    	        	Log.d(TAG,"B mString: "+mString);
    	        	Log.d(TAG,"B fString: "+fString);
    	        	Log.d(TAG,"B mString.trim: "+mString.trim());
    	        	Log.d(TAG,"B fString.trim: "+fString.trim());
    	        }
    	        
    	        mixbook = new MixBook();
    	        mixbook.Inflate(mString);
    			if(mixbook.getRecipeSet().size()==0){
    				recipe = new Recipe();
    				Command command = new Command(
    						0,					//	Number
    						"no ingredients",	//	Ingredient
    						0,					//	Speed
    						0f,					//	Depth
    						0,					//	ZSpeed
    						0f,					//	Aspirate
    						0,					//	AspSpeed
    						false,				//	Blowout
    						false,				//	DropTip
    						false,				//	Suction
    						false,				//	Echo
    						false,				//	Grip
    						false,				//	AutoReturnX
    						false,				//	AutoReturnY
    						true,				//	AutoReturnZ
    						false,				//	Home
    						0f,					//	OffsetX
    						0f,					//	OffsetY
    						0f,					//	OffsetZ
    						0,					//	RowA
    						0,					//	RowB
    						0f,					//	Delay
    						0,					//	Times
    						0,					//	Sensor
    						0f,					//	Condition
    						0,					//	Criterion
    						0,					//	Negative
    						true,
    						0,
    						0,
    						0f,
    						0f
    					);
    				recipe.setName("NO RECIPES!");
    				recipe.add(command);
    			}
    			
    	        // get the listview
    	        expListView = (ExpandableListView) findViewById(R.id.lvExp);
    	        
    	        // preparing list data
    	        if(debug)
    	        	Log.d(TAG,"mixbook: "+mixbook.makeString());
    	        
    	        prepareListData(mixbook);
    	        
    	        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
    	        
    	        // setting list adapter
    	        expListView.setAdapter(listAdapter);
    	        getIntent().putExtra("mixbook", mixbook.makeString());
    		}
    	} else if(requestCode==REQUEST_BOOM){
    		if(debug)
    			Log.d(TAG,"REQUEST_BOOM");
    		if(resultCode==Activity.RESULT_OK && data!=null)
    		{	}
    	}
    }
    
    
    private class ExpandableListAdapter extends BaseExpandableListAdapter {
    	
        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;
        String recipeLabel;
        
        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }
        
        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }
        
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        
        @Override
        public View getChildView(int groupPosition, final int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
     
            final String childText = (String) getChild(groupPosition, childPosition);
     
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }
     
            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);
            recipeButton = new Button(_context);
            recipeButton = (Button) convertView.findViewById(R.id.testButton);
            recipeButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {	}
            	
            });
            
            txtListChild.setTypeface(tf);
            txtListChild.setText(childText);
            return convertView;
        }
        
        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }
     
        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }
     
        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }
     
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
     
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            recipeLabel = new String();
            recipeLabel = headerTitle;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }
            
            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);
            
            headerSpinner = new NoDefaultSpinner(_context);
            headerSpinner = (NoDefaultSpinner) convertView.findViewById(R.id.HeaderSpinner);
            headerSpinner.setFocusable(false);
            headerSpinner.setBackgroundColor(Color.WHITE);
            
            headerSpinner.setOnItemSelectedListener(
            		new OnItemSelectedListener() {
            			
            			String recipeNombre = recipeLabel;
            			
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					if(parent.getItemAtPosition(pos).toString().equals("SET AS SHORTCUT")) {
						SharedPreferences.Editor Ed = settings.edit();
						Ed.putString("shortcutRecipe",RecipesActivity.mixbook.get(recipeNombre).makeString());
						Ed.putString("shortcutTemplate", RecipesActivity.mixbook.getTemplate().makeString());
						Ed.commit();
						
					}else if(parent.getItemAtPosition(pos).toString().equals("RUN!")) {
						Toast.makeText(RecipesActivity.this, "starting single run", Toast.LENGTH_LONG).show();
						
						final ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
						final List<RunningServiceInfo> services = 
								activityManager.getRunningServices(Integer.MAX_VALUE);
						for (int i = 0; i < services.size(); i++){
							if(debug){
								Log.d(TAG,"SERVICES_A|Service Nr. " + i + ":" + services.get(i).service);
								Log.d(TAG,"SERVICES_B|Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
								Log.d(TAG,"SERVICES_C|Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
							}
							if(services.get(i).service.getClassName().equals("com.nwags.BetaBot.Bluetooth.BluetoothSerialService")){
								Intent runIntent = new Intent(RecipesActivity.this,Boom.class);
								runIntent.putExtra("recipe", RecipesActivity.mixbook.get(recipeNombre).makeString());
								runIntent.putExtra("template", RecipesActivity.mixbook.getTemplate().makeString());
								runIntent.putExtra("visualize", false);
								runIntent.putExtra("runcontinously", false);
								RecipesActivity.this.startActivityForResult(runIntent, REQUEST_BOOM);
							}
						}
						
					} else if(parent.getItemAtPosition(pos).toString().equals("EDIT")) {
						
	                    Toast.makeText(mContext,"Opening \"Edit Recipe\" screen", Toast.LENGTH_SHORT).show();
	                    
			            new Thread(new Runnable() {
			            	public void run() {
				            		Intent editIntent = new Intent(RecipesActivity.this,AddRecipeActivity.class);
									editIntent.putExtra("action","EDITRECIPE");	
									editIntent.putExtra("name", recipeNombre);	
									editIntent.putExtra("file", RecipesActivity.this.fString);		
									editIntent.putExtra("mixbook", RecipesActivity.mixbook.makeString());
									RecipesActivity.this.startActivityForResult(editIntent, REQUEST_EDITRECIPE);
			            	}
						}).start();
					} else if(parent.getItemAtPosition(pos).toString().equals("DELETE")){
						Toast.makeText(RecipesActivity.this, "Add DELETE code", Toast.LENGTH_LONG).show();
					} else if(parent.getItemAtPosition(pos).toString().equals("VISUALIZE")){
						Toast.makeText(RecipesActivity.this, "Visualize ...", Toast.LENGTH_LONG).show();
						
						final ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
						final List<RunningServiceInfo> services = 
								activityManager.getRunningServices(Integer.MAX_VALUE);
						for (int i = 0; i < services.size(); i++){
							if(debug){
								Log.d(TAG,"SERVICES_A|Service Nr. " + i + ":" + services.get(i).service);
								Log.d(TAG,"SERVICES_B|Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
								Log.d(TAG,"SERVICES_C|Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
							}
							if(services.get(i).service.getClassName().equals("com.nwags.BetaBot.Bluetooth.BluetoothSerialService")){
								Intent runIntent = new Intent(RecipesActivity.this,Boom.class);
								runIntent.putExtra("recipe", RecipesActivity.mixbook.get(recipeNombre).makeString());
								runIntent.putExtra("template", RecipesActivity.mixbook.getTemplate().makeString());
								runIntent.putExtra("visualize", true);
								runIntent.putExtra("runcontinuously",false);
								RecipesActivity.this.startActivityForResult(runIntent, REQUEST_BOOM);
							}
						}
					} else if(parent.getItemAtPosition(pos).toString().equals("RUN CONTINUOUSLY")){
						Toast.makeText(RecipesActivity.this, "starting continuous run", Toast.LENGTH_LONG).show();
						
						final ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
						final List<RunningServiceInfo> services = 
								activityManager.getRunningServices(Integer.MAX_VALUE);
						for (int i = 0; i < services.size(); i++){
							if(debug){
								Log.d(TAG,"SERVICES_A|Service Nr. " + i + ":" + services.get(i).service);
								Log.d(TAG,"SERVICES_B|Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
								Log.d(TAG,"SERVICES_C|Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
							}
							if(services.get(i).service.getClassName().equals("com.nwags.BetaBot.Bluetooth.BluetoothSerialService")){
								Intent runIntent = new Intent(RecipesActivity.this,Boom.class);
								runIntent.putExtra("recipe", RecipesActivity.mixbook.get(recipeNombre).makeString());
								runIntent.putExtra("template", RecipesActivity.mixbook.getTemplate().makeString());
								runIntent.putExtra("visualize", false);
								runIntent.putExtra("runcontinuously", true);
								RecipesActivity.this.startActivityForResult(runIntent, REQUEST_BOOM);
							}
						}
					}
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {	}
            });
            
            return convertView;
        }
        
        @Override
        public boolean hasStableIds() {
            return false;
        }
        
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
	
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString("mixbook", mixbook.toString());
	}
	
	
	private void restoreState(Bundle inState)
	{
		//TODO: never used locally? remember why this is here
		if(debug)
			Log.d(TAG,"restoreState");
		
        setContentView(R.layout.recipes);
        
        command = new Command();
        
        mString = inState.getString("mixbook");
        if(debug){
        	Log.d(TAG,"C mString"+mString);
        	Log.d(TAG,"C fString"+fString);
        	Log.d(TAG,"C mString.trim"+mString.trim());
        	Log.d(TAG,"C fString.trim"+fString.trim());
        }
        
        mString.replaceAll("\r", "");
		String[] bigpieces = mString.split("\n");
		int bp_length = bigpieces.length;
		
		int bp_n = 0;
		
		mixbook = new MixBook();
		template = new Template();
		recipe = new Recipe();
		
		boolean nr = false;
		String rName = null;
		boolean noRecipe=false;
		while(bp_n<bp_length){
			String[] littlepieces=bigpieces[bp_n++].split(",");
			if(littlepieces.length<5){
				noRecipe=true;
				break;
			}
			
			if(littlepieces[0].equals("TEMPLATE")){
				if(bp_n==1)
					template.setName(littlepieces[1]);
					
				template.addIngredient(littlepieces[2],littlepieces[3],littlepieces[4]);
				
			} else if(littlepieces[0].equals("RECIPE")){
				if(nr==false){
					rName = littlepieces[1];
					recipe = new Recipe();
					recipe.setName(rName);
					nr = true;
				}else if(!(littlepieces[1].equals(rName))){
						mixbook.put(recipe.getName(), recipe);
						
						recipe = new Recipe();
						rName = littlepieces[1];
						recipe.setName(rName);
				}
				
				Command command = new Command(	
						Integer.parseInt(littlepieces[5].trim()),		//	Number
						littlepieces[6].trim(),							//	Ingredient
						Integer.parseInt(littlepieces[7].trim()),		//	Speed
						Float.parseFloat(littlepieces[8].trim()),		//	Depth
						Integer.parseInt(littlepieces[9].trim()),		//	ZSpeed
						Float.parseFloat(littlepieces[10].trim()),		//	Aspirate
						Integer.parseInt(littlepieces[11].trim()),		//	AspSpeed
						Boolean.parseBoolean(littlepieces[12].trim()),	//	Blowout
						Boolean.parseBoolean(littlepieces[13].trim()),	//	DropTip
						Boolean.parseBoolean(littlepieces[14].trim()),	//	Suction
						Boolean.parseBoolean(littlepieces[15].trim()),	//	Echo
						Boolean.parseBoolean(littlepieces[16].trim()),	//	Grip
						Boolean.parseBoolean(littlepieces[17].trim()),	//	AutoReturnX
						Boolean.parseBoolean(littlepieces[18].trim()),	//	AutoReturnY
						Boolean.parseBoolean(littlepieces[19].trim()),	//	AutoReturnZ
						Boolean.parseBoolean(littlepieces[20].trim()),	//	Home
						Float.parseFloat(littlepieces[21].trim()),		//	OffsetX
						Float.parseFloat(littlepieces[22].trim()),		//	OffsetY
						Float.parseFloat(littlepieces[23].trim()),		//	OffsetZ
						Integer.parseInt(littlepieces[24].trim()),		//	RowA
						Integer.parseInt(littlepieces[25].trim()),		//	RowB
						Float.parseFloat(littlepieces[26].trim()),		//	Delay
						Integer.parseInt(littlepieces[27].trim()),		//	Times
						Integer.parseInt(littlepieces[28].trim()),
						Float.parseFloat(littlepieces[29].trim()),
						Integer.parseInt(littlepieces[30].trim()),
						Integer.parseInt(littlepieces[31].trim()),
						Boolean.parseBoolean(littlepieces[32].trim()),
						Integer.parseInt(littlepieces[33].trim()),
						Integer.parseInt(littlepieces[34].trim()),
						Float.parseFloat(littlepieces[35].trim()),
						Float.parseFloat(littlepieces[36].trim())
					);
				recipe.add(command);
			}
		}
		if(noRecipe){
			recipe = new Recipe();
			Command command = new Command(
					0,					//	Number
					"no ingredients",	//	Ingredient
					0,					//	Speed
					0f,					//	Depth
					0,
					0f,					//	Aspirate
					0,					//	AspSpeed
					false,				//	Blowout
					false,				//	DropTip
					false,				//	Suction
					false,				//	Echo
					false,				//	Grip
					false,				//	AutoReturnX
					false,				//	AutoReturnY
					true,				//	AutoReturnZ
					false,				//	Home
					0f,					//	OffsetX
					0f,					//	OffsetY
					0f,					//	OffsetZ
					0,					//	RowA
					0,					//	RowB
					0f,					//	Delay
					0,					//	Times
					0,					//	Sensor
					0f,					//	Condition
					0,					//	Criterion
					0,					//	Negative
					true,
					0,
					0,
					0f,
					0f
				);
			recipe.setName("NO RECIPES!");
			recipe.add(command);
		}
		mixbook.put(recipe.getName(),recipe);
		mixbook.setTemplate(template);
        
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        
        // preparing list data
        if(debug)
        	Log.d(TAG,"Mixbook: "+mixbook.makeString());
        
        prepareListData(mixbook);
        
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        
        // setting list adapter
        expListView.setAdapter(listAdapter);
	}
    
	
	public void onResume(){
        
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        
        // preparing list data
        if(debug)
        	Log.d(TAG,"Mixbook: "+mixbook.makeString());
        prepareListData(mixbook);
        
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        
        // setting list adapter
        expListView.setAdapter(listAdapter);
        super.onResume();
	}
	
	@Override
	public void onBackPressed(){
		setResult(RESULT_OK,getIntent());
		finish();		
	}
    
}
