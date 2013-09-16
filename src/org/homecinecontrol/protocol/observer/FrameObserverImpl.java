package org.homecinecontrol.protocol.observer;

import org.homecinecontrol.protocol.connection.Frame;

public class FrameObserverImpl extends ObservableImpl<Frame> implements
		Observable<Frame> {

	@Override
	public void notifyObserver(Frame fp) {
		synchronized (observers) {
			if (observers.containsKey(fp.getComponent())) {
				observers.get(fp.getComponent()).update(this, fp);
			}
		}
	}
}
