package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import app.tuxguitar.ui.event.UIMenuEvent;
import app.tuxguitar.ui.event.UIMenuHideListener;
import app.tuxguitar.ui.event.UIMenuHideListenerManager;
import app.tuxguitar.ui.event.UIMenuShowListener;
import app.tuxguitar.ui.event.UIMenuShowListenerManager;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMenuListenerManager implements MenuListener {

	private SWTEventReceiver<?> control;
	private UIMenuShowListenerManager menuShowListener;
	private UIMenuHideListenerManager menuHideListener;

	public SWTMenuListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
		this.menuShowListener = new UIMenuShowListenerManager();
		this.menuHideListener = new UIMenuHideListenerManager();
	}

	public boolean isEmpty() {
		return (this.menuShowListener.isEmpty() && this.menuHideListener.isEmpty());
	}

	public void addListener(UIMenuShowListener listener) {
		this.menuShowListener.addListener(listener);
	}

	public void addListener(UIMenuHideListener listener) {
		this.menuHideListener.addListener(listener);
	}

	public void removeListener(UIMenuShowListener listener) {
		this.menuShowListener.removeListener(listener);
	}

	public void removeListener(UIMenuHideListener listener) {
		this.menuHideListener.removeListener(listener);
	}

	public void menuShown(MenuEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.menuShowListener.onMenuShow(new UIMenuEvent(this.control));
		}
	}

	public void menuHidden(MenuEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.menuHideListener.onMenuHide(new UIMenuEvent(this.control));
		}
	}
}
