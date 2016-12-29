<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="http://xml.apache.org/xslt/java">
<xsl:output method="html" indent="yes" omit-xml-declaration="yes" standalone="yes"/>

<xsl:template match="/">
<html>
	<head>
		<title>Servic documentation</title>
		<link rel="stylesheet" type="text/css" href="http://download.java.net/jdk7/docs/api/stylesheet.css" title="Style"/>
		<style>
			.missing{background:yellow;}
		</style>
		<script language="javascript">
			var selected = '';
			function show_service(name)
			{
				if (selected != '')
				{
					document.getElementById(selected).style.visibility = 'hidden';
					document.getElementById('header'+selected).className = '';
				}
				document.getElementById(name).style.top = document.getElementById('anchor').style.top;
				document.getElementById(name).style.visibility = 'visible';
				document.getElementById('header'+name).className = 'navBarCell1Rev';
				selected = name;
			}
		</script>
	</head>
	<body>
		<div id="topNav" class="topNav">
			<ul class="navList" title="Navigation">
				<xsl:for-each select="/doc/service">
					<li id="header{@name}"><a href="javascript:show_service('{@name}')"><xsl:value-of select="@name"/></a></li>
				</xsl:for-each>
			</ul>
		</div>
		<div id="anchor">&#160;</div>

		<xsl:apply-templates select="doc/service"/>
	</body>
	<script language="javascript">
		show_service("<xsl:value-of select="/doc/service[1]/@name"/>");
	</script>
</html>
</xsl:template>

<xsl:template match="service">
	<div id="{@name}" style="visibility:hidden;position:absolute;width:100%">
		<div class="header">
			<h2 title="Class Object" class="title">Service <xsl:value-of select="@name"/></h2>
		</div>
		<div class="contentContainer">
			<div class="description">
				<ul class="blockList">
					<li class="blockList">
						<hr/>
						<br/>
						<div class="block">
							<xsl:call-template name="print"><xsl:with-param name="text" select="description"/></xsl:call-template>
						</div>
					</li>
				</ul>
			</div>
			<div class="summary">
				<ul class="blockList">
					<li class="blockList">
						<ul class="blockList">
							<li class="blockList">
								<h3>Method Summary</h3>
								<table class="overviewSummary" summary="Method Summary table, listing methods, and an explanation" border="0" cellpadding="3" cellspacing="0">
									<caption><span>Methods</span><span class="tabEnd">&#160;</span></caption>
									<tbody>
										<tr>
											<th class="colFirst" scope="col">Modifier and Type</th>
											<th class="colLast" scope="col">Method and Description</th>
										</tr>
										<xsl:for-each select="method">
											<xsl:call-template name="method_summary"/>
										</xsl:for-each>
									</tbody>
								</table>
							</li>
						</ul>
					</li>
				</ul>
			</div>
			<div class="details">
				<ul class="blockList">
					<li class="blockList">
						<ul class="blockList">
							<li class="blockList">
								<a name="method_detail"></a>
								<h3>Method Detail</h3>
								<xsl:for-each select="method">
									<xsl:call-template name="method_detail"/>
								</xsl:for-each>
							</li>
						</ul>
					</li>
				</ul>
			</div>
		</div>
	</div>
</xsl:template>

<xsl:template name="method_summary">
	<tr>
		<xsl:attribute name="class">
			<xsl:choose>
				<xsl:when test="(position() mod 2)= 0">altColor</xsl:when>
				<xsl:otherwise>rowColor</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
		<td class="colFirst"><code>protected <xsl:call-template name="type"><xsl:with-param name="text" select="returnType"/></xsl:call-template></code></td>
		<td class="colLast">
			<code><strong><a href="#{signature}"><xsl:value-of select="name"/></a></strong>(<xsl:for-each select="param">
				<xsl:if test="position() &gt; 1">,&#160;</xsl:if>
				<xsl:call-template name="type"><xsl:with-param name="text" select="type"/></xsl:call-template>&#160;<xsl:call-template name="print"><xsl:with-param name="text" select="name"/></xsl:call-template>
			</xsl:for-each>)</code>
			<div class="block"><xsl:value-of select="description"/></div>
		</td>
	</tr>
</xsl:template>

<xsl:template name="method_detail">
	<ul class="blockList">
		<li class="blockList">
			<a name="{signature}">
				<h4><xsl:value-of select="name"/></h4>
			</a>
			<pre>protected&#160;<xsl:call-template name="type"><xsl:with-param name="text" select="returnType"/></xsl:call-template>&#160;<xsl:value-of select="name"/>(<xsl:for-each select="param">
					<xsl:if test="position() &gt; 1">,&#160;</xsl:if>
					<xsl:call-template name="type"><xsl:with-param name="text" select="type"/></xsl:call-template>&#160;<xsl:call-template name="print"><xsl:with-param name="text" select="name"/></xsl:call-template>
			</xsl:for-each>)</pre>
			<div class="block">
				<xsl:call-template name="print"><xsl:with-param name="text" select="description"/></xsl:call-template>
			</div>
			<dl>
				<dt>
					<span class="strong">Returns:</span>
				</dt>
				<dd>
					<xsl:call-template name="print"><xsl:with-param name="text" select="returnDescription"/></xsl:call-template>
				</dd>
			</dl>
		</li>
	</ul>
</xsl:template>

<xsl:template name="print">
	<xsl:param name="text"/>
	<xsl:if test="string-length($text)=0">
		<span class="missing">?</span>
	</xsl:if>
	<xsl:value-of select="$text"/>
</xsl:template>

<xsl:template name="type">
	<xsl:param name="text"/>
	<xsl:value-of select="$text"/>
	<!--
	<xsl:choose>
		<xsl:when test="$text='void' or $text='byte' or $text='short' or $text='char' or $text='int' or $text='long' or $text='float' or $text='double'">
			<xsl:value-of select="$text"/>
		</xsl:when>
		<xsl:otherwise>
			<a href=""><xsl:value-of select="$text"/></a>
		</xsl:otherwise>
	</xsl:choose>
	-->
</xsl:template>

</xsl:stylesheet>