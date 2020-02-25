import tinyvm.rcx.*;

// Not being able to turn the RCX off
// due to deadlock.

public class Test32
{
  static Object monitor1 = new Object();
  static Object monitor2 = new Object();
  
  public static void method1()
  {
    synchronized (monitor1)
    {
      for (int k = 0; k < 100; k++) { }
      synchronized (monitor2)
      {
        for (int k = 0; k < 2000; k++) { }	      
      }
    }
  }
  
  public static void method2()
  {
    synchronized (monitor2)
    {
      for (int k = 0; k < 1000; k++) { }
      synchronized (monitor1)
      {
        for (int k = 0; k < 1000; k++) { }	      
      }
    }
  }

  public static void main (String[] arg)
  {
    new Thread()
    {
      public void run()
      {
        for (int i = 0; i < 1000; i++)
	{
          method1();		
	}
      }
    }.start();
    
    new Thread()
    {
      public void run()
      {
        for (int i = 0; i < 120; i++) { }
	method2();
      }      
    }.start();
    
  }
}
