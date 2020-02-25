
#include "types.h"
#include "trace.h"
#include "constants.h"
#include "specialsignatures.h"
#include "specialclasses.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "configure.h"
#include "interpreter.h"
#include "memory.h"
#include "exceptions.h"

#define NO_OWNER 0x00

/**
 * Thread currently being executed by engine().
 */
Thread* currentThread;
byte gThreadCounter;

StackFrame *current_stackframe()
{
  byte arraySize;

  arraySize = currentThread->stackFrameArraySize;
  // TBD: is the following needed?
  if (arraySize == 0)
    return null;
  return stackframe_array() + (arraySize - 1);
}

static inline byte get_thread_id (const Object *obj)
{
  return (byte) ((obj->syncInfo & THREAD_MASK) >> THREAD_SHIFT);
}

static inline void set_thread_id (Object *obj, const byte threadId)
{
  obj->syncInfo = (obj->syncInfo & ~THREAD_MASK) | 
                  ((TWOBYTES) threadId << THREAD_SHIFT);
}

static inline void inc_monitor_count (Object *obj)
{
  obj->syncInfo = (obj->syncInfo & ~COUNT_MASK) | 
                   ((obj->syncInfo & COUNT_MASK) + 1);
}

static inline void set_monitor_count (Object *obj, const byte count)
{
  obj->syncInfo = (obj->syncInfo & ~COUNT_MASK) | count;
}

/**
 * @return false iff ran out of memory or already started. 
 */
boolean init_thread (Thread *thread)
{
  thread->threadId = gThreadCounter+1;
  if (thread->state != DEAD || thread->threadId == NO_OWNER)
  {
    // Thread already initialized?
    // This assumes object creation sets state field to zero (DEAD).
    throw_exception (outOfMemoryError);
    return false;
  }
  thread->stackFrameArray = ptr2word (new_primitive_array (T_STACKFRAME, MAX_STACK_FRAMES));
  if (thread->stackFrameArray != JNULL)
    thread->stackArray = ptr2word (new_primitive_array (T_INT, STACK_SIZE));
  else
    thread->stackArray = JNULL;
  if (thread->stackArray == JNULL)
  {
    // Exception already thrown.
    return false;      
  }
  gThreadCounter++;

  #ifdef VERIFY
  assert (is_array (word2obj (thread->stackFrameArray)), THREADS0);
  assert (is_array (word2obj (thread->stackArray)), THREADS1);
  #endif

  thread->stackFrameArraySize = 0;
  thread->state = STARTED;
  if (currentThread == null)
  {
    currentThread = thread;
    #if DEBUG_THREADS
    printf ("First-time init of currentThread: %d\n", (int) currentThread);
    #endif
  }
  else
  {
    thread->nextThread = currentThread->nextThread;
  }
  currentThread->nextThread = ptr2word (thread);
  return true;
}

/**
 * Switches to next thread.
 * @return false iff there are no live threads
 *         to switch to.
 */
