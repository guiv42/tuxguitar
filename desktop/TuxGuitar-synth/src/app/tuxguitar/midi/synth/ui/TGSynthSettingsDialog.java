package app.tuxguitar.midi.synth.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.midi.synth.TGSynthSettings;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGSynthSettingsDialog {

	private TGContext context;
	private TGSynthSettings tgSynthSettings;

	public TGSynthSettingsDialog(TGContext context) {
		this.context = context;
		this.tgSynthSettings = new TGSynthSettings(this.context);
	}

	public void configure(UIWindow parent) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("synth.settings-dialog"));

		// --- SETTINGS ----
		UITableLayout settingsLayout = new UITableLayout();
		UIPanel settings = uiFactory.createPanel(dialog, false);
		settings.setLayout(settingsLayout);
		dialogLayout.set(settings, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel label = uiFactory.createLabel(settings);
		label.setText(TuxGuitar.getProperty("synth.sample-rate"));
		settingsLayout.set(label, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);

		UISpinner spinner = uiFactory.createSpinner(settings);
		spinner.setMinimum(1000);
		spinner.setMaximum(96000);
		spinner.setValue(this.tgSynthSettings.getAudioSampleRate());
		settingsLayout.set(spinner, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);

		UILabel labelUnit = uiFactory.createLabel(settings);
		labelUnit.setText("Hz");
		settingsLayout.set(labelUnit, 1, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);

		// --- BUTTONS ----
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		UIButton buttonDefault = uiFactory.createButton(buttons);
		buttonDefault.setText(TuxGuitar.getProperty("defaults"));
		buttonDefault.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				spinner.setValue(tgSynthSettings.getDefaultAudioSampleRate());
			}
		});
		buttonsLayout.set(buttonDefault, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				tgSynthSettings.saveAudioSampleRate(spinner.getValue());
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

}
