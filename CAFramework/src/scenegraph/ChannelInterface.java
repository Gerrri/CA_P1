package scenegraph;

import java.util.ArrayList;

/**
 * Interface for objects, having a channel.
 * @author Stefan
 *
 */
public interface ChannelInterface {
	/**
	 * Returns all channels of the object, or null if the object has no channels
	 * 
	 * @return list of channels
	 */
	public ArrayList<Channel> getChannels();

	/**
	 * Returns the channel with a given name
	 * 
	 * @param cname
	 *            The name of the queried channel
	 * @return The channel with specified name if it exists, otherwise null
	 */
	public Channel getChannel(String cname);

}