void switch_thread()
{
  Thread *anchorThread;
  Thread *nextThread;
  StackFrame *stackFrame;
  boolean liveThreadExists;

  #if DEBUG_THREADS || DEBUG_BYTECODE
  printf ("\n$$$--- switch_thread: currentThread at %d\n", (int) currentThread);
  #endif

  #ifdef VERIFY
  assert (currentThread != null, THREADS0);
  #endif
  
  anchorThread = currentThread;
  liveThreadExists = false;
  // Save context information
  stackFrame = current_stackframe();

  #if DEBUG_THREADS
  printf ("switchThread: current stack frame: %d\n", (int) stackFrame);
  #endif
  
  #ifdef VERIFY
  assert (stackFrame != null || currentThread->state == STARTED,
          THREADS4);
  #endif

  if (stackFrame != null)
  {
    stackFrame->pc = pc;
    stackFrame->stackTop = stackTop;

    #if DEBUG_THREADS
    printf ("Saving stackFrame before switching:\n"
            "-- pc: %d\n"
            "-- stackTop: %d\n",
            (int) pc, (int) stackTop);
    #endif
  }

  // Loop until a RUNNING frame is found
 LABEL_TASKLOOP:
  switch_thread_hook();
  if (gMustExit)
    return;
  nextThread = (Thread *) word2ptr (currentThread->nextThread);
  if (nextThread->state == WAITING)
  {
    #ifdef VERIFY
    assert (nextThread->waitingOn != JNULL, THREADS3);
    #endif

    if (get_thread_id (word2obj (nextThread->waitingOn)) == NO_OWNER)
    {
      // Note: You can only call enter_monitor on the
      // currentThread.
      currentThread = nextThread;
      // NOW enter the monitor (guaranteed to succeed)
      enter_monitor (word2obj (currentThread->waitingOn));
      // Let the thread run.
      currentThread->state = RUNNING;
      #ifdef SAFE
      currentThread->waitingOn = JNULL;
      #endif
      goto LABEL_UPDATE_THREAD;
    }
  }

  if (nextThread->state == DEAD)
  {
    // This order of deallocation is important
    free_array ((Object *) word2ptr (nextThread->stackArray));
    free_array ((Object *) word2ptr (nextThread->stackFrameArray));

    #ifdef SAFE
    nextThread->stackFrameArray = JNULL;
    nextThread->stackArray = JNULL;
    #endif SAFE

    if (nextThread == anchorThread)
    {
      if (!liveThreadExists)
      {
        #if DEBUG_THREADS
        printf ("switch_thread: all threads are dead: %d\n", (int) nextThread);
        #endif
        #ifdef SAFE
        currentThread = null;
        #endif
        gMustExit = true;
        return;
      }
      // The current anchorThread will be removed.
      anchorThread = currentThread;
    }

    // Remove nextThread from linked list.
    nextThread = (Thread *) word2ptr (nextThread->nextThread);
    currentThread->nextThread = ptr2word (nextThread);
  }
  else
  {
    liveThreadExists = true;
  }  

  currentThread = nextThread;
  if (currentThread->state == STARTED)
  {
    // Put stackTop at the beginning of the stack so we can push arguments
    // to entry methods.
    stackTop = stack_array();
    currentThread->state = RUNNING;
    if (currentThread == bootThread)
    {
      ClassRecord *classRecord;

      classRecord = get_class_record (ENTRY_CLASS);
      // Push fake parameter:
      *stackTop = JNULL;
      // Push stack frame for main method:
      dispatch_special (classRecord, find_method (classRecord, MAIN_V), null);
      // Push another if necessary for the static initializer:
      dispatch_static_initializer (classRecord, pc);
    }
    else
    {
      *stackTop = ptr2word (currentThread);
      dispatch_virtual ((Object *) currentThread, RUN_V, null);
    }
  }

  #if DEBUG_THREADS
  printf ("switch_thread: considered thread %d: %d\n", (int) currentThread,
          (int) (currentThread->state == RUNNING));
  #endif

  if (currentThread->state != RUNNING)
    goto LABEL_TASKLOOP;
 LABEL_UPDATE_THREAD:
  stackFrame = current_stackframe();
  pc = stackFrame->pc;
  stackTop = stackFrame->stackTop;
  localsBase = stackFrame->localsBase;
  
  // Check if this thread has been requested to
  // commit suicide.
  if (gRequestSuicide)
    throw_exception (threadDeath);
}

/**
 * currentThread enters obj's monitor.
 * Note that this operation is atomic as far as the program is concerned.
 */
void enter_monitor (Object* obj)
{
  byte owner;
  byte tid;

  
  if (obj == JNULL)
  {
    throw_exception (nullPointerException);
    return;
  }
  owner = get_thread_id (obj);
  tid = currentThread->threadId;
  if (owner != NO_OWNER && tid != owner)
  {
    // Make thread wait until the monitor is relinquished.
    currentThread->state = WAITING;
    currentThread->waitingOn = ptr2word (obj);
    // Gotta yield
    switch_thread();    
    return;
  }
  set_thread_id (obj, tid);
  inc_monitor_count (obj);

}

void exit_monitor (Object* obj)
{
  byte newMonitorCount;

  if (obj == JNULL)
  {
    // Exiting due to a NPE on monitor_enter [FIXME]
    return;
  }

  #ifdef VERIFY
  assert (get_thread_id(obj) == currentThread->threadId, THREADS5);
  assert (get_monitor_count(obj) > 0, THREADS2);
  #endif

  newMonitorCount = get_monitor_count(obj)-1;
  if (newMonitorCount == 0)
    set_thread_id (obj, NO_OWNER);
  set_monitor_count (obj, newMonitorCount);
}





