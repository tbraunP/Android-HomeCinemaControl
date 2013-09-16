package org.homecinecontrol.protocol.observer;

/**
 * Subject registering for change.
 * 
 * @author pyro
 * 
 * @param <Typ>
 *            - Type which has changed.
 */
public interface Observer<Typ> {

	/**
	 * Update received
	 * 
	 * @param observable
	 *            - Observable notifying about update
	 * @param notification
	 *            - changed information
	 */
	public void update(Object observable, Typ notification);

}
