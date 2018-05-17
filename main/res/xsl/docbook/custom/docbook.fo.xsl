<?xml version='1.0' encoding="utf-8"?>
<xsl:stylesheet
  xmlns:db="http://docbook.org/ns/docbook"
  xmlns:exsl="http://exslt.org/common"
  xmlns:ng="http://docbook.org/docbook-ng"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  >
  <!--
This is a basic customization layer for DocBook 5.0/XSL-FO.
2006-09-12. Edited by Stefano Chizzolini (http://www.stefanochizzolini.it).
  -->
  <xsl:import href="../1.70.1/fo/docbook.xsl"/>

  <xsl:param name="paper.type" select="'A4'"/>
  <xsl:param name="body.font.family" select="'sans-serif'"/>
  <xsl:param name="body.font.master" select="10"/>
  <xsl:param name="section.autolabel" select="1"/>

  <!-- FOP 0.20.5 has the bad habit to download draft image everytime it runs despite
  being not used, so I had to suppress the default url pointing to the online repository. -->
  <xsl:param name="draft.watermark.image"/>

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
        <xsl:variable name="nons">
          <xsl:apply-templates mode="stripNS"/>
        </xsl:variable>
        <xsl:apply-templates select="exsl:node-set($nons)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$rootid != ''">
            <xsl:variable name="root.element" select="key('id', $rootid)"/>
            <xsl:choose>
              <xsl:when test="count($root.element) = 0">
                <xsl:message terminate="yes">
                  <xsl:text>ID '</xsl:text>
                  <xsl:value-of select="$rootid"/>
                  <xsl:text>' not found in document.</xsl:text>
                </xsl:message>
              </xsl:when>
              <xsl:when test="not(contains($root.elements, concat(' ', local-name($root.element), ' ')))">
                <xsl:message terminate="yes">
                  <xsl:text>ERROR: Document root element ($rootid=</xsl:text>
                  <xsl:value-of select="$rootid"/>
                  <xsl:text>) for FO output </xsl:text>
                  <xsl:text>must be one of the following elements:</xsl:text>
                  <xsl:value-of select="$root.elements"/>
                </xsl:message>
              </xsl:when>
              <!-- Otherwise proceed -->
              <xsl:otherwise>
                <xsl:if test="$collect.xref.targets = 'yes' or $collect.xref.targets = 'only'">
                  <xsl:apply-templates select="$root.element" mode="collect.targets"/>
                </xsl:if>
                <xsl:if test="$collect.xref.targets != 'only'">
                  <xsl:apply-templates select="$root.element" mode="process.root"/>
                </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <!-- Otherwise process the document root element -->
          <xsl:otherwise>
            <xsl:variable name="document.element" select="*[1]"/>
            <xsl:choose>
              <xsl:when test="not(contains($root.elements, concat(' ', local-name($document.element), ' ')))">
                <xsl:message terminate="yes">
                  <xsl:text>ERROR: Document root element for FO output </xsl:text>
                  <xsl:text>must be one of the following elements:</xsl:text>
                  <xsl:value-of select="$root.elements"/>
                </xsl:message>
              </xsl:when>
              <!-- Otherwise proceed -->
              <xsl:otherwise>
                <xsl:if test="$collect.xref.targets = 'yes' or $collect.xref.targets = 'only'">
                  <xsl:apply-templates select="/" mode="collect.targets"/>
                </xsl:if>
                <xsl:if test="$collect.xref.targets != 'only'">
                  <xsl:apply-templates select="/" mode="process.root"/>
                </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
