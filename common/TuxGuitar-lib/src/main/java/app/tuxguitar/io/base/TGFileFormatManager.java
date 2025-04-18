package app.tuxguitar.io.base;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.io.tg.TGFileFormatDetectorImpl;
import app.tuxguitar.io.tg.TGSongReaderImpl;
import app.tuxguitar.io.tg.TGSongWriterImpl;
import app.tuxguitar.io.tg.TGStream;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGFileFormatManager {

	private TGContext context;
	private List<TGSongReader> readers;
	private List<TGSongWriter> writers;
	private List<TGSongExporter> exporters;
	private List<TGSongImporter> importers;

	private List<TGFileFormatDetector> fileFormatDetectors;
	private List<TGFileFormat> commonReadFileFormats;
	private TGFileFormat nativeFileFormat;

	private TGFileFormatManager(TGContext context){
		this.context = context;
		this.readers = new ArrayList<TGSongReader>();
		this.writers = new ArrayList<TGSongWriter>();
		this.exporters = new ArrayList<TGSongExporter>();
		this.importers = new ArrayList<TGSongImporter>();
		this.fileFormatDetectors = new ArrayList<TGFileFormatDetector>();
		this.commonReadFileFormats = new ArrayList<TGFileFormat>();
		this.nativeFileFormat = TGStream.TG_FORMAT;
		this.addDefaults();
	}

	private void addDefaults(){
		this.addReader(new TGSongReaderImpl());
		this.addWriter(new TGSongWriterImpl());
		this.addFileFormatDetector(new TGFileFormatDetectorImpl());
		this.addCommonReadFileFormat(TGSongReaderImpl.TG_FORMAT);
	}

	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		TGSongReaderHelper tgSongReaderHelper = new TGSongReaderHelper(this.context);
		tgSongReaderHelper.read(handle);
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		TGSongWriterHelper tgSongWriterHelper = new TGSongWriterHelper(this.context);
		tgSongWriterHelper.write(handle);
	}

	public List<TGSongReader> findSongReaders(Boolean commonFileFormats) {
		List<TGSongReader> readers = new ArrayList<TGSongReader>();
		for(TGSongReader reader : this.readers) {
			if (commonFileFormats == null) {
 				readers.add(reader);
			} else if (commonFileFormats) {
				if (this.isCommonReadFileFormat(reader.getFileFormat())) {
					readers.add(reader);
				}
			} else {
				if (!this.isCommonReadFileFormat(reader.getFileFormat()) && !this.isNativeFileFormat(reader.getFileFormat().getSupportedFormats()) ) {
					readers.add(reader);
				}
			}
		}
		return readers;
	}

	public List<TGSongWriter> findSongWriters(Boolean nativeFileFormat) {
		List<TGSongWriter> writers = new ArrayList<TGSongWriter>();
		for(TGSongWriter writer : this.writers) {
			if( nativeFileFormat == null || nativeFileFormat.equals(this.isNativeFileFormat(writer.getFileFormat()))){
				writers.add(writer);
			}
		}
		return writers;
	}

	public TGSongReader findSongReader(TGFileFormat fileFormat) {
		if( fileFormat != null ) {
			for(TGSongReader reader : this.readers) {
				if( reader.getFileFormat().equals(fileFormat) ){
					return reader;
				}
			}
		}
		return null;
	}

	public TGSongWriter findSongWriter(TGFileFormat fileFormat) {
		if( fileFormat != null ) {
			for(TGSongWriter writer : this.writers) {
				if( writer.getFileFormat().equals(fileFormat) ){
					return writer;
				}
			}
		}
		return null;
	}

	public List<TGFileFormat> findReadFileFormats(Boolean commonFileFormats) {
		List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
		List<TGSongReader> readers = this.findSongReaders(commonFileFormats);
		for(TGSongReader reader : readers) {
			TGFileFormat format = reader.getFileFormat();
			if(!formats.contains(format)){
				formats.add(format);
			}
		}
		return formats;
	}

	public List<TGFileFormat> findWriteFileFormats(Boolean commonFileFormats) {
		List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
		List<TGSongWriter> writers = this.findSongWriters(commonFileFormats);
		for(TGSongWriter writer : writers) {
			TGFileFormat format = writer.getFileFormat();
			if(!formats.contains(format)){
				formats.add(format);
			}
		}
		return formats;
	}

	public TGFileFormat findReaderFileFormatByCode(String formatCode, Boolean commonFileFormats){
		if( formatCode != null ) {
			List<TGFileFormat> fileFormats = this.findReadFileFormats(commonFileFormats);
			for(TGFileFormat fileFormat : fileFormats) {
				if( fileFormat.isSupportedCode(formatCode)){
					return fileFormat;
				}
			}
		}
		return null;
	}

	public TGFileFormat findReaderFileFormatByCode(String formatCode){
		TGFileFormat fileFormat = findReaderFileFormatByCode(formatCode, true);
		if( fileFormat == null ) {
			fileFormat = findReaderFileFormatByCode(formatCode, false);
		}
		return fileFormat;
	}

	public TGFileFormat findReaderFileFormatByMimeType(String mimeType, Boolean commonFileFormats){
		if( mimeType != null ) {
			List<TGFileFormat> fileFormats = this.findReadFileFormats(commonFileFormats);
			for(TGFileFormat fileFormat : fileFormats) {
				if( fileFormat.isSupportedMimeType(mimeType)){
					return fileFormat;
				}
			}
		}
		return null;
	}

	public TGFileFormat findReaderFileFormatByMimeType(String mimeType){
		TGFileFormat fileFormat = findReaderFileFormatByMimeType(mimeType, true);
		if( fileFormat == null ) {
			fileFormat = findReaderFileFormatByMimeType(mimeType, false);
		}
		return fileFormat;
	}

	public TGFileFormat findWriterFileFormatByCode(String formatCode, Boolean commonFileFormats){
		if( formatCode != null ) {
			List<TGFileFormat> fileFormats = this.findWriteFileFormats(commonFileFormats);
			for(TGFileFormat fileFormat : fileFormats) {
				if( fileFormat.isSupportedCode(formatCode) ){
					return fileFormat;
				}
			}
		}
		return null;
	}

	public TGFileFormat findWriterFileFormatByCode(String formatCode){
		TGFileFormat fileFormat = findWriterFileFormatByCode(formatCode, true);
		if( fileFormat == null ) {
			fileFormat = findWriterFileFormatByCode(formatCode, false);
		}
		return fileFormat;
	}

	public TGFileFormat findWriterFileFormatByMimeType(String mimeType, Boolean commonFileFormats){
		if( mimeType != null ) {
			List<TGFileFormat> fileFormats = this.findReadFileFormats(commonFileFormats);
			for(TGFileFormat fileFormat : fileFormats) {
				if( fileFormat.isSupportedMimeType(mimeType)){
					return fileFormat;
				}
			}
		}
		return null;
	}

	public TGFileFormat findWriterFileFormatByMimeType(String mimeType){
		TGFileFormat fileFormat = findWriterFileFormatByMimeType(mimeType, true);
		if( fileFormat == null ) {
			fileFormat = findWriterFileFormatByMimeType(mimeType, false);
		}
		return fileFormat;
	}

	public void addReader(TGSongReader stream){
		if(!this.readers.contains(stream)){
			this.readers.add(stream);
			this.fireFileFormatAvailabilityEvent();
		}
	}

	public void removeReader(TGSongReader stream){
		if( this.readers.contains(stream)){
			this.readers.remove(stream);
			this.fireFileFormatAvailabilityEvent();
		}
	}

	public void addWriter(TGSongWriter stream){
		if(!this.writers.contains(stream)){
			this.writers.add(stream);
			this.fireFileFormatAvailabilityEvent();
		}
	}

	public void removeWriter(TGSongWriter stream){
		if( this.writers.contains(stream)){
			this.writers.remove(stream);
			this.fireFileFormatAvailabilityEvent();
		}
	}

	public void addImporter(TGSongImporter importer){
		if(!this.importers.contains(importer)){
			this.importers.add(importer);
			this.fireFileFormatAvailabilityEvent();
		}
	}

	public void removeImporter(TGSongImporter importer){
		if( this.importers.contains(importer)){
			this.importers.remove(importer);
			this.fireFileFormatAvailabilityEvent();
		}
	}

	public List<TGSongImporter> getImporters(){
		return new ArrayList<TGSongImporter>(this.importers);
	}

	public void addExporter(TGSongExporter exporter){
		if(!this.exporters.contains(exporter)){
			this.exporters.add(exporter);
			this.fireFileFormatAvailabilityEvent();
		}
	}

	public void removeExporter(TGSongExporter exporter){
		if( this.exporters.contains(exporter)){
			this.exporters.remove(exporter);
			this.fireFileFormatAvailabilityEvent();
		}
	}

	public List<TGSongExporter> getExporters(){
		return new ArrayList<TGSongExporter>(this.exporters);
	}

	public void addFileFormatDetector(TGFileFormatDetector detector){
		if(!this.fileFormatDetectors.contains(detector)){
			this.fileFormatDetectors.add(detector);
		}
	}

	public void removeFileFormatDetector(TGFileFormatDetector detector){
		if( this.fileFormatDetectors.contains(detector)){
			this.fileFormatDetectors.remove(detector);
		}
	}

	public List<TGFileFormatDetector> getFileFormatDetectors(){
		return new ArrayList<TGFileFormatDetector>(this.fileFormatDetectors);
	}

	public void addCommonReadFileFormat(TGFileFormat fileFormat){
		if(!this.commonReadFileFormats.contains(fileFormat)){
			this.commonReadFileFormats.add(fileFormat);
		}
	}

	public void removeCommonReadFileFormat(TGFileFormat fileFormat){
		if( this.commonReadFileFormats.contains(fileFormat)){
			this.commonReadFileFormats.remove(fileFormat);
		}
	}

	public boolean isCommonReadFileFormat(TGFileFormat fileFormat){
		return this.commonReadFileFormats.contains(fileFormat);
	}

	public boolean isNativeFileFormat(TGFileFormat fileFormat){
		return this.nativeFileFormat.equals(fileFormat);
	}

	public boolean isNativeFileFormat(String formatCode){
		for (String supportedFormatCode : this.nativeFileFormat.getSupportedFormats()) {
			if (supportedFormatCode.equals(formatCode)) {
				return true;
			}
		}
		return false;
	}

	public boolean isNativeFileFormat(String[] supportedFormats) {
		for (String supportedFormat : supportedFormats) {
			boolean supported = false;
			for (String supportedNativeFormat : this.nativeFileFormat.getSupportedFormats()) {
				supported |= (supportedFormat.equals(supportedNativeFormat));
			}
			if (!supported) {
				return false;
			}
		}
		return true;
	}

	public void fireFileFormatAvailabilityEvent(){
		TGEventManager.getInstance(this.context).fireEvent(new TGFileFormatAvailabilityEvent());
	}

	public void addFileFormatAvailabilityListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGFileFormatAvailabilityEvent.EVENT_TYPE, listener);
	}

	public void removeFileFormatAvailabilityListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGFileFormatAvailabilityEvent.EVENT_TYPE, listener);
	}

	public static TGFileFormatManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGFileFormatManager.class.getName(), new TGSingletonFactory<TGFileFormatManager>() {
			public TGFileFormatManager createInstance(TGContext context) {
				return new TGFileFormatManager(context);
			}
		});
	}
}
