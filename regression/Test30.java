import tinyvm.rcx.*;

public class Test30
{
  public static void main (String[] aArg)
  {
     SensorListener pSL = new SensorListener() 
     {
	public void stateChanged(Sensor i, boolean val) 
	{
	}
	
	public void stateChanged(Sensor i, int val) 
	{
	   LCD.showNumber(val);
	}
     };
     Sensor.S1.activate();
     Sensor.S2.activate();
     Sensor.S3.activate();
     Sensor.S1.addSensorListener (pSL);
     Sensor.S2.addSensorListener (pSL);
     Sensor.S3.addSensorListener (pSL);
     for (;;)
     {
       for (int i = 0; i < 10000; i++) { }
       Sound.beep();
     }
  }
}
