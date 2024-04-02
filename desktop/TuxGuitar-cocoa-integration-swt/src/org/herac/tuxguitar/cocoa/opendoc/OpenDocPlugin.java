package org.herac.tuxguitar.cocoa.opendoc;

import org.herac.tuxguitar.cocoa.TGCocoaIntegrationPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGEarlyInitPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class OpenDocPlugin implements TGEarlyInitPlugin {
	
	private OpenDocListener openDocListener;
	
	public String getModuleId() {
		return TGCocoaIntegrationPlugin.MODULE_ID;
	}
	
	public void earlyInit() throws TGPluginException {
		if( this.openDocListener != null ) {
			this.openDocListener.disconnect();
		}
		this.openDocListener = new OpenDocListener();
	}
	
	public void connect(TGContext context) throws TGPluginException {
		this.openDocListener.process();
	}

	public void disconnect(TGContext context) throws TGPluginException {
		this.openDocListener.disconnect();
	}

}
