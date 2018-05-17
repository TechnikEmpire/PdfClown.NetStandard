<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  exclude-result-prefixes="m db"
  version="1.0"
  xmlns="http://docbook.org/ns/docbook"
  xmlns:db="http://docbook.org/ns/docbook"
  xmlns:m="http://www.stefanochizzolini.it/ns/mentor"
  xmlns:xl="http://www.w3.org/1999/xlink"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  >
  <!--
DocBook-5.0 filter for Mentor 0.2.
2011-11-02, edited by Stefano Chizzolini (http://www.stefanochizzolini.it).
  -->
  <xsl:output omit-xml-declaration="no" method="xml" version="1.0" indent="no" encoding="utf-8"/>

  <xsl:strip-space elements="*"/>

  <!-- Mentor file path. -->
  <xsl:param name="path"/>
  <!-- Main directory (no trailing slash). -->
  <xsl:param name="mainDir"/>
  <!-- Language. -->
  <xsl:param name="lang" select="'en'"/>

  <xsl:variable name="resourceFileTitle" select="'README'"/>
  <xsl:variable name="sourceFileExtension" select="'.mentor'"/>
  <xsl:variable name="targetFileExtension" select="'.html'"/>

  <xsl:variable name="resources" select="document(concat($mainDir,'/','INDEX.mentor'))/*"/>
  
  <xsl:variable name="path.relative">
    <xsl:call-template name="getRelativePath">
      <xsl:with-param name="sourcePath" select="concat($mainDir,'/')"/>
      <xsl:with-param name="targetPath" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="path.dir">
    <xsl:variable name="lastIndex">
      <xsl:call-template name="getLastIndex">
        <xsl:with-param name="value" select="$path"/>
        <xsl:with-param name="delimiter" select="'/'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="substring($path,0,$lastIndex)"/>
  </xsl:variable>
  <xsl:variable name="path.file">
    <xsl:value-of select="substring-after($path,concat($path.dir,'/'))"/>
  </xsl:variable>  
  <xsl:variable name="path.file.name">
    <xsl:value-of select="substring-before($path.file,'.')"/>
  </xsl:variable>

  <xsl:template match="/">
    <xsl:variable name="this" select="child::*[1]"/>
    <xsl:variable name="this.resource" select="$resources//m:resource[@href=$path.relative][1]"/>
    <xsl:variable name="this.project" select="document(concat($mainDir,'/',$this.resource/ancestor-or-self::m:resource[@type='project'][1]/@href))/*"/>

    <xsl:comment>
<xsl:text>

*** NOTE ***
This file was AUTOMATICALLY GENERATED through Mentor 0.2 stylesheets.
Mentor 0.2 is an XML vocabulary for project metadocumentation.

DO NOT MODIFY THIS FILE BY HAND: TWEAK ITS SOURCE (</xsl:text><xsl:value-of select="$path.file"/><xsl:text> file) INSTEAD.

</xsl:text>
    </xsl:comment>

    <article xml:lang="{$lang}" version="5.0">
      <info>
        <title>
          <xsl:call-template name="getTitle">
            <xsl:with-param name="resource" select="$this"/>
            <xsl:with-param name="href" select="$path"/>
          </xsl:call-template>
        </title>
        <subtitle>
          <xsl:call-template name="buildBreadCrumb">
            <xsl:with-param name="resource" select="$this.resource"/>
          </xsl:call-template>
        </subtitle>
        <releaseinfo>
          <xsl:text>Project version: </xsl:text>
          <xsl:value-of select="$this.project/@version"/>
          <xsl:text> - </xsl:text><xsl:value-of select="$path.file.name"/>
          <xsl:text> revision: </xsl:text>
          <xsl:value-of select="$this.project/m:meta/m:revision"/><xsl:text> (</xsl:text><xsl:value-of select="$this.project/m:meta/m:date"/><xsl:text>)</xsl:text>
        </releaseinfo>
        <!--
        <authorgroup>
          <xsl:apply-templates select="$this.project/m:meta/m:author"/>
        </authorgroup>
        -->
      </info>

      <section xml:id="Introduction">
        <title>Introduction</title>
        <xsl:apply-templates select="$this/m:description"/>
      </section>

      <xsl:if test="local-name($this)='project'">
        <xsl:variable name="whatsnewResource" select="$this.resource/m:resource[@type='whatsnew'][1]"/>
        <xsl:if test="$whatsnewResource">
          <xsl:variable name="whatsnew" select="document(concat($mainDir,'/',$whatsnewResource/@href))/*"/>
          <xsl:apply-templates select="$whatsnew/m:entries/m:release[@version=$this.project/@version]">
            <xsl:with-param name="isFrontPage" select="true()"/>
          </xsl:apply-templates>
        </xsl:if>
      </xsl:if>
      
      <xsl:apply-templates select="$this"/>
      
      <xsl:call-template name="buildResourcesSection">
        <xsl:with-param name="resource" select="$this.resource"/>
      </xsl:call-template>
    </article>
  </xsl:template>

  <!-- Builds the site index. -->
  <xsl:template match="m:resources">
    <xsl:apply-templates select="m:comment"/>

    <section xml:id="map">
      <title>Project Map</title>
      
      <itemizedlist>
        <xsl:apply-templates select="m:resource"/>
      </itemizedlist>
    </section>
  </xsl:template>

  <xsl:template match="m:resource[ancestor::m:resources]|m:see[ancestor::m:resources]">
      <listitem>
        <xsl:choose>
          <!-- The resource is internally described. -->
          <xsl:when test="m:title">
            <link>
              <xsl:attribute name="xl:href">
                <xsl:call-template name="getHRef">
                  <xsl:with-param name="targetHRef" select="@href"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:value-of select="m:title"/>
            </link><xsl:text>: </xsl:text><xsl:apply-templates select="m:tip"/>
          </xsl:when>
          <!-- The resource is externally described. -->
          <xsl:otherwise>
            <xsl:variable name="extResource" select="document(concat($mainDir,'/',@href))/*"/>
            <link>
              <xsl:attribute name="xl:href">
                <xsl:call-template name="getHRef">
                  <xsl:with-param name="targetHRef" select="@href"/>
                </xsl:call-template>
              </xsl:attribute>
              <emphasis>
                <xsl:if test="@type='project'">
                  <xsl:attribute name="role">bold</xsl:attribute>
                </xsl:if>
                <xsl:call-template name="getTitle">
                  <xsl:with-param name="resource" select="$extResource"/>
                  <xsl:with-param name="href" select="@href"/>
                </xsl:call-template>
              </emphasis>
            </link><xsl:text>: </xsl:text><xsl:apply-templates select="$extResource/m:tip"/>
          </xsl:otherwise>
        </xsl:choose>
        <itemizedlist>
          <xsl:apply-templates select="m:resource"/>
        </itemizedlist>
      </listitem>
  </xsl:template>

  <xsl:template match="m:project">
    <section xml:id="Copyright">
      <title>Copyright</title>
      <para><xsl:value-of select="concat('Copyright &#169; ',m:copyright/m:year,' ',m:copyright/m:holder/m:name/m:first,' ',m:copyright/m:holder/m:name/m:last)"/></para>
      <para><xsl:text>Contacts:</xsl:text>
      <itemizedlist>
      <xsl:for-each select="m:copyright/m:holder/m:contact">
        <listitem><xsl:apply-templates select="."/></listitem>
      </xsl:for-each>
      </itemizedlist>
      </para>
    </section>

    <xsl:apply-templates select="m:license"/>
    <xsl:apply-templates select="m:comment"/>
  </xsl:template>

  <xsl:template match="m:resource">
    <xsl:apply-templates select="m:comment"/>
  </xsl:template>

  <xsl:template match="m:whatsNew | m:changeLog">
    <xsl:apply-templates select="m:comment"/>
    <xsl:apply-templates select="m:entries/m:release"/>
  </xsl:template>

  <xsl:template match="m:todos | m:issues">
    <xsl:apply-templates select="m:comment"/>
    <section xml:id="list">
      <title>
        <xsl:choose>
          <xsl:when test="name() = 'todos'">TODO</xsl:when>
          <xsl:when test="name() = 'issues'">ISSUES</xsl:when>
        </xsl:choose>
        <xsl:text> list</xsl:text>
      </title>
      <xsl:apply-templates select="m:entries"/>
    </section>
  </xsl:template>

  <xsl:template match="m:credits">
    <section xml:id="list">
      <title>CREDITS list</title>
      <xsl:apply-templates select="m:comment"/>

      <xsl:variable name="agents" select="m:agents/m:agent"/>
      <xsl:for-each select="$agents[not(@role=preceding-sibling::m:agent/@role)]/@role">
        <xsl:variable name="role" select="."/>
        <section xml:id="{$role}">
          <title><xsl:value-of select="$role"/></title>
          <itemizedlist>
            <xsl:apply-templates select="$agents[@role=$role]"/>
          </itemizedlist>
        </section>
      </xsl:for-each>
    </section>
  </xsl:template>

  <xsl:template match="m:agent">
    <listitem>
      <emphasis role="bold">
        <xsl:value-of select="concat(m:name/m:first,' ',m:name/m:last)"/>
      </emphasis>
      <xsl:value-of select="concat(' [',m:tag,']')"/>
      <xsl:if test="m:name/m:title">
        <xsl:value-of select="concat(', ',m:name/m:title)"/>
      </xsl:if>
      <xsl:if test="m:name/m:affiliation">
        <xsl:value-of select="concat(', ',m:name/m:affiliation)"/>
      </xsl:if>
      <xsl:for-each select="m:contact">
        <xsl:value-of select="', '"/>
        <xsl:apply-templates select="."/>
      </xsl:for-each>
      <itemizedlist>
        <xsl:for-each select="m:activity">
          <listitem>
            <xsl:value-of select="concat(.,' (',./@version,')')"/>
          </listitem>
        </xsl:for-each>
      </itemizedlist>
    </listitem>
  </xsl:template>

  <xsl:template match="m:entries">
    <itemizedlist>
      <xsl:for-each select="*">
        <listitem>
          <xsl:value-of select="concat('[',name())"/>
          <xsl:if test="@id!=''">:<xsl:value-of select="@id"/></xsl:if>
          <xsl:text>] </xsl:text>
          <xsl:apply-templates select="."/>
        </listitem>
      </xsl:for-each>
    </itemizedlist>
  </xsl:template>
  
  <xsl:template match="m:release[ancestor::m:whatsNew]">
    <xsl:param name="isFrontPage" select="false()"/>
    
    <section xml:id="{concat('releasechanges_',@version)}">
      <xsl:choose>
        <xsl:when test="$isFrontPage">
          <xsl:variable name="apos">'</xsl:variable>
          <title><xsl:value-of select="concat('What',$apos,'s new?')"/></title>
        </xsl:when>
        <xsl:otherwise>
          <title>Version <xsl:value-of select="@version"/></title>
      <para>
