package app.tuxguitar.ui.swt.resource;

import java.io.InputStream;

import org.eclipse.swt.graphics.Device;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIResourceFactory;

public class SWTResourceFactory implements UIResourceFactory {

	private Device device;

	public SWTResourceFactory(Device device){
		this.device = device;
	}

	public UIColor createColor(int red, int green, int blue) {
		return new SWTColor(this.device , red, green , blue);
	}

	public UIColor createColor(UIColorModel model) {
		return this.createColor(model.getRed(), model.getGreen(), model.getBlue());
	}

	public UIFont createFont(String name, float height, boolean bold, boolean italic) {
		return this.createFont(new UIFontModel(name, height, bold, italic));
	}

	public UIFont createFont(UIFontModel model) {
		return new SWTFont(this.device, model);
	}

	public UIImage createImage(float width, float height) {
		return new SWTImage(this.device, width, height);
	}

	public UIImage createImage(InputStream inputStream) {
		return new SWTImage(this.device, inputStream);
	}
}
