package org.herac.tuxguitar.util.plugin;

public interface TGEarlyInitPlugin extends TGPlugin {

	// called before UI is initialized
	void earlyInit() throws TGPluginException;
	
}
