package org.herac.tuxguitar.io.devfileformat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class TGFileFormatDetectorImpl extends TGStream implements TGFileFormatDetector{

	private static final String XSD_SCHEMA = "tuxguitar.xsd";
	
	public TGFileFormatDetectorImpl() {
		super();
	}
	
	@Override
	public TGFileFormat getFileFormat(InputStream inputStream) {
		try {
			return this.getFileFormatXML(getDecompressedContent(inputStream));
		} catch (IOException e) {
			return null;
		}
	}
	
	public TGFileFormat getFileFormatXML(InputStream inputStream) {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			URL urlXsd = getClass().getClassLoader().getResource(XSD_SCHEMA);
			schema = factory.newSchema(urlXsd);
			schema.newValidator().validate(new StreamSource(inputStream));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return TGStream.TG_FORMAT ;
	}

}
