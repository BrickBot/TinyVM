Version 0.2.6
-------------

  - Fixed a memory leak bug.
  - Increased stack size.
  - Fixed multi-dimensional array allocation bug, reported
    by K. Eide.
  
Version 0.2.5
-------------

  - Fixed linker bug: it was dumping a ZipException for
    non-existing zip/jar entries. Reported by E. Harshbarger.
    
  - Fixed tvmc/tvmp bug: Scripts were using -classpath instead
    of -bootclasspath. The effect was that java.util.Random
    couldn't be used with Java 2. Reported by J. Knudsen. The 
    new scripts work only with a Java 2 compiler, but tvmc1 
    and tvmp1 are provided for JDK 1.1.
  
Version 0.2.4
-------------

  - Fixed another bug that has to do with negative 
    numbers. In this case, BIPUSH didn't work properly
    in the RCX because char is unsigned by default.
    The bug was reported by E. Harshbarger.
    
Version 0.2.3
-------------

  - Fixed bug reported by S. Hilmer: Fields of types
    byte and short could not hold negative values.

Version 0.2.2
-------------

  - Added auto power off. The RCX is switched off
    after 8 minutes in download mode.

  - Worked around a bug in the latest CygWin version;
    tvm_firmdl3.c was not working. Now it works, but
    only in slow mode.
    
  - Scripts only worked with CygWin B20. Added check
    for $OSTYPE = "cygwin".
    
Version 0.2.1
-------------

  - Added a little check in tvmfirmdl to see if 
    old-tvmfirmdl exists. It reminds users to run
    make.
  - Now shipping Kekoa Proudfoot's firmdl3.c, 
    slightly modified (tools/tvm_firmdl3.c). This
    is a faster and apparently more reliable 
    firmware downloader. Changed the default speed
    to slow, because it seems to be more reliable.
    Use tvmfirmdl --fast for fast downloads.
  - Removed some dead code that we had in case
    support for longs and floats was added. But now
    there's leJOS, of course. Footprint is similar 
    to that of 0.1.1.
  - The firmware responds to <transfer data> packets.
    This appears to make program downloading more
    reliable. Of course, this also means old firmware is
    imcompatible with this version.
    
