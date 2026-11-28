package app.tuxguitar.app.util;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusGainedListener;
import app.tuxguitar.ui.event.UIFocusLostListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

// panel with "measures: from/to", to be re-used in dialogs
// implements consistency check between "from" and "to" fields

public class TGMeasureRangePanel implements Runnable {
	private final static long FOCUS_LOSS_TIMEOUT_MS = 50;

	private UILegendPanel panel;
	private UISpinner fromSpinner;
	private UISpinner toSpinner;
	private UIButton firstMeasureButton;
	private UIButton lastMeasureButton;
	private int from;
	private int to;
	private int nbMeasures;
	private TGMeasureRangeDialogUpdater updater;
	private boolean fromHasFocus;
	private boolean toHasFocus;
	private boolean fromHadFocus;
	private long startTime;
	private boolean waitingForFocus;
	private Object mutex = new Object();
	private TGContext context;

	public TGMeasureRangePanel(UIWindow parent, UIFactory uiFactory, int nbMeasures, int from, int to,
			TGMeasureRangeDialogUpdater updater, TGContext context) {
		this.updater = updater;
		this.from = from;
		this.to = to;
		this.context = context;
		this.nbMeasures = nbMeasures;

		UITableLayout rangeLayout = new UITableLayout();
		this.panel = uiFactory.createLegendPanel(parent);
		panel.setLayout(rangeLayout);

		UILabel fromLabel = uiFactory.createLabel(panel);
		fromLabel.setText(TuxGuitar.getProperty("edit.from") + ":");
		rangeLayout.set(fromLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		// ---- spinners
		this.fromSpinner = uiFactory.createSpinner(panel);
		this.fromSpinner.setMinimum(1);
		this.fromSpinner.setMaximum(nbMeasures);
		this.fromSpinner.setValue(from);
		rangeLayout.set(this.fromSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1,
				180f, null, null);
		this.fromSpinner.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMeasureRangePanel.this.from = TGMeasureRangePanel.this.fromSpinner.getValue();
				TGMeasureRangePanel.this.firstMeasureButton.setEnabled(TGMeasureRangePanel.this.from != 1);
				if (TGMeasureRangePanel.this.updater != null) {
					TGMeasureRangePanel.this.updater.update(getFrom(), getTo());
				}
			}
		});
		this.fromSpinner.addFocusGainedListener(new UIFocusGainedListener() {
			@Override
			public void onFocusGained(UIFocusEvent event) {
				TGMeasureRangePanel.this.fromHasFocus = true;
				TGMeasureRangePanel.this.updateFocusStatus();
			}
		});
		this.fromSpinner.addFocusLostListener(new UIFocusLostListener() {
			@Override
			public void onFocusLost(UIFocusEvent event) {
				if (TGMeasureRangePanel.this.fromHasFocus) {
					TGMeasureRangePanel.this.fromHasFocus = false;
					TGMeasureRangePanel.this.fromHadFocus = true;
					TGMeasureRangePanel.this.updateFocusStatus();
				}
			}
		});

		UILabel toLabel = uiFactory.createLabel(panel);
		toLabel.setText(TuxGuitar.getProperty("edit.to") + ":");
		rangeLayout.set(toLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		this.toSpinner = uiFactory.createSpinner(panel);
		this.toSpinner.setMinimum(1);
		this.toSpinner.setMaximum(nbMeasures);
		this.toSpinner.setValue(to);
		rangeLayout.set(this.toSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1,
				180f, null, null);
		this.toSpinner.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMeasureRangePanel.this.to = TGMeasureRangePanel.this.toSpinner.getValue();
				TGMeasureRangePanel.this.lastMeasureButton
						.setEnabled(TGMeasureRangePanel.this.to != TGMeasureRangePanel.this.nbMeasures);
				if (TGMeasureRangePanel.this.updater != null) {
					TGMeasureRangePanel.this.updater.update(getFrom(), getTo());
				}
			}
		});
		this.toSpinner.addFocusGainedListener(new UIFocusGainedListener() {
			@Override
			public void onFocusGained(UIFocusEvent event) {
				TGMeasureRangePanel.this.toHasFocus = true;
				TGMeasureRangePanel.this.updateFocusStatus();
			}
		});
		this.toSpinner.addFocusLostListener(new UIFocusLostListener() {
			@Override
			public void onFocusLost(UIFocusEvent event) {
				if (TGMeasureRangePanel.this.toHasFocus) {
					TGMeasureRangePanel.this.toHasFocus = false;
					TGMeasureRangePanel.this.fromHadFocus = false;
					TGMeasureRangePanel.this.updateFocusStatus();
				}
			}
		});

		// ---- buttons
		this.firstMeasureButton = uiFactory.createButton(panel);
		this.firstMeasureButton.setText(TuxGuitar.getProperty("measure.first"));
		rangeLayout.set(this.firstMeasureButton, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		this.firstMeasureButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMeasureRangePanel.this.fromSpinner.setValue(1);
			}
		});
		this.lastMeasureButton = uiFactory.createButton(panel);
		this.lastMeasureButton.setText(TuxGuitar.getProperty("measure.last"));
		rangeLayout.set(this.lastMeasureButton, 2, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		this.lastMeasureButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMeasureRangePanel.this.toSpinner.setValue(nbMeasures);
			}
		});
		this.fromSpinner.setFocus();
	}

	public UILegendPanel getPanel() {
		return this.panel;
	}

	public int getFrom() {
		return this.from;
	}

	public int getTo() {
		return this.to;
	}

	private void updateFocusStatus() {
		if (this.fromHasFocus || this.toHasFocus) {
			// stop waiting, from or to has the focus
			synchronized (this.mutex) {
				this.waitingForFocus = false;
				this.mutex.notifyAll();
			}
		} else {
			// focus lost by one spinner, wait a bit to see if the other spinner gains focus
			synchronized (this.mutex) {
				if (!this.waitingForFocus) {
					this.waitingForFocus = true;
					// Start the thread
					Thread thread = new Thread(this);
					thread.start();
				}
				this.startTime = System.currentTimeMillis();
				// notify the thread
				this.mutex.notifyAll();
			}
		}
	}

	@Override
	public void run() {
		try {
			synchronized (this.mutex) {
				long timeToWait = ((this.startTime + FOCUS_LOSS_TIMEOUT_MS) - System.currentTimeMillis());
				this.waitingForFocus = (timeToWait > 0);
				if (this.waitingForFocus) {
					this.mutex.wait(timeToWait);
				}
			}
			if (this.waitingForFocus) {
				// now, focus has been lost by both "from" and "to" spinners
				if (this.from >= this.to) {
					if (fromHadFocus) {
						forceValues(to);
					} else {
						forceValues(from);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void forceValues(int value) {
		TGSynchronizer.getInstance(context).executeLater(new Runnable() {
			@Override
			public void run() {
				fromSpinner.setValue(value);
				toSpinner.setValue(value);
				if (TGMeasureRangePanel.this.updater != null) {
					TGMeasureRangePanel.this.updater.update(value, value);
				}
			}
		});
	}
}