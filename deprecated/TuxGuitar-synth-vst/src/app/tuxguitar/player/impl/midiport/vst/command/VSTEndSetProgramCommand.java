package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTEndSetProgramCommand extends TGAbstractCommand<Void> {

	public static final Integer COMMAND_ID = 18;

	public VSTEndSetProgramCommand(TGConnection connection) {
		super(connection);
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);

		return null;
	}
}
