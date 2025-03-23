package app.tuxguitar.midi.synth;

import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.midi.synth.ui.TGSynthSettingsDialog;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGSynthSettingsHandler implements TGPluginSettingsHandler {

	private TGContext context;

	public TGSynthSettingsHandler(TGContext context) {
		this.context = context;
	}

	@Override
	public void openSettingsDialog(UIWindow parent) {
		new TGSynthSettingsDialog(this.context).configure(parent);
	}

}
