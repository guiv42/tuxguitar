package app.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.ui.resource.UIResource;

public class TGResourceBuffer {

	private List<Object> registry;
	private Map<Object, UIResource> buffer;

	public TGResourceBuffer() {
		this.buffer = new HashMap<Object, UIResource>();
		this.registry = new ArrayList<Object>();
	}

	@SuppressWarnings("unchecked")
	public <T extends UIResource> T getResource(Object key) {
		if( this.buffer.containsKey(key) ) {
			return (T) this.buffer.get(key);
		}
		return null;
	}

	public void setResource(Object key, UIResource resource) {
		if( this.buffer.containsKey(key) ) {
			this.disposeResource(key);
		}

		this.buffer.put(key, resource);
	}

	public void disposeResource(Object key) {
		UIResource resource = this.getResource(key);
		if( resource != null && !resource.isDisposed() ) {
			resource.dispose();
		}
		this.buffer.remove(key);
	}

	public void disposeAllResources() {
		List<Object> keys = new ArrayList<Object>(this.buffer.keySet());
		for(Object key : keys) {
			this.disposeResource(key);
		}
	}

	public void disposeUnregisteredResources() {
		List<Object> keys = new ArrayList<Object>(this.buffer.keySet());
		for(Object key : keys) {
			if(!this.isRegistered(key)) {
				this.disposeResource(key);
			}
		}
	}

	public void clearRegistry() {
		this.registry.clear();
	}

	public void register(Object key) {
		if(!this.isRegistered(key)) {
			this.registry.add(key);
		}
	}

	public void unregister(Object key) {
		if( this.isRegistered(key)) {
			this.registry.remove(key);
		}
	}

	public boolean isRegistered(Object key) {
		return this.registry.contains(key);
	}

	public boolean isResourceDisposed(Object key) {
		UIResource resource = this.getResource(key);

		return (resource == null || resource.isDisposed());
	}
}
