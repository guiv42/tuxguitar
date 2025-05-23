package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.clef.TGClefDialogController;
import app.tuxguitar.android.view.dialog.info.TGSongInfoDialogController;
import app.tuxguitar.android.view.dialog.keySignature.TGKeySignatureDialogController;
import app.tuxguitar.android.view.dialog.repeat.TGRepeatAlternativeDialogController;
import app.tuxguitar.android.view.dialog.repeat.TGRepeatCloseDialogController;
import app.tuxguitar.android.view.dialog.tempo.TGTempoDialogController;
import app.tuxguitar.android.view.dialog.timeSignature.TGTimeSignatureDialogController;
import app.tuxguitar.android.view.dialog.tripletFeel.TGTripletFeelDialogController;
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import app.tuxguitar.player.base.MidiPlayer;

public class TGCompositionMenu extends TGMenuBase {

	public TGCompositionMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_composition, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		boolean running = MidiPlayer.getInstance(this.findContext()).isRunning();

		this.initializeItem(menu, R.id.action_change_tempo, new TGTempoDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_clef, new TGClefDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_key_signature, new TGKeySignatureDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_time_signature, new TGTimeSignatureDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_triplet_feel, new TGTripletFeelDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_properties, new TGSongInfoDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_repeat_alternative, new TGRepeatAlternativeDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_repeat_close, new TGRepeatCloseDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_repeat_open, this.createActionProcessor(TGRepeatOpenAction.NAME), !running);
	}
}
