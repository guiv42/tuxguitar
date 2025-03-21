package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTSetSampleRateCommand extends TGAbstractCommand<Void> {

	public static final Integer COMMAND_ID = 10;

	private Float value;

	public VSTSetSampleRateCommand(TGConnection connection, Float value) {
		super(connection);

		this.value = value;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeFloat(this.value);

		return null;
	}
}
