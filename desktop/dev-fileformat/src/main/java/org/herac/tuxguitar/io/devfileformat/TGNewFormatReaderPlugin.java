package org.herac.tuxguitar.io.devfileformat;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGNewFormatReaderPlugin extends TGSongReaderPlugin {
	
	public TGNewFormatReaderPlugin() {
		super(true);
	}

	@Override
	public String getModuleId() {
		return TGStream.MODULE_NAME;
	}

	@Override
	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new TGSongReaderImpl();
	}

	@Override
	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new TGFileFormatDetectorImpl();
	}
	
}
