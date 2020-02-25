package java.lang;

/**
 * Mathematical operations.
 */
public class Math
{
  public static int abs (int n)
  {
    return n >= 0 ? n : -n;
  }
  
  public static int max (int n1, int n2)
  {
    return n1 > n2 ? n1 : n2;
  }
  
  public static int min (int n1, int n2)
  {
    return n1 < n2 ? n1 : n2;
  }
}
  
