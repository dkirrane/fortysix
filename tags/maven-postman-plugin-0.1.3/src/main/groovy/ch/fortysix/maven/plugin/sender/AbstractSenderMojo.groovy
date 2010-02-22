package ch.fortysix.maven.plugin.sender;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.gmaven.mojo.GroovyMojo;

abstract class AbstractSenderMojo extends GroovyMojo {
	
	/**
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	org.apache.maven.execution.MavenSession session
	
	/**
	 * Encoding of the source. 
	 * Advice is taken from: <a href="http://docs.codehaus.org/display/MAVENUSER/POM+Element+for+Source+File+Encoding">POM Element for Source File Encoding</a>
	 * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
	 */
	String sourceEncoding;	
	
	/**
	 * Indicates whether this report should skip the sending mails (no mails send).
	 * @parameter  default-value="false"
	 */
	boolean skipMails	= false
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	org.apache.maven.project.MavenProject project
	
	/**
	 * Indicates whether you need TLS/SSL
	 * @parameter  default-value="false"
	 */
	boolean mailssl	= false
	
	/**
	 * Indicates to use an alternative way to configure the ssl connection to the smtp server. 
	 * This might be needed in specific environments.
	 * @parameter  default-value="false"
	 */
	boolean mailAltConfig = false
	
	/**
	 * Host name of the SMTP server. The default value is localhost.
	 * @parameter expression="${mailhost}" default-value="localhost"
	 */
	String mailhost
	
	/**
	 * TCP port of the SMTP server. The default value is 25.
	 * @parameter expression="${mailport}" default-value="25"
	 */
	String mailport
	
	/**
	 * User name for SMTP auth
	 * @parameter 
	 */
	String mailuser
	
	/**
	 * Password for SMTP auth
	 * @parameter 
	 */
	String mailpassword
	
	/**
	 * Email subject line. 
	 * @parameter default-value="[${project.artifactId}]"
	 */
	String subject
	
	/**
	 * Email address of sender.
	 * @parameter 
	 * @required
	 */
	String from
	
	/**
	 * Who should receive a mail? One can use an id of a developer registered in the pom or an email address directly.
	 * <pre>
	 * 	 		&lt;receivers&gt;	 
	 * 		  		&lt;receiver&gt;developerId&lt;/receiver&gt;
	 * 		  		&lt;receiver&gt;sam@topland.com&lt;/receiver&gt;
	 * 	 		&lt;/receivers&gt;
	 * </pre>
	 * @parameter 
	 * @required
	 */	
	Set receivers;	
	
	
	/**
	 * Report output directory. Note that this parameter is only relevant if the goal is run from the command line or
	 * from the default build lifecycle. If the goal is run indirectly as part of a site generation, the output
	 * directory configured in the Maven Site Plugin is used instead.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 */
	File outputDirectory
	
	/**
	 * flag to indicate whether to halt the build on any error. The default value is true.
	 * @parameter default-value="true"
	 */
	boolean failonerror = true
	
	/**
	 * The content type to use for the message. 
	 * This is only the fallback contenttype if the environment does not support 'multipart/alternative'.
	 * @parameter default-value="text/html"
	 */
	String mailcontenttype	
	
	/**
	 * Whether 'multipart/alternative' mails can be send.
	 * This is detected automatically, but it allows a user to disable it and force the usage of 'mailcontenttype'. 
	 * @parameter default-value="true"
	 */
	boolean multipartSupported = true
	
	/**
	 * Sends the emails...
	 */
	def mailSender
	
	/**
	 * The text message to be send
	 * @parameter default-value="build for ${project.groupId}:${project.artifactId}:${project.version} executed"
	 */	
	String textMessage
	
	/**
	 * The html message body to be send 
	 * @parameter default-value="<body>build for ${project.groupId}:${project.artifactId}:${project.version} executed</body>"
	 */	
	String htmlMessage
	
	/**
	 * The text message body to be send, if set the content of this will replace the <code>textMessage</code>. 
	 * @parameter 
	 */	
	File textMessageFile
	
	/**
	 * The html message body to be send, if set the content of this will replace the <code>htmlMessage</code>. 
	 * @parameter 
	 */	
	File htmlMessageFile
	
	
	
}