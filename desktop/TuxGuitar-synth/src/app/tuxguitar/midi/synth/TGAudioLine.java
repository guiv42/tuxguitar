package app.tuxguitar.midi.synth;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import app.tuxguitar.util.TGException;

public class TGAudioLine {

	public static final AudioFormat DEFAULT_AUDIO_FORMAT = new AudioFormat(TGSynthSettings.getDefaultAudioSampleRate(), 16, TGAudioBuffer.CHANNELS, true, TGAudioBuffer.BIGENDIAN);

	private SourceDataLine line;

	public TGAudioLine(TGSynthesizer synthesizer) {
		try {
			AudioFormat audioFormat = new AudioFormat(synthesizer.getSettings().getAudioSampleRate(), 16, TGAudioBuffer.CHANNELS, true, TGAudioBuffer.BIGENDIAN);
			this.line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, audioFormat));
			this.line.open(audioFormat, (TGAudioBuffer.CHANNELS * TGAudioBuffer.BUFFER_SIZE) * Math.max(synthesizer.getSettings().getAudioBufferSize(), 1));
			this.line.start();
		} catch (Throwable e) {
			throw new TGException(e);
		}
	}

	public void write(TGAudioBuffer buffer) {
		this.line.write(buffer.getBuffer(), 0, buffer.getLength());
	}
}
