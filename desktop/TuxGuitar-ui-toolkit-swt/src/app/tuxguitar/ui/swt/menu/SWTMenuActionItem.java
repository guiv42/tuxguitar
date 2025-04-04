package app.tuxguitar.ui.swt.menu;

import org.eclipse.swt.widgets.MenuItem;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.swt.event.SWTSelectionListenerManager;

public class SWTMenuActionItem extends SWTMenuItem implements UIMenuActionItem {

	protected SWTSelectionListenerManager selectionListener;

	public SWTMenuActionItem(MenuItem item, SWTMenu parent) {
		super(item, parent);

		this.selectionListener = new SWTSelectionListenerManager(this);
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.selectionListener);
		}
	}
}
