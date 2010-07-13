/**
 * 
 */
package ch.fortysix.maven.plugin.postman.taglist


;

import java.util.Set;

/**
 * Maps a taglist tag (by displayName) to a set of receivers.
 * This class has to be in the same package as the plugin using it. 
 * @author Domi
 *
 */
class TagClass {
	String displayName;
	Set receivers;
}
