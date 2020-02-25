/**
 * tvmemul.c
 * Entry source file for TinyVM emulator.
 *
 * HISTORY
 * -------
 * First version - By Jose Solorzano
 */

#include <stdlib.h>
#include "types.h"
#include "constants.h"
#include "classes.h"
#include "threads.h"
#include "specialclasses.h"
#include "specialsignatures.h"
#include "language.h"
#include "memory.h"
#include "interpreter.h"
#include "exceptions.h"
#include "load.h"
#include "trace.h"
#include <sys/time.h>

#define DEBUG_RUNS  0
#define MEMORY_SIZE 8192 /* 16 Kb */

Thread   *bootThread;
TWOBYTES *gMemory;
TWOBYTES gMemorySize = MEMORY_SIZE;

extern struct timeval start;

void switch_thread_hook()
{
  // NOP
}

void run(void)
{
  #if DEBUG_RUNS
  int count = 0;
  #endif
  
  // Initialize time count
  if (gettimeofday (&start, NULL))
    perror ("Unable to gettimeofday.\n");
  // Initialize memory
  gMemory = (TWOBYTES *) malloc (gMemorySize * sizeof (TWOBYTES));
  init_memory (gMemory, gMemorySize);
  // Initialize exceptions
  init_exceptions();
  // Create the boot thread (bootThread is a special global)
  bootThread = (Thread *) new_object_for_class (JAVA_LANG_THREAD);
  #if DEBUG_THREADS
  printf ("Created bootThread: %d. Initializing...\n", (int) bootThread);
  #endif
  
  #if DEBUG_RUNS
  for (count = 0; count < 2; count++)
  {
  #endif DEBUG_RUNS
  
  init_threads();
  if (!init_thread (bootThread))
  {
    printf ("Unable to initialize threading module.\n");
    exit (1);	  
  }
  // Execute the bytecode interpreter
  engine();
  // Engine returns when all non-daemon threads are dead
  #if DEBUG_STARTUP
  printf ("Engine finished.\n");
  #endif

  #if DEBUG_RUNS
  }
  #endif DEBUG_RUNS
}

int main (int argc, char *argv[])
{
  if (argc != 2)
    abort_tool ("Use: %s <path>\n", argv[0]);
  #if DEBUG_STARTUP
  printf ("Reading binary %s\n", argv[1]);
  #endif
  readBinary (argv[1]);
  run();
  return 0;
} 



