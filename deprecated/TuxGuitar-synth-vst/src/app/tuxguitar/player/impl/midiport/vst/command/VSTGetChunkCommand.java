package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTGetChunkCommand extends TGAbstractCommand<byte[]> {

	public static final Integer COMMAND_ID = 15;

	public VSTGetChunkCommand(TGConnection connection) {
		super(connection);
	}

	public byte[] process() throws IOException {
		this.writeInteger(COMMAND_ID);

		int length = this.readInteger();

		return (length > 0 ? this.readBytes(length) : null);
	}
}
