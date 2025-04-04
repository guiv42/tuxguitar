package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTBeginSetProgramCommand extends TGAbstractCommand<Void> {

	public static final Integer COMMAND_ID = 17;

	public VSTBeginSetProgramCommand(TGConnection connection) {
		super(connection);
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);

		return null;
	}
}
