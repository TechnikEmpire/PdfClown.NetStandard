#!/bin/bash
#TODO: substitute with Ant build!
#
# Mentor 0.2 Recursive compilation script.
# 2011-11-02. Edited by Stefano Chizzolini (http://www.stefanochizzolini.it)
#
# CLI parameters:
# <none>

mainDir=$(pwd)
files=$(find $(readlink -f $mainDir/..) -name '*.mentor')
for file in $files
do
  $mainDir/res/script/mentor.sh $file $mainDir
done
