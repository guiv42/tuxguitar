package app.tuxguitar.ui.jfx.menu;

import javafx.scene.control.MenuItem;

import app.tuxguitar.ui.menu.UIMenu;

public interface JFXMenuItemContainer extends UIMenu {

	void addItem(JFXMenuItem<? extends MenuItem> uiControl);

	void removeItem(JFXMenuItem<? extends MenuItem> uiControl);
}
