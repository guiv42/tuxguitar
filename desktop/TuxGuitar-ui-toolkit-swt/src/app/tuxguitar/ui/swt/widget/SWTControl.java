package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.swt.event.SWTDisposeListenerManager;
import app.tuxguitar.ui.swt.event.SWTFocusListenerManager;
import app.tuxguitar.ui.swt.event.SWTKeyListenerManager;
import app.tuxguitar.ui.swt.event.SWTMouseDragListenerManager;
import app.tuxguitar.ui.swt.event.SWTMouseListenerManager;
import app.tuxguitar.ui.swt.event.SWTMouseMoveListenerManager;
import app.tuxguitar.ui.swt.event.SWTMouseTrackListenerManager;
import app.tuxguitar.ui.swt.event.SWTMouseWheelListenerManager;
import app.tuxguitar.ui.swt.event.SWTResizeListenerManager;
import app.tuxguitar.ui.swt.event.SWTZoomListenerManager;
import app.tuxguitar.ui.swt.menu.SWTMenu;
import app.tuxguitar.ui.swt.resource.SWTColor;
import app.tuxguitar.ui.swt.resource.SWTCursor;
import app.tuxguitar.ui.swt.resource.SWTFont;
import app.tuxguitar.ui.widget.UIControl;

public abstract class SWTControl<T extends Control> extends SWTEventReceiver<T> implements UIControl {

	private SWTContainer<? extends Composite> parent;
	private SWTDisposeListenerManager disposeListener;
	private SWTResizeListenerManager resizeListener;
	private SWTKeyListenerManager keyListener;
	private SWTMouseListenerManager mouseListener;
	private SWTMouseMoveListenerManager mouseMoveListener;
	private SWTMouseWheelListenerManager mouseWheelListener;
	private SWTMouseDragListenerManager mouseDragListener;
	private SWTMouseTrackListenerManager mouseTrackListener;
	private SWTFocusListenerManager focusListener;
	private SWTZoomListenerManager zoomListener;

	protected UISize packedSize;
	private UIColor bgColor;
	private UIColor fgColor;
	private UIFont font;
	private UICursor cursor;
	private UIPopupMenu popupMenu;

	public SWTControl(T control, SWTContainer<? extends Composite> parent) {
		super(control);

		this.parent = parent;
		if( this.parent != null ) {
			this.parent.addChild(this);
		}
		this.packedSize = new UISize();
		this.disposeListener = new SWTDisposeListenerManager(this);
		this.resizeListener = new SWTResizeListenerManager(this);
		this.keyListener = new SWTKeyListenerManager(this);
		this.mouseListener = new SWTMouseListenerManager(this);
		this.mouseMoveListener = new SWTMouseMoveListenerManager(this);
		this.mouseWheelListener = new SWTMouseWheelListenerManager(this);
		this.mouseDragListener = new SWTMouseDragListenerManager(this);
		this.mouseTrackListener = new SWTMouseTrackListenerManager(this);
		this.focusListener = new SWTFocusListenerManager(this);
		this.zoomListener = new SWTZoomListenerManager(this);
	}

	public UIControl getParent() {
		return this.parent;
	}

	public void setControlBounds(UIRectangle bounds) {
		if(!this.getControlBounds().equals(bounds)) {
			this.getControl().setBounds(Math.round(bounds.getX()), Math.round(bounds.getY()), Math.round(bounds.getWidth()), Math.round(bounds.getHeight()));
		}
	}

	public UIRectangle getControlBounds() {
		Rectangle bounds = this.getControl().getBounds();
		return new UIRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public void setBounds(UIRectangle bounds) {
		this.setControlBounds(bounds);
	}

	public UIRectangle getBounds() {
		return this.getControlBounds();
	}

	public void setPackedSize(UISize packedSize) {
		this.packedSize.setWidth(packedSize.getWidth());
		this.packedSize.setHeight(packedSize.getHeight());
	}

	public UISize getPackedSize() {
		return new UISize(this.packedSize.getWidth(), this.packedSize.getHeight());
	}

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		int wHint = (fixedWidth != null ? fixedWidth.intValue() : SWT.DEFAULT);
		int hHint = (fixedHeight != null ? fixedHeight.intValue() : SWT.DEFAULT);

		Point point = this.getControl().computeSize(wHint, hHint);
		this.packedSize.setWidth(fixedWidth != null ? fixedWidth.intValue() : point.x);
		this.packedSize.setHeight(fixedHeight != null ? fixedHeight.intValue() : point.y);
	}

	public void dispose() {
		this.getControl().dispose();
	}

	public boolean isDisposed() {
		return this.getControl().isDisposed();
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
		return this.getControl().getToolTipText();
	}

	public void setToolTipText(String text) {
		this.getControl().setToolTipText(text);
	}

