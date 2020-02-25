
#include "types.h"

#ifndef _INTERPRETER_H
#define _INTERPRETER_H

extern boolean gMustExit;
extern boolean gRequestSuicide;
extern byte *pc;
extern STACKWORD *stackTop;
extern STACKWORD *localsBase;
extern unsigned short numOpcodes;

extern void engine();

#endif
