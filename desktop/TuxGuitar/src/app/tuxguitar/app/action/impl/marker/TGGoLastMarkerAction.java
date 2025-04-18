package app.tuxguitar.app.action.impl.marker;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGGoLastMarkerAction extends TGActionBase{

	public static final String NAME = "action.marker.go-last";

	public TGGoLastMarkerAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGMarker marker = getSongManager(context).getLastMarker(song);
		if( marker != null ) {
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, marker);

			TGActionManager.getInstance(getContext()).execute(TGGoToMarkerAction.NAME, context);
		}
	}
}
