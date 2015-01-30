<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="yes" omit-xml-declaration="yes" standalone="yes"/>

<xsl:template match="/">//package enter_package_path;

import org.terifan.net.rpc.client.RemoteObjectStub;
import org.terifan.net.rpc.shared.RemoteObject.ServiceName;


/**
 * <xsl:choose>
					<xsl:when test="string-length(/doc/service/description) &gt; 0"><xsl:value-of select="/doc/service/description"/></xsl:when>
					<xsl:otherwise>?</xsl:otherwise>
	</xsl:choose>
 */
@ServiceName("<xsl:value-of select="/doc/service/@name"/>")
public class <xsl:value-of select="/doc/service/@name"/> extends RemoteObjectStub
{
	<xsl:for-each select="/doc/service/method"><xsl:variable name="ret">
			<xsl:choose>
				<xsl:when test="string-length(returnType)&gt;0"><xsl:value-of select="returnType"/></xsl:when>
				<xsl:otherwise>void</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="retfunc">
			<xsl:choose>
				<xsl:when test="returnType='void'">super.invokeQueued</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="type">
					<xsl:choose>
						<xsl:when test="returnType='boolean'">Boolean</xsl:when>
						<xsl:when test="returnType='byte'">Byte</xsl:when>
						<xsl:when test="returnType='char'">Character</xsl:when>
						<xsl:when test="returnType='short'">Short</xsl:when>
						<xsl:when test="returnType='int'">Integer</xsl:when>
						<xsl:when test="returnType='long'">Long</xsl:when>
						<xsl:when test="returnType='float'">Float</xsl:when>
						<xsl:when test="returnType='double'">Double</xsl:when>
						<xsl:otherwise><xsl:value-of select="returnType"/></xsl:otherwise>
					</xsl:choose>
					</xsl:variable>return (<xsl:value-of select="$type"/>)super.invoke</xsl:otherwise>
			</xsl:choose></xsl:variable><xsl:if test="position() &gt; 1"><xsl:text>


	</xsl:text></xsl:if>/**
	 * <xsl:choose>
				<xsl:when test="string-length(description) &gt; 0"><xsl:value-of select="description"/></xsl:when>
				<xsl:otherwise>?</xsl:otherwise>
			</xsl:choose>
	 * <xsl:for-each select="param">
	 * @param <xsl:choose>
				<xsl:when test="string-length(name) &gt; 0"><xsl:value-of select="name"/></xsl:when>
				<xsl:otherwise>param<xsl:value-of select="position()"/></xsl:otherwise>
			</xsl:choose>
	 *     <xsl:choose>
				<xsl:when test="string-length(description) &gt; 0"><xsl:value-of select="description"/></xsl:when>
				<xsl:otherwise>?</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	 * @return
	 *     <xsl:choose>
				<xsl:when test="string-length(returnDescription) &gt; 0"><xsl:value-of select="returnDescription"/></xsl:when>
				<xsl:otherwise>?</xsl:otherwise>
			</xsl:choose>
	 */
	public <xsl:value-of select="concat($ret,' ',name)"/>(<xsl:for-each select="param">
			<xsl:if test="position() &gt; 1">, </xsl:if>
			<xsl:value-of select="type"/><xsl:text> </xsl:text><xsl:choose>
				<xsl:when test="string-length(name) &gt; 0"><xsl:value-of select="name"/></xsl:when>
				<xsl:otherwise>param<xsl:value-of select="position()"/></xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>)
	{
		<xsl:value-of select="$retfunc"/>("<xsl:value-of select="name"/>", <xsl:for-each select="param">
			<xsl:if test="position() &gt; 1">, </xsl:if>
			<xsl:choose>
				<xsl:when test="string-length(name) &gt; 0"><xsl:value-of select="name"/></xsl:when>
				<xsl:otherwise>param<xsl:value-of select="position()"/></xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>);
	}</xsl:for-each>
}
</xsl:template>

</xsl:stylesheet>