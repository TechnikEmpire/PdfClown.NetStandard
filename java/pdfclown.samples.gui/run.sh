#!/bin/bash
#
# Shell script to run PDF Clown for Java GUI samples.

java -Xbootclasspath/a:./build/package/pdfclown-samples-gui.jar:../pdfclown.lib/build/package/pdfclown.jar -jar ./build/package/pdfclown-samples-gui.jar #2> ../log/java.log
