package de.lars.remotelightweb.ui.utils;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.types.*;
import de.lars.remotelightweb.ui.components.settingpanels.*;

public class SettingPanelUtil {

	/**
	 * 
	 * @param s Setting subclass
	 * @return The corresponding setting panel
	 */
	public static SettingPanel getSettingPanel(Setting s) {
		if(s instanceof SettingString) {
			return new SettingStringPanel((SettingString) s);
		}
		if(s instanceof SettingBoolean) {
			return new SettingBooleanPanel((SettingBoolean) s);
		}
		if(s instanceof SettingColor) {
			return new SettingColorPanel((SettingColor) s);
		}
		if(s instanceof SettingDouble) {
			return new SettingDoublePanel((SettingDouble) s);
		}
		if(s instanceof SettingInt) {
			return new SettingIntPanel((SettingInt) s);
		}
		if(s instanceof SettingSelection) {
			return new SettingSelectionPanel((SettingSelection) s);
		}
		return null;
	}
	
}
