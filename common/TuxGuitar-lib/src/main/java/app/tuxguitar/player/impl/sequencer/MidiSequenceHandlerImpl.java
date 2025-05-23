package app.tuxguitar.player.impl.sequencer;

import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.song.models.TGTimeSignature;

public class MidiSequenceHandlerImpl extends MidiSequenceHandler{

	private MidiSequencerImpl seq;

	public MidiSequenceHandlerImpl(MidiSequencerImpl seq,int tracks) {
		super(tracks);
		this.seq = seq;
		this.seq.getMidiTrackController().init(getTracks());
	}

	public void addControlChange(long tick,int track,int channel, int controller, int value) {
		this.seq.addEvent(MidiEvent.controlChange(tick, track, channel, controller, value));
	}

	public void addNoteOff(long tick,int track,int channel, int note, int velocity, int voice, boolean bendMode) {
		this.seq.addEvent(MidiEvent.noteOff(tick, track, channel, note, velocity, voice, bendMode));
	}

	public void addNoteOn(long tick,int track,int channel, int note, int velocity, int voice, boolean bendMode) {
		this.seq.addEvent(MidiEvent.noteOn(tick, track, channel, note, velocity, voice, bendMode));
	}

	public void addPitchBend(long tick,int track,int channel, int value, int voice, boolean bendMode) {
		this.seq.addEvent(MidiEvent.pitchBend(tick, track, channel, value, voice, bendMode));
	}

	public void addProgramChange(long tick,int track,int channel, int instrument) {
		this.seq.addEvent(MidiEvent.programChange(tick, track, channel, instrument));
	}

	public void addTrackName(long tick, int track, String name) {
		this.seq.addEvent(MidiEvent.trackName(tick, track, name));
	}

	public void addTempoInUSQ(long tick,int track,int usq) {
		this.seq.addEvent(MidiEvent.tempoInUSQ(tick, usq));
	}

	public void addTimeSignature(long tick,int track,TGTimeSignature ts) {
		//not implemented
	}

	public void notifyFinish(){
		//not implemented
	}
}
