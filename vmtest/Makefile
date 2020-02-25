
VMSRC=../vmsrc
LD=ld
CC=gcc
CCOPT=-I../vmsrc -O2 -Wall -Wno-unused -DEMULATE

DUMP_OBJ=dump.o load.o

VMEMUL_OBJ=interpreter.o threads.o exceptions.o \
	   memory.o language.o \
           tvmemul.o traceemul.o nativeemul.o load.o opcodeinfo.o

DUMP_TARGET=../bin/dump
VMEMUL_TARGET=../bin/tvm-emul

default: programs

programs: ${VMEMUL_TARGET} ${DUMP_TARGET}

interpreter.o: ${VMSRC}/interpreter.c
	${CC} ${CCOPT} -c ${VMSRC}/interpreter.c

threads.o: ${VMSRC}/threads.c
	${CC} ${CCOPT} -c ${VMSRC}/threads.c

exceptions.o: ${VMSRC}/exceptions.c
	${CC} ${CCOPT} -c ${VMSRC}/exceptions.c

memory.o: ${VMSRC}/memory.c
	${CC} ${CCOPT} -c ${VMSRC}/memory.c

language.o: ${VMSRC}/language.c
	${CC} ${CCOPT} -c ${VMSRC}/language.c

nativeemul.o: nativeemul.c ${VMSRC}/specialsignatures.h

%.o: %.c
	${CC} ${CCOPT} -c $*.c

${DUMP_TARGET}: ${DUMP_OBJ}
	${CC} ${DUMP_OBJ} -o ${DUMP_TARGET}

${VMEMUL_TARGET}: ${VMEMUL_OBJ}
	${CC} ${VMEMUL_OBJ} -o ${VMEMUL_TARGET}

clean:
	rm -rf ${VMSRC}/*.o *.o core ${DUMP_TARGET} ${VMEMUL_TARGET}

