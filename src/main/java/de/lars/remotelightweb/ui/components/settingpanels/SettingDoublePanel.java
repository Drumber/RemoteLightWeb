package de.lars.remotelightweb.ui.components.settingpanels;

import com.vaadin.flow.component.textfield.NumberField;

import de.lars.remotelightcore.settings.types.SettingDouble;

public class SettingDoublePanel extends SettingPanel {
	
	private NumberField field;

	public SettingDoublePanel(SettingDouble setting) {
		super(setting);
		
		field = new NumberField();
		field.setHasControls(true);
		field.setValue(setting.getValue());
		field.setMin(setting.getMin());
		field.setMax(setting.getMax());
		field.setStep(setting.getStepsize());
		field.addValueChangeListener(e -> onChanged(this));
		this.setFlexGrow(2, field);
		add(field);
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			// TODO help text
		}
	}

	@Override
	public void setValue() {
		((SettingDouble) getSetting()).setValue(field.getValue());
		
	}

	@Override
	public void updateComponents() {
		field.setValue(((SettingDouble) getSetting()).getValue());
	}

}
