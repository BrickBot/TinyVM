
#include "types.h"
#include "trace.h"
#include "constants.h"
#include "specialsignatures.h"
#include "specialclasses.h"
#include "exceptions.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "configure.h"
#include "interpreter.h"
#include "memory.h"

Object *outOfMemoryError;
Object *noSuchMethodError;
Object *stackOverflowError;
Object *nullPointerException;
Object *classCastException;
Object *arithmeticException;
Object *arrayIndexOutOfBoundsException;
Object *threadDeath;

// Temporary globals:

static TWOBYTES gCurrentOffset;
static MethodRecord *gMethodRecord = null;
static StackFrame *gStackFrame;
static ExceptionRecord *gExceptionRecord;
static byte gNumExceptionHandlers;
static MethodRecord *gExcepMethodRec = null;
#ifdef EMULATE
static byte *gExceptionPc;
#endif

void init_exceptions()
{
  outOfMemoryError = new_object_for_class (JAVA_LANG_OUTOFMEMORYERROR);
  noSuchMethodError = new_object_for_class (JAVA_LANG_NOSUCHMETHODERROR);
  stackOverflowError = new_object_for_class (JAVA_LANG_STACKOVERFLOWERROR);
  nullPointerException = new_object_for_class (JAVA_LANG_NULLPOINTEREXCEPTION);
  classCastException = new_object_for_class (JAVA_LANG_CLASSCASTEXCEPTION);
  arithmeticException = new_object_for_class (JAVA_LANG_ARITHMETICEXCEPTION);
  arrayIndexOutOfBoundsException = new_object_for_class (JAVA_LANG_ARRAYINDEXOUTOFBOUNDSEXCEPTION);
  threadDeath = new_object_for_class (JAVA_LANG_THREADDEATH);
}

/**
 * @return false iff all threads are dead.
 */
void throw_exception (Object *exception)
{
  Thread *auxCurrentThread;
  
  if (currentThread == null)
  {
    // No context
    return;
  }
  #ifdef VERIFY
  assert (exception != null, EXCEPTIONS0);
  #endif
  #if EMULATE
  gExceptionPc = pc;
  #endif
  gExcepMethodRec = null;

  #if 0
  trace (-1, get_class_index(exception), 3);
  #endif

 LABEL_PROPAGATE:
  gStackFrame = current_stackframe();
  gMethodRecord = gStackFrame->methodRecord;
  if (gExcepMethodRec == null)
    gExcepMethodRec = gMethodRecord;
  gExceptionRecord = (ExceptionRecord *) (get_binary_base() + gMethodRecord->exceptionTable);
  gCurrentOffset = ptr2word(pc) - ptr2word(get_binary_base() + gMethodRecord->codeOffset);

  #if 0
  trace (-1, gCurrentOffset, 5);
  #endif

  gNumExceptionHandlers = gMethodRecord->numExceptionHandlers;
  while (gNumExceptionHandlers--)
  {
    if (gCurrentOffset >= gExceptionRecord->start && gCurrentOffset <= gExceptionRecord->end)
    {
      // Check if exception class applies
      if (instance_of (exception, gExceptionRecord->classIndex))
      {
        // Clear operand stack:
        stackTop = gStackFrame->localsBase + gMethodRecord->numLocals;
        *stackTop = ptr2word(exception);
        //gStackFrame->stackTop = stackTop;
        // Jump to handler:
        pc = get_binary_base() + gMethodRecord->codeOffset + 
             gExceptionRecord->handler;
        //gStackFrame->pc = pc;
        return;
      }
    }
    gExceptionRecord++;
  }
  // Save currentThread in temporary.
  auxCurrentThread = currentThread;
  // Go up the stack.
  do_return (0);
  // Note: return takes care of synchronized methods.
  if (auxCurrentThread->state == DEAD)
  {
    // do_return must have set the state to DEAD.
    byte pClassIndex = get_class_index (exception);
    if (pClassIndex != JAVA_LANG_THREADDEATH)
    {
      // Traces are not shown for uncaught instances of java.lang.ThreadDeath.
      #ifdef EMULATE
      printf ("*** UNCAUGHT EXCEPTION/ERROR: \n");
      printf ("--  Exception class   : %d\n", (int) pClassIndex);
      printf ("--  Thread            : %d\n", (int) auxCurrentThread->threadId);
      printf ("--  Method signature  : %d\n", (int) gExcepMethodRec->signatureId);
      printf ("--  Root method sig.  : %d\n", (int) gMethodRecord->signatureId);
      printf ("--  Bytecode offset   : %d\n", (int) gExceptionPc - 
              (int) get_code_ptr(gExcepMethodRec));
	  
      printf ("\n(Note: tvmld and tvmld-emul take a -verbose option, "
              "and you can disassemble classes with tvmp or javap).\n");
      #else
    
      trace (4, gExcepMethodRec->signatureId, pClassIndex % 10);

      #endif EMULATE
    }
    // We're done. 
    return;
  }
  // Try again to find handler in current stack frame.
  goto LABEL_PROPAGATE; 
}




