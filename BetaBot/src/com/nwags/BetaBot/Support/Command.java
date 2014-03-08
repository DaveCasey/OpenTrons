package com.nwags.BetaBot.Support;

public class Command {
	public int 	Number;
	public String 	Ingredient;
	public int		Speed;
	public float 	Depth;
	public int		ZSpeed;
	public float 	Aspirate;
	public int		AspSpeed;
	public boolean 	Blowout;
	public boolean 	DropTip;
	public boolean 	Suction;
	public boolean 	Echo;
	public boolean 	Grip;
	public boolean 	AutoReturnX;
	public boolean 	AutoReturnY;
	public boolean 	AutoReturnZ;
	public boolean 	Home;
	public float 	OffsetX;
	public float 	OffsetY;
	public float 	OffsetZ;
	public int 		RowA;
	public int 		RowB;
	public float 	Delay;
	public int 		Times;
	public int		Sensor;
	public float	Condition;
	public int		Criterion;
	public int		Negative;
	public boolean	Trace;
	public int		Conversion;
	public int		Mix;
	public float	Grip_timer;
	public float	Suction_timer;
	//public String	Reserved10;
	
	public String 	NumberS;
	//public String Ingredient;
	public String	SpeedS;
	public String 	DepthS;
	public String	ZSpeedS;
	public String 	AspirateS;
	public String	AspSpeedS;
	public String 	BlowoutS;
	public String 	DropTipS;
	public String 	SuctionS;
	public String 	EchoS;
	public String 	GripS;
	public String 	AutoReturnXS;
	public String 	AutoReturnYS;
	public String 	AutoReturnZS;
	public String 	HomeS;
	public String 	OffsetXS;
	public String 	OffsetYS;
	public String 	OffsetZS;
	public String 	RowAS;
	public String 	RowBS;
	public String 	DelayS;
	public String 	TimesS;
	public String	SensorS;
	public String	ConditionS;
	public String	CriterionS;
	public String	NegativeS;
	public String	TraceS;
	public String	ConversionS;
	public String	MixS;
	public String	Grip_timerS;
	public String	Suction_timerS;
	//public String	Reserved10;
	
	
	
	public Command(	int 	number,
					String 	ingredient,
					int		speed,
					float 	depth,
					int		zspeed,
					float 	aspirate,
					int		aspspeed,
					boolean blowout,
					boolean droptip,
					boolean suction,
					boolean echo,
					boolean grip,
					boolean autoreturnx,
					boolean autoreturny,
					boolean autoreturnz,
					boolean home,
					float 	offsetx,
					float 	offsety,
					float 	offsetz,
					int 	rowa,
					int 	rowb,
					float 	delay,
					int 	times,
					int		sensor,
					float	condition,
					int		criterion,
					int		negative,
					boolean trace,
					int 	conversion,
					int 	mix,
					float	grip_timer,
					float	suction_timer)
	{
		Number = number;
		Ingredient = ingredient;
		Speed = speed;
		Depth = depth;
		ZSpeed = zspeed;
		Aspirate = aspirate;
		AspSpeed = aspspeed;
		Blowout = blowout;
		DropTip = droptip;
		Suction = suction;
		Echo = echo;
		Grip = grip;
		AutoReturnX = autoreturnx;
		AutoReturnY = autoreturny;
		AutoReturnZ = autoreturnz;
		Home = home;
		OffsetX = offsetx;
		OffsetY = offsety;
		OffsetZ = offsetz;
		RowA = rowa;
		RowB = rowb;
		Delay = delay;
		Times = times;
		Sensor = sensor;
		Condition = condition;
		Criterion = criterion;
		Negative = negative;
		Trace = trace;
		Conversion = conversion;
		Mix = mix;
		Grip_timer = grip_timer;
		Suction_timer = suction_timer;
		
		stringy();
	}
	
