/**
 * This is included inside a switch statement.
 */

case OP_ILOAD:
case OP_FLOAD:
case OP_ALOAD:
  // Arguments: 1
  // Stack: +1
  *(++stackTop) = localsBase[*pc++];
  #if DEBUG_BYTECODE
  printf ("\n  OP_ALOAD pushed: %d\n\n", (int) stackTop[0]);
  #endif
  goto LABEL_ENGINELOOP;
case OP_ILOAD_0:
case OP_ILOAD_1:
case OP_ILOAD_2:
case OP_ILOAD_3:
  // Arguments: 0
  // Stack: +1
  *(++stackTop) = localsBase[*(pc-1)-OP_ILOAD_0];
  goto LABEL_ENGINELOOP;
case OP_ALOAD_0:
case OP_ALOAD_1:
case OP_ALOAD_2:
case OP_ALOAD_3:
  // Arguments: 0
  // Stack: +1
  *(++stackTop) = localsBase[*(pc-1)-OP_ALOAD_0];
  #if DEBUG_BYTECODE
  printf ("\n  OP_ALOAD_<N> pushed: %d\n\n", (int) stackTop[0]);
  #endif
  goto LABEL_ENGINELOOP;
case OP_ISTORE:
case OP_FSTORE:
case OP_ASTORE:
  // Arguments: 1
  // Stack: -1
  localsBase[*pc++] = *stackTop--;
  goto LABEL_ENGINELOOP;
case OP_ISTORE_0:
case OP_ISTORE_1:
case OP_ISTORE_2:
case OP_ISTORE_3:
  // Arguments: 0
  // Stack: -1
  localsBase[*(pc-1)-OP_ISTORE_0] = *stackTop--;
  goto LABEL_ENGINELOOP;
case OP_ASTORE_0:
case OP_ASTORE_1:
case OP_ASTORE_2:
case OP_ASTORE_3:
  // Arguments: 0
  // Stack: -1
  #if DEBUG_BYTECODE
  printf ("\n  OP_ASTORE: %d, %d\n\n", (int) (*(pc-1)-OP_ASTORE_0), (int) *stackTop);
  #endif
  localsBase[*(pc-1)-OP_ASTORE_0] = *stackTop--;
  goto LABEL_ENGINELOOP;
case OP_IINC:
  // Arguments: 2
  // Stack: +0
  localsBase[*pc] += byte2jint(*(pc+1));
  pc += 2; 
  goto LABEL_ENGINELOOP;

// Notes:
// - OP_WIDE is unexpected in TinyVM.
// - No support for STORE or LOAD opcodes for F, D or L.

/*end*/







