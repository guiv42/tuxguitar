package app.tuxguitar.midi.synth;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;

public class TGSynthSettings {

	private static final Integer DEFAULT_AUDIO_BUFFER_SIZE = 10;

	private static final String AUDIO_BUFFER_SIZE = "synth.audio.buffer.size";
	private static final String MIDI_PROGRAM_PREFIX = "synth.program";
	private static final String AUDIO_SAMPLE_RATE = "synth.audio.sample.rate";

	private static final int DEFAULT_AUDIO_SAMPLE_RATE = 44100;
	private static final Integer REMOTE_HOST_DEFAULT_SERVER_PORT_VALUE = 60982;
	private static final String REMOTE_HOST_DEFAULT_SERVER_PORT = "synth.host.default.server.port";

	private TGContext context;
	private TGConfigManager config;

	public TGSynthSettings(TGContext context){
		this.context = context;
	}

	public TGConfigManager getConfig(){
		if( this.config == null ){
			this.config = new TGConfigManager(this.context, "tuxguitar-synth");
		}
		return this.config;
	}

	public void save(){
		this.getConfig().save();
	}

	public void loadPrograms(TGSynthModel synthesizer) {
		for( int b = 0; b < TGSynthModel.BANKS_LENGTH ; b ++ ){
			for( int p = 0 ; p < TGSynthModel.PROGRAMS_LENGTH ; p ++ ){
				loadProgram(synthesizer, b, p);
			}
		}
	}

	public void loadProgram(TGSynthModel synthesizer, int bank, int program) {
		String prefix = (MIDI_PROGRAM_PREFIX + "." + bank + "." + program);

		TGProgram tgProgram = TGProgramPropertiesUtil.getProgram(this.getConfig().getProperties(), prefix);
		if( tgProgram != null ) {
			synthesizer.getProgram(bank, program).copyFrom(tgProgram);
		}
	}

	public Integer getAudioBufferSize() {
		return this.getConfig().getIntegerValue(AUDIO_BUFFER_SIZE, DEFAULT_AUDIO_BUFFER_SIZE);
	}

	public int getAudioSampleRate() {
		return this.getConfig().getIntegerValue(AUDIO_SAMPLE_RATE, DEFAULT_AUDIO_SAMPLE_RATE);
	}

	public static int getDefaultAudioSampleRate() {
		return DEFAULT_AUDIO_SAMPLE_RATE;
	}

	public void saveAudioSampleRate(int sampleRate) {
		this.getConfig().setValue(AUDIO_SAMPLE_RATE, sampleRate);
		this.getConfig().save();
	}

	public Integer getRemoteHostServerPort() {
		return this.getConfig().getIntegerValue(REMOTE_HOST_DEFAULT_SERVER_PORT, REMOTE_HOST_DEFAULT_SERVER_PORT_VALUE);
	}
}
