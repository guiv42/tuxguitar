package app.tuxguitar.midi.synth.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.util.TGProcess;
import app.tuxguitar.editor.util.TGSyncProcess;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGProgramPropertiesUtil;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthChannelProperties;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.midi.synth.TGSynthesizer;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UICheckTableSelectionEvent;
import app.tuxguitar.ui.event.UICheckTableSelectionListener;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckTable;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIReadOnlyTextField;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UITableItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

public class TGSynthDialog implements TGChannelSettingsDialog, TGEventListener {

	private TGContext context;
	private TGSynthesizer synthesizer;
	private TGChannel channel;
	private TGSong song;

	private UIWindow dialog;
	private UIButton buttonReceiverAdd;
	private UIButton buttonReceiverEdit;
	private UIButton buttonOutputAdd;
	private UIButton buttonOutputEdit;
	private UIButton buttonOutputDelete;
	private UIButton buttonOutputMoveUp;
	private UIButton buttonOutputMoveDown;
	private UIReadOnlyTextField receiver;
	private UICheckTable<TGProgramElement> outputs;

	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateProcess;

	private Map<TGAudioProcessor, TGAudioProcessorUI> processorsUI;

	public TGSynthDialog(TGContext context, TGSynthesizer synthesizer, TGChannel channel, TGSong song){
		this.context = context;
		this.synthesizer = synthesizer;
		this.channel = channel;
		this.song = song;
		this.processorsUI = new HashMap<TGAudioProcessor, TGAudioProcessorUI>();
		this.createSyncProcesses();
	}

	public void open(final UIWindow parent) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();

		this.dialog = uiFactory.createWindow(parent, false, false);
		this.dialog.setLayout(dialogLayout);

