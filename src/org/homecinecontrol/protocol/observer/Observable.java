package org.homecinecontrol.protocol.observer;

/**
 * Register a new observer
 * 
 * @author pyro
 * 
 */
public interface Observable<Type> {
	/**
	 * Register a new observer
	 * 
	 * @param id
	 *            - component id filtering the changes the observer should be
	 *            notified about (e.g. special frame ids)
	 * @param subject
	 *            - observer waiting for change
	 */
	public void register(int id, Observer<Type> observer);

	/**
	 * Unregister a observer
	 * 
	 * @param subject
	 */
	public void unregister(Observer<Type> subject);
}
