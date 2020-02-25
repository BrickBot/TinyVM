import tinyvm.rcx.*;

// Not being able to turn the RCX off.
// Doesn't actually work!

public class Test31
{
  public static void main (String[] arg)
  {
    for (;;)
    {
      try 
      {
        for (int k = 0; k < 10000; k++) { }
      }
      catch (Throwable t)
      {
      }
    }
  }
}
