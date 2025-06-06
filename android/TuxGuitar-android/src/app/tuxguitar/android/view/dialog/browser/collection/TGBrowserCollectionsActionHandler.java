package app.tuxguitar.android.view.dialog.browser.collection;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import app.tuxguitar.android.browser.TGBrowserCollection;
import app.tuxguitar.android.menu.controller.TGMenuController;
import app.tuxguitar.android.view.dialog.TGDialogController;
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import app.tuxguitar.editor.action.TGActionProcessor;

public class TGBrowserCollectionsActionHandler {

	private TGBrowserCollectionsDialog view;

	public TGBrowserCollectionsActionHandler(TGBrowserCollectionsDialog view) {
		this.view = view;
	}

	public TGActionProcessorListener createAction(String actionId) {
		return new TGActionProcessorListener(this.view.findContext(), actionId);
	}

	public TGActionProcessorListener createAddCollectionAction(TGBrowserCollection collection) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGBrowserAddCollectionAction.NAME);
		tgActionProcessor.setAttribute(TGBrowserAddCollectionAction.ATTRIBUTE_COLLECTION, collection);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createRemoveCollectionAction(TGBrowserCollection collection) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGBrowserRemoveCollectionAction.NAME);
		tgActionProcessor.setAttribute(TGBrowserRemoveCollectionAction.ATTRIBUTE_COLLECTION, collection);
		return this.createConfirmableActionProcessor(tgActionProcessor, this.view.findActivity().getString(R.string.browser_collections_dlg_remove_confirm_question));
	}

	public TGActionProcessorListener createOpenDialogAction(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.view.findActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createOpenMenuAction(TGMenuController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, this.view.findActivity());
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createConfirmableActionProcessor(final TGActionProcessor actionProcessor, final String confirmMessage) {
		TGActionProcessorListener tgActionProcessor = this.createOpenDialogAction(new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, new Runnable() {
			public void run() {
				actionProcessor.process();
			}
		});
		return tgActionProcessor;
	}
}
