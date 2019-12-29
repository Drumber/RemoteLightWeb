package de.lars.remotelightweb.ui.components.settingpanels;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import de.lars.remotelightclient.settings.Setting;

public abstract class SettingPanel extends HorizontalLayout {
	
	private Setting setting;
	private SettingChangedListener listener;
	
	public SettingPanel(Setting setting) {
		this.setting = setting;
		this.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		Label name = new Label(setting.getName());
		add(name);
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	public interface SettingChangedListener {
		public void onSettingChanged(SettingPanel settingPanel);
	}
	
	public abstract void setValue();
	public abstract void updateComponents();

	public synchronized void setSettingChangedListener(SettingChangedListener l) {
		this.listener = l;
	}
	
	public void onChanged(SettingPanel settingPanel) {
		if(listener != null) {
			listener.onSettingChanged(settingPanel);
		}
	}

}
