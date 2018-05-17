#!/bin/bash
#
# Shell script to run PDF Clown for Java samples.

java -Xbootclasspath/a:./build/package/pdfclown-samples-cli.jar:../pdfclown.lib/build/package/pdfclown.jar:../util.reflex/build/package/reflex.jar -jar ./build/package/pdfclown-samples-cli.jar #2> ../log/java.log
