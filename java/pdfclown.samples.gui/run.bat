@echo off

rem Win32 batch file
rem
rem Shell script to run PDF Clown for Java GUI samples.

java -Xbootclasspath/a:.\build\package\pdfclown-samples-gui.jar;..\pdfclown.lib\build\package\pdfclown.jar -jar .\build\package\pdfclown-samples-gui.jar
