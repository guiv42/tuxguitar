package app.tuxguitar.ui.resource;

public class UIPosition {

	private float x;
	private float y;

	public UIPosition() {
		this(0, 0);
	}

	public UIPosition(float x, float y) {
		this.x = x;
		debug(y);
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		// DEBUG
		debug(y);
		this.y = y;
	}

	public void set(UIPosition position) {
		this.x = position.getX();
		debug(position.getY());
		this.y = position.getY();
	}

	// DEBUG
	private void debug(float y) {
		if (y<-1f) {
			try {
				throw new Exception("negative Y value :" + String.valueOf(y));
			}
			catch(Throwable e) {
				e.printStackTrace();
			}
		}
	}
	// END DEBUG

	public void add(UIPosition position) {
		this.x += position.getX();
		this.y += position.getY();
	}

	public void copyFrom(UIPosition position) {
		this.setX(position.getX());
		this.setY(position.getY());
	}

	public UIPosition clone() {
		UIPosition uiPosition = new UIPosition();
		uiPosition.copyFrom(this);

		return uiPosition;
	}

	@Override
	public boolean equals(Object obj) {
		if( obj instanceof UIPosition ) {
			UIPosition uiPosition = (UIPosition) obj;

			return (this.getX() == uiPosition.getX() && this.getY() == uiPosition.getY());
		}
		return super.equals(obj);
	}
}