	public UIColor getColor(Color handle) {
		return (handle != null ? new SWTColor(handle) : null);
	}

	public UIColor getBgColor() {
		if( this.bgColor == null ) {
			this.bgColor = this.getColor(this.getControl().getBackground());
		}
		return this.bgColor;
	}

	public void setBgColor(UIColor color) {
		this.bgColor = color;
		this.getControl().setBackground(this.bgColor != null ? ((SWTColor) this.bgColor).getHandle() : null);
	}

	public UIColor getFgColor() {
		if( this.fgColor == null ) {
			this.fgColor = this.getColor(this.getControl().getForeground());
		}
		return this.fgColor;
	}

	public void setFgColor(UIColor color) {
		this.fgColor = color;
		this.getControl().setForeground(this.fgColor != null ? ((SWTColor) this.fgColor).getHandle() : null);
	}

	public UIFont getFont(Font handle) {
		return (handle != null ? new SWTFont(handle) : null);
	}

	public UIFont getFont() {
		if( this.font == null ) {
			this.font = this.getFont(this.getControl().getFont());
		}
		return this.font;
	}

	public void setFont(UIFont font) {
		this.font = font;
		this.getControl().setFont(this.font != null ? ((SWTFont) this.font).getHandle() : null);
	}

	public UICursor getCursor() {
		return (this.cursor != null ? this.cursor : UICursor.NORMAL);
	}

	public void setCursor(UICursor cursor) {
		this.cursor = cursor;
		this.getControl().setCursor(SWTCursor.getCursor(this.getControl(), this.getCursor()));
	}

	public UIPopupMenu getPopupMenu() {
		return this.popupMenu;
	}

	public void setPopupMenu(UIPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
		this.getControl().setMenu(this.popupMenu != null ? ((SWTMenu) this.popupMenu).getControl() : null);
	}

	public void setFocus() {
		this.getControl().setFocus();
	}

	public void redraw() {
		this.getControl().redraw();
	}

	public void addDisposeListener(UIDisposeListener listener) {
		if( this.disposeListener.isEmpty() ) {
			this.getControl().addDisposeListener(this.disposeListener);
		}
		this.disposeListener.addListener(listener);
	}

	public void removeDisposeListener(UIDisposeListener listener) {
		this.disposeListener.removeListener(listener);
		if( this.disposeListener.isEmpty() ) {
			this.getControl().removeDisposeListener(this.disposeListener);
		}
	}

	public void addMouseUpListener(UIMouseUpListener listener) {
		if( this.mouseListener.isEmpty() ) {
			this.getControl().addMouseListener(this.mouseListener);
		}
		this.mouseListener.addListener(listener);
	}

	public void removeMouseUpListener(UIMouseUpListener listener) {
		this.mouseListener.removeListener(listener);
		if( this.mouseListener.isEmpty() ) {
			this.getControl().removeMouseListener(this.mouseListener);
		}
	}

	public void addMouseDownListener(UIMouseDownListener listener) {
		if( this.mouseListener.isEmpty() ) {
			this.getControl().addMouseListener(this.mouseListener);
		}
		this.mouseListener.addListener(listener);
	}

	public void removeMouseDownListener(UIMouseDownListener listener) {
		this.mouseListener.removeListener(listener);
		if( this.mouseListener.isEmpty() ) {
			this.getControl().removeMouseListener(this.mouseListener);
		}
	}

