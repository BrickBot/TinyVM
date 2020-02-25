
import tinyvm.rcx.*;
import java.util.Random;

public class Test35
{
    public static void main (String[] args) 
    {
      Sensor.S1.activate();
      int t1 = Time.currentTimeMillis();
      for (int i = 0; i < 500; i++)
      {
	//Sensor.readSensorValue ((short) 0x1000, (byte) 0, (byte) 0);
	//Time.currentTimeMillis();
      }
      int t2 = Time.currentTimeMillis();
      LCD.showNumber (t2 - t1);
      Button.VIEW.waitForPressAndRelease();
    }
}
