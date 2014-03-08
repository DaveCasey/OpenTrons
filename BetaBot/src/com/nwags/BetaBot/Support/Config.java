package com.nwags.BetaBot.Support;

import java.util.HashSet;
import java.util.Set;

public class Config 
{
	private static final Set<BetaBotType> axisVals = new HashSet<BetaBotType>();
	private static final Set<BetaBotType> motorVals = new HashSet<BetaBotType>();
	private static final Set<BetaBotType> sysVals = new HashSet<BetaBotType>();
	
	public Config()
	{
		motorVals.add(new BetaBotType("tr", "float")); 
		motorVals.add(new BetaBotType("sa", "float")); 
		motorVals.add(new BetaBotType("mi", "int")); 
		motorVals.add(new BetaBotType("po", "boolean")); 
		motorVals.add(new BetaBotType("pm", "boolean")); 
		motorVals.add(new BetaBotType("ma", "int"));
		
		axisVals.add(new BetaBotType("tm", "float")); // travel max
		axisVals.add(new BetaBotType("vm", "float")); // velocity max
		axisVals.add(new BetaBotType("jm", "float")); // jerk max
		axisVals.add(new BetaBotType("jd", "float")); // junction deviation
		axisVals.add(new BetaBotType("ra", "float")); // radius
		axisVals.add(new BetaBotType("fr", "float")); // feed rate max
		axisVals.add(new BetaBotType("am", "int"));   // axis mode
		axisVals.add(new BetaBotType("sv", "float")); // homing search velocity
		axisVals.add(new BetaBotType("lv", "float")); // homing latch velocity
		axisVals.add(new BetaBotType("sn", "int"));   // switch min?
		axisVals.add(new BetaBotType("sx", "int"));   // switch max?
		axisVals.add(new BetaBotType("zb", "float")); // zero backoff
		
		sysVals.add(new BetaBotType("fb", "float")); 
		sysVals.add(new BetaBotType("fv", "float")); 
		sysVals.add(new BetaBotType("hv", "int")); 
		sysVals.add(new BetaBotType("id", "string")); 
		sysVals.add(new BetaBotType("ja", "float")); 
		sysVals.add(new BetaBotType("ct", "float")); 
		sysVals.add(new BetaBotType("st", "int")); 
		sysVals.add(new BetaBotType("mt", "int"));
		sysVals.add(new BetaBotType("ej", "boolean")); 
		sysVals.add(new BetaBotType("jv", "int")); 
		sysVals.add(new BetaBotType("tv", "int")); 
		sysVals.add(new BetaBotType("qv", "int")); 
		sysVals.add(new BetaBotType("sv", "int")); 
		sysVals.add(new BetaBotType("si", "int")); 
		sysVals.add(new BetaBotType("ic", "int")); 
		sysVals.add(new BetaBotType("ec", "boolean")); 
		sysVals.add(new BetaBotType("ee", "boolean")); 
		sysVals.add(new BetaBotType("ex", "int"));
		
		//added
		sysVals.add(new BetaBotType("gpl", "int"));
		sysVals.add(new BetaBotType("gun", "boolean"));
		sysVals.add(new BetaBotType("gco", "int"));
		sysVals.add(new BetaBotType("gpa", "int"));
		sysVals.add(new BetaBotType("gdi", "int"));
		
		//nwags
		sysVals.add(new BetaBotType("nwa", "string"));
		sysVals.add(new BetaBotType("nwadt", "float"));
		sysVals.add(new BetaBotType("nwae", "float"));
		sysVals.add(new BetaBotType("nwsal", "int"));
		
		//nwags-homing-distance
		/*
		sysVals.add(new BetaBotType("nwxh", "float"));
		sysVals.add(new BetaBotType("nwyh", "float"));
		sysVals.add(new BetaBotType("nwzh", "float"));
		sysVals.add(new BetaBotType("nwah", "float"));
		
		sysVals.add(new BetaBotType("nwxsh", "float"));
		sysVals.add(new BetaBotType("nwysh", "float"));
		sysVals.add(new BetaBotType("nwzsh", "float"));
		sysVals.add(new BetaBotType("nwash", "float"));
		
		sysVals.add(new BetaBotType("nwxta", "float"));
		sysVals.add(new BetaBotType("nwyta", "float"));
		sysVals.add(new BetaBotType("nwzta", "float"));
		sysVals.add(new BetaBotType("nwata", "float"));
		
		sysVals.add(new BetaBotType("nwxtb", "float"));
		sysVals.add(new BetaBotType("nwytb", "float"));
		sysVals.add(new BetaBotType("nwztb", "float"));
		sysVals.add(new BetaBotType("nwatb", "float"));
		
		sysVals.add(new BetaBotType("nwxn", "float"));
		sysVals.add(new BetaBotType("nwyn", "float"));
		sysVals.add(new BetaBotType("nwzn", "float"));
		sysVals.add(new BetaBotType("nwan", "float"));
		
		//nwags-homing-speed
		sysVals.add(new BetaBotType("nwxhf", "float"));
		sysVals.add(new BetaBotType("nwyhf", "float"));
		sysVals.add(new BetaBotType("nwzhf", "float"));
		sysVals.add(new BetaBotType("nwahf", "float"));
		
		sysVals.add(new BetaBotType("nwxshf", "float"));
		sysVals.add(new BetaBotType("nwyshf", "float"));
		sysVals.add(new BetaBotType("nwzshf", "float"));
		sysVals.add(new BetaBotType("nwashf", "float"));
		
		sysVals.add(new BetaBotType("nwxtaf", "float"));
		sysVals.add(new BetaBotType("nwytaf", "float"));
		sysVals.add(new BetaBotType("nwztaf", "float"));
		sysVals.add(new BetaBotType("nwataf", "float"));
		
		sysVals.add(new BetaBotType("nwxtbf", "float"));
		sysVals.add(new BetaBotType("nwytbf", "float"));
		sysVals.add(new BetaBotType("nwztbf", "float"));
		sysVals.add(new BetaBotType("nwatbf", "float"));
		
		sysVals.add(new BetaBotType("nwxnf", "float"));
		sysVals.add(new BetaBotType("nwynf", "float"));
		sysVals.add(new BetaBotType("nwznf", "float"));
		sysVals.add(new BetaBotType("nwanf", "float"));
		*/
	}
	
	public Set<BetaBotType> getAxis()
	{
		return axisVals;
	}
	
	public Set<BetaBotType> getMotor()
	{
		return motorVals;
	}
	
	public Set<BetaBotType> getSys()
	{
		return sysVals;
	}
	
	public class BetaBotType
	{
		public String name;
		public String type;
		
		private BetaBotType(String name, String type)
		{
			this.name = name;
			this.type = type;
		}
	}
}
