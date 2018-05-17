<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  version="1.0"
  xmlns:db="http://docbook.org/ns/docbook"
  xmlns:xl="http://www.w3.org/1999/xlink"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  >
  <!--
Ad-hoc plain-text filter for Mentor-derived DocBook 5.0 (just a tiny subset!).
2006-09-12. Edited by Stefano Chizzolini (http://www.stefanochizzolini.it).

PS: Sure, before attempting such a convoluted (and redundant) path I got a try with XSL-FO-generated plain text (via FOP), but the result was really poor, just unacceptable. I wonder why the Norman Walsh's gang seems not to have bothered to craft a stylesheet for plain-text rendering along with already-supported formats such as HTML, XSL-FO etc. ;-)
  -->
  <xsl:output method="text" indent="no" encoding="iso-8859-1"/>

  <xsl:strip-space elements="*"/>

  <xsl:template match="db:info">
    <xsl:apply-templates select="db:title"/>
    <xsl:apply-templates select="db:subtitle"/>
    <xsl:text>&#xa;</xsl:text><xsl:apply-templates select="db:authorgroup"/>
    <xsl:text>&#xa;</xsl:text><xsl:apply-templates select="db:releaseinfo"/>
  </xsl:template>

  <xsl:template match="db:section">
    <xsl:text>&#xa;</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="db:para">
    <xsl:text>&#xa;</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="db:date"/>
  <xsl:template match="db:pubdate"/>

  <xsl:template match="db:releaseinfo">
    <xsl:apply-templates/>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="db:title[parent::db:section]">
    <xsl:text>---------------&#xa;</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>&#xa;---------------</xsl:text>
  </xsl:template>

  <xsl:template match="db:title[parent::db:info]">
    <xsl:apply-templates/><xsl:text>&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="db:subtitle">
    <xsl:apply-templates/><xsl:text>&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="db:author">
    <xsl:value-of select="concat(db:firstname,' ',db:surname)"/>
    <xsl:text> </xsl:text>&lt;<xsl:value-of select="db:email"/><xsl:text>&gt;&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="db:itemizedlist">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="db:listitem">
    <xsl:text>&#xa;</xsl:text>
    <xsl:call-template name="indent">
      <xsl:with-param name="level" select="count(ancestor::db:itemizedlist)"/>
    </xsl:call-template>
    <xsl:text>* </xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="db:link">
    <xsl:value-of select="text()"/>
    <xsl:text> [</xsl:text><xsl:value-of select="@xl:href"/><xsl:text>]</xsl:text>
  </xsl:template>

  <xsl:template name="indent">
    <xsl:param name="level"/>

    <xsl:if test="$level &gt; 0">
      <xsl:text> </xsl:text>
      <xsl:call-template name="indent">
        <xsl:with-param name="level" select="number($level) - 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
