package app.tuxguitar.player.impl.midiport.fluidsynth;

import java.io.File;

import app.tuxguitar.gm.port.GMOutputPort;
import app.tuxguitar.gm.port.GMReceiver;

public class MidiOutputPortImpl extends GMOutputPort{

	private String key;
	private String name;
	private MidiSynth synth;
	private MidiReceiverImpl receiver;
	private String soundFont;

	public MidiOutputPortImpl(MidiSynth synth, File soundfont){
		this.key = getUniqueKey(soundfont);
		this.name = getUniqueName(soundfont);
		this.soundFont = soundfont.getAbsolutePath();
		this.receiver = new MidiReceiverImpl(synth);
		this.synth = synth;
	}

	public void open(){
		if(!this.synth.isConnected(this)){
			this.synth.connect(this);
		}
	}

	public void close(){
		if(this.synth.isConnected(this)){
			this.synth.disconnect(this);
		}
	}

	public GMReceiver getReceiver(){
		this.open();
		return this.receiver;
	}

	public void check(){
		// Not implemented
	}

	public String getSoundFont() {
		return this.soundFont;
	}

	public String getKey(){
		return this.key;
	}

	public String getName(){
		return this.name;
	}

	public String getUniqueKey(File soundfont){
		return ("tuxguitar-fluidsynth_" + soundfont.getAbsolutePath());
	}

	private String getUniqueName(File soundfont){
		String name = soundfont.getName();
		int extensionIndex = name.lastIndexOf(".");
		if( extensionIndex > 0 ){
			name = name.substring( 0, extensionIndex );
		}
		return ("TG FluidSynth " + "[" + name + "]");
	}
}
