package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIFocusGainedListener;
import app.tuxguitar.ui.event.UIFocusLostListener;
import app.tuxguitar.ui.event.UIKeyPressedListener;
import app.tuxguitar.ui.event.UIKeyReleasedListener;
import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseDownListener;
import app.tuxguitar.ui.event.UIMouseDragListener;
import app.tuxguitar.ui.event.UIMouseEnterListener;
import app.tuxguitar.ui.event.UIMouseExitListener;
import app.tuxguitar.ui.event.UIMouseMoveListener;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.event.UIMouseWheelListener;
import app.tuxguitar.ui.event.UIResizeListener;
import app.tuxguitar.ui.event.UIZoomListener;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.event.QTDisposeListenerManager;
import app.tuxguitar.ui.qt.event.QTEventFilter;
import app.tuxguitar.ui.qt.event.QTFocusGainedListenerManager;
import app.tuxguitar.ui.qt.event.QTFocusLostListenerManager;
import app.tuxguitar.ui.qt.event.QTKeyPressedListenerManager;
import app.tuxguitar.ui.qt.event.QTKeyReleasedListenerManager;
import app.tuxguitar.ui.qt.event.QTMenuOpenListenerManager;
import app.tuxguitar.ui.qt.event.QTMouseDoubleClickListenerManager;
import app.tuxguitar.ui.qt.event.QTMouseDownListenerManager;
import app.tuxguitar.ui.qt.event.QTMouseDragListenerManager;
import app.tuxguitar.ui.qt.event.QTMouseEnterListenerManager;
import app.tuxguitar.ui.qt.event.QTMouseExitListenerManager;
import app.tuxguitar.ui.qt.event.QTMouseMoveListenerManager;
import app.tuxguitar.ui.qt.event.QTMouseUpListenerManager;
import app.tuxguitar.ui.qt.event.QTMouseWheelListenerManager;
import app.tuxguitar.ui.qt.event.QTResizeListenerManager;
import app.tuxguitar.ui.qt.event.QTZoomListenerManager;
import app.tuxguitar.ui.qt.resource.QTColor;
import app.tuxguitar.ui.qt.resource.QTCursor;
import app.tuxguitar.ui.qt.resource.QTFont;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIControl;
import io.qt.core.QEvent.Type;
import io.qt.core.QPoint;
import io.qt.core.QRect;
import io.qt.core.QSize;
import io.qt.gui.QColor;
import io.qt.gui.QFont;
import io.qt.gui.QPalette;
import io.qt.gui.QPalette.ColorRole;
import io.qt.widgets.QApplication;
import io.qt.widgets.QWidget;
import io.qt.core.QMetaObject;
import io.qt.core.Qt;
import io.qt.core.Qt.ConnectionType;

public abstract class QTWidget<T extends QWidget> extends QTComponent<T> implements UIControl {

	private QTContainer parent;

	private QTEventFilter eventFilter;
	private QTDisposeListenerManager disposeListener;
	private QTFocusGainedListenerManager focusGainedListener;
	private QTFocusLostListenerManager focusLostListener;
	private QTKeyPressedListenerManager keyPressedListener;
	private QTKeyReleasedListenerManager keyReleasedListener;
	private QTMouseUpListenerManager mouseUpListener;
	private QTMouseDownListenerManager mouseDownListener;
	private QTMouseDoubleClickListenerManager mouseDoubleClickListener;
	private QTMouseMoveListenerManager mouseMoveListener;
	private QTMouseDragListenerManager mouseDragListener;
	private QTMouseEnterListenerManager mouseEnterListener;
	private QTMouseExitListenerManager mouseExitListener;
	private QTMouseWheelListenerManager mouseWheelListener;
	private QTResizeListenerManager resizeListener;
	private QTMenuOpenListenerManager menuOpenListener;
	private QTZoomListenerManager zoomListener;

	private UISize packedSize;
	private UIColor bgColor;
	private UIColor fgColor;
	private UIFont font;
	private UICursor cursor;
	private UIPopupMenu popupMenu;
	private boolean ignoreEvents;

