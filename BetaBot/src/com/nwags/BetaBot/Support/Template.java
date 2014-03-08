package com.nwags.BetaBot.Support;

import java.util.ArrayList;
import java.util.HashMap;

public class Template {
	private HashMap<String, String> Xs;
	private HashMap<String, String> Ys;
	private HashMap<String, String> Zs;
	private ArrayList<String> Ingredients;
	String Name;
	
	public Template(){
		Xs = new HashMap<String, String>();
		Ys = new HashMap<String, String>();
		Zs = new HashMap<String, String>();
		Ingredients = new ArrayList<String>();
	}
	
	public Template(String name){
		Name=name;
		Xs = new HashMap<String, String>();
		Ys = new HashMap<String, String>();
		Zs = new HashMap<String, String>();
		Ingredients = new ArrayList<String>();
	}
	
	public void clear(){
		Xs.clear();
		Ys.clear();
		Zs.clear();
		Ingredients.clear();
		Name="";
	}
	
	public String getX(String ingredient){
		return Xs.get(ingredient);
	}
	
	public String getY(String ingredient){
		return Ys.get(ingredient);
	}
	
	public String getZ(String ingredient){
		return Zs.get(ingredient);
	}
	
	public void putX(String ingredient, String ex){
		Xs.put(ingredient, ex);
	}
	
	public void putY(String ingredient, String why){
		Ys.put(ingredient, why);
	}
	
	public void putZ(String ingredient, String zee){
		Zs.put(ingredient, zee);
	}
	
	public void setName(String name){
		Name = name;
	}
	
	public String getName(){
		return Name;
	}
	
	public String getIngredient(int index){
		return Ingredients.get(index);
	}
	
	public void addIngredient(String ingredient, String x, String y){
		Ingredients.add(ingredient);
		Xs.put(ingredient, x);
		Ys.put(ingredient, y);
		Zs.put(ingredient, "0");
	}
	
	public void addIngredient(String ingredient, String x, String y, String z){
		Ingredients.add(ingredient);
		Xs.put(ingredient, x);
		Ys.put(ingredient, y);
		Zs.put(ingredient, z);
	}
	
	public void setIngredient(String ingredient, String x, String y, String z){
		Xs.remove(ingredient);
		Ys.remove(ingredient);
		Zs.remove(ingredient);
		Xs.put(ingredient, x);
		Ys.put(ingredient, y);
		Zs.put(ingredient, z);
	}
	
	public boolean replaceIngredient(String oldIngredient, String newIngredient){
		boolean result = false;
		if(Ingredients.contains(oldIngredient)){
			String tX = Xs.get(oldIngredient);
			String tY = Ys.get(oldIngredient);
			String tZ = Zs.get(oldIngredient);
			Xs.remove(oldIngredient);
			Ys.remove(oldIngredient);
			Zs.remove(oldIngredient);
			Ingredients.remove(oldIngredient);
			Ingredients.add(newIngredient);
			Xs.put(newIngredient, tX);
			Ys.put(newIngredient, tY);
			Zs.put(newIngredient, tZ);
			result = true;
		}
		
		return result;
	}
	
	public String makeString(){
		StringBuilder sb = new StringBuilder();
		int j = 0;
		while(Ingredients.size()>j){
			sb.append("TEMPLATE");
			sb.append(",");
			sb.append(Name);
			sb.append(",");
			sb.append(Ingredients.get(j));
			sb.append(",");
			sb.append(Xs.get(Ingredients.get(j)));
			sb.append(",");
			sb.append(Ys.get(Ingredients.get(j)));
			sb.append(",");
			sb.append(Zs.get(Ingredients.get(j++)));
			sb.append("\r\n");
		}
		
		return sb.toString();
	}
	
	public int size(){
		return Ingredients.size();
	}
	
	public boolean Inflate(String tString){
		Ingredients.clear();
		Xs.clear();
		Ys.clear();
		Zs.clear();
		boolean result = false;
		String holder = tString.replaceAll("\r","");
		String[] bigs = holder.split("\n");
		
		
		int i = 0;
		while(i<bigs.length){
			String[] littles = bigs[i++].split(",");
			if(littles.length<5)
				continue;
			
			if(littles[0].equals("TEMPLATE")){
				if(i==1){
					Name = littles[1];
					result = true;
				}
				Ingredients.add(littles[2]);
				Xs.put(littles[2], littles[3]);
				Ys.put(littles[2], littles[4]);
				Zs.put(littles[2], littles[5].trim());
			}
		}
		
		return result;
	}
	
	
}
