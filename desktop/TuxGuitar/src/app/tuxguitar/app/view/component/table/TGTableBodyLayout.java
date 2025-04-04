package app.tuxguitar.app.view.component.table;

import java.util.List;

import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UILayoutContainer;

public class TGTableBodyLayout extends UITableLayout {

	private float rowHeight;
	private boolean initialized;

	public TGTableBodyLayout() {
		super(0f);
	}

	public void computeChildrenPackedSize(UILayoutContainer container) {
		super.computeChildrenPackedSize(container);

		this.rowHeight = 0f;

		List<UIControl> controls = container.getChildren();
		for(UIControl control : controls) {
			this.rowHeight = Math.max(this.rowHeight, control.getPackedSize().getHeight());
			initialized = true;
		}

		int row = 0;
		for(UIControl control : controls) {
			this.set(control, (++row), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			this.set(control, UITableLayout.PACKED_HEIGHT, this.rowHeight);
			this.set(control, UITableLayout.MARGIN, 0f);
			this.set(control, UITableLayout.MARGIN_TOP, 1f);
			this.computeChildPackedSize(control);
		}
	}

	public float getRowHeight() {
		return this.rowHeight;
	}

	public boolean isInitialized() {
		return initialized;
	}
}
