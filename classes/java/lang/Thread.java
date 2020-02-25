package java.lang;

/**
 * A thread (task) of execution.
 */
public abstract class Thread
{
  // Note 1: This class cannot have a static initializer.

  // Note 2: The following fiels are used by the VM.
  // Their sizes and location can only be changed
  // if classes.h is changed accordingly. Needless
  // to say, they are read-only.

  private Thread _TVM_nextThread;
  private Object _TVM_waitingOn;
  private Object _TVM_stackFrameArray;
  private Object _TVM_stackArray;
  private byte _TVM_stackFrameArraySize;
  private byte _TVM_threadId; 
  private byte _TVM_state; 

  public Thread()
  {
  }
  
  public boolean isAlive()
  {
    return _TVM_state != 0;
  }
  
  /**
   * Starts this thread. May throw OutOfMemoryError if
   * more than 254 threads are started.
   */
  public native void start();

  /**
   * Must be implemented by subclasses.
   * Not to be called directly, execution
   * of this method is scheduled by start().
   */
  public abstract void run();

  /**
   * Preempts the current thread.
   */
  public static native void yield();
}



