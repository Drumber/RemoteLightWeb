package de.lars.remotelightweb.ui.components.settingpanels;

import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import de.lars.remotelightcore.settings.Setting;
import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.config.TC_HIDE_ON_CLICK;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;

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
	
	protected IconButton getHelpIcon() {
		IconButton button = new IconButton(VaadinIcon.QUESTION_CIRCLE.create());
		TooltipConfiguration ttconfig = new TooltipConfiguration(setting.getDescription());
		ttconfig.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
		Tooltips.getCurrent().setTooltip(button, ttconfig);
		return button;
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
