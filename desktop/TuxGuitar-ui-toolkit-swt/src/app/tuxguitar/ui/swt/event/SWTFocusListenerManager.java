package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusGainedListener;
import app.tuxguitar.ui.event.UIFocusGainedListenerManager;
import app.tuxguitar.ui.event.UIFocusLostListener;
import app.tuxguitar.ui.event.UIFocusLostListenerManager;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTFocusListenerManager implements FocusListener {

	private SWTEventReceiver<?> control;
	private UIFocusGainedListenerManager focusGainedListener;
	private UIFocusLostListenerManager focusLostListener;

	public SWTFocusListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
		this.focusGainedListener = new UIFocusGainedListenerManager();
		this.focusLostListener = new UIFocusLostListenerManager();
	}

	public boolean isEmpty() {
		return (this.focusGainedListener.isEmpty() && this.focusLostListener.isEmpty());
	}

	public void addListener(UIFocusGainedListener listener) {
		this.focusGainedListener.addListener(listener);
	}

	public void addListener(UIFocusLostListener listener) {
		this.focusLostListener.addListener(listener);
	}

	public void removeListener(UIFocusGainedListener listener) {
		this.focusGainedListener.removeListener(listener);
	}

	public void removeListener(UIFocusLostListener listener) {
		this.focusLostListener.removeListener(listener);
	}

	public void focusGained(FocusEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.focusGainedListener.onFocusGained(new UIFocusEvent(this.control));
		}
	}

	public void focusLost(FocusEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.focusLostListener.onFocusLost(new UIFocusEvent(this.control));
		}
	}
}