		// ----------------------------------------------------------------------
		UITableLayout receiverLayout = new UITableLayout();
		UIPanel receiverPanel = uiFactory.createPanel(this.dialog, false);
		receiverPanel.setLayout(receiverLayout);
		dialogLayout.set(receiverPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.receiver = uiFactory.createReadOnlyTextField(receiverPanel);
		receiverLayout.set(this.receiver, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		// ----------------------------------------------------------------------
		this.buttonReceiverAdd = uiFactory.createButton(receiverPanel);
		this.buttonReceiverAdd.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onAddReceiver();
			}
		});
		receiverLayout.set(this.buttonReceiverAdd, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		this.buttonReceiverEdit = uiFactory.createButton(receiverPanel);
		this.buttonReceiverEdit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onEditReceiver();
			}
		});
		receiverLayout.set(this.buttonReceiverEdit, 1, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		// ----------------------------------------------------------------------

		UITableLayout outputsLayout = new UITableLayout();
		UIPanel compositeTable = uiFactory.createPanel(this.dialog, false);
		compositeTable.setLayout(outputsLayout);
		dialogLayout.set(compositeTable, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.outputs = uiFactory.createCheckTable(compositeTable, true);
		this.outputs.setColumns(1);
		this.outputs.addCheckSelectionListener(new UICheckTableSelectionListener<TGProgramElement>() {
			public void onSelect(UICheckTableSelectionEvent<TGProgramElement> event) {
				if( event.getSelectedItem() != null ) {
					onEnableOuput(event.getSelectedItem().getValue(), TGSynthDialog.this.outputs.isCheckedItem(event.getSelectedItem()));
				}
			}
		});
		this.outputs.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
				onEditElement(TGSynthDialog.this.outputs.getSelectedValue());
			}
		});
		outputsLayout.set(this.outputs, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		outputsLayout.set(this.outputs, UITableLayout.PACKED_WIDTH, 250f);
		outputsLayout.set(this.outputs, UITableLayout.PACKED_HEIGHT, 200f);

		// ------------------BUTTONS--------------------------
		UITableLayout outputsButtonsLayout = new UITableLayout(0f);
		UIPanel compositeButtons = uiFactory.createPanel(compositeTable, false);
		compositeButtons.setLayout(outputsButtonsLayout);
		outputsLayout.set(compositeButtons, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

		this.buttonOutputAdd = uiFactory.createButton(compositeButtons);
		this.buttonOutputAdd.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onAddOutput();
			}
		});

		this.buttonOutputEdit = uiFactory.createButton(compositeButtons);
		this.buttonOutputEdit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onEditElement(TGSynthDialog.this.outputs.getSelectedValue());
			}
		});

		this.buttonOutputDelete = uiFactory.createButton(compositeButtons);
		this.buttonOutputDelete.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onRemoveOutput(TGSynthDialog.this.outputs.getSelectedValue());
			}
		});

		this.buttonOutputMoveUp = uiFactory.createButton(compositeButtons);
		this.buttonOutputMoveUp.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onMoveOutputUp(TGSynthDialog.this.outputs.getSelectedValue());
			}
		});

		this.buttonOutputMoveDown = uiFactory.createButton(compositeButtons);
		this.buttonOutputMoveDown.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onMoveOutputDown(TGSynthDialog.this.outputs.getSelectedValue());
			}
		});

		outputsButtonsLayout.set(this.buttonOutputAdd, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, false, false);
		outputsButtonsLayout.set(this.buttonOutputDelete, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, false, false);
		outputsButtonsLayout.set(this.buttonOutputMoveUp, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, false, false);
		outputsButtonsLayout.set(this.buttonOutputMoveDown, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, false, false);
		outputsButtonsLayout.set(this.buttonOutputEdit, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, false, true);

		this.loadIcons();
		this.loadProperties();
		this.updateItems();

		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
			}
		});
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void close() {
		if( this.isOpen()) {
			this.dialog.dispose();
		}
	}

	public boolean isOpen(){
		return (this.dialog != null && !this.dialog.isDisposed());
	}

	public void openReceiverSelectionDialog() {
		final List<String> supportedTypes = TGSynthManager.getInstance(this.context).getAllSupportedMidiTypes();

		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();

		final UIWindow dialog = uiFactory.createWindow(this.dialog, false, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("synth-host.ui.midi.processor.dialog.title"));

		// ----------------------------------------------------------------------
		UITableLayout typeGroupLayout = new UITableLayout();
		UILegendPanel typeGroup = uiFactory.createLegendPanel(dialog);
		typeGroup.setLayout(typeGroupLayout);
		typeGroup.setText(TuxGuitar.getProperty("synth-host.ui.midi.processor.tip"));
		dialogLayout.set(typeGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel typeLabel = uiFactory.createLabel(typeGroup);
		typeLabel.setText(TuxGuitar.getProperty("synth-host.ui.midi.processor.type") + ":");
		typeGroupLayout.set(typeLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, true);

		final UIDropDownSelect<String> typeCombo = uiFactory.createDropDownSelect(typeGroup);
		typeGroupLayout.set(typeCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		for(String supportedType : supportedTypes){
			typeCombo.addItem(new UISelectItem<String>(supportedType, supportedType));
		}
		if( supportedTypes.size() > 0 ){
			typeCombo.setSelectedValue(supportedTypes.get(0));
		}

		//------------------BUTTONS----------------------------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onReceiverSelected(typeCombo.getSelectedValue());
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		// ----------------------------------------------------------------------

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void openOutputSelectionDialog() {
		final List<String> supportedTypes = TGSynthManager.getInstance(this.context).getAllSupportedAudioTypes();

		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();

		final UIWindow dialog = uiFactory.createWindow(this.dialog, true, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("synth-host.ui.audio.processor.dialog.title"));
		dialog.setImage(TGIconManager.getInstance(this.context).getAppIcon());

		// ----------------------------------------------------------------------
		UITableLayout typeGroupLayout = new UITableLayout();
		UILegendPanel typeGroup = uiFactory.createLegendPanel(dialog);
		typeGroup.setLayout(typeGroupLayout);
		typeGroup.setText(TuxGuitar.getProperty("synth-host.ui.audio.processor.tip"));
		dialogLayout.set(typeGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel typeLabel = uiFactory.createLabel(typeGroup);
		typeLabel.setText(TuxGuitar.getProperty("synth-host.ui.audio.processor.type") + ":");
		typeGroupLayout.set(typeLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, true);

		final UIDropDownSelect<String> typeCombo = uiFactory.createDropDownSelect(typeGroup);
		typeGroupLayout.set(typeCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		for(String supportedType : supportedTypes){
			typeCombo.addItem(new UISelectItem<String>(supportedType, supportedType));
		}
		if( supportedTypes.size() > 0 ){
			typeCombo.setSelectedValue(supportedTypes.get(0));
		}

		//------------------BUTTONS----------------------------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onOutputSelected(typeCombo.getSelectedValue());
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		// ----------------------------------------------------------------------

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void openProcessorUI(TGProgramElement element) {
		TGAudioProcessorUI ui = this.getProcessorUI(element);
		if( ui != null ) {
			if( ui.isOpen()) {
				ui.focus();
			} else {
				ui.open(this.dialog);
			}
		}
	}

	public void addListeners(){
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}

	public void removeListeners(){
		TuxGuitar.getInstance().getSkinManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}

	public void onAddReceiver() {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				openReceiverSelectionDialog();
			}
		});
	}

	public void onAddOutput() {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				openOutputSelectionDialog();
			}
		});
	}

	public void onEditElement(final TGProgramElement element) {
		if( element != null ) {
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					openProcessorUI(element);
				}
			});
		}
	}

	public void onRemoveOutput(TGProgramElement output) {
		if( output != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {
				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				program.removeOutput(output);

				this.onProgramUpdated(program);
			}
		}
	}

	public void onMoveOutputUp(TGProgramElement output) {
		if( output != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {
				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				program.moveOutputUp(output);

				this.onProgramUpdated(program);
			}
		}
	}

	public void onMoveOutputDown(TGProgramElement output) {
		if( output != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {
				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				program.moveOutputDown(output);

				this.onProgramUpdated(program);
			}
		}
	}

	public void onOutputSelected(String type) {
		if( type != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {
				TGProgramElement element = new TGProgramElement();
				element.setId("custom-" + System.currentTimeMillis());
				element.setType(type);
				element.setEnabled(true);

				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				program.addOutput(element);

				this.onProgramUpdated(program);
			}
		}
	}

	public void onEnableOuput(final TGProgramElement output, boolean enabled) {
		if( output != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {
				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				for(int i = 0; i < program.countOutputs(); i ++) {
					TGProgramElement current = program.getOutput(i);
					if( current.equals(output) ) {
						current.setEnabled(enabled);
					}
				}
				this.onProgramUpdated(program);
			}
		}
	}

	public void onReceiverSelected(String type) {
		if( type != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {
				TGProgramElement element = null;
				if( type != null ) {
					element = new TGProgramElement();
					element.setId("custom-" + System.currentTimeMillis());
					element.setType(type);
					element.setEnabled(true);
				}

				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				program.setReceiver(element);

				this.onProgramUpdated(program);
			}
		}
	}

	public void onEditReceiver() {
		TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
		if( channel != null && channel.getProgram().getReceiver() != null ) {
			this.onEditElement(channel.getProgram().getReceiver());
		}
	}

	public void onProgramUpdated(TGProgram program) {
		TGSynthChannelProperties properties = new TGSynthChannelProperties();
		TGProgramPropertiesUtil.setProgram(properties, TGSynthChannel.CUSTOM_PROGRAM_PREFIX, program);
		TGFactory factory = TGDocumentManager.getInstance(TGSynthDialog.this.context).getSongManager().getFactory();

		List<TGChannelParameter> parameters = new ArrayList<TGChannelParameter>();
		for(Entry<String, String> entry : properties.getProperties().entrySet()) {
			TGChannelParameter parameter = factory.newChannelParameter();
			parameter.setKey(entry.getKey());
			parameter.setValue(entry.getValue());
			parameters.add(parameter);
		}
		this.callUpdateChannelParametersAction(parameters);
	}

	public void onProcessorUpdated(TGProgramElement source, TGAudioProcessor processor) {
		TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
		if( channel != null ) {
			TGProgram program = new TGProgram();
			program.copyFrom(channel.getProgram());

			TGProgramElement element = null;
			if( program.getReceiver() != null && program.getReceiver().equals(source) ) {
				element = program.getReceiver();
			} else {
				int outputCount = program.countOutputs();
				for(int i = 0 ; i < outputCount; i ++) {
					TGProgramElement output = program.getOutput(i);
					if( output.equals(source) ) {
						element = output;
					}
				}
			}

			if( element != null ) {
				processor.storeParameters(element.getParameters());

				this.onProgramUpdated(program);
			}
		}
	}

	public String getProcessorLabel(TGProgramElement element) {
		TGAudioProcessorUI ui = this.getProcessorUI(element);
		if( ui != null ) {
			String label = ui.getLabel();
			if( label != null ) {
				return label;
			}
		}
		return element.getType();
	}

	public TGAudioProcessorUI getProcessorUI(TGProgramElement element) {
		TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
		if( channel != null && channel.getProcessor() != null ) {
			TGAudioProcessor processor = channel.getProcessor().getAudioProcessor(element);
			if( processor != null ) {
				return this.getProcessorUI(element, processor);
			}
		}
		return null;
	}

	public TGAudioProcessorUI getProcessorUI(TGProgramElement element, TGAudioProcessor processor) {
		if( this.processorsUI.containsKey(processor) ) {
			return this.processorsUI.get(processor);
		}

		TGAudioProcessorUICallback callback = this.createProcessorCallback(element, processor);
		TGAudioProcessorUI ui = TGAudioProcessorUIManager.getInstance(this.context).createUI(element.getType(), processor, callback);
		if( ui != null ) {
			this.processorsUI.put(processor, ui);
		}
		return ui;
	}

	public TGAudioProcessorUICallback createProcessorCallback(final TGProgramElement element, final TGAudioProcessor processor) {
		return new TGSynthDialogDelayedCallback(this.context, new Runnable() {
			public void run() {
				onProcessorUpdated(element, processor);
			}
		});
	}

	public void loadMidiReceiver(TGProgram program) {
		this.receiver.setText(program != null && program.getReceiver() != null ? this.getProcessorLabel(program.getReceiver()) : "");
	}

	public void loadOutputsItems(TGProgram program) {
		TGProgramElement selection = this.outputs.getSelectedValue();
		this.outputs.setIgnoreEvents(true);
		this.outputs.removeItems();

		if( program != null ) {
			int outputCount = program.countOutputs();
			for(int i = 0 ; i < outputCount; i ++) {
				TGProgramElement output = program.getOutput(i);
				UITableItem<TGProgramElement> item = new UITableItem<TGProgramElement>(output);
				item.setText(0, this.getProcessorLabel(output));
				this.outputs.addItem(item);
				this.outputs.setCheckedItem(item, Boolean.TRUE.equals(output.isEnabled()));
			}
		}

		if( selection != null ) {
			this.outputs.setSelectedValue(selection);
		}
		this.outputs.setIgnoreEvents(false);
	}

	public void loadChannel() {
		TGProgram program = null;
		TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
		if( channel != null ) {
			program = channel.getProgram();
		}
		this.loadMidiReceiver(program);
		this.loadOutputsItems(program);
	}

	public void loadProperties(){
		if( this.isOpen()){
			this.dialog.setText(TuxGuitar.getProperty("synth-host.ui.dialog.title"));

			this.buttonReceiverAdd.setToolTipText(TuxGuitar.getProperty("synth-host.ui.midi.receiver.add"));
			this.buttonReceiverEdit.setToolTipText(TuxGuitar.getProperty("synth-host.ui.midi.receiver.edit"));
			this.buttonOutputAdd.setToolTipText(TuxGuitar.getProperty("synth-host.ui.audio.processor.add"));
			this.buttonOutputEdit.setToolTipText(TuxGuitar.getProperty("synth-host.ui.audio.processor.edit"));
			this.buttonOutputDelete.setToolTipText(TuxGuitar.getProperty("synth-host.ui.audio.processor.remove"));
			this.buttonOutputMoveUp.setToolTipText(TuxGuitar.getProperty("synth-host.ui.audio.processor.move-up"));
			this.buttonOutputMoveDown.setToolTipText(TuxGuitar.getProperty("synth-host.ui.audio.processor.move-down"));
		}
	}

	public void loadIcons(){
		if( this.isOpen()){
			this.dialog.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getAppIcon());
			this.buttonReceiverAdd.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListAdd());
			this.buttonReceiverEdit.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListEdit());
			this.buttonOutputAdd.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListAdd());
			this.buttonOutputEdit.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListEdit());
			this.buttonOutputDelete.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getImageByName(TGIconManager.LIST_REMOVE));
			this.buttonOutputMoveUp.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getImageByName(TGIconManager.ARROW_UP));
			this.buttonOutputMoveDown.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getImageByName(TGIconManager.ARROW_DOWN));
		}
	}

	public void updateItems() {
		if( this.isOpen()){
			this.loadChannel();
		}
	}

	public void createSyncProcesses() {
		this.updateProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});
		this.loadIconsProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		this.loadPropertiesProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});
	}

	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateProcess.process();
		}
	}

	public void processEvent(TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
		else if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
	}

	public void callUpdateChannelParametersAction(List<TGChannelParameter> parameters) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGUpdateChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.channel);
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PARAMETERS, parameters);
		tgActionProcessor.process();
	}
}
