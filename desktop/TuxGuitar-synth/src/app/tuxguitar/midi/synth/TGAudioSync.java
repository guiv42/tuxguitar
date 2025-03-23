package app.tuxguitar.midi.synth;

import app.tuxguitar.util.TGException;

public class TGAudioSync {

	private long duration;
	private long lastSync;
	private long lastDelay;
	private Object lock;

	public TGAudioSync(int sampleRate) {
		this.lock = new Object();
		this.duration = Math.round((TGAudioBuffer.BUFFER_SIZE / 2) * 1000000000.00 / sampleRate);
	}

	public void sync() {
		long currentTime = System.nanoTime();
		long elapsedTime = (this.lastSync > 0 ? currentTime - this.lastSync - this.lastDelay : 0);
		long delayTime = (this.duration - elapsedTime);

		if( delayTime > 0 ) {
			synchronized (this.lock) {
				try {
					this.lock.wait((long) (delayTime / 1000000), (int) (delayTime % 1000000));
				} catch (InterruptedException e) {
					throw new TGException(e);
				}
			}
		}

		this.lastSync = currentTime;
		this.lastDelay = delayTime;
	}
}
