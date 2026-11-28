package app.tuxguitar.app.view.dialog.measure;

import java.util.Iterator;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.util.TGMeasureRangeDialogUpdater;
import app.tuxguitar.app.util.TGMeasureRangePanel;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGMeasureCopyDialog implements TGMeasureRangeDialogUpdater{
	// to memorize user preferences (used each time dialog is opened)
	static boolean initCopyAllTracks = true;
	static boolean initCopyMarkers = false;
	private UICheckBox copyMarkers;
	private UIButton buttonOK;
	private TGMeasureRangePanel measureRange;
	private TGSong song;

	public void show(final TGViewContext context) {
		this.song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		final TGBeatRange beats = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("edit.copy"));

		//----------- measure range -----------------------------------------------------------
		int measureCount = this.song.countMeasureHeaders();
		int from;
		int to;
		if (beats!=null && !beats.isEmpty()) {
			from = beats.firstMeasure().getNumber();
			to = beats.lastMeasure().getNumber();
		} else {
			from = header.getNumber();
			to = header.getNumber();
		}
		this.measureRange = new TGMeasureRangePanel(dialog, uiFactory, measureCount, from, to, this, context.getContext());
		UILegendPanel rangePanel = this.measureRange.getPanel();
		rangePanel.setText(TuxGuitar.getProperty("edit.copy"));
		dialogLayout.set(rangePanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//------------ OPTIONS -----------------------------------------------
		UICheckBox allTracks = null;
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		int rowCheckBox = 1;
		if( this.song.countTracks() > 1 ){
			allTracks = uiFactory.createCheckBox(options);
			allTracks.setText(TuxGuitar.getProperty("edit.all-tracks"));
			allTracks.setSelected(initCopyAllTracks);
			optionsLayout.set(allTracks, rowCheckBox, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			rowCheckBox++;
		}
		final UICheckBox allTracksFinal = allTracks;
		this.copyMarkers = uiFactory.createCheckBox(options);
		this.copyMarkers.setText(TuxGuitar.getProperty("edit.copy-markers"));
		this.copyMarkers.setSelected(initCopyMarkers);
		optionsLayout.set(this.copyMarkers, rowCheckBox, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		this.buttonOK = uiFactory.createButton(buttons);
		this.buttonOK.setText(TuxGuitar.getProperty("ok"));
		this.buttonOK.setDefaultButton();
		this.buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if (allTracksFinal!=null) {
					initCopyAllTracks = allTracksFinal.isSelected();
				}
				initCopyMarkers = copyMarkers.isSelected();
				processAction(context.getContext(), measureRange.getFrom(), measureRange.getTo(),
						(allTracksFinal != null ? allTracksFinal.isSelected() : false),
						copyMarkers.isSelected());
				dialog.dispose();
			}
		});
		buttonsLayout.set(this.buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

		this.update(from, to);
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void processAction(TGContext context, Integer measure1, Integer measure2, Boolean copyAllTracks, Boolean copyMarkers) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGCopyMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_1, measure1);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_2, measure2);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_ALL_TRACKS, copyAllTracks);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_COPY_MARKERS, copyMarkers);
		tgActionProcessor.process();
	}

	@Override
	public void update(int from, int to) {
		boolean atLeastOneMarker = false;
		Iterator<TGMeasureHeader> headers = this.song.getMeasureHeaders();
		while (headers.hasNext() && !atLeastOneMarker) {
			TGMeasureHeader header = headers.next();
			int measureNb = header.getNumber();
			atLeastOneMarker = (measureNb>=from && measureNb<=to && header.getMarker() != null);
		}
		this.copyMarkers.setEnabled(atLeastOneMarker);
		if (!atLeastOneMarker) {
			this.copyMarkers.setSelected(false);
		}
		this.buttonOK.setEnabled(from <= to);
	}
}
