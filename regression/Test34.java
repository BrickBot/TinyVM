
import tinyvm.rcx.*;
import java.util.Random;

public class Test34
{
    static Random RAND = new Random (Time.currentTimeMillis());
    
    public static void main (String[] args) 
    {
      LCD.showNumber (Math.abs(RAND.nextInt()) % 100);
      Time.sleep (500);      
    }
}