Version 0.2.0
-------------

  - Time and Timer classes added. Time allows access to
    the system_time, and sleep() functionality. Timer
    is similar to javax.swing.Timer. Contributed
    by Ryan VanderBijl.
  - Added system time abilities. System time is the
    number of milliseconds since the RCX was reset.
    Contributed by Ryan VanderBijl.
  - Added TextLCD class for displaying text to the LCD.
    Contributed by Ryan VanderBijl, who borrowed code from
    legOS. Original work by Markus L. Noga.
  - Added a HelloWorld example program, by Ryan VanderBijl.
    This one prints "hello world" on the LCD. The old
    hello example was removed.
  - Added a LineFollow example program, by Ryan VanderBijl
  - Fixed apparent Thread.start() bug: the native
    method implementation was not unwinding the stack as
    it should've. 
  - Added Thread.yield().
  - Added serial2rcx example, contributed by
    Schiedermeier Reinhard.
  - Fixed export's in shell scripts; they were only valid
    for bash. Ryan pointed this out, BTW.
  - Changed magic number to 0x1CAF: Old binaries will not
    be compatible with new firmware, and vice versa. The
    firmware makes sure the magic number is right only
    starting with this version.
  - The linker now makes sure there's a static main method in
    the entry class.
  - Added java.lang.Math (not really useful methods there), 
    java.util.Random, and java.util.BitMap.
  - The firmware can now rerun programs. If a program is
    downloaded, the firmware does not execute it
    immediately anymore. You have to press Run. When the
    program finishes, the firmware goes back to program-download
    mode, but you can rerun the program. 
    NOTE that reruning a program does not reinitialize
    memory. All static state is preserved, in addition
    to the main thread, which is reused. In effect, all
    of TinyVM's memory is persistent across runs, until
    the next download occurs. This means that 
    the RCX will eventually run out of memory
    if objects are allocated in non-initializers. 
    Additionally, it makes the implementation of daemon 
    threads unfeasible, and implies that Thread.start()
    may be called again on a dead thread. This may also 
    affect the way you write programs for TinyVM.
  - If your program runs out of memory, it will be
    deleted. You have to download it again, as explained 
    above.
  - If you turn your RCX off while a program is running,
    it may actually not shut down. You'll hear upward
    tones instead. This can happen if there's a deadlock
    in your program (or maybe even if there's an infinite 
    loop inside a try-catch-throwable block, although I
    couldn't come up with a example). The reasons for
    this are the new rerun semantics previously described. 
    A program cannot be interrupted and restarted at
    main() without potentially breaking the runtime. 
    All threads must be allowed to finish normally,
    releasing monitors, etc. (So the persistence thing
    previously described is, in essense, a limitation
    turned into a feature).
  - As a workaround for the previous problem, you can
    press Prgm-On/Off to *delete* a program from memory.
  - The serial/sensor bug mentioned in release notes for
    0.1.4 should be correctly fixed now.
  - Fixed apparent exception bug, found while revising
    the code: if an uncaught exception occurs in one thread,
    a different thread could die as a result. This hasn't
    been reported, possibly because most uncaught exceptions
    result in program death anyway.
  - Fixed array deallocation bug: it was not deallocating
    all of the stack frame array.
  - throw_exception now checks if currentThread is null.
    With rerun, it's now possible for init_thread to throw
    OutOfMemoryError before any context exists.
  - Simplified allocation routine.
  - Added a bunch of Thread.yield() calls in the sensor
    listener loop. In a program that registers one listener
    with each sensor, the performance seems to improve by
    roughly 50%, i.e. the performance of the fourth thread.
    However, sensor events are not delivered as often.
    Comments?
  - Added Thread.isAlive(). This also has to do with the
    new rerun semantics. Call it before starting a thread.
  - Footprint report: Firmware download count is 1080;
    Test13 can create 4060 instances of java.lang.Object.
    Expect an improvement, as the code is due for an
    optimization.
    
Version 0.1.6:
--------------

  - Added Motor.flt() to float motors.
  
  - Inserted a small delay after program exit,
    before entering download mode, so that Motor.stop()
    has the expected effect.

Version 0.1.5:
--------------

  - Using O_BINARY in open() calls, for CygWin 
    compatibility.

  - Modified scripts to be more compatible with
    CygWin.

Version 0.1.4:
--------------
 
  - Fixed serial bug reported by Tim Rinkens. (Sensors
    were shut down and couldn't get reactivated as soon
    as the static initializer of Serial was executed.
    They are still deactivated when Serial is first used,
    but they can be reactivated now. I don't know 
    how to do better than that at present).

  - Added the view example.

  - Fixed shutdown_timer bug. (On power shutdown,
    shutdown_timer should've been called; this means
    that motors could still keep going after the power
    was shut down).

Version 0.1.3:
--------------

  - Found that the hello example did not compile in
    the last version. Fixed it.

  - Decided to make the firmware show battery power 
    whenever it enters program-download mode.

  - Fixed virtual method linker bug. (The linker was
    unable to find certain virtual methods in classfiles
    compiled with JDK1.2.x).

  - Apparently fixed On/Off bug. (The LCD is not cleared 
    by shutdown_buttons in some RCXs).

  - Got rid of TINYVM_HOME.

  - TINYVMPATH is no longer necessary. CLASSPATH is
    used.

  - More appropriate message for linker failure when
    instanceof or checkcast of array classes is 
    encountered.

  - Multi-dimensional array allocation was broken.
    Fixed it.

  - Fixed checkcast bug. (It was not allowing casts of
    null).

  - Footprint report:
    Test13 creates 4238 objects.
    Firmware download count: ~1020

Version 0.1.2:
--------------

  - Fixed serial comm bug. (If you tried to download
    a program to the RCX while another program was
    running, the transferred data could clobber 
    program memory. TinyVM is now shutting down
    serial communications right after a program is
    downloaded).

  - Added a couple dummy classes to satisfy the
    jikes compiler. Users simply have to set JAVAC
    to "jikes". (Jikes is a really fast Java compiler
    by IBM).

  - Changed implementation of Sensor.readSensorValue.
    Reading values from sensors should be more
    efficient now. (I might have to adjust the rotator
    example a bit).

  - Publicized the SensorListener interface and
    Sensor.addListener. It probably still needs to
    be optimized for performance.

  - Added examples to some classes in the API
    documentation.

  - Removed "queued" parameter from most methods in
    Sound. It really didn't make sense to have it 
    in some APIs. Hopefully that won't be much of a 
    problem for anyone.

  - Removed previously deprecated APIs from ROM.
    Again, sorry if this affects you.

  - Fixed MONITORENTER bug. (It was not throwing
    NPE on null monitors - it was crashing instead).

  - TinyVM's linker is now also a loader: The -d
    option will download the program to the RCX.

  - TinyVM's firmware is now listening for the 
    standard "Delete Firmware" opcode (0x65/0x6d).
    Consequently, Prgm-Run On/Off is no longer
    useful, so it has been removed.

  - tvmfirmdl doesn't need an argument now.

  - Added the Serial API and a corresponding example.

  - Footprint report:
    Test13 creates 4262 objects.
    The firmware download count is about 1000.    

Version 0.1.1:
--------------

  - Fixed PUTSTATIC bug. (Setting a static field
    in uninitialized class could produce subsequent
    erroneous behavior).

  - Fixed exceptions bug. (Very critical bug:
    exceptions were not pushed on the stack before
    they were caught).

  - Changed RCX API so that its style conforms
    a bit more with other Java APIs. Namely, 
    deprecated routines in ROM, and split them up
    into LCD and Sound, which have more convenient
    APIs. Removed some ROM routines, which hopefully
    haven't been used too much yet. Class Native
    now contains native APIs. ROM contains certain
    unclassified APIs.

  - Added buttons API.

  - Improved the usability of the firmware a bit.
    Basically, you can now press the On/Off button
    to turn off the RCX and kill running programs.

  - To continue on exception "traces" you have to
    press On/Off instead of View.

  - TinyVM has been ported to JDK1.2. This required
    a couple of fixes under jtools.

  - Footprint: Test13 can create 4338 empty objects
    (17352 bytes of allocation space).









