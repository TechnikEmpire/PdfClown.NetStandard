<?xml version='1.0' encoding="utf-8"?>
<xsl:stylesheet
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  >
  <!--
This is a customization layer for DocBook 5.0/XSL-FO guides.
2006-09-12. Edited by Stefano Chizzolini (http://www.stefanochizzolini.it).
  -->
  <xsl:import href="docbook.fo.xsl"/>

  <xsl:param name="fop.extensions" select="1"/>

  <xsl:param name="body.start.indent" select="'0pt'"/>
  <xsl:param name="page.margin.inner" select="'0.75in'"/>
  <xsl:param name="page.margin.outer" select="'0.75in'"/>
  <xsl:param name="hyphenate.verbatim" select="1"/>
<xsl:param name="formal.title.placement">
figure after
example before
equation before
table before
procedure before
task before
</xsl:param>
  <xsl:attribute-set name="monospace.verbatim.properties" use-attribute-sets="verbatim.properties monospace.properties">
    <xsl:attribute name="wrap-option">wrap</xsl:attribute>
    <xsl:attribute name="hyphenation-character">&#x25BA;</xsl:attribute>
    <xsl:attribute name="font-size">8pt</xsl:attribute>
  </xsl:attribute-set>

  <!-- Abstract -->
  <xsl:template match="abstract" mode="titlepage.mode">
    <fo:block padding-top="4em">
      <xsl:apply-templates mode="titlepage.mode"/>
    </fo:block>
  </xsl:template>

  <!-- Copyright -->
  <xsl:template match="copyright" mode="titlepage.mode">
    <fo:block padding-top="1.5em">
      <xsl:call-template name="gentext">
        <xsl:with-param name="key" select="'Copyright'"/>
      </xsl:call-template>
      <xsl:call-template name="gentext.space"/>
      <xsl:call-template name="dingbat">
        <xsl:with-param name="dingbat">copyright</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="gentext.space"/>
      <xsl:call-template name="copyright.years">
        <xsl:with-param name="years" select="year"/>
        <xsl:with-param name="print.ranges" select="$make.year.ranges"/>
        <xsl:with-param name="single.year.ranges" select="$make.single.year.ranges"/>
      </xsl:call-template>
      <xsl:call-template name="gentext.space"/>
      <xsl:apply-templates select="holder" mode="titlepage.mode"/>
    </fo:block>
  </xsl:template>

  <!-- LegalNotice -->
  <xsl:template match="legalnotice" mode="titlepage.mode">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <fo:block id="{$id}" padding-top="5em">
      <xsl:if test="title">
        <xsl:call-template name="formal.object.heading"/>
      </xsl:if>
      <xsl:apply-templates mode="titlepage.mode"/>
    </fo:block>
  </xsl:template>

  <!-- Pubdate -->
  <!-- Pubdate is inhibited from autonomous display (see releaseinfo). -->
  <xsl:template match="bookinfo/pubdate|info/pubdate" mode="titlepage.mode" priority="2"/>

  <!-- ReleaseInfo -->
  <xsl:template match="releaseinfo" mode="titlepage.mode">
    <fo:block padding-top=".5em">
      <xsl:apply-templates mode="titlepage.mode"/>
    </fo:block>

    <xsl:call-template name="gentext">
      <xsl:with-param name="key" select="'published'"/>
    </xsl:call-template>
    <xsl:text> </xsl:text>
    <xsl:apply-templates select="../pubdate/node()" mode="titlepage.mode"/>
  </xsl:template>

  <!-- EBNF productionset -->
  <!-- NOTE: This rule was necessary because FOP 0.20.5 seems irritatingly not to care at all
  of column width expressed as relative units (percentage); so I had to patch up disgusting
  absolute-unit column definitions... -->
  <!-- NOTE: This rule gets rid of the default layout, arranging elements just in a single
  column instead of distinct ones and using verbatim style. -->
  <xsl:template match="productionset">
    <xsl:variable name="id"><xsl:call-template name="object.id"/></xsl:variable>

    <xsl:choose>
      <xsl:when test="title">
        <fo:block id="{$id}" xsl:use-attribute-sets="formal.object.properties">
          <xsl:call-template name="formal.object.heading">
            <xsl:with-param name="placement" select="'before'"/>
          </xsl:call-template>

          <fo:table table-layout="fixed" xsl:use-attribute-sets="monospace.verbatim.properties">
            <fo:table-column column-width="1cm"/>
            <fo:table-column column-width="16cm"/>
            <fo:table-body start-indent="0pt" end-indent="0pt">
              <xsl:apply-templates select="production|productionrecap"/>
            </fo:table-body>
          </fo:table>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
        <fo:table id="{$id}" table-layout="fixed" xsl:use-attribute-sets="monospace.verbatim.properties">
          <fo:table-column column-width="1cm"/>
          <fo:table-column column-width="16cm"/>
          <fo:table-body start-indent="0pt" end-indent="0pt">
            <xsl:apply-templates select="production|productionrecap"/>
          </fo:table-body>
        </fo:table>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- EBNF production -->
  <!-- NOTE: According to the productionset custom template, this rule gets rid of the default
  layout, arranging elements just in a single column instead of distinct ones. -->
  <xsl:template match="production">
    <xsl:param name="recap" select="false()"/>
    <xsl:variable name="id"><xsl:call-template name="object.id"/></xsl:variable>
    <fo:table-row>
      <!-- Enumerator -->
      <fo:table-cell>
        <fo:block text-align="start">
          <xsl:text>[</xsl:text>
          <xsl:number count="production" level="any"/>
          <xsl:text>]</xsl:text>
        </fo:block>
      </fo:table-cell>
      <!-- Production rule -->
      <fo:table-cell>
        <!-- Rule body -->
        <fo:block text-align="start">
          <!-- Left-hand side -->
          <xsl:choose>
            <xsl:when test="$recap">
              <fo:basic-link internal-destination="{$id}" xsl:use-attribute-sets="xref.properties">
                <xsl:apply-templates select="lhs"/>
              </fo:basic-link>
            </xsl:when>
            <xsl:otherwise>
              <fo:wrapper id="{$id}">
                <xsl:apply-templates select="lhs"/>
              </fo:wrapper>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text> </xsl:text><xsl:copy-of select="$ebnf.assignment"/><xsl:text> </xsl:text>
          <xsl:apply-templates select="rhs"/>
          <xsl:copy-of select="$ebnf.statement.terminator"/><xsl:text> </xsl:text>
          <xsl:if test="rhs/lineannotation">
            <xsl:apply-templates select="rhs/lineannotation" mode="rhslo"/>
          </xsl:if>
        </fo:block>
        <!-- Comments and constraints -->
        <fo:block text-align="start">
          <xsl:choose>
            <xsl:when test="constraint">
              <xsl:apply-templates select="constraint"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>&#160;</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>
</xsl:stylesheet>
