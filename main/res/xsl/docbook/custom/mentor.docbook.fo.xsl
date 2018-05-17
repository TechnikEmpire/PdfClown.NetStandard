<?xml version='1.0' encoding="utf-8"?>
<xsl:stylesheet
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  >
  <!--
This is a customization layer for DocBook 5.0/XSL-FO guides.
2006-09-12. Edited by Stefano Chizzolini (http://www.stefanochizzolini.it).

Changes:
* 2008-05-01: suppressed hyphenation and justified alignment.
  -->
  <xsl:import href="docbook.fo.xsl"/>

  <xsl:param name="alignment">left</xsl:param>
  <xsl:param name="hyphenate">false</xsl:param>

  <xsl:attribute-set name="list.block.spacing">
    <xsl:attribute name="space-before.optimum">0.2em</xsl:attribute>
    <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
    <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">0.2em</xsl:attribute>
    <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
    <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="list.item.spacing">
    <xsl:attribute name="space-before.optimum">0.2em</xsl:attribute>
    <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
    <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:template match="ulink" name="ulink">
    <xsl:variable name ="ulink.url">
      <xsl:call-template name="fo-external-image">
        <xsl:with-param name="filename" select="@url"/>
      </xsl:call-template>
    </xsl:variable>

    <fo:basic-link xsl:use-attribute-sets="xref.properties"
                  external-destination="{$ulink.url}">
      <xsl:choose>
        <xsl:when test="count(child::node())=0">
          <xsl:call-template name="hyphenate-url">
            <xsl:with-param name="url" select="@url"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </fo:basic-link>

    <xsl:if test="count(child::node()) != 0
                  and string(.) != @url
                  and $ulink.show != 0">
      <!-- yes, show the URI -->
      <xsl:choose>
        <xsl:when test="$ulink.footnotes != 0 and not(ancestor::footnote)">
          <fo:footnote>
            <xsl:call-template name="ulink.footnote.number"/>
            <fo:footnote-body xsl:use-attribute-sets="footnote.properties">
              <fo:block>
                <xsl:call-template name="ulink.footnote.number"/>
                <xsl:text> </xsl:text>
                <fo:basic-link external-destination="{$ulink.url}">
                  <xsl:value-of select="@url"/>
                </fo:basic-link>
              </fo:block>
            </fo:footnote-body>
          </fo:footnote>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template match="subtitle" mode="article.titlepage.recto.auto.mode">
    <fo:block xsl:use-attribute-sets="article.titlepage.recto.style" font-size="16pt">
      <xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="authorgroup" mode="article.titlepage.recto.auto.mode">
    <fo:block xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em">
      <xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="author" mode="article.titlepage.recto.auto.mode">
    <fo:block xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em">
      <xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="author" mode="titlepage.mode">
    <fo:block>
      <xsl:call-template name="anchor"/>
      <fo:inline font-size="10pt"><xsl:call-template name="person.name"/></fo:inline>
      <xsl:if test="affiliation/orgname">
        <xsl:text>, </xsl:text>
        <xsl:apply-templates select="affiliation/orgname" mode="titlepage.mode"/>
      </xsl:if>
      <xsl:if test="email|affiliation/address/email">
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="(email|affiliation/address/email)[1]"/>
      </xsl:if>
    </fo:block>
  </xsl:template>
</xsl:stylesheet>
