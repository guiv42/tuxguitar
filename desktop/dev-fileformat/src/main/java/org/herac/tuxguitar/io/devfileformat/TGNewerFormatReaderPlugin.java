package org.herac.tuxguitar.io.devfileformat;

import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;

/* despite its name, this plugin does not read any file format
 * it just throws an exception with the right error messages when a newer file format is detected
 * which cannot be decoded
 */

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGNewerFormatReaderPlugin extends TGSongReaderPlugin {
	
	public TGNewerFormatReaderPlugin() {
		super(true);
	}

	@Override
	public String getModuleId() {
		return TGStream.MODULE_NAME;
	}

	@Override
	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new TGNewerFormatSongReaderImpl();
	}

	@Override
	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new TGFileFormatDetector() {
			@Override
			// this plugin is not supposed to be identified as a candidate to decode song based on file content
			// so, never recognize anything
			public TGFileFormat getFileFormat(InputStream inputStream) {
				return null;
			}
		};
	}
	
}