	public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		if( this.mouseListener.isEmpty() ) {
			this.getControl().addMouseListener(this.mouseListener);
		}
		this.mouseListener.addListener(listener);
	}

	public void removeMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		this.mouseListener.removeListener(listener);
		if( this.mouseListener.isEmpty() ) {
			this.getControl().removeMouseListener(this.mouseListener);
		}
	}

	public void addMouseMoveListener(UIMouseMoveListener listener) {
		if( this.mouseMoveListener.isEmpty() ) {
			this.getControl().addMouseMoveListener(this.mouseMoveListener);
		}
		this.mouseMoveListener.addListener(listener);
	}

	public void removeMouseMoveListener(UIMouseMoveListener listener) {
		this.mouseMoveListener.removeListener(listener);
		if( this.mouseMoveListener.isEmpty() ) {
			this.getControl().removeMouseMoveListener(this.mouseMoveListener);
		}
	}

	public void addMouseDragListener(UIMouseDragListener listener) {
		if( this.mouseDragListener.isEmpty() ) {
			this.getControl().addMouseListener(this.mouseDragListener);
			this.getControl().addMouseMoveListener(this.mouseDragListener);
		}
		this.mouseDragListener.addListener(listener);
	}

	public void removeMouseDragListener(UIMouseDragListener listener) {
		this.mouseDragListener.removeListener(listener);
		if( this.mouseDragListener.isEmpty() ) {
			this.getControl().removeMouseListener(this.mouseDragListener);
			this.getControl().removeMouseMoveListener(this.mouseDragListener);
		}
	}

	public void addMouseWheelListener(UIMouseWheelListener listener) {
		if( this.mouseWheelListener.isEmpty() ) {
			this.getControl().addMouseWheelListener(this.mouseWheelListener);
		}
		this.mouseWheelListener.addListener(listener);
	}

	public void removeMouseWheelListener(UIMouseWheelListener listener) {
		this.mouseWheelListener.removeListener(listener);
		if( this.mouseWheelListener.isEmpty() ) {
			this.getControl().removeMouseWheelListener(this.mouseWheelListener);
		}
	}

	public void addMouseEnterListener(UIMouseEnterListener listener) {
		if( this.mouseTrackListener.isEmpty() ) {
			this.getControl().addMouseTrackListener(this.mouseTrackListener);
		}
		this.mouseTrackListener.addListener(listener);
	}

	public void removeMouseEnterListener(UIMouseEnterListener listener) {
		this.mouseTrackListener.removeListener(listener);
		if( this.mouseTrackListener.isEmpty() ) {
			this.getControl().removeMouseTrackListener(this.mouseTrackListener);
		}
	}

	public void addMouseExitListener(UIMouseExitListener listener) {
		if( this.mouseTrackListener.isEmpty() ) {
			this.getControl().addMouseTrackListener(this.mouseTrackListener);
		}
		this.mouseTrackListener.addListener(listener);
	}

	public void removeMouseExitListener(UIMouseExitListener listener) {
		this.mouseTrackListener.removeListener(listener);
		if( this.mouseTrackListener.isEmpty() ) {
			this.getControl().removeMouseTrackListener(this.mouseTrackListener);
		}
	}

	public void addKeyPressedListener(UIKeyPressedListener listener) {
		if( this.keyListener.isEmpty() ) {
			this.getControl().addKeyListener(this.keyListener);
		}
		this.keyListener.addListener(listener);
	}

	public void removeKeyPressedListener(UIKeyPressedListener listener) {
		this.keyListener.removeListener(listener);
		if( this.keyListener.isEmpty() ) {
			this.getControl().removeKeyListener(this.keyListener);
		}
	}

	public void addKeyReleasedListener(UIKeyReleasedListener listener) {
		if( this.keyListener.isEmpty() ) {
			this.getControl().addKeyListener(this.keyListener);
		}
		this.keyListener.addListener(listener);
	}

	public void removeKeyReleasedListener(UIKeyReleasedListener listener) {
		this.keyListener.removeListener(listener);
		if( this.keyListener.isEmpty() ) {
			this.getControl().removeKeyListener(this.keyListener);
		}
	}

	public void addResizeListener(UIResizeListener listener) {
		if( this.resizeListener.isEmpty() ) {
			this.getControl().addListener(SWT.Resize, this.resizeListener);
		}
		this.resizeListener.addListener(listener);
	}

	public void removeResizeListener(UIResizeListener listener) {
		this.resizeListener.removeListener(listener);
		if( this.resizeListener.isEmpty() ) {
			this.getControl().removeListener(SWT.Resize, this.resizeListener);
		}
	}

	public void addFocusGainedListener(UIFocusGainedListener listener) {
		if( this.focusListener.isEmpty() ) {
			this.getControl().addFocusListener(this.focusListener);
		}
		this.focusListener.addListener(listener);
	}

	public void removeFocusGainedListener(UIFocusGainedListener listener) {
		this.focusListener.removeListener(listener);
		if( this.focusListener.isEmpty() ) {
			this.getControl().removeFocusListener(this.focusListener);
		}
	}

	public void addFocusLostListener(UIFocusLostListener listener) {
		if( this.focusListener.isEmpty() ) {
			this.getControl().addFocusListener(this.focusListener);
		}
		this.focusListener.addListener(listener);
	}

	public void removeFocusLostListener(UIFocusLostListener listener) {
		this.focusListener.removeListener(listener);
		if( this.focusListener.isEmpty() ) {
			this.getControl().removeFocusListener(this.focusListener);
		}
	}

	public void addZoomListener(UIZoomListener listener) {
		if( this.zoomListener.isEmpty() ) {
			this.getControl().addListener(SWTZoomListenerManager.EVENT_TYPE, this.zoomListener);
		}
		this.zoomListener.addListener(listener);
	}

	public void removeZoomListener(UIZoomListener listener) {
		this.zoomListener.removeListener(listener);
		if( this.zoomListener.isEmpty() ) {
			this.getControl().removeListener(SWTZoomListenerManager.EVENT_TYPE, this.zoomListener);
		}
	}
}
