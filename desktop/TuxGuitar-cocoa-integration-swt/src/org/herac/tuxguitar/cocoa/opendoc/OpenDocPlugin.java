package org.herac.tuxguitar.cocoa.opendoc;

import org.herac.tuxguitar.cocoa.TGCocoaIntegrationPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class OpenDocPlugin implements TGPlugin {
	
	private OpenDocListener openDocListener;
	
	public String getModuleId() {
		return TGCocoaIntegrationPlugin.MODULE_ID;
	}
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.openDocListener == null ){
				this.openDocListener = new OpenDocListener();
			}
			if (Boolean.TRUE.equals(context.getAttribute("EARLY_PLUGIN_INIT"))) {
				this.openDocListener.init();
			} else {
				this.openDocListener.process();
			}
		} catch( Throwable throwable ){
			throw new TGPluginException( throwable );
		}
		
	}

	public void disconnect(TGContext context) throws TGPluginException {
		this.openDocListener.disconnect();
	}
}
