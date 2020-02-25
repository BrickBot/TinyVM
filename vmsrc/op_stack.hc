/**
 * This is included inside a switch statement.
 */

case OP_BIPUSH:
  // Stack size: +1
  // Arguments: 1
  // TBD: check negatives
  *(++stackTop) = (JINT) (JBYTE) (*pc++);
  goto LABEL_ENGINELOOP;
case OP_SIPUSH:
  // Stack size: +1
  // Arguments: 2
  #if 0
  printf ("  OP_SIPUSH args: %d, %d (%d)\n", (int) pc[0], (int) pc[1], (int) pc[2]);
  #endif
  *(++stackTop) = (JINT) (JSHORT) (((TWOBYTES) pc[0] << 8) | pc[1]);
  pc += 2;
  goto LABEL_ENGINELOOP;
case OP_LDC:
  // Stack size: +1
  // Arguments: 1
  gConstRec = get_constant_record (*pc++);
  if (gConstRec->constantType == T_REFERENCE)
  {
    // Strings not supported!!
    *(++stackTop) = obj2word(nullPointerException);
  }
  else
  {
    #ifdef VERIFY
    assert (gConstRec->constantType == T_INT, INTERPRETER0);
    #endif
    make_word (get_constant_ptr(gConstRec), 4, ++stackTop);
  }
  goto LABEL_ENGINELOOP;

case OP_ACONST_NULL:
  // Stack size: +1
  // Arguments: 0
  *(++stackTop) = JNULL;
  goto LABEL_ENGINELOOP;
case OP_ICONST_M1:
case OP_ICONST_0:
case OP_ICONST_1:
case OP_ICONST_2:
case OP_ICONST_3:
case OP_ICONST_4:
case OP_ICONST_5:
  // Stack size: +1
  // Arguments: 0
  *(++stackTop) = *(pc-1) - OP_ICONST_0;
  goto LABEL_ENGINELOOP;
case OP_POP2:
  // Stack size: -2
  // Arguments: 0
  stackTop--;
  // Fall through
case OP_POP:
  // Stack size: -1
  // Arguments: 0
  stackTop--;
  goto LABEL_ENGINELOOP;
case OP_DUP:
  // Stack size: +1
  // Arguments: 0
  *(stackTop+1) = *stackTop;
  stackTop++;
  goto LABEL_ENGINELOOP;
case OP_DUP2:
  // Stack size: +2
  // Arguments: 0
  *(stackTop+1) = *(stackTop-1);
  *(stackTop+2) = *stackTop;
  stackTop += 2;
  goto LABEL_ENGINELOOP;
case OP_DUP_X1:
  // Stack size: +1
  // Arguments: 0
  stackTop++;
  *stackTop = *(stackTop-1);
  *(stackTop-1) = *(stackTop-2);
  *(stackTop-2) = *stackTop;
  goto LABEL_ENGINELOOP;
case OP_DUP2_X1:
  // Stack size: +2
  // Arguments: 0
  stackTop += 2;
  *stackTop = *(stackTop-2);
  *(stackTop-1) = *(stackTop-3);
  *(stackTop-2) = *(stackTop-4);
  *(stackTop-3) = *stackTop;
  *(stackTop-4) = *(stackTop-1);
  goto LABEL_ENGINELOOP;
case OP_DUP_X2:
  // Stack size: +1
  // Arguments: 0
  stackTop++;
  *stackTop = *(stackTop-1);
  *(stackTop-1) = *(stackTop-2);
  *(stackTop-2) = *(stackTop-3);
  *(stackTop-3) = *stackTop;
  goto LABEL_ENGINELOOP;
case OP_DUP2_X2:
  // Stack size: +2
  // Arguments: 0
  stackTop += 2;
  gByte = 4;
  while (gByte--)
  {
    *stackTop = *(stackTop-2);  
    stackTop--;
  }
  stackTop[0] = stackTop[4];
  stackTop[-1] = stackTop[3];
  goto LABEL_ENGINELOOP;
case OP_SWAP:
  gStackWord = *stackTop;
  *stackTop = *(stackTop-1);
  *(stackTop-1) = gStackWord;
  goto LABEL_ENGINELOOP;

// Notes:
// - LDC_W should not occur in tinyvm.
// - LDC2_W is not supported (longs and doubles).
// - Arguments of LDC are postprocessed.
// - NOOP is in op_skip.hc.
// - All the DCONST and FCONST opcodes are unsupported.

/*end*/







