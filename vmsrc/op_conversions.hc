/**
 * This is included inside a switch statement.
 */

case OP_I2B:
  stackTop[0] = (JBYTE) word2jint(stackTop[0]);
  goto LABEL_ENGINELOOP;
case OP_I2S:
case OP_I2C:
  stackTop[0] = (JSHORT) word2jint(stackTop[0]);
  goto LABEL_ENGINELOOP;   

// Notes:
// - Not supported: I2L, L2I, L2F, L2D, F2L, D2L.
// - Also unsupported: any conversions to and from FP numbers.

/*end*/

