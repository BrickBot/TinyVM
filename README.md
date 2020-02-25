
Summary
-------

1. JDK's and TinyVM's bin directories must be in PATH.
2. 'make' completes installation.
3. RCXTTY is serial device (e.g. com1 or /dev/ttyS0).

Firmware   : tvmfirmdl
Compiling  : tvmc <class>.java
Running    : tvmld <class> -d

Exceptions: 
  [XXXX Y], where XXXX = method signature 
  and Y = exception class index % 10.
  Use tvmld -verbose to get class & signature
  indices. Use tvm-emul to emulate.

Website    : http://tinyvm.sourceforge.net
Author     : Jose Solorzano (jhsolorz@yahoo.com)
Email list : tinyvm-discussion@lists.sourceforge.net
Developers : tinyvm-develop@lists.sourceforge.net

What is TinyVM?
---------------

TinyVM is a replacement firmware and VM that can 
execute Java programs in the Lego Mindstorms RCX. 
It supports several of the most useful features of 
the Java language, such as:

  - Object orientation.
  - Multi-threading.
  - Synchronization.
  - Exceptions.
  - Type safety (in principle, no hanging/crashing).

Like other firmware replacements (e.g. legOS and
pbForth), TinyVM breaks the 32-variable limit 
imposed by Lego's official firmware. Additionally,
TinyVM offers:

  - No need to install a cross-compiler.
  - Easy to install in general, even under CygWin.
  - Over 16 Kb of memory for programs and objects.
  - Firmware deletes itself when you try to
    download other firmware.
  - An emulation tool.
  - Access to buttons, motors and sensors.
  - Sensor listeners.
  - Nice APIs like you only find in Java.

Starting with 0.2.0:

  - Capability to rerun programs.
  - Full persistence of all objects across runs.
  - Timers and time routines (Time.sleep).
  - java.util.Random.
  - Capability to print characters on the LCD.

