package app.tuxguitar.android.view.dialog.channel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import app.tuxguitar.android.R;
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import app.tuxguitar.android.view.util.TGSelectableItem;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiInstrument;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;

import java.util.ArrayList;
import java.util.List;

public class TGChannelEditDialog extends TGModalFragment {

	private TGEventListener eventListener;

	private ArrayAdapter<TGSelectableItem> instrumentPrograms;
	private ArrayAdapter<TGSelectableItem> percussionPrograms;

	public TGChannelEditDialog() {
		super(R.layout.view_channel_edit_dialog);
	}

	public TGChannel getChannel() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(false, false, R.string.channel_edit_dlg_title);
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.eventListener = new TGChannelEditEventListener(this);

		this.fillProgramAdapters();
		this.fillBanks();
		this.updateItems();
	}

	@Override
	public void onShowView() {
		this.appendListeners();
	}

	@Override
	public void onHideView() {
		this.removeListeners();
	}

	public void updateItems() {
		this.updateStates();

		this.fillPrograms();

		this.fillNameValue();
		this.fillBankValue();
		this.fillProgramValue();
		this.fillPercussionValue();
		this.fillVolumeValue();
		this.fillBalanceValue();
		this.fillReverbValue();
		this.fillChorusValue();
		this.fillPhaserValue();
		this.fillTremoloValue();
	}

	public void updateStates() {
		TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGChannel channel = this.getChannel();

		boolean percussionChannel = channel.isPercussionChannel();
		boolean anyPercussionChannel = songManager.isAnyPercussionChannel(song);
		boolean anyTrackConnectedToChannel = songManager.isAnyTrackConnectedToChannel(song, channel.getChannelId());

		this.setViewEnabled(R.id.channel_edit_dlg_percussion_value, (!anyTrackConnectedToChannel && (!anyPercussionChannel || percussionChannel)));
		this.setViewEnabled(R.id.channel_edit_dlg_bank_value, !percussionChannel);
	}

	public void appendListeners() {
		this.getView().findViewById(R.id.channel_edit_dlg_name_value).setOnFocusChangeListener(createNameFocusChangeListener());
		((Spinner) this.getView().findViewById(R.id.channel_edit_dlg_bank_value)).setOnItemSelectedListener(createBankSelectedListener());
		((Spinner) this.getView().findViewById(R.id.channel_edit_dlg_program_value)).setOnItemSelectedListener(createProgramSelectedListener());
		((CheckBox) this.getView().findViewById(R.id.channel_edit_dlg_percussion_value)).setOnCheckedChangeListener(createPercussionChangeListener());
		((SeekBar) this.getView().findViewById(R.id.channel_edit_dlg_volume_value)).setOnSeekBarChangeListener(createVolumeChangeListener());
		((SeekBar) this.getView().findViewById(R.id.channel_edit_dlg_balance_value)).setOnSeekBarChangeListener(createBalanceChangeListener());
		((SeekBar) this.getView().findViewById(R.id.channel_edit_dlg_reverb_value)).setOnSeekBarChangeListener(createReverbChangeListener());
		((SeekBar) this.getView().findViewById(R.id.channel_edit_dlg_chorus_value)).setOnSeekBarChangeListener(createChorusChangeListener());
		((SeekBar) this.getView().findViewById(R.id.channel_edit_dlg_phaser_value)).setOnSeekBarChangeListener(createPhaserChangeListener());
		((SeekBar) this.getView().findViewById(R.id.channel_edit_dlg_tremolo_value)).setOnSeekBarChangeListener(createTremoloChangeListener());

		TGEditorManager.getInstance(findContext()).addUpdateListener(this.eventListener);
	}

	public void removeListeners() {
		TGEditorManager.getInstance(findContext()).removeUpdateListener(this.eventListener);
	}

	public List<TGSelectableItem> createUnamedPrograms(){
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = 0; i < 128; i++) {
			Short value = Short.valueOf(Integer.valueOf(i).shortValue());
			selectableItems.add(new TGSelectableItem(value, getString(R.string.channel_edit_dlg_program_value, value)));
		}
		return selectableItems;
	}

	public List<TGSelectableItem> createInstrumentPrograms(){
		MidiInstrument[] instruments = MidiPlayer.getInstance(findContext()).getInstruments();
		if (instruments != null) {
			int count = instruments.length;
			if (count > 128) {
				count = 128;
			}

			List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
			for(int i = 0; i < count; i++) {
				selectableItems.add(new TGSelectableItem(Short.valueOf(Integer.valueOf(i).shortValue()), instruments[i].getName()));
			}
			return selectableItems;
		}
		return createUnamedPrograms();
	}

	public void fillProgramAdapters() {
		this.instrumentPrograms = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createInstrumentPrograms());
		this.instrumentPrograms.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		this.percussionPrograms = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createUnamedPrograms());
		this.percussionPrograms.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	public void fillPrograms() {
		updateSpinnerAdapter(R.id.channel_edit_dlg_program_value, (this.getChannel().isPercussionChannel() ? this.percussionPrograms : this.instrumentPrograms));
	}

	public void fillProgramValue() {
		updateSpinnerValue(R.id.channel_edit_dlg_program_value, Short.valueOf(this.getChannel().getProgram()));
	}

	public short findSelectedProgram() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.channel_edit_dlg_program_value);
		return ((Short) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).shortValue();
	}

	public List<TGSelectableItem> createBankValues(){
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = 0; i < 128; i++) {
			Short value = Short.valueOf(Integer.valueOf(i).shortValue());
			selectableItems.add(new TGSelectableItem(value, getString(R.string.channel_edit_dlg_bank_value, value)));
		}
		return selectableItems;
	}

	public void fillBanks() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createBankValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		updateSpinnerAdapter(R.id.channel_edit_dlg_bank_value, arrayAdapter);
	}

	public void fillBankValue() {
		updateSpinnerValue(R.id.channel_edit_dlg_bank_value, Short.valueOf(this.getChannel().getBank()));
	}

	public short findSelectedBank() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.channel_edit_dlg_bank_value);
		return ((Short) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).shortValue();
	}

	public void fillNameValue() {
		this.setTextFieldValue(R.id.channel_edit_dlg_name_value, this.getChannel().getName());
	}

	public String findNameValue() {
		return this.getTextFieldValue(R.id.channel_edit_dlg_name_value);
	}

	public void fillPercussionValue() {
		this.setCheckBoxValue(R.id.channel_edit_dlg_percussion_value, this.getChannel().isPercussionChannel());
	}

	public Boolean findPercussionValue() {
		return this.getCheckBoxValue(R.id.channel_edit_dlg_percussion_value);
	}

	public void fillVolumeValue() {
		this.setSeekBarValue(R.id.channel_edit_dlg_volume_value, Integer.valueOf(this.getChannel().getVolume()));
	}

	public void fillBalanceValue() {
		this.setSeekBarValue(R.id.channel_edit_dlg_balance_value, Integer.valueOf(this.getChannel().getBalance()));
	}

	public void fillReverbValue() {
		this.setSeekBarValue(R.id.channel_edit_dlg_reverb_value, Integer.valueOf(this.getChannel().getReverb()));
	}

	public void fillChorusValue() {
		this.setSeekBarValue(R.id.channel_edit_dlg_chorus_value, Integer.valueOf(this.getChannel().getChorus()));
	}

	public void fillPhaserValue() {
		this.setSeekBarValue(R.id.channel_edit_dlg_phaser_value, Integer.valueOf(this.getChannel().getPhaser()));
	}

	public void fillTremoloValue() {
		this.setSeekBarValue(R.id.channel_edit_dlg_tremolo_value, Integer.valueOf(this.getChannel().getTremolo()));
	}

	public void setViewEnabled(int id, boolean enabled) {
		this.getView().findViewById(id).setEnabled(enabled);
	}

	public void updateSpinnerValue(int id, Object value) {
		this.setSpinnerValue(id, new TGSelectableItem(value, null));
	}

	public void updateSpinnerAdapter(int id, ArrayAdapter<TGSelectableItem> adapter) {
		Spinner spinner = (Spinner) this.getView().findViewById(id);
		if(!isSameValue(adapter, spinner.getAdapter())) {
			spinner.setAdapter(adapter);
		}
	}

	public void setTextFieldValue(int id, String value) {
		if(!isSameValue(value, getTextFieldValue(id))) {
			((EditText) this.getView().findViewById(id)).setText(value);
		}
	}

	public String getTextFieldValue(int id) {
		return ((EditText) this.getView().findViewById(id)).getText().toString();
	}

	public void setCheckBoxValue(int id, Boolean value) {
		if(!isSameValue(value, getCheckBoxValue(id))) {
			((CheckBox) this.getView().findViewById(id)).setChecked(value);
		}
	}

	public Boolean getCheckBoxValue(int id) {
		return ((CheckBox) this.getView().findViewById(id)).isChecked();
	}

	public TGSelectableItem getSpinnerValue(int id) {
		return (TGSelectableItem) ((Spinner) this.getView().findViewById(id)).getSelectedItem();
	}

	@SuppressWarnings("unchecked")
	public void setSpinnerValue(int id, TGSelectableItem selectedItem) {
		if(!isSameValue(selectedItem, getSpinnerValue(id))) {
			Spinner spinner = (Spinner) this.getView().findViewById(id);
			spinner.setSelection(((ArrayAdapter<TGSelectableItem>) spinner.getAdapter()).getPosition(selectedItem), false);
		}
	}

	public Integer getSeekBarValue(int id) {
		return ((SeekBar) this.getView().findViewById(id)).getProgress();
	}

	public void setSeekBarValue(int id, Integer value) {
		if(!isSameValue(value, getSeekBarValue(id))) {
			SeekBar seekBar = ((SeekBar) this.getView().findViewById(id));
			seekBar.setProgress(value);
		}
	}

	public boolean isSameValue(Object v1, Object v2) {
		if( v1 == v2 ) {
			return true;
		}
		return ( v1 != null && v2 != null && v1.equals( v2 ) );
	}

	public boolean isChannelNameUpdated() {
		String value = this.findNameValue();

		return (value != null && !value.equals(this.getChannel().getName()));
	}

	public TGActionProcessor createUpdateChannelAction() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGUpdateChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.getChannel());
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_NAME, this.findNameValue());
		return tgActionProcessor;
	}

	public TGActionProcessor createUpdateAttributteAction(String attributeName, Object attributeValue) {
		TGActionProcessor tgActionProcessor = this.createUpdateChannelAction();
		tgActionProcessor.setAttribute(attributeName, attributeValue);
		return tgActionProcessor;
	}

	public TGActionProcessor createUpdateBankAction() {
		TGActionProcessor tgActionProcessor = this.createUpdateChannelAction();
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK, this.findSelectedBank());
		return tgActionProcessor;
	}

	public TGActionProcessor createUpdateProgramAction() {
		TGActionProcessor tgActionProcessor = this.createUpdateChannelAction();
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, this.findSelectedProgram());
		return tgActionProcessor;
	}

	public TGActionProcessor createUpdatePercussionAction() {
		boolean percussionChannel = this.findPercussionValue();
		short bank = (percussionChannel ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
		short program = (percussionChannel ? TGChannel.DEFAULT_PERCUSSION_PROGRAM : TGChannel.DEFAULT_PROGRAM);

		TGActionProcessor tgActionProcessor = this.createUpdateChannelAction();
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK, Short.valueOf(bank));
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, Short.valueOf(program));
		return tgActionProcessor;
	}

	public View.OnFocusChangeListener createNameFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus && isChannelNameUpdated()) {
					createUpdateChannelAction().process();
				}
			}
		};
	}

	public OnItemSelectedListener createProgramSelectedListener() {
		return new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	createUpdateProgramAction().process();
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	createUpdateProgramAction().process();
		    }
		};
	}

	public OnItemSelectedListener createBankSelectedListener() {
		return new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if(!getChannel().isPercussionChannel()) {
					createUpdateBankAction().process();
				}
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
				if(!getChannel().isPercussionChannel()) {
					createUpdateBankAction().process();
				}
		    }
		};
	}

	public OnCheckedChangeListener createPercussionChangeListener() {
		return new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				createUpdatePercussionAction().process();
			}
		};
	}

	private OnSeekBarChangeListener createVolumeChangeListener() {
		return createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_VOLUME);
	}

	private OnSeekBarChangeListener createBalanceChangeListener() {
		return createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_BALANCE);
	}

	private OnSeekBarChangeListener createReverbChangeListener() {
		return createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_REVERB);
	}

	private OnSeekBarChangeListener createChorusChangeListener() {
		return createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_CHORUS);
	}

	private OnSeekBarChangeListener createPhaserChangeListener() {
		return createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_PHASER);
	}

	private OnSeekBarChangeListener createTremoloChangeListener() {
		return createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_TREMOLO);
	}

	private OnSeekBarChangeListener createShortLevelChangeListener(final String attribute) {
		return new OnSeekBarChangeListener() {

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// nothing to do
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// nothing to do
			}

			@Override
			public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
				if( progress >= 0 && progress <= 127 ) {
					createUpdateAttributteAction(attribute, Short.valueOf(Integer.valueOf(progress).shortValue())).process();
				}
			}
		};
	}
}
