#!/bin/bash
#
# PDF Clown API Documentation doxygen-postprocessing script.
# 2006-09-12. Edited by Stefano Chizzolini (http://www.stefanochizzolini.it)
#
# CLI parameters:
# $1 API documentation directory.

DocDir=$1
ReplaceCmd="rpl -e -i -q"

# Some post-process tweaking on the doc output...

# Suppress the clumsy namespace subsection menu!
$ReplaceCmd '<div class=\042tabs\042>\012  <ul>\012    <li id=\042current\042><a href=\042namespaces.html\042><span>Packages</span></a></li>\012    <li><a href=\042namespacemembers.html\042><span>Package&nbsp;Functions</span></a></li>\012  </ul></div>' '' $DocDir/*.html; $ReplaceCmd '<div class=\042tabs\042>\012  <ul>\012    <li><a href=\042namespaces.html\042><span>Packages</span></a></li>\012    <li><a href=\042namespacemembers.html\042><span>Package&nbsp;Functions</span></a></li>\012  </ul></div>' '' $DocDir/*.html

# Suppress the clumsy file subsection menu!
$ReplaceCmd '<div class=\042tabs\042>\012  <ul>\012    <li><a href=\042files.html\042><span>File&nbsp;List</span></a></li>\012    <li><a href=\042globals.html\042><span>File&nbsp;Members</span></a></li>\012  </ul></div>' '' $DocDir/*.html

# Remap java-related 'package' term in favor of C# equivalent!
$ReplaceCmd 'Package' 'Namespace' $DocDir/*.html

# Suppress class description headers!
$ReplaceCmd 'Detailed Description' '' $DocDir/*.html

# Suppress erroneous documentation pointers!
$ReplaceCmd 'Go to the documentation of this file.' '' $DocDir/*.html

# Edit frontpage!
$ReplaceCmd '</h1>\012<p>' '</h1><p><b>PDF Clown</b> is a C# library devoted to the manipulation of PDF files.</p><h2>Introduction</h2><p>This is the <b>PDF Clown API</b>.</p><ul><li><a href=\042namespaces.html\042>Namespaces</a></li><li><a href=\042classes.html\042>Classes</a></li></ul><h2>See also</h2><ul><li><a href=\042../faq.html\042>FAQ</a></li><li><a href=\042../README.html\042>Documentation index</a></li><li><a href=\042http://www.pdfclown.org\042>PDF Clown home page</a></li></ul>' $DocDir/index.html
$ReplaceCmd 'Documentation</h1>' 'API Reference</h1>' $DocDir/index.html

# Edit footnote!
$ReplaceCmd '<address' '<p>Copyright 2006-2013 Stefano Chizzolini. For more information, please visit <a href="http://www.pdfclown.org">PDF Clown home page</a>.</p><address' $DocDir/*.html