	public QTWidget(T control, QTContainer parent, boolean immediatelyShow) {
		super(control);

		this.parent = parent;
		if( this.parent != null ) {
			this.parent.addChild(this);
		}
		this.packedSize = new UISize();

		this.eventFilter = new QTEventFilter();
		this.disposeListener = new QTDisposeListenerManager(this);
		this.focusGainedListener = new QTFocusGainedListenerManager(this);
		this.focusLostListener = new QTFocusLostListenerManager(this);
		this.keyPressedListener = new QTKeyPressedListenerManager(this);
		this.keyReleasedListener = new QTKeyReleasedListenerManager(this);
		this.mouseUpListener = new QTMouseUpListenerManager(this);
		this.mouseDownListener = new QTMouseDownListenerManager(this);
		this.mouseDoubleClickListener = new QTMouseDoubleClickListenerManager(this);
		this.mouseMoveListener = new QTMouseMoveListenerManager(this);
		this.mouseWheelListener = new QTMouseWheelListenerManager(this);
		this.mouseDragListener = new QTMouseDragListenerManager(this);
		this.mouseEnterListener = new QTMouseEnterListenerManager(this);
		this.mouseExitListener = new QTMouseExitListenerManager(this);
		this.menuOpenListener = new QTMenuOpenListenerManager(this);
		this.resizeListener = new QTResizeListenerManager(this);
		this.zoomListener = new QTZoomListenerManager(this);

		this.getControl().installEventFilter(this.eventFilter);

		if( immediatelyShow && parent != null && parent.getContainerControl().isVisible() ) {
			this.showLater();
		}
	}

	public QTWidget(T control, QTContainer parent) {
		this(control, parent, true);
	}

	public UIControl getParent() {
		return this.parent;
	}

	public void setBounds(UIRectangle bounds) {
		if(!this.getBounds().equals(bounds)) {
			this.getControl().setGeometry(Math.round(bounds.getX()), Math.round(bounds.getY()), Math.round(bounds.getWidth()), Math.round(bounds.getHeight()));
		}
	}

	public UIRectangle getBounds() {
		QRect qRect = this.getControl().geometry();
		return new UIRectangle(qRect.x(), qRect.y(), qRect.width(), qRect.height());
	}

	public void setPackedSize(UISize packedSize) {
		this.packedSize.setWidth(packedSize.getWidth());
		this.packedSize.setHeight(packedSize.getHeight());
	}

	public UISize getPackedSize() {
		return new UISize(this.packedSize.getWidth(), this.packedSize.getHeight());
	}

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		QSize qSize = this.getControl().sizeHint();

