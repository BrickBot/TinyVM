
Jose's comments (02/28/00):
---------------------------

- TinyVM is linked with Kekoa Proudfoot's rcx library
  from librcx.

- TinyVM sources #include "rom.h" from Kekoa Proudfoot's
  librcx.

- Several files were copied from Kekoa's librcx, 
  and modified slightly.

- I've read Kekoa's RCX internals page, especially
  the ROM disassembly and the bytecode specification,
  without which I couldn't have written the code necessary
  to download binaries into the RCX.

- Dave Baum's email/posting dated 29 Aug 1998, with subject
  "RE: Lego Mindstorms Page," describing the packet format, 
  was very helpful in understanding how RCX communication 
  works.

- An article that appeared in Dr. Dobb's Journal a
  few months ago by Markus Noga about legOS, is what
  led me to purchase the RIS and later to write
  this firmware replacement program. (The name TinyVM
  probably came from a comment about his tiny OS).
  Needless to say, I've read some of the legOS
  files.

- I'd like to thank anyone else who has played
  a part in figuring out RCX internals.

- SPECIAL THANKS to my wife, Erika, for putting up 
  with me during the development of this project.

After 0.1beta1:
---------------

- Thanks to Christophe Vanhecke, for lending TinyVM
  some webspace at his site: www.crazyrobots.com.

- Carlos Solorzano (my brother) helped me port
  TinyVM to JDK1.2 over the phone.

After 0.1.3:
------------

- Thanks to Donald Doherty and his team for coming
  up with TinyVM cross-platform edition. Eventually
  I will merge that edition with this edition, or
  with TinyVM's successor.

After 0.1.4:
------------

- The new scripts, which run under CygWin, are due
  to feedback from Andy Gombos.

After 0.1.6:
------------

- Thanks to Schiedermeier Reinhard (schieder <at> informatik.fh)
  for contributing the serial2rcx example.

- Thanks go to Ryan VanderBijl (rvbijl39 <at> calvin <dot> edu)
  for pestering Jose a lot, and contributing various things,
  like the TextLCD class, and the LineFollower example. 
  He also added time/timer support! The TextLCD class is based
  on legOS code by Markus Noga.
  
After 0.2.0:
------------

- We are now shipping Kekoa Proudfoot's firmdl3.c, slightly
  modified, as tools/tvm_firmdl3.c. This replaces tvmfirmdl.c,
  which was essentially Kekoa Proudfoot's original version of 
  his firmware downloader.
  
  

