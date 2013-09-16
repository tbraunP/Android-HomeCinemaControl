package org.homecinecontrol.protocol.observer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;

public abstract class ObservableImpl<Typ> implements Observable<Typ> {
	@SuppressLint("UseSparseArrays")
	protected Map<Integer, Observer<Typ>> observers = new HashMap<Integer, Observer<Typ>>();

	@Override
	public void register(int id, Observer<Typ> subject) {
		synchronized (observers) {
			observers.put(id, subject);
		}
	}

	@Override
	public void unregister(Observer<Typ> subject) {
		synchronized (observers) {
			Integer key = null;
			do {
				key = null;

				for (Entry<Integer, Observer<Typ>> it : observers.entrySet()) {
					if (it.getValue() == subject) {
						key = it.getKey();
						break;
					}
				}
				if (key != null) {
					observers.remove(key);
				}
			} while (key != null);
		}
	}

	/**
	 * Method notifying all registered Observers about the change of fp. If
	 * synchronization is needed, use synchronized(observers){ ... }
	 * 
	 * @param fp
	 */
	public abstract void notifyObserver(Typ fp);
}
