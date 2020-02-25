#!/bin/sh

CLASSPATH=.
export CLASSPATH
tvmc Rotator.java
tvmld Rotator -o Rotator.tvm

echo "Run 'tvm Rotator.tvm' to download the program."
