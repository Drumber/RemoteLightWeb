package de.lars.remotelightweb.ui.components.settingpanels;

import com.vaadin.flow.component.checkbox.Checkbox;

import de.lars.remotelightcore.settings.types.SettingBoolean;

public class SettingBooleanPanel extends SettingPanel {
	
	private Checkbox checkBox;

	public SettingBooleanPanel(SettingBoolean setting) {
		super(setting);
		
		checkBox = new Checkbox();
		checkBox.setValue(setting.getValue());
		checkBox.addValueChangeListener(e -> onChanged(this));
		this.setFlexGrow(2, checkBox);
		add(checkBox);
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			// TODO help text
		}
	}

	@Override
	public void setValue() {
		((SettingBoolean) getSetting()).setValue(checkBox.getValue());
	}

	@Override
	public void updateComponents() {
		checkBox.setValue(((SettingBoolean) getSetting()).getValue());
	}

}
