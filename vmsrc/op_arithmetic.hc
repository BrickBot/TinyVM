/**
 * This is included inside a switch statement.
 */

case OP_ISUB:
  // Arguments: 0
  // Stack: -2 +1
  *stackTop = -word2jint(*stackTop);
  // Fall through!
case OP_IADD:
  // Arguments: 0
  // Stack: -2 +1
  stackTop--;
  *stackTop = word2jint(*stackTop) + word2jint(*(stackTop+1));
  goto LABEL_ENGINELOOP;
case OP_IMUL:
  stackTop--;
  *stackTop = word2jint(*stackTop) * word2jint(*(stackTop+1));
  goto LABEL_ENGINELOOP;
case OP_IDIV:
case OP_IREM:
  gInt = word2jint(stackTop[0]);
  if (gInt == 0)
  {
    throw_exception (arithmeticException);
    goto LABEL_ENGINELOOP;
  }
  stackTop--;
  stackTop[0] = (*(pc-1) == OP_IDIV) ? word2jint(stackTop[0]) / gInt :
                                       word2jint(stackTop[0]) % gInt;
  goto LABEL_ENGINELOOP;
case OP_INEG:
  *stackTop = -word2jint(*stackTop);
  goto LABEL_ENGINELOOP;
  
// Notes:
// - Not supported: LADD, LSUB, LMUL, LREM, FREM, DREM
// - None of the floating point opcodes supported.

/*end*/