TinyVM is also the progenitor of leJOS (see
http://sourceforge.net/projects/lejos).
  
Requirements
------------

- This distribution was developed and tested under Linux
  and CygWin. (For CygWin, see notes below).
  It has been reported to work on other Unix systems. 
  Additionally, a cross-platform edition of TinyVM, 
  contributed by Donald Doherty, based on TinyVM 0.1.3,
  is available at
  
  http://ourworld.compuserve.com/homepages/brainstage/mindstorms.htm
  
- JDK1.1.X or JDK1.2.X, available from http://java.sun.com.
  The JDK is used to run certain tools provided with TinyVM, 
  e.g. the linker. Other JVMs might also work. The default
  Java compiler is javac, but you can define a different
  one by setting the JAVAC environment variable

CygWin Notes
------------

Under CygWin B20, you should create a softlink from /bin
to the directory containing sh, as follows:

  ln -s /cygwin~1/cygwinb20/h-i586~1/bin /bin

Alternatively, you can remove the #!/bin/sh line from
all scripts: tvmc, tvmp, tvmld, tvmld-emul, aux-tvmld,
and tvmfirmdl.

Setup
-----

Environment:
  - Your PATH is assumed to point to the 
    JDK's bin directory.
  - You should also add $TINYVM_HOME/bin to
    your PATH.
  - RCXTTY must be defined to point to the
    IR device file. For example, in my Linux 
    system it's /dev/ttyS1. In my Win95 partition,
    it's COM2. I believe /dev/ttyS0 and COM1 are
    more common, though.
  - TINYVMPATH can be used in addition to
    CLASSPATH to allow the linker to locate
    your class files.

Building the Tools:
  Simply go to installation directory and
  type 'make'. At this point all of the tools necessary
  to run TinyVM should be available under bin.

Downloading the Firmware:
  Run the tvmfirmdl utility. When the firmware
  starts, the LCD shows the power level (a number
  in the 200s or 300s), and you should hear a 
  double beep. Press On/Off to confirm that the
  firmware is working. Send email to jhsolorz@yahoo.com
  if you can't turn your RCX off.

Example
-------

  1. Go to the example directory.

     cd $TINYVM_HOME/examples/view

  2. Set the CLASSPATH variable.
 
     export CLASSPATH=.

  3. Compile the program.

     tvmc *.java

  4. Make sure the RCX is on and in range.

  5. If you haven't done so yet, download
     TinyVM's firmware to the RCX.

     tvmfirmdl

     This may take a couple of minutes. When 
     the firmware download is finished, you
     should hear two beeps, and the LCD 
     will show the RCX's power level, e.g.

                    [0312 0]

     TinyVM is in program-download mode at 
     this point.

     (Note that tvmfirmdl is essentially the same
     as Kekoa Proudfoot's firmdl program).

  6. Link & load the program. In this case, the
     name of the main class is View, so you need
     to type

     tvmld View -d

     This will create a temporary file in the current
     directory, which is downloaded to the RCX
     using the tvm utility. (This can also be done
     in two steps: tvmld View -o View.tvm;
     tvm View.tvm).

     If the RCX is in range, the LCD will show 
     the download count as the number of bytes 
     divided by 100. If nothing happens, make
     sure the RCX is in range. Is the TinyVM
     firmware there? If the RCX begins to beep,
     kill tvm, turn off the RCX, wait until the
     IR tower light is off, power up the RCX,
     and try again.

     You'll see a standing-man figure when the
     program download is finished. Press [Run].

     You can use the View program to test your
     sensors and motors. Press [Run] to activate or
     passivate a sensor or motor. Press [Prgm]
     to change the input mode of sensors. Press
     [View] to select a sensor or motor.

  7. When a program finishes by itself, the firmware
     goes back to program-download mode. At this
     point you can press [Run] to rerun your
     program. If the program is based on an infinite
     loop (like the View example above), you have to
     turn the RCX off and on. It will go back to
     program-download mode.
     
Rerunning and Memory
--------------------

All of a program's memory persists across a rerun.
Additionally, classes are not reinitialized. To 
reinitialize a program, you have to download it again. 
You can delete a program by holding the [Prgm] button 
while you press [On/Off]. To avoid running out of
memory, create all objects in static or instance
initializers. In other words, don't assume that 
because you're allocating an object in main(), it
will only be allocated once. 

What [On/Off] does
------------------

If you press the [On/Off] button while your program
is running, all threads are asked to throw an instance
of java.lang.ThreadDeath. This is a special Error
which, if uncaught, is not reported by the VM.
This means that if your program is deadlocked, the
program will not exit, and you won't be able to
turn the RCX off. You'll hear a series of beeps 
instead. As a workaround, you can hold the [Prgm]
button while you press [On/Off] to delete the program. 

The whole rerun/persistence issue and the
problem just described are due to limitations in
the format of the binary file.

Rerunning and Threads
---------------------

Because of the persistence/rerun semantics, your
thread objects should also be created in initializers.
The pattern recommended for starting a thread is
as follows:

public class MyClass
{
  static Thread myThread = new Thread();
  
  void startMyThread()
  {
    if (!myThread.isAlive())
    {
      myThread.start();
      // Other initializations related to 
      // starting the thread go here.
    }
  }
}
 
Examples of this pattern can be found in
classes/tinyvm/rcx/Sensor.java and Timer.java.
    
Footprint
---------

Even though tinyvm.srec is about 25 Kb (03/19/00), it
occupies only about 10 Kb in RAM. There's about 18 Kb
of space left for your programs and objects. Run 
regression/Test13.java if you want to find out how 
many empty objects a program can create before running 
out of memory (which should be more than 4000).

Emulation
---------

You should be able to run linked (.tvm) programs in your PC. 
All you need to do is use tvmld-emul instead of tvmld, and 
tvm-emul instead of tvm. In some cases it might be easier 
to debug programs with the emulated VM, and you'll also 
get slightly more detailed exception information.

RCX functionality is not available in the emulation tool. 
You will only see ROM call traces.

Porting
-------

The part of TinyVM that runs in the RCX is written 
in C, and is easily cross-compiled with gcc. Note
that there's no assembly code in this distribution
yet. There are parts written in assembly in Kekoa
Proudfoot's librcx library, which is linked by TinyVM.
Also, vmsrc/main.c and vmsrc/native.c are RCX-specific. 
Everything else is platform-indepentdent for the most 
part.

TinyVM is thus highly portable. In fact, the vmtest
directory contains source that allows TinyVM to run on a 
Unix system: vmtest/tvmemul.c replaces vmsrc/main.c, 
and vmtest/nativeemul.c replaces vmsrc/native.c.

Limitations
-----------

Due to size constraints, some language features
have been omitted. Others just haven't been 
implemented yet. The limitations that come to 
mind are:

  - Garbage collection is not performed.
  - Because of the above, and the full persistence
    feature/bug, all objects should be created
    in initializers.
  - Switch statements are unsupported. The linker
    will reject them.
  - Floating point operations are not supported.
  - Operations on variables of type long are not 
    supported.
  - Maximum array length is 255.
  - Maximum number of classes is 255.
  - Maximum recursion level is 8.
  - Maximum operand stack size is 32 words.
  - String constants are not used by the VM to
    create String objects (i.e. don't use Strings).
  - The instanceof operation will always be true  
    for interfaces. In bytecode, CHECKCAST succeeds
    on all interface classes (which is a bit unsafe).
  - The instanceof and checkcast operations are
    rejected by the linker when they are performed
    on array classes (e.g. b instanceof byte[]).
  - All threads are non-daemon threads. 
  - There are no objects of type java.lang.Class, 
    which means that the ".class" construct won't
    work. Class.forName() throws ClassNotFoundException
    at all times.
  - Because of the previous limitation, static 
    synchronized methods are not synchronized.
  - Most JDK APIs are unavailable.
  - No auto-power-off.  

Known Bugs
----------

  - MONITOREXIT ignores null objects (it assumes
    MONITORENTER has handled them). Furthermore, it 
    doesn't check monitor validity. Until this bug 
    is fixed (actually, at all times) all monitors should 
    be constants.

  - Initializers (static or otherwise) are not
    implicitly synchronized. This could lead to race
    conditions. (As an example, consider the hack 
    in classes/tinyvm/rcx/Sensor.java:addListener. If Native
    were not forced to get initialized, the listener
    thread may eventually try to use Native.MEMORY_MONITOR,
    which could be null if some other thread is in
    the middle of trying to initialize Native). All this 
    means is that you have to be more careful with 
    initializers and multi-threading than you normally 
    would in a conventional JVM implementation.

  - Unknown native methods are not executed. The linker
    warns about them.

  - You will not be able to turn off the RCX if your
    program deadlocks. You'll hear a series of beeps
    instead. Hold [Prgm] while you press [On/Off] to 
    delete the program.
    
  - The whole rerun/persistence semantics could be 
    considered bug, not a feature. It's the result of
    a limitation in the format of the binary file.
    
  - If you try to start a thread that is alive, the
    VM will throw an OutOfMemoryError. 
    
Documentation
-------------

A User's Guide is now available at 
  
  http://tinyvm.sourceforge.net

You should also be able to get the API documentation from
that site, but you can always create a local copy by
running

  cd $TINYVM_HOME; make javadoc

About the License
-----------------

TinyVM is released under the Mozilla Public License (see LICENSE),
since Kekoa Proudfoot's librcx is under that same license, 
and TinyVM is linked against -lrcx. We're not including librcx
with this distribution, since no modifications were made to it,
and userd don't need to compile tinyvm.srec. A few files
included in this distribution are based on librcx files. 

Kekoa Proudfoot's librcx can be downloaded at:

    http://graphics.stanford.edu/~kekoa/rcx/tools.html

The original author of TinyVM is Jose Solorzano.

Comments/Problems
-----------------

For bugs, we recommend using the bug reporting infrastructure at 
   
    http://sourceforge.net/projects/tinyvm
     
You can also inquire at tinyvm-discussion@lists.sourceforge.net 
or tinyvm-develop@lists.sourceforge.net. Feel free to contact
Jose Solorzano at jhsolorz@yahoo.com.







