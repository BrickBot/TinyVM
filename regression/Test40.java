import tinyvm.rcx.*;

public class Test40
{
 public static void main(String[] args)
 throws Exception
 {
  int[][] array = new int[30][2];

  LCD.showNumber(array.length);
  Time.sleep(2000);
  LCD.showNumber(array[0].length);
  Time.sleep(2000);
 }
}

