#!/bin/sh
java -jar ./SpanningTree.jar -st TAC125_0_50 0 1 1 positiveControlClean -output 0_50.out
java -jar ./SpanningTree.jar -st TAC125_9_10 0 1 1 positiveControlClean -output 9_10.out
java -jar ./SpanningTree.jar -st TAC125_default_thr 0 1 1 positiveControlClean -output default_thr.out
