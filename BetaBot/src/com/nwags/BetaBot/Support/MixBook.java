package com.nwags.BetaBot.Support;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class MixBook {
	private LinkedHashMap<String,Recipe> recipes;
	private Template template;
	
	public MixBook(){
		recipes = new LinkedHashMap<String,Recipe>();
		template = new Template();
	}
	
	public MixBook(String name){
		recipes = new LinkedHashMap<String,Recipe>();
		template = new Template(name);
	}
	
	public Recipe get(String key){
		return recipes.get(key);
	}
	
	public void put(String key, Recipe recipe){
		recipes.put(key, recipe);
	}
	
	public void clear(){
		recipes.clear();
		template = new Template();
	}
	
	public void clearRecipes(){
		recipes.clear();
	}
	
	public boolean containsRecipe(String key){
		return recipes.containsKey(key);
	}
	
	public void setTemplate(Template atemplate){
		template = atemplate;
	}
	
	public Template getTemplate(){
		return template;
	}
	
	public String makeRecipeString(){
		StringBuilder sb = new StringBuilder();
		for(String key:recipes.keySet()){
			sb.append(recipes.get(key).makeString());
		}
		return sb.toString();
	}
	
	public String makeString(){
		StringBuilder sb = new StringBuilder();
		if(template.makeString()!=null)
			sb.append(template.makeString());
		
		sb.append(makeRecipeString());
		return sb.toString();
	}
	
	public void replace(String oldrecipename, Recipe newrecipe){
		if(recipes.containsKey(oldrecipename))
			recipes.remove(oldrecipename);
		
		recipes.put(newrecipe.getName(), newrecipe);
	}
	
	public Set<String> getRecipeSet(){
		return recipes.keySet();
	}
	
	public int Inflate(String mString){
		template.clear();
		recipes.clear();
		int result = -1;
		boolean hasTemplate = false;
		boolean hasRecipe = false;
		String holder = mString.replaceAll("\r", "");
		String[] bigs = holder.split("\n");
		String ingredient = null;
		String rName = null;
		Recipe recipe = new Recipe();
		Command command;
		int i = 0;
		int j = bigs.length;
		while(i<j){
			String[] littles = bigs[i++].split(",");
			
			if(littles[0].equals("TEMPLATE")){
				if(littles.length<5)
					continue;
				
				
				if(i==1)
					template.setName(littles[1]);
				
				if(littles.length==5)
					template.addIngredient(littles[2], littles[3], littles[4].trim());
				if(littles.length==6)
					template.addIngredient(littles[2], littles[3], littles[4].trim(), littles[5].trim());
				hasTemplate = true;
				result = 1;
			}else if(littles[0].equals("RECIPE")){
				if(littles.length<37)
					continue;
					
				if(rName==null){
					rName = littles[1];
					recipe.setName(rName);
				}else if(!rName.equals(littles[1])){
					recipes.put(recipe.getName(), recipe);
					rName = littles[1];
					recipe = new Recipe(rName);
				}
				
				command = new Command(	
						Integer.parseInt(littles[5].trim()),		//	Number
						littles[6].trim(),							//	Ingredient
						Integer.parseInt(littles[7].trim()),		//	Speed
						Float.parseFloat(littles[8].trim()),		//	Depth
						Integer.parseInt(littles[9].trim()),		//	ZSpeed
						Float.parseFloat(littles[10].trim()),		//	Aspirate
						Integer.parseInt(littles[11].trim()),		//	AspSpeed
						Boolean.parseBoolean(littles[12].trim()),	//	Blowout
						Boolean.parseBoolean(littles[13].trim()),	//	DropTip
						Boolean.parseBoolean(littles[14].trim()),	//	Suction
						Boolean.parseBoolean(littles[15].trim()),	//	Echo
						Boolean.parseBoolean(littles[16].trim()),	//	Grip
						Boolean.parseBoolean(littles[17].trim()),	//	AutoReturnX
						Boolean.parseBoolean(littles[18].trim()),	//	AutoReturnY
						Boolean.parseBoolean(littles[19].trim()),	//	AutoReturnZ
						Boolean.parseBoolean(littles[20].trim()),	//	Home
						Float.parseFloat(littles[21].trim()),		//	OffsetX
						Float.parseFloat(littles[22].trim()),		//	OffsetY
						Float.parseFloat(littles[23].trim()),		//	OffsetZ
						Integer.parseInt(littles[24].trim()),		//	RowA
						Integer.parseInt(littles[25].trim()),		//	RowB
						Float.parseFloat(littles[26].trim()),		//	Delay
						Integer.parseInt(littles[27].trim()),		//	Times
						Integer.parseInt(littles[28].trim()),
						Float.parseFloat(littles[29].trim()),
						Integer.parseInt(littles[30].trim()),
						Integer.parseInt(littles[31].trim()),
						Boolean.parseBoolean(littles[32].trim()),
						Integer.parseInt(littles[33].trim()),
						Integer.parseInt(littles[34].trim()),
						Float.parseFloat(littles[35].trim()),
						Float.parseFloat(littles[36].trim())
					);
				recipe.add(command);	
			}
		}
		if(rName!=null){
			recipes.put(recipe.getName(), recipe);
			hasRecipe = true;
			if(!hasTemplate)
				result=4;
		}
		if(hasTemplate && hasRecipe){
			result = 2;
			for(String key:getRecipeSet()){
				Iterator<Command> iterator = get(key).iterator();
				while (iterator.hasNext()) {
	    			command = iterator.next();
	    			ingredient = command.Ingredient;
	    			if((template.getX(ingredient)==null)||(template.getY(ingredient)==null)){
	    				result = 3;
	    			}
				}
			}
		}
		
		return result;
	}
	
	
}
