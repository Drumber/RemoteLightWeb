package de.lars.remotelightweb.ui.components;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;

@JsModule("./icons/icomoon-iconset-svg.js")
public enum MenuIcons {

	ABOUT("About"),
	ADD("Add"),
	ANIMATION("Animation"),
	ARDUINO("Arduino"),
	ARTNET("Art-Net"),
	CHAIN("Chain_Link"),
	COLOR_PALETTE("Color-Palette"),
	LED_STRIP("LED_Strip"),
	LED_STRIP_GLOWING("LED_Strip-Glowing"),
	LED_STRIP_MIDDLE("LED_Strip-Middle"),
	LINK("Link"),
	LINK_STRIPS("Link_Strips"),
	MENU("Menu"),
	MUSICSYMC("MusicSync"),
	RASPBERRYPI("Raspberry-Pi"),
	SCENE("Scene"),
	SCREENCOLOR("ScreenColor"),
	SCRIPTS("Scripts"),
	SETTINGS("Settings"),
	OUTPUTS("Outputs"),
	ERROR("Error"),
	HELP("Help");
	
	private final String iconName;
	
	MenuIcons(String iconName) {
		this.iconName = iconName;
	}
	
	/**
	 * Create a new iron-icon
	 * @return	new iron-icon with default size {@code 30px}
	 */
	public Icon create() {
		return create(30);
	}
	
	/**
	 * Create a new iron-icon
	 * @param size	icon size in pixel
	 * @return		new iron-icon
	 */
	public Icon create(int size) {
		Icon icon = new Icon("icomoon", iconName);
		icon.setSize(size + "px");
		return icon;
	}
	
}