		this.packedSize.setWidth(fixedWidth != null ? fixedWidth : qSize.width());
		this.packedSize.setHeight(fixedHeight != null ? fixedHeight : qSize.height());
	}

	public void dispose() {
		if( this.parent != null ) {
			this.parent.removeChild(this);
		}
		this.getControl().dispose();

		super.dispose();
	}

	public boolean isDisposed() {
		return (super.isDisposed());
	}

	public boolean isEnabled() {
		return this.getControl().isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.getControl().setEnabled(enabled);
	}

	public boolean isVisible() {
		return this.getControl().isVisible();
	}

	public void setVisible(boolean visible) {
		this.getControl().setVisible(visible);
	}

	public String getToolTipText() {
		return this.getControl().toolTip();
	}

	public void setToolTipText(String toolTipText) {
		this.getControl().setToolTip(toolTipText);
	}

	public UIColor getColor(QColor handle) {
		return (handle != null ? new QTColor(handle) : null);
	}

	public void setColor(ColorRole colorRole, UIColor color) {
		QPalette qPalette = this.getControl().palette();
		qPalette.setColor(colorRole, color != null ? ((QTColor) color).getControl() : null);

		this.getControl().setPalette(qPalette);
	}

	public UIColor getBgColor() {
		if( this.bgColor == null ) {
			this.bgColor = this.getColor(this.getControl().palette().color(this.getControl().backgroundRole()));
		}
		return this.bgColor;
	}

	public void setBgColor(UIColor color) {
		this.bgColor = color;
		this.setColor(this.getControl().backgroundRole(), this.bgColor);
	}

	public UIColor getFgColor() {
		if( this.fgColor == null ) {
			this.fgColor = this.getColor(this.getControl().palette().color(this.getControl().foregroundRole()));
		}
		return this.fgColor;
	}

	public void setFgColor(UIColor color) {
		this.fgColor = color;
		this.getControl().setAutoFillBackground(this.fgColor != null);
		this.setColor(this.getControl().foregroundRole(), this.fgColor);
	}

	public UIFont getFont(QFont handle) {
		return (handle != null ? new QTFont(handle) : null);
	}

	public UIFont getFont() {
		if( this.font == null ) {
			this.font = this.getFont(this.getControl().font());
		}
		return this.font;
	}

	public void setFont(UIFont font) {
		this.font = font;
		this.getControl().setFont(this.font != null ? ((QTFont) this.font).getControl() : null);
	}

	public UICursor getCursor() {
		return (this.cursor != null ? this.cursor : UICursor.NORMAL);
	}

	public void setCursor(UICursor cursor) {
		this.cursor = cursor;
		this.getControl().setCursor(QTCursor.getCursor(this.getCursor()));
	}

	public UIPopupMenu getPopupMenu() {
		return this.popupMenu;
	}

	public void setPopupMenu(UIPopupMenu popupMenu) {
		if( this.popupMenu == null && popupMenu != null ) {
			this.addMouseDownListener(this.menuOpenListener);
		}
		else if( this.popupMenu != null && popupMenu == null ) {
			this.removeMouseDownListener(this.menuOpenListener);
		}
		this.popupMenu = popupMenu;
	}

	public void setFocus() {
		this.getControl().setFocus();
	}

	public void redraw() {
		Runnable runnable = new Runnable() {
			public void run() {
				if(!QTWidget.this.isDisposed()) {
					QTWidget.this.repaint();
				}
			}
		};
		QMetaObject.invokeMethod(runnable::run, Qt.ConnectionType.QueuedConnection);
	}

	public void repaint() {
		this.getControl().repaint();
	}

	public QTEventFilter getEventFilter() {
		return this.eventFilter;
	}

	public void show() {
		if(!this.getControl().isVisible()) {
			this.getControl().show();
		}
	}

	public void showLater() {
		Runnable runnable = new Runnable() {
			public void run() {
				if(!QTWidget.this.isDisposed()) {
					QTWidget.this.show();
				}
			}
		};
		QMetaObject.invokeMethod(runnable::run, Qt.ConnectionType.QueuedConnection);
	}

	public void openPopupMenu(final UIPosition pos) {
// TODO QT 5->6 //		if( this.popupMenu != null ) {
			Runnable runnable = new Runnable() {
				public void run() {
					if(!QTWidget.this.isDisposed() && QTWidget.this.popupMenu != null ) {
						QPoint position = QTWidget.this.getControl().mapToGlobal(new QPoint(Math.round(pos.getX()), Math.round(pos.getY())));
						QTWidget.this.popupMenu.open(new UIPosition(position.x(), position.y()));
					}
				}
// TODO QT 5->6 //		}
		};
		QMetaObject.invokeMethod(runnable::run, Qt.ConnectionType.QueuedConnection);
	}

	public boolean isIgnoreEvents() {
		return this.ignoreEvents;
	}

	public void setIgnoreEvents(boolean ignoreEvents) {
		this.ignoreEvents = ignoreEvents;
		this.eventFilter.setIgnoreEvents(this.ignoreEvents);
		this.getControl().blockSignals(ignoreEvents);
	}

	public void addDisposeListener(UIDisposeListener listener) {
		if( this.disposeListener.isEmpty() ) {
			this.getEventFilter().connect(Type.Destroy, this.disposeListener);
		}
		this.disposeListener.addListener(listener);
	}

	public void removeDisposeListener(UIDisposeListener listener) {
		this.disposeListener.removeListener(listener);
		if( this.disposeListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.Destroy, this.disposeListener);
		}
	}

	public void addMouseUpListener(UIMouseUpListener listener) {
		if( this.mouseUpListener.isEmpty() ) {
			this.getEventFilter().connect(Type.MouseButtonRelease, this.mouseUpListener);
		}
		this.mouseUpListener.addListener(listener);
	}

	public void removeMouseUpListener(UIMouseUpListener listener) {
		this.mouseUpListener.removeListener(listener);
		if( this.mouseUpListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.MouseButtonRelease, this.mouseUpListener);
		}
	}

	public void addMouseDownListener(UIMouseDownListener listener) {
		if( this.mouseDownListener.isEmpty() ) {
			this.getEventFilter().connect(Type.MouseButtonPress, this.mouseDownListener);
		}
		this.mouseDownListener.addListener(listener);
	}

	public void removeMouseDownListener(UIMouseDownListener listener) {
		this.mouseDownListener.removeListener(listener);
		if( this.mouseDownListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.MouseButtonPress, this.mouseDownListener);
		}
	}

	public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		if( this.mouseDoubleClickListener.isEmpty() ) {
			this.getEventFilter().connect(Type.MouseButtonDblClick, this.mouseDoubleClickListener);
		}
		this.mouseDoubleClickListener.addListener(listener);
	}

	public void removeMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		this.mouseDoubleClickListener.removeListener(listener);
		if( this.mouseDoubleClickListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.MouseButtonDblClick, this.mouseDoubleClickListener);
		}
	}

	public void addMouseMoveListener(UIMouseMoveListener listener) {
		if( this.mouseMoveListener.isEmpty() ) {
			this.getControl().setMouseTracking(true);
			this.getEventFilter().connect(Type.MouseMove, this.mouseMoveListener);
		}
		this.mouseMoveListener.addListener(listener);
	}

	public void removeMouseMoveListener(UIMouseMoveListener listener) {
		this.mouseMoveListener.removeListener(listener);
		if( this.mouseMoveListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.MouseMove, this.mouseMoveListener);
			this.getControl().setMouseTracking(false);
		}
	}

	public void addMouseDragListener(UIMouseDragListener listener) {
		if( this.mouseDragListener.isEmpty() ) {
			this.addMouseUpListener(this.mouseDragListener);
			this.addMouseDownListener(this.mouseDragListener);
			this.addMouseMoveListener(this.mouseDragListener);
		}
		this.mouseDragListener.addListener(listener);
	}

	public void removeMouseDragListener(UIMouseDragListener listener) {
		this.mouseDragListener.removeListener(listener);
		if( this.mouseDragListener.isEmpty() ) {
			this.removeMouseUpListener(this.mouseDragListener);
			this.removeMouseDownListener(this.mouseDragListener);
			this.removeMouseMoveListener(this.mouseDragListener);
		}
	}

	public void addMouseWheelListener(UIMouseWheelListener listener) {
		if( this.mouseWheelListener.isEmpty() ) {
			this.getEventFilter().connect(Type.Wheel, this.mouseWheelListener);
		}
		this.mouseWheelListener.addListener(listener);
	}

	public void removeMouseWheelListener(UIMouseWheelListener listener) {
		this.mouseWheelListener.removeListener(listener);
		if( this.mouseWheelListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.Wheel, this.mouseWheelListener);
		}
	}

	public void addMouseEnterListener(UIMouseEnterListener listener) {
		if( this.mouseEnterListener.isEmpty() ) {
			this.getEventFilter().connect(Type.HoverEnter, this.mouseEnterListener);
		}
		this.mouseEnterListener.addListener(listener);
	}

	public void removeMouseEnterListener(UIMouseEnterListener listener) {
		this.mouseEnterListener.removeListener(listener);
		if( this.mouseEnterListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.HoverEnter, this.mouseEnterListener);
		}
	}

	public void addMouseExitListener(UIMouseExitListener listener) {
		if( this.mouseExitListener.isEmpty() ) {
			this.getEventFilter().connect(Type.HoverLeave, this.mouseExitListener);
		}
		this.mouseExitListener.addListener(listener);
	}

	public void removeMouseExitListener(UIMouseExitListener listener) {
		this.mouseExitListener.removeListener(listener);
		if( this.mouseExitListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.HoverLeave, this.mouseExitListener);
		}
	}

	public void addKeyPressedListener(UIKeyPressedListener listener) {
		if( this.keyPressedListener.isEmpty() ) {
			this.getEventFilter().connect(Type.KeyPress, this.keyPressedListener);
		}
		this.keyPressedListener.addListener(listener);
	}

	public void removeKeyPressedListener(UIKeyPressedListener listener) {
		this.keyPressedListener.removeListener(listener);
		if( this.keyPressedListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.KeyPress, this.keyPressedListener);
		}
	}

	public void addKeyReleasedListener(UIKeyReleasedListener listener) {
		if( this.keyReleasedListener.isEmpty() ) {
			this.getEventFilter().connect(Type.KeyRelease, this.keyReleasedListener);
		}
		this.keyReleasedListener.addListener(listener);
	}

	public void removeKeyReleasedListener(UIKeyReleasedListener listener) {
		this.keyReleasedListener.removeListener(listener);
		if( this.keyReleasedListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.KeyRelease, this.keyReleasedListener);
		}
	}

	public void addFocusGainedListener(UIFocusGainedListener listener) {
		if( this.focusGainedListener.isEmpty() ) {
			this.getEventFilter().connect(Type.FocusIn, this.focusGainedListener);
		}
		this.focusGainedListener.addListener(listener);
	}

	public void removeFocusGainedListener(UIFocusGainedListener listener) {
		this.focusGainedListener.removeListener(listener);
		if( this.focusGainedListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.FocusIn, this.focusGainedListener);
		}
	}

	public void addFocusLostListener(UIFocusLostListener listener) {
		if( this.focusLostListener.isEmpty() ) {
			this.getEventFilter().connect(Type.FocusOut, this.focusLostListener);
		}
		this.focusLostListener.addListener(listener);
	}

	public void removeFocusLostListener(UIFocusLostListener listener) {
		this.focusLostListener.removeListener(listener);
		if( this.focusLostListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.FocusOut, this.focusLostListener);
		}
	}

	public void addResizeListener(UIResizeListener listener) {
		if( this.resizeListener.isEmpty() ) {
			this.getEventFilter().connect(Type.Resize, this.resizeListener);
		}
		this.resizeListener.addListener(listener);
	}

	public void removeResizeListener(UIResizeListener listener) {
		this.resizeListener.removeListener(listener);
		if( this.resizeListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.Resize, this.resizeListener);
		}
	}

	public void addZoomListener(UIZoomListener listener) {
		if( this.zoomListener.isEmpty() ) {
			this.getEventFilter().connect(Type.Wheel, this.zoomListener);
		}
		this.zoomListener.addListener(listener);
	}

	public void removeZoomListener(UIZoomListener listener) {
		this.zoomListener.removeListener(listener);
		if( this.zoomListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.Wheel, this.zoomListener);
		}
	}
}