	public Command(){
		Number = 0;
		Ingredient = "";
		Speed = 0;
		Depth = 0f;
		ZSpeed = 0;
		Aspirate = 0f;
		AspSpeed = 0;
		Blowout = false;
		DropTip = false;
		Suction = false;
		Echo = false;
		Grip = false;
		AutoReturnX = false;
		AutoReturnY = false;
		AutoReturnZ = true;
		Home = false;
		OffsetX = 0f;
		OffsetY = 0f;
		OffsetZ = 0f;
		RowA = 0;
		RowB = 0;
		Delay = 0f;
		Times = 0;
		Sensor = 0;
		Condition = 0f;
		Criterion = 0;
		Negative = 0;
		Trace = true;
		Conversion = 0;
		Mix = 0;
		Grip_timer = 0.0f;
		Suction_timer = 0.0f;
		//Reserved10 = "";
		
		stringy();
	}
	
	public Command(Command _command){
		this(	_command.Number,
				_command.Ingredient,
				_command.Speed,
				_command.Depth,
				_command.ZSpeed,
				_command.Aspirate,
				_command.AspSpeed,
				_command.Blowout,
				_command.DropTip,
				_command.Suction,
				_command.Echo,
				_command.Grip,
				_command.AutoReturnX,
				_command.AutoReturnY,
				_command.AutoReturnZ,
				_command.Home,
				_command.OffsetX,
				_command.OffsetY,
				_command.OffsetZ,
				_command.RowA,
				_command.RowB,
				_command.Delay,
				_command.Times,
				_command.Sensor,
				_command.Condition,
				_command.Criterion,
				_command.Negative,
				_command.Trace,
				_command.Conversion,
				_command.Mix,
				_command.Grip_timer,
				_command.Suction_timer);
				
	}
	
	//	GETTERS
	/*
	public int getNumber(){
		return Number;
	}
	
	public String getNumberS(){
		return String.valueOf(Number);
	}
	
	public String getIngredient(){
		return Ingredient;
	}
	
	public int getSpeed(){
		return Speed;
	}
	
	public String getSpeedS(){
		return String.valueOf(Speed);
	}
	
	public int getZSpeed(){
		return ZSpeed;
	}
	
	public String getZSpeedS(){
		return String.valueOf(ZSpeed);
	}
	
	public float getDepth(){
		return Depth;
	}
	
	public String getDepthS(){
		return String.valueOf(Depth);
	}
	
	public float getAspirate(){
		return Aspirate;
	}
	
	public String getAspirateS(){
		return String.valueOf(Aspirate);
	}
	
	public int getAspSpeed(){
		return AspSpeed;
	}
	
	public String getAspSpeedS(){
		return String.valueOf(AspSpeed);
	}
	
	public boolean getBlowout(){
		return Blowout;
	}
	
	public String getBlowoutS(){
		return String.valueOf(Blowout);
	}
	
	public boolean getDropTip(){
		return DropTip;
	}
	
	public String getDropTipS(){
		return String.valueOf(DropTip);
	}
	
	public boolean getSuction(){
		return Suction;
	}
	
	public String getSuctionS(){
		return String.valueOf(Suction);
	}
	
	public boolean getEcho(){
		return Echo;
	}
	
	public String getEchoS(){
		return String.valueOf(Echo);
	}
	
	public boolean getGrip(){
		return Grip;
	}
	
	public String getGripS(){
		return String.valueOf(Grip);
	}
	
	public boolean getAutoReturnX(){
		return AutoReturnX;
	}
	
	public String getAutoReturnXS(){
		return String.valueOf(AutoReturnX);
	}
	
	public boolean getAutoReturnY(){
		return AutoReturnY;
	}
	
	public String getAutoReturnYS(){
		return String.valueOf(AutoReturnY);
	}
	
	public boolean getAutoReturnZ(){
		return AutoReturnZ;
	}
	
	public String getAutoReturnZS(){
		return String.valueOf(AutoReturnZ);
	}
	
	public boolean getHome(){
		return Home;
	}
	
	public String getHomeS(){
		return String.valueOf(Home);
	}
	
	public float getOffsetX(){
		return OffsetX;
	}
	
	public String getOffsetXS(){
		return String.valueOf(OffsetX);
	}
	
	public float getOffsetY(){
		return OffsetY;
	}
	
	public String getOffsetYS(){
		return String.valueOf(OffsetY);
	}
	
	public float getOffsetZ(){
		return OffsetZ;
	}
	
	public String getOffsetZS(){
		return String.valueOf(OffsetZ);
	}
	
	public int getRowA(){
		return RowA;
	}
	
	public String getRowAS(){
		return String.valueOf(RowA);
	}
	
	public int getRowB(){
		return RowB;
	}
	
	public String getRowBS(){
		return String.valueOf(RowB);
	}
	
	public float getDelay(){
		return Delay;
	}
	
	public String getDelayS(){
		return String.valueOf(Delay);
	}
	
	public int getTimes(){
		return Times;
	}
	
	public String getTimesS(){
		return String.valueOf(Times);
	}
	
	public int getSensor(){
		return Sensor;
	}
	
	public String getSensorS(){
		return String.valueOf(Sensor);
	}
	
	public float getCondition(){
		return Condition;
	}
	
	public String getConditionS(){
		return String.valueOf(Condition);
	}
	
	public int getCriterion(){
		return Criterion;
	}
	
	public String getCriterionS(){
		return String.valueOf(Criterion);
	}

	
	public int getNegative(){
		return Negative;
	}
	
	public String getNegativeS(){
		return String.valueOf(Negative);
	}
	
	public boolean getTrace(){
		return Trace;
	}
	
	public String getTraceS(){
		return String.valueOf(Trace);
	}
	
	public int getConversion(){
		return Conversion;
	}
	
	public String getConversionS(){
		return String.valueOf(Conversion);
	}
	
	public int getMix(){
		return Mix;
	}
	
	public String getMixS(){
		return String.valueOf(Mix);
	}
	
	public float getGrip_timer(){
		return Grip_timer;
	}
	
	public String getGrip_timerS(){
		return String.valueOf(Grip_timer);
	}
	
	public float getSuction_timer(){
		return Suction_timer;
	}
	
	public String getSuction_timerS(){
		return String.valueOf(Suction_timer);
	}
	
	public String getReserved10(){
		return Reserved10;
	}
	*/
	
