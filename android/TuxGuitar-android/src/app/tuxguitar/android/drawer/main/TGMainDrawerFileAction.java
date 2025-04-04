package app.tuxguitar.android.drawer.main;

import app.tuxguitar.android.action.TGActionProcessorListener;

public class TGMainDrawerFileAction {

	private int label;
	private TGActionProcessorListener processor;

	public TGMainDrawerFileAction(int label, TGActionProcessorListener processor) {
		this.label = label;
		this.processor = processor;
	}

	public int getLabel() {
		return label;
	}

	public TGActionProcessorListener getProcessor() {
		return processor;
	}
}
