package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.util.TGContext;

public class TGUndoableTimeSignatureController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableTimeSignature.startUndo(context);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		TGMeasure measure = ((TGMeasure) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGTimeSignature timeSignature = ((TGTimeSignature) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE));
		boolean applyToEnd = ((Boolean) actionContext.getAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END)).booleanValue();

		return ((TGUndoableTimeSignature) undoableEdit).endUndo(timeSignature, measure.getStart(), applyToEnd);
	}
}
