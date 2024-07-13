package org.herac.tuxguitar.io.base;

public class TGFileFormat {

	private String name;
	private String mimeType;
	private String[] supportedFormats;
	// how to recognize format
	private boolean recognizeFormatByContent;
	private boolean recognizeFormatByMimeType;
	private boolean recognizeFormatByFileExtension;
	
	public TGFileFormat(String name, String mimeType, String[] supportedFormats, 
			boolean recognizeByContent, boolean recognizeByMimeType, boolean recognizeByFileExtension) {
		this.name = name;
		this.mimeType = mimeType;
		this.supportedFormats = supportedFormats;
		this.recognizeFormatByContent = recognizeByContent;
		this.recognizeFormatByMimeType = recognizeByMimeType;
		this.recognizeFormatByFileExtension = recognizeByFileExtension;
	}
	
	public TGFileFormat(String name, String mimeType, String[] supportedFormats) {
		this(name, mimeType, supportedFormats, true, true, true);
	}

	
	public String getName() {
		return this.name;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
	
	public String[] getSupportedFormats() {
		return this.supportedFormats;
	}
	
	public boolean isSupportedMimeType(String mimeType) {
		if( mimeType != null ) {
			return (mimeType.toLowerCase().equals(this.mimeType.toLowerCase()));
		}
		return false;
	}
	
	public boolean isSupportedCode(String formatCode) {
		if( formatCode != null ) {
			for(int i = 0 ; i < this.supportedFormats.length ; i ++) {
				if( formatCode.toLowerCase().equals(this.supportedFormats[i].toLowerCase()) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setRecognizeOptions(boolean byContent, boolean byMimeType, boolean byFileExtension) {
		this.recognizeFormatByContent = byContent;
		this.recognizeFormatByMimeType = byMimeType;
		this.recognizeFormatByFileExtension = byFileExtension;
	}
	
	public boolean canRecognizeByContent() {
		return this.recognizeFormatByContent;
	}
	
	public boolean canRecognizeByMimeType() {
		return this.recognizeFormatByMimeType;
	}
	
	public boolean canRecognizeByFileExtension() {
		return this.recognizeFormatByFileExtension;
	}
	
	public boolean equals(Object obj) {
		if( obj instanceof TGFileFormat ) {
			return (this.getName() != null && this.getName().equals(((TGFileFormat) obj).getName()));
		}
		return super.equals(obj);
	}
}
