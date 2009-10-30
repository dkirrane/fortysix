/**
 * 
 */
package ch.fortysix.maven.plugin;
import java.io.File;

import groovy.util.AntBuilder;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.groovy.GroovyMojo;

/**
 * @author Domi
 * 
 * @goal apply
 */
class ModifyArchiveMojo extends GroovyMojo {

	/**
	 * Message to print
	 * 
	 * @parameter expression="${apply.message}" default-value="Hello Maven World"
	 */
	String message

	/**
	 * where to work on the archive
	 * 
	 * @parameter expression="${apply.workDir}" default-value="${project.build.directory}/modifyArchive"
	 */
	String workDir

	/**
	 * The archive to be modified
	 * 
	 * @parameter expression="${apply.archive}" 
	 * @required 
	 */
	String archive

	/**
	 * The archive to be modified
	 * 
	 * @parameter  
	 */
	FileCopy [] fileCopies
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	org.apache.maven.project.MavenProject project

	
	void execute() {

		ant.echo "hier spielt ANT mit: $message"
		ant.mkdir(dir:workDir)
		
		def aFile = new File(archive)
		if(aFile.exists()){
			ant.unzip(src:archive,dest:workDir)
		}else{
			fail("file $aFile does NOT exist")
		}

		if(fileCopies){
			fileCopies.each{
					def src = it.src
					def dest = it.dest
					File fromSrc = new File(it.src)
					File toDest = new File(it.dest)
					log.info "$it.src ($fromSrc.absolutePath) to $it.dest ";
					
					if(fromSrc.isFile()){
						// if the src is a file and the dest contains a '.' we do a file2file copy 
						log.info "copy file $fromSrc"
						if(dest.indexOf(".") == -1){
							ant.copy(file:src,toDir:dest,verbose:true,overwrite:true)
						}else{
							// if dest contains no '.' we do a file2directory copy
							ant.copy(file:src,tofile:dest,verbose:true,overwrite:true)
						}
					}else if(fromSrc.isDirectory()){
						// we do directory2directory copy
						log.info "copy directory $fromSrc"
						ant.copy(todir:dest,verbose:true){
							fileset(dir:src) {
						        include(name:"**/*")
						    }							
						}
					}else{
						log.warn "$fromSrc is not a valid file/directory"
					}
				} 
		}
		
	}

}
