@echo off

rem Win32 batch file
rem
rem Shell script to run PDF Clown for Java samples on MS Windows.

java -Xbootclasspath/a:.\build\package\pdfclown-samples-cli.jar;..\pdfclown.lib\build\package\pdfclown.jar;..\util.reflex\build\package\reflex.jar -jar .\build\package\pdfclown-samples-cli.jar
