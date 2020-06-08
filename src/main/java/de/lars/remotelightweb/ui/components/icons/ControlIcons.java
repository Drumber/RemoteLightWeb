package de.lars.remotelightweb.ui.components.icons;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;

@JsModule("./icons/controlpanel-iconset-svg.js")
public enum ControlIcons {

	BRIGHTNESS("Brightness"),
	DARK_MODE("DarkMode"),
	LIGHT_MODE("LightMode"),
	SHUTDOWN("Shutdown"),
	UPDATE("Update");
	
	private final String iconName;
	
	ControlIcons(String iconName) {
		this.iconName = iconName;
	}
	
	/**
	 * Create a new iron-icon
	 * @return	new iron-icon with default size {@code 30px}
	 */
	public Icon create() {
		return create(36);
	}
	
	/**
	 * Create a new iron-icon
	 * @param size	icon size in pixel
	 * @return		new iron-icon
	 */
	public Icon create(int size) {
		Icon icon = new Icon("controlicons", iconName);
		icon.setSize(size + "px");
		return icon;
	}
}
