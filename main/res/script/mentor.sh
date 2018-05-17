#!/bin/bash
#TODO: substitute with Ant build!
#
# Mentor 0.2 Compilation script.
# 2011-11-02. Edited by Stefano Chizzolini (http://www.stefanochizzolini.it)
#
# CLI parameters:
# $1 File path.
path=$(readlink -f $1)
# $2 Distribution main directory.
mainDir=$(readlink -f $2)

xalan='java org.apache.xalan.xslt.Process'
fop='fop'
xslDir="$mainDir/res/xsl"
abstractPath="$(dirname $path)/$(basename $path .mentor)"

getRelativePath(){
  source="$1"
  target="$2"

  common_part=$source/
  back=
  while [ "${target#$common_part}" = "${target}" ]; do
    common_part=$(dirname $common_part)/
    back="../${back}"
  done

  echo ${back}${target#$common_part}
}

echo $'\n'"$abstractPath begins."

# Generate DocBook!
echo $'\n'"Compiling $abstractPath.docbook ..."
$xalan -IN $abstractPath.mentor -XSL $xslDir/mentor/mentor.docbook.xsl -OUT $abstractPath.docbook -PARAM path $path -PARAM mainDir $mainDir

# Generate plain text!
echo $'\n'"Compiling $abstractPath.txt ..."
$xalan -IN $abstractPath.docbook -XSL $xslDir/mentor/mentor.docbook.txt.xsl -OUT $abstractPath.txt

# Generate HTML!
echo $'\n'"Compiling $abstractPath.html ..."
$xalan -IN $abstractPath.docbook -XSL $xslDir/docbook/custom/docbook.html.xsl -OUT $abstractPath.html -PARAM html.stylesheet $(getRelativePath $(dirname $path) $mainDir)/res/styles/mentor.css

# Generate PDF!
#echo $'\n'"Compiling $abstractPath.pdf ..."
#$fop -xml $abstractPath.docbook -xsl $xslDir/docbook/custom/mentor.docbook.fo.xsl -pdf $abstractPath.pdf

# Discard DocBook!
rm $abstractPath.docbook

echo $'\n'"$abstractPath ends." $'\n'
