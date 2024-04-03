package org.herac.tuxguitar.cocoa.opendoc;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

import java.util.ArrayList;
import java.util.List;

public class OpenDocListener implements Listener {

	private List<String> eventsText;
	private boolean enabled;
	private TGPluginManager pluginManager;

	public OpenDocListener(TGContext context){
		this.eventsText = new ArrayList<String>();
		this.enabled = false;
		this.pluginManager = TGPluginManager.getInstance(context);
		Display.getCurrent().addListener(SWT.OpenDocument, this);
	}

	public void process() {
		// need to wait for all other plugins to be initialized, or openDoc may fail
		if (!pluginManager.allEnabledPluginsInitialized()) {
			new Thread() {
				public void run() {
					try {
						while (!pluginManager.allEnabledPluginsInitialized()) {
							Thread.sleep(50);
						}
						process();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
			return;
		}

		this.enabled = true;
		TuxGuitar.getInstance().getPlayer().reset();
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGReadURLAction.NAME);
		synchronized(this.getClass()) {
			while (!this.eventsText.isEmpty()) {
				try {
					tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, new File(this.eventsText.get(0)).toURI().toURL());
					tgActionProcessor.process();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				eventsText.remove(0);
			}
		}
	}

	public void disconnect() {
		this.enabled = false;
		Display.getCurrent().removeListener(SWT.OpenDocument, this);
	}

	public void handleEvent(Event event) {
		synchronized (this.getClass()) {
			if (event.text != null) {
				this.eventsText.add(event.text);
			}
		}
		if (this.enabled) {
			process();
		}
	}
}