<literallayout>Release date: <xsl:value-of select="@date"/>
Backward compatibility: <xsl:value-of select="@compatible"/></literallayout>
      </para>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="m:description"/>

      <xsl:apply-templates select="m:entries"/>
    </section>
  </xsl:template>

  <xsl:template match="m:release[ancestor::m:changeLog]">
    <section xml:id="{concat('releasechanges_',@version)}">
      <title>Version <xsl:value-of select="@version"/></title>
      <para>
<literallayout>Release date: <xsl:value-of select="@date"/>
Backward compatibility: <xsl:value-of select="@compatible"/></literallayout>
      </para>
      <xsl:apply-templates select="m:description"/>

      <itemizedlist>
        <xsl:apply-templates select="m:feature"/>
      </itemizedlist>
    </section>
  </xsl:template>

  <xsl:template match="m:feature[ancestor::m:release]">
    <xsl:variable name="featureId" select="@idref"/>
    <listitem><xsl:value-of select="/*/m:features/m:feature[@id=$featureId]/m:title"/>

      <xsl:apply-templates select="m:entries"/>
    </listitem>
  </xsl:template>

  <xsl:template match="m:contact|m:src">
    <xsl:value-of select="@type"/><xsl:text>: </xsl:text>
    <xsl:choose>
      <xsl:when test="@type='url'">
        <link xl:href="{text()}"><xsl:value-of select="text()"/></link>
      </xsl:when>
      <xsl:when test="@type='email'">
        <link xl:href="{concat('mailto:',text())}"><xsl:value-of select="text()"/></link>
      </xsl:when>
      <xsl:when test="@type='mail'">
        <xsl:value-of select="m:name"/><xsl:text>, </xsl:text><xsl:value-of select="m:place"/><xsl:text>, </xsl:text><xsl:value-of select="m:city"/><xsl:text>, </xsl:text><xsl:value-of select="m:subCountry"/><xsl:text> </xsl:text><xsl:value-of select="m:postalCode"/><xsl:text> </xsl:text><xsl:value-of select="m:country"/><xsl:text>.</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="m:license">
    <section xml:id="License">
      <title>License</title>
      <xsl:apply-templates select="m:description"/>
      <para><xsl:text>References:</xsl:text>
        <itemizedlist>
          <xsl:for-each select="m:inherits">
            <listitem><xsl:value-of select="m:base/@name"/> (<xsl:value-of select="m:base/m:title"/>) version <xsl:value-of select="m:base/@version"/><xsl:text>:</xsl:text>
              <itemizedlist>
                <listitem><xsl:text>sources:</xsl:text>
                  <itemizedlist>
                    <xsl:for-each select="m:base/m:src">
                      <listitem><xsl:apply-templates select="."/></listitem>
                    </xsl:for-each>
                  </itemizedlist>
                </listitem>
                <listitem><xsl:text>restrictions:</xsl:text>
                  <xsl:choose>
                    <xsl:when test="m:restrictions/m:restriction">
                      <itemizedlist>
                        <xsl:for-each select="m:restrictions/m:restriction">
                          <listitem><xsl:apply-templates select="."/></listitem>
                        </xsl:for-each>
                      </itemizedlist>
                    </xsl:when>
                    <xsl:otherwise><xsl:text> none</xsl:text></xsl:otherwise>
                  </xsl:choose>
                </listitem>
                <listitem><xsl:text>extensions:</xsl:text>
                  <xsl:choose>
                    <xsl:when test="m:extensions/m:extension">
                      <itemizedlist>
                        <xsl:for-each select="m:extensions/m:extension">
                          <listitem><xsl:apply-templates select="."/></listitem>
                        </xsl:for-each>
                      </itemizedlist>
                    </xsl:when>
                    <xsl:otherwise><xsl:text> none</xsl:text></xsl:otherwise>
                  </xsl:choose>
                </listitem>
              </itemizedlist>
            </listitem>
          </xsl:for-each>
        </itemizedlist>
      </para>
    </section>

    <xsl:if test="m:disclaimer">
      <section xml:id="Disclaimer">
        <title>Disclaimer</title>
        <xsl:apply-templates select="m:disclaimer"/>
      </section>
    </xsl:if>
  </xsl:template>

  <xsl:template match="m:author">
    <author>
      <firstname><xsl:value-of select="m:name/m:first"/></firstname>
      <surname><xsl:value-of select="m:name/m:last"/></surname>
      <xsl:apply-templates select="m:contact"/>
    </author>
  </xsl:template>

  <xsl:template match="m:contact[parent::m:author]">
    <xsl:choose>
      <xsl:when test="@type='email'">
        <email><xsl:value-of select="."/></email>
      </xsl:when>
      <xsl:when test="@type='url'">
        <authorblurb>Contact: <link xl:href="{text()}"><xsl:value-of select="text()"/></link></authorblurb>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="db:*">
    <xsl:element name="{name()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <xsl:template name="buildBreadCrumb">
    <xsl:param name="resource"/>

    <xsl:variable name="resource.parent" select="$resource/parent::m:resource"/>
    <xsl:if test="$resource.parent">
      <xsl:variable name="resource.parent.href">
        <xsl:call-template name="getHRef">
          <xsl:with-param name="targetHRef" select="$resource.parent/@href"/>
        </xsl:call-template>      
      </xsl:variable>
      <xsl:variable name="resource.parent.title" select="document(concat($mainDir,'/',$resource.parent/@href))/child::*[1]/m:title"/>      
      <xsl:call-template name="buildBreadCrumb">
        <xsl:with-param name="resource" select="$resource.parent"/>
      </xsl:call-template>
      <link xl:href="{$resource.parent.href}"><xsl:value-of select="$resource.parent.title"/></link>
      <xsl:text> &gt; </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template name="buildResourcesSection">
    <xsl:param name="resource"/>

    <section xml:id="resources">
      <title>Resources</title>
      <itemizedlist>
        <xsl:for-each select="$resource/m:resource|$resource/m:see">
          <listitem>
            <xsl:choose>
              <!-- The resource is internally described. -->
              <xsl:when test="m:title">
                <link>
                  <xsl:attribute name="xl:href">
                    <xsl:call-template name="getHRef">
                      <xsl:with-param name="targetHRef" select="@href"/>
                    </xsl:call-template>
                  </xsl:attribute>
                  <xsl:value-of select="m:title"/>
                </link><xsl:text>: </xsl:text><xsl:apply-templates select="m:tip"/>
              </xsl:when>
              <!-- The resource is externally described. -->
              <xsl:otherwise>
                <xsl:variable name="extResource" select="document(concat($mainDir,'/',@href))/*"/>
                <link>
                  <xsl:attribute name="xl:href">
                    <xsl:call-template name="getHRef">
                      <xsl:with-param name="targetHRef" select="@href"/>
                    </xsl:call-template>
                  </xsl:attribute>
                  <emphasis>
                    <xsl:if test="@type='project'">
                      <xsl:attribute name="role">bold</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="getTitle">
                      <xsl:with-param name="resource" select="$extResource"/>
                      <xsl:with-param name="href" select="@href"/>
                    </xsl:call-template>
                  </emphasis>
                </link><xsl:text>: </xsl:text><xsl:apply-templates select="$extResource/m:tip"/>
              </xsl:otherwise>
            </xsl:choose>
          </listitem>
        </xsl:for-each>
        
        <listitem xml:id="navigation"><xsl:text>Navigation:</xsl:text>
          <itemizedlist>
            <listitem><link xl:href=".">Current directory</link>: browse current section contents</listitem>

            <!-- Parent resource -->
            <xsl:variable name="resource.parent" select="$resource/parent::m:resource"/>
            <xsl:if test="$resource.parent">
              <listitem>
                <link>
                  <xsl:attribute name="xl:href">
                    <xsl:call-template name="getHRef">
                      <xsl:with-param name="targetHRef" select="$resource.parent/@href"/>
                    </xsl:call-template>
                  </xsl:attribute>
                  <xsl:text>Parent section</xsl:text>
                </link>
                <xsl:text>: move to parent section</xsl:text>
              </listitem>
            </xsl:if>

            <!-- Previous resource -->
            <xsl:variable name="resource.prev" select="($resource/preceding-sibling::m:resource[contains(@href,$sourceFileExtension)][1]|$resource/parent::m:resource)[last()]"/>
            <xsl:if test="$resource.prev">
              <listitem>
                <link>
                  <xsl:attribute name="xl:href">
                    <xsl:call-template name="getHRef">
                      <xsl:with-param name="targetHRef" select="$resource.prev/@href"/>
                    </xsl:call-template>
                  </xsl:attribute>
                  <xsl:text>Previous section</xsl:text>
                </link>
                <xsl:text>: move to previous section</xsl:text>
              </listitem>
            </xsl:if>

            <!-- Next resource -->
            <xsl:variable name="resource.next" select="$resource/child::m:resource[contains(@href,$sourceFileExtension)][1]|$resource/following::m:resource[contains(@href,$sourceFileExtension)][1]"/>
            <xsl:if test="$resource.next">
              <listitem>
                <link>
                  <xsl:attribute name="xl:href">
                    <xsl:call-template name="getHRef">
                      <xsl:with-param name="targetHRef" select="$resource.next/@href"/>
                    </xsl:call-template>
                  </xsl:attribute>
                  <xsl:text>Next section</xsl:text>
                </link>
                <xsl:text>: move to next section</xsl:text>
              </listitem>
            </xsl:if>
            
            <!-- Index -->
            <xsl:variable name="resource.index" select="$resource/ancestor::m:resources//m:resource[@type='index'][1]"/>
            <listitem>
              <link>
                <xsl:attribute name="xl:href">
                  <xsl:call-template name="getHRef">
                    <xsl:with-param name="targetHRef" select="$resource.index/@href"/>
                  </xsl:call-template>
                </xsl:attribute>
                <xsl:text>INDEX</xsl:text>
              </link>
              <xsl:text>: move to the distribution map</xsl:text>
            </listitem>
          </itemizedlist>
        </listitem>
      </itemizedlist>
    </section>
  </xsl:template>

  <xsl:template name="getAbsolutePath">
    <xsl:param name="sourceDir"/>
    <xsl:param name="targetRelativePath"/>

    <xsl:choose>
      <!-- Move upwards! -->
      <xsl:when test="starts-with($targetRelativePath,'../')">
        <xsl:variable name="lastIndex">
          <xsl:call-template name="getLastIndex">
            <xsl:with-param name="value" select="$sourceDir"/>
            <xsl:with-param name="delimiter" select="'/'"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="getAbsolutePath">
          <xsl:with-param name="sourceDir" select="substring($sourceDir,0,$lastIndex)"/>
          <xsl:with-param name="targetRelativePath" select="substring-after($targetRelativePath,'../')"/>
        </xsl:call-template>
      </xsl:when>
      <!-- Navigation has finished. -->
      <xsl:otherwise>
        <xsl:if test="not(starts-with($targetRelativePath,'/')) and not(contains($targetRelativePath,'://'))">
          <xsl:value-of select="concat($sourceDir,'/')"/>
        </xsl:if>
        <xsl:value-of select="$targetRelativePath"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getFileName">
    <xsl:param name="path"/>

    <xsl:variable name="lastIndex">
      <xsl:call-template name="getLastIndex">
        <xsl:with-param name="value" select="$path"/>
        <xsl:with-param name="delimiter" select="'/'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="substring-before(substring($path,$lastIndex + 1),'.')"/>
  </xsl:template>

  <!-- Builds an index-based href. -->
  <xsl:template name="getHRef">
    <xsl:param name="targetHRef"/>

    <xsl:call-template name="getRelativePath">
      <xsl:with-param name="sourcePath" select="concat($path.dir,'/')"/>
      <xsl:with-param name="targetPath">
        <xsl:variable name="absolutePath">
          <xsl:call-template name="getAbsolutePath">
            <xsl:with-param name="sourceDir" select="$mainDir"/>
            <xsl:with-param name="targetRelativePath" select="$targetHRef"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="contains($absolutePath,$sourceFileExtension)">
            <xsl:value-of select="concat(substring-before($absolutePath,$sourceFileExtension),$targetFileExtension)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$absolutePath"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="getLastIndex">
    <xsl:param name="value"/>
    <xsl:param name="delimiter"/>
    <xsl:param name="position" select="0"/>

    <xsl:choose>
      <xsl:when test="contains($value,$delimiter)">
        <xsl:call-template name="getLastIndex">
          <xsl:with-param name="value" select="substring-after($value,$delimiter)"/>
          <xsl:with-param name="delimiter" select="$delimiter"/>
          <xsl:with-param name="position" select="$position + string-length(substring-before($value,$delimiter)) + 1"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$position"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getRelativePath">
    <xsl:param name="sourcePath"/>
    <xsl:param name="targetPath"/>

    <xsl:choose>
      <xsl:when test="contains($sourcePath,'/') and not(contains($targetPath,'://'))">
        <xsl:variable name="baseDir" select="concat(substring-before($sourcePath,'/'),'/')"/>
        <xsl:choose>
          <!-- Base directory is common to both paths: it must be trimmed. -->
          <xsl:when test="starts-with($targetPath,$baseDir)">
            <xsl:call-template name="getRelativePath">
              <xsl:with-param name="sourcePath" select="substring-after($sourcePath,$baseDir)"/>
              <xsl:with-param name="targetPath" select="substring-after($targetPath,$baseDir)"/>
            </xsl:call-template>
          </xsl:when>
          <!-- Base directory is within the source path only: it must be navigated upwards. -->
          <xsl:otherwise>
            <xsl:text>../</xsl:text>
            <xsl:call-template name="getRelativePath">
              <xsl:with-param name="sourcePath" select="substring-after($sourcePath,$baseDir)"/>
              <xsl:with-param name="targetPath" select="$targetPath"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <!-- No base directory: navigation has finished. -->
      <xsl:otherwise>
        <xsl:value-of select="$targetPath"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getTitle">
    <xsl:param name="resource"/>
    <xsl:param name="href"/>
    
    <xsl:choose>
      <xsl:when test="$resource/m:title">
        <xsl:value-of select="$resource/m:title"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="getFileName">
          <xsl:with-param name="path" select="$href"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
