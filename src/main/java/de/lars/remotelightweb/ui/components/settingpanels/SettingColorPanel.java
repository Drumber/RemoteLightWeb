package de.lars.remotelightweb.ui.components.settingpanels;

import com.github.juchar.colorpicker.ColorPicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightweb.ui.utils.ColorUtils;

public class SettingColorPanel extends SettingPanel {
	
	private Button button;
	private ColorPicker cp;

	public SettingColorPanel(SettingColor setting) {
		super(setting);
		
		cp = new ColorPicker(setting.getValue(), setting.getValue());
		cp.setHexEnabled(false);
		cp.setAlphaEnabled(false);
		cp.setPinnedInputs(true);
		cp.setPinnedPalettes(true);
		cp.setHslEnabled(false);
		cp.setPalette(ColorUtils.getColorPalette());
		
		Dialog dialog = new Dialog(cp);
		Button btnOk = new Button("Ok", e -> {
			dialog.close();
		});
		dialog.add(btnOk);
		
		button = new Button();
		button.getStyle().set("border-style", "dashed");
		button.getStyle().set("border-width", "1px");
		button.getStyle().set("background-color", ColorUtils.getColorHex(setting.getValue()));
		button.addClickListener(e -> dialog.open());
		add(button);
		
		cp.addValueChangeListener(e -> {
			button.getStyle().set("background-color", ColorUtils.getColorHex(cp.getValue()));
			this.onChanged(this);
		});
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			add(getHelpIcon());
		}
	}

	@Override
	public void setValue() {
		((SettingColor) getSetting()).setValue(cp.getValue());
	}

	@Override
	public void updateComponents() {
		cp.setValue(((SettingColor) getSetting()).getValue());
		button.getStyle().set("background", ColorUtils.getColorHex(cp.getValue()));
	}

}
