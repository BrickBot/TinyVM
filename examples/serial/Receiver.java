import tinyvm.rcx.*;

/**
 * This program listens for messages from your PC.
 * It accepts a few standard opcodes, such as
 * Set Motor On/Off (0x21). You should be able to
 * control it using the Lego Mindstorms GUI.
 */
public class Receiver
{
  static byte[] iPacket = new byte[10];
  
  public static void main (String[] arg)
  {
    Sensor.S2.activate();
    LCD.showNumber (1111);
    int pCount = 0;
    for (;;)
    {
      if (Serial.isPacketAvailable())
      {
        Serial.readPacket (iPacket);
        int pOpCode = iPacket[0] & 0xF7;
	if (pOpCode == 0x10)
	{
            // Alive?
            iPacket[0] = (byte) ~(iPacket[0] & 0xFF);
            Serial.sendPacket (iPacket, 0, 1);
	}
        else if (pOpCode == 0x15)
	{
            // Get versions
            iPacket[0] = (byte) ~(iPacket[0] & 0xFF);
            iPacket[1] = 0x00;
            iPacket[2] = 0x03;
            iPacket[3] = 0x00;
            iPacket[4] = 0x01;
            iPacket[5] = 0x00;
            iPacket[6] = 0x03;
            iPacket[7] = 0x00;
            iPacket[8] = 0x09;
            Serial.sendPacket (iPacket, 0, 9);
        }
  	else if (pOpCode == 0x30)
	{
	    // Get battery power
            iPacket[0] = (byte) ~(iPacket[0] & 0xFF);
            iPacket[1] = 0x20;
            iPacket[2] = 0x40;
            Serial.sendPacket (iPacket, 0, 3);
	}
  	else if (pOpCode == 0x65)
	{
            // Delete firmware
            iPacket[0] = (byte) ~(iPacket[0] & 0xFF);
            Serial.sendPacket (iPacket, 0, 1);
            // Exit program
            return;
	}
        else if (pOpCode == 0x21)
	{
	    // Set Motor On/Off
            int pFlags = iPacket[1] & 0xFF;
            if ((pFlags & 0x01) != 0)
	    {
              if ((pFlags & 0x040) != 0) 
                Motor.A.stop();
              if ((pFlags & 0x080) != 0) 
                Motor.A.forward();
	    }
            if ((pFlags & 0x02) != 0)
	    {
              if ((pFlags & 0x040) != 0) 
                Motor.B.stop();
              if ((pFlags & 0x080) != 0) 
                Motor.B.forward();
	    }
            if ((pFlags & 0x04) != 0)
	    {
              if ((pFlags & 0x040) != 0) 
                Motor.C.stop();
              if ((pFlags & 0x080) != 0) 
                Motor.C.forward();
	    }
            iPacket[0] = (byte) ~(iPacket[0] & 0xFF);
            Serial.sendPacket (iPacket, 0, 1);
	}
  	else
	{
            LCD.showNumber (iPacket[0]);
  	    iPacket[0] = (byte) ~(iPacket[0] & 0xFF);
            Serial.sendPacket (iPacket, 0, 1);
            Sound.beep();
	}
      }
    }
  }
}

