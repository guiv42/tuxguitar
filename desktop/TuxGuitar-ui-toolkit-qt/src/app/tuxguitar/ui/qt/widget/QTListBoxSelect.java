package app.tuxguitar.ui.qt.widget;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.qt.event.QTSelectionListenerManager;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIListBoxSelect;
import app.tuxguitar.ui.widget.UISelectItem;
import io.qt.core.QMargins;
import io.qt.widgets.QListWidget;
import io.qt.widgets.QListWidgetItem;
import io.qt.widgets.QScrollBar;

public class QTListBoxSelect<T> extends QTWidget<QListWidget> implements UIListBoxSelect<T> {

	private List<UISelectItem<T>> items;
	private QTSelectionListenerManager selectionListener;

	public QTListBoxSelect(QTContainer parent) {
		super(new QListWidget(parent.getContainerControl()), parent);

		this.selectionListener = new QTSelectionListenerManager(this);
		this.items = new ArrayList<UISelectItem<T>>();
	}

	public T getSelectedValue() {
		UISelectItem<T> selectedItem = this.getSelectedItem();
		return (selectedItem != null ? selectedItem.getValue() : null);
	}

	public void setSelectedValue(T value) {
		this.setSelectedItem(new UISelectItem<T>(null, value));
	}

	public UISelectItem<T> getSelectedItem() {
		int index = this.getControl().currentRow();
		return (index >= 0 && index < this.items.size() ? this.items.get(index) : null);
	}

	public void setSelectedItem(UISelectItem<T> item) {
		int index = (item != null ? this.items.indexOf(item) : -1);
		this.getControl().setCurrentRow(index);
	}

	public void addItem(UISelectItem<T> item) {
		this.items.add(item);
		this.getControl().addItem(item.getText());
	}

	public void removeItem(UISelectItem<T> item) {
		int index = (item != null ? this.items.indexOf(item) : -1);
		if( index >= 0 && index < this.items.size() ) {
			this.items.remove(item);
			QListWidgetItem widgetItem = this.getControl().item(index);
			if( widgetItem != null ) {
				widgetItem.dispose();
			}
		}
	}

	public void removeItems() {
		this.items.clear();
		this.getControl().clear();
	}

	public int getItemCount() {
		return this.items.size();
	}

	@Override
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		QMargins margins = this.getControl().contentsMargins();

		float width = (margins.left() + margins.right());
		float height = (margins.top() + margins.bottom());

		if(!this.items.isEmpty() ) {
			width += (this.getControl().sizeHintForColumn(0));
			for(int i = 0; i < this.items.size(); i ++) {
				height += this.getControl().sizeHintForRow(i);
			}
		}

		QScrollBar vScroll = this.getControl().verticalScrollBar();
		if( vScroll != null && vScroll.isEnabled()) {
			width += vScroll.sizeHint().width();
		}
		if( fixedWidth != null && fixedWidth != width ) {
			width = fixedWidth;
		}
		if( fixedHeight != null && fixedHeight != height ) {
			height = fixedHeight;
		}
		this.setPackedSize(new UISize(width, height));
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().currentRowChanged.connect(this.selectionListener, QTSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().currentRowChanged.disconnect();
		}
	}
}