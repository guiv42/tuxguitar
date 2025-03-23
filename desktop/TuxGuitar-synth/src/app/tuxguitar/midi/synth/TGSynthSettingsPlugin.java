package app.tuxguitar.midi.synth;

import app.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGSynthSettingsPlugin extends TGPluginSettingsAdapter{

	@Override
	public String getModuleId() {
		return TGSynthPlugin.MODULE_ID;
	}

	@Override
	protected TGPluginSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new TGSynthSettingsHandler(context);
	}

}
