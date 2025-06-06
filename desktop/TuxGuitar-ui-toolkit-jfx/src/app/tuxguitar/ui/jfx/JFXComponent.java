package app.tuxguitar.ui.jfx;

import java.util.HashMap;
import java.util.Map;

import app.tuxguitar.ui.UIComponent;

public abstract class JFXComponent<T> implements UIComponent {

	private T component;
	private Map<String, Object> data;
	private boolean disposed;

	public JFXComponent(T component) {
		this.component = component;
	}

	public T getControl() {
		return this.component;
	}

	@SuppressWarnings("unchecked")
	public <Data> Data getData(String key) {
		return (Data) this.getData().get(key);
	}

	public <Data> void setData(String key, Data data) {
		this.getData().put(key, data);
	}

	public Map<String, Object> getData() {
		if( this.data == null ) {
			this.data = new HashMap<String, Object>();
		}
		return this.data;
	}

	public void clearData() {
		if( this.data != null ) {
			this.data.clear();
			this.data = null;
		}
	}

	public boolean isDisposed(){
		return this.disposed;
	}

	public void dispose(){
		this.disposed = true;
		this.component = null;
		this.clearData();
	}
}