	//	SETTERS
	/*
	public void setNumber(int number){
		Number = number;
	}
	
	public void setIngredient(String ingredient){
		Ingredient = ingredient;
	}
	
	public void setSpeed(int speed){
		Speed = speed;
	}
	
	public void setDepth(float depth){
		Depth = depth;
	}
	
	public void setZSpeed(int zspeed){
		ZSpeed = zspeed;
	}
	
	public void setAspirate(float aspirate){
		Aspirate = aspirate;
	}
	
	public void setAspSpeed(int aspspeed){
		AspSpeed = aspspeed;
	}
	
	public void setBlowout(boolean blowout){
		Blowout = blowout;
	}
	
	public void setDropTip(boolean droptip){
		DropTip = droptip;
	}
	
	public void setSuction(boolean suction){
		Suction = suction;
	}
	
	public void setEcho(boolean echo){
		Echo = echo;
	}
	
	public void setGrip(boolean grip){
		Grip = grip;
	}
	
	public void setAutoReturnX(boolean arx){
		AutoReturnX = arx;
	}
	
	public void setAutoReturnY(boolean ary){
		AutoReturnY = ary;
	}
	
	public void setAutoReturnZ(boolean arz){
		AutoReturnZ = arz;
	}
	
	public void setOffsetX(float ox){
		OffsetX = ox;
	}
	
	public void setOffsetY(float oy){
		OffsetY = oy;
	}
	
	public void setOffsetZ(float oz){
		OffsetZ = oz;
	}
	
	public void setRowA(int rowa){
		RowA = rowa;
	}
	public void setRowB(int rowb){
		RowB = rowb;
	}
	
	public void setDelay(float delay){
		Delay = delay;
	}
	
	public void setTimes(int times){
		Times = times;
	}
	
	public void setSensor(int sensor){
		Sensor = sensor;
	}
	
	public void setCondition(float condition){
		Condition = condition;
	}
	
	public void setCriterion(int criterion){
		Criterion = criterion;
	}
	
	public void setNegative(int negative){
		Negative = negative;
	}
	
	public void setTrace(boolean trace){
		Trace = trace;
	}
	
	public void setConversion(int conversion){
		Conversion = conversion;
	}
	
	public void setReserved10(String reserved10){
		Reserved10 = reserved10;
	}
	*/
	
	
	public String makeString(){
		StringBuilder sb = new StringBuilder();
		sb.append(NumberS)
		.append(",")
		.append(Ingredient)
		.append(",")
		.append(SpeedS)
		.append(",")
		.append(DepthS)
		.append(",")
		.append(ZSpeedS)
		.append(",")
		.append(AspirateS)
		.append(",");
		sb.append(AspSpeedS);
		sb.append(",");
		sb.append(BlowoutS);
		sb.append(",");
		sb.append(DropTipS);
		sb.append(",");
		sb.append(SuctionS);
		sb.append(",");
		sb.append(EchoS);
		sb.append(",");
		sb.append(GripS);
		sb.append(",");
		sb.append(AutoReturnXS);
		sb.append(",");
		sb.append(AutoReturnYS);
		sb.append(",");
		sb.append(AutoReturnZS);
		sb.append(",");
		sb.append(HomeS);
		sb.append(",");
		sb.append(OffsetXS);
		sb.append(",");
		sb.append(OffsetYS);
		sb.append(",");
		sb.append(OffsetZS);
		sb.append(",");
		sb.append(RowAS);
		sb.append(",");
		sb.append(RowBS);
		sb.append(",");
		sb.append(DelayS);
		sb.append(",");
		sb.append(TimesS);
		sb.append(",");
		sb.append(SensorS);
		sb.append(",");
		sb.append(ConditionS);
		sb.append(",");
		sb.append(CriterionS);
		sb.append(",");
		sb.append(NegativeS);
		sb.append(",");
		sb.append(TraceS);
		sb.append(",");
		sb.append(ConversionS);
		sb.append(",");
		sb.append(MixS);
		sb.append(",");
		sb.append(Grip_timerS);
		sb.append(",");
		sb.append(Suction_timerS);
		
		return sb.toString();
	}
	
