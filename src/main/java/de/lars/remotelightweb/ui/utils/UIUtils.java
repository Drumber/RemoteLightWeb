package de.lars.remotelightweb.ui.utils;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;

public class UIUtils {

	public static Button createButton(String text, VaadinIcon icon, ButtonVariant... variants) {
		Button button = new Button(text, icon.create());
		button.addThemeVariants(variants);
		return button;
	}

	public static Button createButton(VaadinIcon icon, ButtonVariant... variants) {
		Button button = new Button(icon.create());
		button.addThemeVariants(variants);
		return button;
	}
	
	public static Button createButton(String text, ButtonVariant... variants) {
		Button button = new Button(text);
		button.addThemeVariants(variants);
		return button;
	}
	
	/**
	 * Create button with margin
	 * @param text Button text
	 * @param marginArg (e.g. '5px 5px')
	 */
	public static Button createButton(String text, String marginArg) {
		Button button = new Button(text);
		button.getStyle().set("margin", marginArg);
		return button;
	}
	
	public static Button createButton(String text, String marginArg, ComponentEventListener<ClickEvent<Button>> listener) {
		Button button = createButton(text, marginArg);
		button.addClickListener(listener);
		return button;
	}
	
	public static Button addMargin(Button button, String marginArg) {
		button.getStyle().set("margin", marginArg);
		return button;
	}

}
