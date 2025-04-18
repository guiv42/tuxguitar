package app.tuxguitar.android.action.listener.transport;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

public class TGDisableOnPlayInterceptor implements TGActionInterceptor {

	private TGContext context;
	private List<String> actionIds;

	public TGDisableOnPlayInterceptor(TGContext context){
		this.context = context;
		this.actionIds = new ArrayList<String>();
	}

	public boolean containsActionId(String id) {
		return this.actionIds.contains(id);
	}

	public void addActionId(String id) {
		this.actionIds.add(id);
	}

	public void removeActionId(String id) {
		this.actionIds.remove(id);
	}

	public boolean intercept(String id, TGActionContext context) throws TGActionException {
		boolean intercepted = false;

		if( this.containsActionId(id) ) {
			intercepted = MidiPlayer.getInstance(this.context).isRunning();

			if (intercepted) {
				TGActivity activity = TGActivityController.getInstance(this.context).getActivity();
				if (activity != null) {
					activity.updateCache(true);
				}
			}
		}
		return intercepted;
	}
}
