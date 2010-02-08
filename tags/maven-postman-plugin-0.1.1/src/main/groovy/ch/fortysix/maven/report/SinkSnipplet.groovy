/**
 * 
 */
package ch.fortysix.maven.report;

import org.apache.maven.doxia.sink.Sink;

/**
 * The representation of a snipplet as Sink is used to add content
 * to a Maven report page
 * @author Domi
 *
 */
interface SinkSnipplet {
	void addToSink(Sink sink)
}
