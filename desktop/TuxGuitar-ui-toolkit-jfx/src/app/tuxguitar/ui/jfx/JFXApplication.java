package app.tuxguitar.ui.jfx;

import java.net.URL;

import app.tuxguitar.ui.UIApplication;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.appearance.UIAppearance;
import app.tuxguitar.ui.jfx.appearance.JFXAppearance;

import javafx.application.Platform;

public class JFXApplication extends JFXComponent<JFXApplicationHandle> implements UIApplication {

	private UIFactory uiFactory;
	private UIAppearance uiAppearance;

	public JFXApplication() {
		super(JFXApplicationHandle.getInstance());

		this.uiFactory = new JFXFactory(this);
		this.uiAppearance = new JFXAppearance();
	}

	public void dispose() {
		this.getControl().getStage().close();
	}

	public UIFactory getFactory() {
		return this.uiFactory;
	}

	public UIAppearance getAppearance() {
		return this.uiAppearance;
	}

	public void openUrl(URL url) {
		this.getControl().getHostServices().showDocument(url.toExternalForm());
	}

	public void runInUiThread(Runnable runnable) {
		Platform.runLater(runnable);
	}

	public boolean isInUiThread() {
		return Platform.isFxApplicationThread();
	}

	public void start(Runnable runnable) {
		this.getControl().start(runnable);
	}
}
