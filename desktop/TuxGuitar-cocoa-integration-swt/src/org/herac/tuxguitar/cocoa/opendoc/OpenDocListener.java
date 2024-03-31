package org.herac.tuxguitar.cocoa.opendoc;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

//debug
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OpenDocListener {
	
	public OpenDocListener(){
		Display.getCurrent().addListener(SWT.OpenDocument, new Listener() {
			public void handleEvent(Event event) {
				if (event.text != null) {
					TuxGuitar.getInstance().getPlayer().reset();
					TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGReadURLAction.NAME);
					try {
						tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, new File(event.text).toURI().toURL());
						tgActionProcessor.process();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				// debug log
				try {
					BufferedWriter debugWriter = new BufferedWriter(new FileWriter("/tmp/tuxguitar-menu-debug.log"));
					debugWriter.append("received event\n");
					if (event.text != null) debugWriter.append("tried to open file: " + event.text + "\n");
					debugWriter.flush();
					debugWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
