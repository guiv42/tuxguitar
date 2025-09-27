package app.tuxguitar.io.gtp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.gm.GMChannelRoute;
import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.gm.GMChannelRouterConfigurator;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;

public abstract class GTPOutputStream extends GTPFileFormat implements TGSongWriter{

	private GMChannelRouter channelRouter;
	private OutputStream outputStream;
	private int[] keySignatures;

	public GTPOutputStream(GTPSettings settings){
		super(settings);
	}

	public abstract void writeSong(TGSong song) throws TGFileFormatException;

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try {
			this.outputStream = handle.getOutputStream();
			this.init(handle.getFactory());
			this.writeSong(handle.getSong());
		} catch (TGFileFormatException tgFormatException) {
			throw tgFormatException;
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}

	protected void skipBytes(int count) throws IOException {
		for(int i = 0;i < count;i++){
			this.outputStream.write(0);
		}
	}

	protected void writeByte(byte v) throws IOException {
		this.outputStream.write(v);
	}

	protected void writeUnsignedByte(int v) throws IOException {
		this.outputStream.write(v);
	}

	protected void writeBytes(byte[] v) throws IOException {
		this.outputStream.write(v);
	}

	protected void writeBoolean(boolean v) throws IOException {
		this.outputStream.write(v ? 1 : 0);
	}

	protected void writeInt(int v) throws IOException {
		byte[] bytes = { (byte)(v & 0x00FF),(byte)((v >> 8) & 0x000000FF),(byte) ((v >> 16) & 0x000000FF),(byte)((v >> 24) & 0x000000FF) };
		this.outputStream.write(bytes);
	}

	protected void writeString(byte[] bytes, int maximumLength) throws IOException {
		int length = (maximumLength == 0 || maximumLength > bytes.length ? bytes.length : maximumLength );
		for(int i = 0 ; i < length; i ++){
			this.outputStream.write( bytes[ i ] );
		}
	}

	protected void writeStringInteger(String string, String charset) throws IOException {
		byte[] bytes = string.getBytes(charset);
		this.writeInt( bytes.length );
		this.writeString( bytes , 0 );
	}

	protected void writeStringInteger(String string) throws IOException {
		this.writeStringInteger(string, getSettings().getCharset());
	}

	protected void writeStringByte(String string, int size, String charset) throws IOException {
		byte[] bytes = string.getBytes(charset);
		this.writeByte( (byte)( size == 0 || size > bytes.length ? bytes.length : size ));
		this.writeString( bytes , size );
		this.skipBytes( size - bytes.length );
	}

	protected void writeStringByte(String string, int size) throws IOException {
		this.writeStringByte(string, size, getSettings().getCharset());
	}

	protected void writeStringByteSizeOfInteger(String string, String charset) throws IOException {
		byte[] bytes = string.getBytes(charset);
		this.writeInt( (bytes.length + 1) );
		this.writeStringByte(string, bytes.length, charset);
	}

	protected void writeStringByteSizeOfInteger(String string) throws IOException {
		writeStringByteSizeOfInteger(string, getSettings().getCharset());
	}

	protected void close() throws IOException{
		this.outputStream.flush();
		this.outputStream.close();
	}

	protected List<TGString> createWritableStrings(TGTrack track) {
		int minimum = 4;
		int maximum = 7;
		int count = track.stringCount();
		if( count >= minimum && count <= maximum ) {
			return track.getStrings();
		}
		int writableCount = count;
		if( writableCount < minimum ) {
			writableCount = minimum;
		}
		if( writableCount > maximum ) {
			writableCount = maximum;
		}
		List<TGString> strings = track.getStrings();
		List<TGString> writableStrings = createDefaultStrings(track, writableCount);
		for(TGString string : strings) {
			if( string.getNumber() >= minimum && string.getNumber() <= maximum ) {
				for(TGString writableString : writableStrings) {
					if( writableString.getNumber() == string.getNumber() ) {
						writableString.setValue(string.getValue());
					}
				}
			}
		}
		return writableStrings;
	}

	protected List<TGString> createDefaultStrings(TGTrack track, int count) {
		if( this.isPercussionChannel(track.getSong(), track.getChannelId()) ) {
			return new TGSongManager().createPercussionStrings(count);
		}
		return new TGSongManager().createDefaultInstrumentStrings(count);
	}

	protected void configureChannelRouter( TGSong song ){
		this.channelRouter = new GMChannelRouter();

		GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(this.channelRouter);
		gmChannelRouterConfigurator.configureRouter(song.getChannels());
	}

	protected GMChannelRoute getChannelRoute(int channelId){
		GMChannelRoute gmChannelRoute = this.channelRouter.getRoute(channelId);
		if( gmChannelRoute == null || gmChannelRoute.getChannel1() < 0  || gmChannelRoute.getChannel2() < 0 ){
			Integer defaultChannel = (gmChannelRoute != null && gmChannelRoute.getChannel1() >= 0 ? gmChannelRoute.getChannel1() : 15);

			gmChannelRoute = new GMChannelRoute(channelId);
			gmChannelRoute.setChannel1(defaultChannel);
			gmChannelRoute.setChannel2(defaultChannel);
		}

		return gmChannelRoute;
	}

	protected boolean isPercussionChannel( TGSong song, int channelId ){
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ){
			TGChannel channel = it.next();
			if( channel.getChannelId() == channelId ){
				return channel.isPercussionChannel();
			}
		}
		return false;
	}

	protected void makeKeySignatures(TGSong song) {
		this.keySignatures = new int[song.countMeasureHeaders()];
		if (song.countTracks() > 0) {
			TGTrack track = song.getTrack(0);
			for (int i = 0; i < track.countMeasures(); i++) {
				this.keySignatures[i] = track.getMeasure(i).getKeySignature();
			}
		}
	}

	protected byte translateKeySignature(int index) {
		int keySignature = this.keySignatures[index];
		if (keySignature > 7) {
			return (byte)(7 - keySignature);
		}
		return (byte)keySignature;
	}

	protected boolean isNewKeySignature(int index) {
		if (index == 0) {
			return true;
		}
		return this.keySignatures[index] != this.keySignatures[index - 1];
	}
}
