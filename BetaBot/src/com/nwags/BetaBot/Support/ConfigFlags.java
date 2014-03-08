package com.nwags.BetaBot.Support;

import java.util.HashSet;
import java.util.Set;

public class ConfigFlags 
{
	private static final Set<BetaBotFlags> axisFlags = new HashSet<BetaBotFlags>();
	private static final Set<BetaBotFlags> motorFlags = new HashSet<BetaBotFlags>();
	private static final Set<BetaBotFlags> sysFlags = new HashSet<BetaBotFlags>();
	
	public ConfigFlags()
	{
		motorFlags.add(new BetaBotFlags("tr", false)); 
		motorFlags.add(new BetaBotFlags("sa", false)); 
		motorFlags.add(new BetaBotFlags("mi", false)); 
		motorFlags.add(new BetaBotFlags("po", false)); 
		motorFlags.add(new BetaBotFlags("pm", false)); 
		motorFlags.add(new BetaBotFlags("ma", false));
		
		axisFlags.add(new BetaBotFlags("tm", false)); // travel max
		axisFlags.add(new BetaBotFlags("vm", false)); // velocity max
		axisFlags.add(new BetaBotFlags("jm", false)); // jerk max
		axisFlags.add(new BetaBotFlags("jd", false)); // junction deviation
		axisFlags.add(new BetaBotFlags("ra", false)); // radius
		axisFlags.add(new BetaBotFlags("fr", false)); // feed rate max
		axisFlags.add(new BetaBotFlags("am", false));   // axis mode
		axisFlags.add(new BetaBotFlags("sv", false)); // homing search velocity
		axisFlags.add(new BetaBotFlags("lv", false)); // homing latch velocity
		axisFlags.add(new BetaBotFlags("sn", false));   // switch min?
		axisFlags.add(new BetaBotFlags("sx", false));   // switch max?
		axisFlags.add(new BetaBotFlags("zb", false)); // zero backoff
		
		sysFlags.add(new BetaBotFlags("fb", false)); 
		sysFlags.add(new BetaBotFlags("fv", false)); 
		sysFlags.add(new BetaBotFlags("hv", false)); 
		sysFlags.add(new BetaBotFlags("id", false)); 
		sysFlags.add(new BetaBotFlags("ja", false)); 
		sysFlags.add(new BetaBotFlags("ct", false)); 
		sysFlags.add(new BetaBotFlags("st", false)); 
		sysFlags.add(new BetaBotFlags("mt", false));
		sysFlags.add(new BetaBotFlags("ej", false)); 
		sysFlags.add(new BetaBotFlags("jv", false)); 
		sysFlags.add(new BetaBotFlags("tv", false)); 
		sysFlags.add(new BetaBotFlags("qv", false)); 
		sysFlags.add(new BetaBotFlags("sv", false)); 
		sysFlags.add(new BetaBotFlags("si", false)); 
		sysFlags.add(new BetaBotFlags("ic", false)); 
		sysFlags.add(new BetaBotFlags("ec", false)); 
		sysFlags.add(new BetaBotFlags("ee", false)); 
		sysFlags.add(new BetaBotFlags("ex", false));
		
		//added
		sysFlags.add(new BetaBotFlags("gpl", false));
		sysFlags.add(new BetaBotFlags("gun", false));
		sysFlags.add(new BetaBotFlags("gco", false));
		sysFlags.add(new BetaBotFlags("gpa", false));
		sysFlags.add(new BetaBotFlags("gdi", false));
		
		//nwags
		sysFlags.add(new BetaBotFlags("nwa", false));
		sysFlags.add(new BetaBotFlags("nwadt", false));
		sysFlags.add(new BetaBotFlags("nwae", false));
		sysFlags.add(new BetaBotFlags("nwsal", false));
	}
	
	public Set<BetaBotFlags> getAxis()
	{
		return axisFlags;
	}
	
	public Set<BetaBotFlags> getMotor()
	{
		return motorFlags;
	}
	
	public Set<BetaBotFlags> getSys()
	{
		return sysFlags;
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
	
	private class BetaBotFlags
	{
		public String name;
		public boolean flag;
		private BetaBotFlags(String name, boolean flag)
		{
			this.name = name;
			this.flag = flag;
		}
	}
	
	public void falsify() {
		for(BetaBotFlags bbf:axisFlags){
			bbf.flag = false;
		}
		for(BetaBotFlags bbf:motorFlags){
			bbf.flag = false;
		}
		for(BetaBotFlags bbf:sysFlags){
			bbf.flag = false;
		}
	}
}
