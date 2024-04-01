package org.herac.tuxguitar.cocoa.opendoc;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import java.util.ArrayList;
import java.util.List;

public class OpenDocListener {
	
	private List<String> eventsText;
	private boolean enabled;
	
	public OpenDocListener(){
		this.eventsText = new ArrayList<String>();
		this.enabled = false;
	}
	
	public void init() {
		Display.getCurrent().addListener(SWT.OpenDocument, new Listener() {
			public void handleEvent(Event event) {
				if (event.text != null) {
					eventsText.add(event.text);
					if (enabled) {
						process();
					}
				}
			}
		});
	}
	
	public void process() {
		this.enabled = true;
		if (!this.eventsText.isEmpty()) {
			// just a test: trying with 1st element of list
			TuxGuitar.getInstance().getPlayer().reset();
			TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGReadURLAction.NAME);
			try {
				tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, new File(this.eventsText.get(0)).toURI().toURL());
				eventsText.remove(0);
				tgActionProcessor.process();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public void disconnect() {
		// todo: remove listener
	}
	
}
