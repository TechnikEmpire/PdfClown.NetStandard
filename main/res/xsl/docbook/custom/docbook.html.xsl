<?xml version='1.0' encoding="utf-8"?>
<xsl:stylesheet
  version="1.0"
  xmlns:db="http://docbook.org/ns/docbook"
  xmlns:exsl="http://exslt.org/common"
  xmlns:ng="http://docbook.org/docbook-ng"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  >
  <!--
This is a basic customization layer for DocBook 5.0/HTML.
2006-09-12. Edited by Stefano Chizzolini (http://www.stefanochizzolini.it).
  -->
  <xsl:import href="../1.70.1/html/docbook.xsl"/>

  <xsl:output method="html" encoding="utf-8" indent="no"/>

  <xsl:template match="/">
    <xsl:choose>
      <!-- ORIGINAL version: -->
      <!-- <xsl:when test="function-available('exsl:node-set')
          and (*/self::ng:* or */self::db:*)"> -->
      <!-- HACKED version (function-available() doesn't seem to work properly with Xalan-J 2.7.0): -->
      <xsl:when test="*/self::ng:* or */self::db:*">
        <!-- Hack! If someone hands us a DocBook V5.x or DocBook NG document,
        toss the namespace and continue. Someday we'll reverse this logic
        and add the namespace to documents that don't have one.
        But not before the whole stylesheet has been converted to use
        namespaces. i.e., don't hold your breath -->
        <xsl:message>Stripping NS from DocBook 5/NG document.</xsl:message>
        <xsl:variable name="nons">
          <xsl:apply-templates mode="stripNS"/>
        </xsl:variable>
        <xsl:message>Processing stripped document.</xsl:message>
        <xsl:apply-templates select="exsl:node-set($nons)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$rootid != ''">
            <xsl:choose>
              <xsl:when test="count(key('id',$rootid)) = 0">
                <xsl:message terminate="yes">
                  <xsl:text>ID '</xsl:text>
                  <xsl:value-of select="$rootid"/>
                  <xsl:text>' not found in document.</xsl:text>
                </xsl:message>
              </xsl:when>
              <xsl:otherwise>
                <xsl:if test="$collect.xref.targets = 'yes' or $collect.xref.targets = 'only'">
                  <xsl:apply-templates select="key('id', $rootid)" mode="collect.targets"/>
                </xsl:if>
                <xsl:if test="$collect.xref.targets != 'only'">
                  <xsl:apply-templates select="key('id',$rootid)" mode="process.root"/>
                  <xsl:if test="$tex.math.in.alt != ''">
                    <xsl:apply-templates select="key('id',$rootid)" mode="collect.tex.math"/>
                  </xsl:if>
                </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="$collect.xref.targets = 'yes' or $collect.xref.targets = 'only'">
              <xsl:apply-templates select="/" mode="collect.targets"/>
            </xsl:if>
            <xsl:if test="$collect.xref.targets != 'only'">
              <xsl:apply-templates select="/" mode="process.root"/>
              <xsl:if test="$tex.math.in.alt != ''">
                <xsl:apply-templates select="/" mode="collect.tex.math"/>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
