package app.tuxguitar.android.action.listener.cache;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGContext;

public class TGUpdateBuffer {

	private static final Integer UPDATE_CACHE = 1;
	private static final Integer UPDATE_ITEMS = 2;

	private TGContext context;
	private Integer updateCache;
	private Boolean updateSong;
	private Boolean updateSongLoaded;
	private Boolean updateSongSaved;
	private List<Integer> updateMeasures;
	private List<Runnable> updateRunnables;

	public TGUpdateBuffer(TGContext context) {
		this.context = context;
		this.updateMeasures = new ArrayList<Integer>();
		this.updateRunnables = new ArrayList<Runnable>();
	}

	public void clear() {
		this.updateRunnables.clear();
		this.updateMeasures.clear();
		this.updateCache = null;
		this.updateSong = false;
		this.updateSongLoaded = false;
		this.updateSongSaved = false;
	}

	public void apply(TGAbstractContext srcContext) {
		// Update song structure
		this.applyUpdateSong(srcContext);
		this.applyUpdateLoadedSong(srcContext);
		this.applyUpdateSavedSong(srcContext);
		this.applyUpdateMeasures(srcContext);

		// Process runnables after update
		this.applyUpdateRunnables();

		// Refresh cache
		this.applyUpdateCache(srcContext);
	}

	public void applyUpdateSong(TGAbstractContext sourceContext) {
		if( this.updateSong ) {
			TGEditorManager.getInstance(this.context).updateSong(sourceContext);
		}
	}

	public void applyUpdateLoadedSong(TGAbstractContext sourceContext) {
		if( this.updateSongLoaded ) {
			TGEditorManager.getInstance(this.context).updateLoadedSong(sourceContext);
		}
	}

	public void applyUpdateSavedSong(TGAbstractContext sourceContext) {
		if( this.updateSongSaved ) {
			TGEditorManager.getInstance(this.context).updateSavedSong(sourceContext);
		}
	}

	public void applyUpdateMeasures(TGAbstractContext sourceContext) {
		if(!this.updateSong && !this.updateSongLoaded && !this.updateSongSaved ) {
			TGEditorManager.getInstance(this.context).updateMeasures(this.updateMeasures, sourceContext);
		}
	}

	public void applyUpdateCache(TGAbstractContext sourceContext) {
		if( this.updateCache != null && this.updateCache.intValue() >= UPDATE_CACHE ) {
			TGActivity activity = TGActivityController.getInstance(this.context).getActivity();
			if( activity != null ) {
				activity.updateCache(this.updateCache.equals(UPDATE_ITEMS), sourceContext);
			}
		}
	}

	public void applyUpdateRunnables() {
		for(Runnable runnable : this.updateRunnables) {
			runnable.run();
		}
	}

	public void requestUpdateCache(Boolean updateItems) {
		if( this.updateCache == null || this.updateCache.intValue() < UPDATE_ITEMS.intValue() ) {
			this.updateCache = (updateItems ? UPDATE_ITEMS : UPDATE_CACHE);
		}
	}

	public void requestUpdateMeasure(Integer number) {
		if(!this.updateMeasures.contains(number)) {
			this.updateMeasures.add(number);
		}
	}

	public void requestUpdateSong() {
		this.updateSong = true;
	}

	public void requestUpdateLoadedSong() {
		this.updateSongLoaded = true;
	}

	public void requestUpdateSavedSong() {
		this.updateSongSaved = true;
	}

	public void doPostUpdate(Runnable runnable) {
		this.updateRunnables.add(runnable);
	}
}