	public void clear(){
		Number = 0;
		Ingredient = "";
		Speed = 0;
		Depth = 0f;
		ZSpeed = 0;
		Aspirate = 0f;
		AspSpeed = 0;
		Blowout = false;
		DropTip = false;
		Suction = false;
		Echo = false;
		Grip = false;
		AutoReturnX = false;
		AutoReturnY = false;
		AutoReturnZ = true;
		Home = false;
		OffsetX = 0f;
		OffsetY = 0f;
		OffsetZ = 0f;
		RowA = 0;
		RowB = 0;
		Delay = 0f;
		Times = 0;
		Sensor = 0;
		Condition = 0f;
		Criterion = 0;
		Negative = 0;
		Trace = true;
		Conversion = 0;
		Mix = 0;
		Grip_timer = 0f;
		Suction_timer = 0f;
		
		stringy();
	}
	
	public void stringy(){
		NumberS=String.valueOf(Number);
		SpeedS=String.valueOf(Speed);
		DepthS=String.valueOf(Depth);
		ZSpeedS=String.valueOf(ZSpeed);
		AspirateS=String.valueOf(Aspirate);
		AspSpeedS=String.valueOf(AspSpeed);
		BlowoutS=String.valueOf(Blowout);
		DropTipS=String.valueOf(DropTip);
		SuctionS=String.valueOf(Suction);
		EchoS=String.valueOf(Echo);
		GripS=String.valueOf(Grip);
		AutoReturnXS=String.valueOf(AutoReturnX);
		AutoReturnYS=String.valueOf(AutoReturnY);
		AutoReturnZS=String.valueOf(AutoReturnZ);
		HomeS=String.valueOf(Home);
		OffsetXS=String.valueOf(OffsetX);
		OffsetYS=String.valueOf(OffsetY);
		OffsetZS=String.valueOf(OffsetZ);
		RowAS=String.valueOf(RowA);
		RowBS=String.valueOf(RowB);
		DelayS=String.valueOf(Delay);
		TimesS=String.valueOf(Times);
		SensorS=String.valueOf(Sensor);
		ConditionS=String.valueOf(Condition);
		CriterionS=String.valueOf(Criterion);
		NegativeS=String.valueOf(Negative);
		TraceS=String.valueOf(Trace);
		ConversionS=String.valueOf(Conversion);
		MixS=String.valueOf(Mix);
		Grip_timerS=String.valueOf(Grip_timer);
		Suction_timerS=String.valueOf(Suction_timer);
	}
}
