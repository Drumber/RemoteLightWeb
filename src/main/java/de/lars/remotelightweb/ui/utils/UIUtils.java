package de.lars.remotelightweb.ui.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class UIUtils {

	public static Button createButton(String text, VaadinIcon icon, ButtonVariant... variants) {
		Icon i = new Icon(icon);
		i.getElement().setAttribute("slot", "prefix");
		Button button = new Button(text, i);
		button.addThemeVariants(variants);
		return button;
	}

	public static Button createButton(VaadinIcon icon, ButtonVariant... variants) {
		Button button = new Button(new Icon(icon));
		button.addThemeVariants(variants);
		return button;
	}
	
	public static Button createButton(String text, ButtonVariant... variants) {
		Button button = new Button(text);
		button.addThemeVariants(variants);
		return button;
	}

	public static void setAriaLabel(String value, Component... components) {
		for (Component component : components) {
			component.getElement().setAttribute("aria-label", value);
		}
	}

}
