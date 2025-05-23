package app.tuxguitar.app.view.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.menu.impl.BeatMenuItem;
import app.tuxguitar.app.view.menu.impl.CompositionMenuItem;
import app.tuxguitar.app.view.menu.impl.EditMenuItem;
import app.tuxguitar.app.view.menu.impl.FileMenuItem;
import app.tuxguitar.app.view.menu.impl.HelpMenuItem;
import app.tuxguitar.app.view.menu.impl.MarkerMenuItem;
import app.tuxguitar.app.view.menu.impl.MeasureMenuItem;
import app.tuxguitar.app.view.menu.impl.ToolMenuItem;
import app.tuxguitar.app.view.menu.impl.TrackMenuItem;
import app.tuxguitar.app.view.menu.impl.TransportMenuItem;
import app.tuxguitar.app.view.menu.impl.ViewMenuItem;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.io.base.TGFileFormatAvailabilityEvent;
import app.tuxguitar.ui.menu.UIMenuBar;
import app.tuxguitar.ui.menu.UIMenuItem;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGMenuManager implements TGEventListener {

	private TGContext context;
	private UIMenuBar menu;
	private UIPopupMenu popupMenu;
	private List<TGMenuItem> loadedMenuItems;
	private List<TGMenuItem> loadedPopupMenuItems;

	private TGSyncProcessLocked loadIconsProcess;
	private TGSyncProcessLocked loadPropertiesProcess;
	private TGSyncProcessLocked updateItemsProcess;
	private TGSyncProcessLocked createMenuProcess;

	public TGMenuManager(TGContext context){
		this.context = context;
		this.loadedMenuItems = new ArrayList<TGMenuItem>();
		this.loadedPopupMenuItems = new ArrayList<TGMenuItem>();
		this.createSyncProcesses();
		this.loadItems();
		this.appendListeners();
	}

	public void loadItems(){
		this.createMenuProcess.process();
		this.createPopupMenu();
	}

	public void createMenu() {
		UIWindow window = TGWindow.getInstance(this.context).getWindow();
		if(!window.isDisposed()) {
			if( this.menu == null || this.menu.isDisposed() ) {
				this.menu = TGApplication.getInstance(this.context).getFactory().createMenuBar(window);
			}
			List<UIMenuItem> items = this.menu.getItems();
			for(UIMenuItem uiMenuItem : items){
				uiMenuItem.dispose();
			}

			this.loadedMenuItems.clear();
			this.loadedMenuItems.add(new FileMenuItem(this.menu));
			this.loadedMenuItems.add(new EditMenuItem(this.menu));
			this.loadedMenuItems.add(new ViewMenuItem(this.menu));
			this.loadedMenuItems.add(new CompositionMenuItem(this.menu));
			this.loadedMenuItems.add(new TrackMenuItem(this.menu));
			this.loadedMenuItems.add(new MeasureMenuItem(this.menu));
			this.loadedMenuItems.add(new BeatMenuItem(this.menu));
			this.loadedMenuItems.add(new MarkerMenuItem(this.menu));
			this.loadedMenuItems.add(new TransportMenuItem(this.menu));
			this.loadedMenuItems.add(new ToolMenuItem(this.menu));
			this.loadedMenuItems.add(new HelpMenuItem(this.menu));
			this.showMenuItems(this.loadedMenuItems);
		}
	}

	public void createPopupMenu() {
		UIWindow window = TGWindow.getInstance(this.context).getWindow();
		if(!window.isDisposed()) {
			if( this.popupMenu == null || this.popupMenu.isDisposed() ){
				this.popupMenu = TGApplication.getInstance(this.context).getFactory().createPopupMenu(window);
			}
			List<UIMenuItem> items = this.popupMenu.getItems();
			for(UIMenuItem uiMenuItem : items){
				uiMenuItem.dispose();
			}

			this.loadedPopupMenuItems.clear();
			this.loadedPopupMenuItems.add(new EditMenuItem(this.popupMenu));
			this.loadedPopupMenuItems.add(new CompositionMenuItem(this.popupMenu));
			this.loadedPopupMenuItems.add(new TrackMenuItem(this.popupMenu));
			this.loadedPopupMenuItems.add(new MeasureMenuItem(this.popupMenu));
			this.loadedPopupMenuItems.add(new BeatMenuItem(this.popupMenu));
			this.loadedPopupMenuItems.add(new MarkerMenuItem(this.popupMenu));
			this.loadedPopupMenuItems.add(new TransportMenuItem(this.popupMenu));
			this.showMenuItems(this.loadedPopupMenuItems);
		}
	}

	private void showMenuItems(List<TGMenuItem> items){
		Iterator<TGMenuItem> it = items.iterator();
		while(it.hasNext()){
			TGMenuItem item = (TGMenuItem)it.next();
			item.showItems();
		}
	}

	public void updateItems(){
		if(!this.isDisposed()){
			this.updateItems(this.loadedMenuItems);
			this.updateItems(this.loadedPopupMenuItems);
		}
	}

	public void updateItems(List<TGMenuItem> menuItems){
		for(TGMenuItem menuItem : menuItems) {
			menuItem.update();
		}
	}

	public void loadProperties(){
		if(!this.isDisposed()){
			this.loadProperties(this.loadedMenuItems);
			this.loadProperties(this.loadedPopupMenuItems);
		}
	}

	public void loadProperties(List<TGMenuItem> menuItems){
		for(TGMenuItem menuItem : menuItems) {
			menuItem.loadProperties();
		}
	}

	public void loadIcons(){
		if(!this.isDisposed()){
			this.loadItems();
		}
	}

	public UIPopupMenu getPopupMenu() {
		return this.popupMenu;
	}

	private boolean isMenuDisposed(){
		return (this.menu == null || this.menu.isDisposed());
	}

	private boolean isPopupMenuDisposed(){
		return (this.popupMenu == null || this.popupMenu.isDisposed());
	}

	private boolean isDisposed(){
		return (this.isMenuDisposed() || this.isPopupMenuDisposed());
	}

	public void createSyncProcesses() {
		this.updateItemsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});

		this.loadIconsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});

		this.loadPropertiesProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});

		this.createMenuProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				createMenu();
			}
		});
	}

	public void appendListeners() {
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
		TuxGuitar.getInstance().getFileFormatManager().addFileFormatAvailabilityListener(this);
	}

	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION || type == TGUpdateEvent.CUSTOM_TEMPLATE_CHANGED){
			this.updateItemsProcess.process();
		}
	}

	public void processEvent(final TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
		else if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
		else if( TGFileFormatAvailabilityEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.createMenuProcess.process();
		}
	}
}
