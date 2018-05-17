# Object
Object = faq
SourceDir = ./src
SourceExtension = .docbook
ObjectDependencies = $(SourceDir)/*$(SourceExtension)
ObjectSource = $(SourceDir)/$(Object)$(SourceExtension)
TargetDir = ./build
HtmlTarget = $(TargetDir)/$(Object).html
HtmlCompiler = java org.apache.xalan.xslt.Process
HtmlFilter = ../../xsl/docbook/custom/docbook.html.xsl
InstallDir=../../../doc

all: $(HtmlTarget)

$(HtmlTarget): $(ObjectDependencies)
	@echo "Compiling $@..."
	$(HtmlCompiler) -IN $(ObjectSource) -XSL $(HtmlFilter) -OUT $@

install: $(HtmlTarget)
	cp --target-directory=$(InstallDir) $(HtmlTarget)

clean:
	rm $(TargetDir)/*

FORCE: