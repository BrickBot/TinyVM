
This document contains information for those interested
in modifying the C source code of the VM, in particular
for cross-compilation to the H8300 microcontroller.

Cross-compiler for Unix
-----------------------

You should download binutils-2.9.1 and gcc-2.8.1 from
www.gnu.org, and install them, in that order, using:

	./configure --target=h8300-hitachi-hms --prefix=/usr/local
	make
	su
	make install
	
Create Makefile.config containing:

  BINDIR=/usr/local/bin
  BINPREFIX=h8300-hitachi-hms-
	
Cross-compiler for CygWin
-------------------------

For CygWin B20, you can download binaries for a cross-compiler from
http://legos.sourceforge.net/files/windows/cygwin/win-h8-egcs-1.1.2.zip
(information stolen from the legOS FAQ pages :-) and unzip it in 
c:\cygnus\cygwin-b20\H-i586-cygwin32. For other CygWin versions, you
probably will have to build the cross-compiler yourself. 

Create a soft-link from /bin to /cygnus/cygwin-b20/H-i586-cygwin32/bin.

Create Makefile.config containing:

  BINDIR=/cygnus/cygwin-b20/H-i586-cygwin32/bin
  BINPREFIX=h8300-hms-

Setting up the environment under CygWin can be problematic.
For reference, here's what my cygnus.bat file contains:

  @ECHO OFF
  SET MAKE_MODE=UNIX
  SET PATH=%PATH%;c:\cygnus\cygwin~1\h-i586~1\bin
  SET HOME=/home/jose
  bash

And this is what my $HOME/.bashrc file looks like:

  echo Running .bashrc

  export TINYVM_ROOT=//d/projects/tinyvm
  export LIBRCX_HOME=//d/projects/librcx
  export RCXTTY=COM2
  
  OLD_PATH=$PATH
  export PATH=/cygnus/cygwin-b20/H-i586-cygwin32/h8300-hms/bin/:/cygnus/cygwin-b20/H-i586-cygwin32/bin
  export PATH=$PATH:/cygnus/cygwin-b20/H-i586-cygwin32/lib/gcc-lib/h8300-hms/egcs-2.91.66/
  export PATH=$PATH://d/jdk1.2.2/bin
  export PATH=$PATH:$TINYVM_ROOT/bin
  export PATH=$PATH:$OLD_PATH
  

Building
--------

make         : What users run to install. Creates jar files and tools.
make all     : make + vmsrc directory, which creates tinyvm.srec.
make release : make all + releases according to VERSION file.
make clean   : remove class files, object files, etc.

Organization
------------

bin     : User tools.
lib     : Jar files (user API and linker utilities).
vmsrc   : C source code for the VM that runs in the RCX.
vmtest  : C source code for emulation.
jtools  : Java source code for linker.
classes : Java source code for user API.

Adding a Native Method
----------------------

Modify common/signatures.db, vmsrc/native.c and vmtest/nativeemul.c.
Also, before the next release, make sure the magic number is changed
in vmsrc/magic.h and jtools/js/tinyvm/Constants.java. This kind of
thing must be announced in the tinyvm-develop list.

Naming conventions
------------------

In Java code, please stick to what the Java Language Spec says:

- Class names are capitalized.
- Field and method names start with a lower case letter,
  and they have no underscores.
- Constant names are all uppercase, and they typically
  have underscores.
- Package names are all lowercase.


