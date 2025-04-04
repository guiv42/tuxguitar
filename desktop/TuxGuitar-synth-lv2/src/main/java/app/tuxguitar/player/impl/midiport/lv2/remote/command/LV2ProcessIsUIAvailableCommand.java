package app.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class LV2ProcessIsUIAvailableCommand extends TGAbstractCommand<Boolean> {

	public static final Integer COMMAND_ID = 11;

	public LV2ProcessIsUIAvailableCommand(TGConnection connection) {
		super(connection);
	}

	public Boolean process() throws IOException {
		this.writeInteger(COMMAND_ID);

		return this.readBoolean();
	}
}
