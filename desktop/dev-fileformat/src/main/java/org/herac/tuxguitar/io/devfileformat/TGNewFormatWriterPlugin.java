package org.herac.tuxguitar.io.devfileformat;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.plugin.TGSongWriterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGNewFormatWriterPlugin extends TGSongWriterPlugin{
	
	public TGNewFormatWriterPlugin() {
		super(true);
	}
	
	public String getModuleId(){
		return TGStream.MODULE_NAME;
	}

	@Override
	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new TGSongWriterImpl();
	}
	
}
