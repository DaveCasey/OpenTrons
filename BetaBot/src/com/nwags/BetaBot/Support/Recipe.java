package com.nwags.BetaBot.Support;

import java.util.ArrayList;
import java.util.Iterator;

public class Recipe {
	String Name;
	int UID;
	private static int Count = 0;
	private ArrayList<Command> Commands;
	
	public Recipe(){
		Name = "";
		UID = Count++;
		Commands = new ArrayList<Command>();
	}
	
	public Recipe(String name){
		Name = name;
		UID = Count++;
		Commands = new ArrayList<Command>();
	}
	
	public void add(Command command){
		Commands.add(command);
	}
	
	public Command get(int index){
		return Commands.get(index);
	}
	
	public void clear(){
		Commands.clear();
	}
	
	public void clearAll(){
		Commands.clear();
		Name = "";
	}
	
	public void setName(String name){
		Name = name;
	}
	
	public String getName(){
		return Name;
	}
	
	public Iterator<Command> iterator(){
		return Commands.iterator();
	}
	
	public Command remove(int index){
		return Commands.remove(index);
	}
	
	public String makeString(){
		StringBuilder sb = new StringBuilder();
		int j = 0;
		while(Commands.size()>j){
			sb.append("RECIPE");
			sb.append(",");
			sb.append(Name);
			sb.append(",,0,0,");
			sb.append(Commands.get(j++).makeString());
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	public int size(){
		return Commands.size();
	}
	
	public boolean Inflate(String rString){
		Commands.clear();
		boolean result = false;
		String holder = rString.replaceAll("\r", "");
		String[] bigs = holder.split("\n");
		
		
		String rName = null;
		Command command;
		int i = 0;
		while(i<bigs.length){
			String[] littles = bigs[i++].split(",");
			if(littles.length<16)
				continue;
			
			
			if(littles[0].equals("RECIPE")){
				if(rName==null)
					rName = littles[1].trim();
					Name = rName;
				
				command = new Command(	Integer.parseInt(littles[5].trim()),		//	Number
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
										Boolean.parseBoolean(littles[20].trim()), 	//	Home
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
				Commands.add(command);
				result = true;
			}	
		}
		
		return result;
	}
	
}
