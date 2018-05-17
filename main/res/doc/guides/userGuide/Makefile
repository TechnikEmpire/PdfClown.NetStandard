# Book
Book = userGuide
SourceDir = ./src
SourceExtension = .docbook
BookDependencies = $(SourceDir)/*$(SourceExtension)
BookSource = $(SourceDir)/$(Book)$(SourceExtension)
TargetDir = ./build
PdfTarget = $(TargetDir)/$(Book).pdf
PdfCompiler = fop
FoFilter = ../../../xsl/docbook/custom/guide$(SourceExtension).fo.xsl
InstallDir=../../../../doc

all: $(PdfTarget)

$(PdfTarget): $(BookDependencies)
	@echo "Compiling $@..."
	$(PdfCompiler) -q -xml $(BookSource) -xsl $(FoFilter) -pdf $@

install: $(PdfTarget)
	cp --target-directory=$(InstallDir) $(PdfTarget)

clean:
	rm $(PdfTarget)

FORCE: