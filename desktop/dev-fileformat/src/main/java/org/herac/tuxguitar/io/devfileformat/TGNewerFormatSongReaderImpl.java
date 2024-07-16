package org.herac.tuxguitar.io.devfileformat;

import java.io.InputStream;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TGNewerFormatSongReaderImpl extends TGStream implements TGSongReader {
	
	// format can not be recognized by content, only by mime type or file extension
	public static TGFileFormat TG_NEWER_FORMAT = new TGFileFormat("TuxGuitar X", "application/x-tuxguitar", new String[]{ TG_FORMAT_CODE }, false, true, true);
	
	@Override
	public TGFileFormat getFileFormat() {
		return TG_NEWER_FORMAT;
	}

	@Override
	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		/* despite its name, this method does not read any file format, it always throws an exception
		 * added value: it selects the right error message when a newer file format is detected
		 * which cannot be decoded
		 */
		boolean newerMajorVersion = false;
		try {
			InputStream inputStream = handle.getInputStream();
			Document xmlDocument = this.getDocument(inputStream);
			Node root = getChildNode(xmlDocument, TAG_TGFile);
			// checking major version
			Node nodeVersion = getChildNode(root, TAG_FORMAT_VERSION);
			int major = readAttributeInt(nodeVersion, "major");
			newerMajorVersion =  (major > FILE_FORMAT_TGVERSION.getMajor());
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
		if (newerMajorVersion) {
			throw new TGFileFormatException(TuxGuitar.getProperty("error.new-major-version"));
		}
		else {
			throw new TGFileFormatException();
		}
	}
}
