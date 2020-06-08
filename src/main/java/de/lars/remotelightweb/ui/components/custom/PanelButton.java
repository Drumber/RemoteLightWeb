package de.lars.remotelightweb.ui.components.custom;

import com.github.appreciated.card.RippleClickableCard;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;

@CssImport("./styles/panel-button-style.css")
public class PanelButton extends RippleClickableCard {
	private static final long serialVersionUID = -4708398229887690740L;
	
	public final String CLASS_NAME = "panel-button";
	
	private Icon icon;
	private Label text;
	private Div container;
	
	public PanelButton(String text) {
		this(text, null);
	}
	
	public PanelButton(Icon icon) {
		this(null, icon);
	}
	
	public PanelButton(String text, Icon icon) {
		if(text != null)
			this.text = new Label(text);
		this.icon = icon;
		
		container = new Div();
		container.addClassName(CLASS_NAME + "__panel-container");
		add(container);
		
		setElevation(0);
		setElevationOnActionEnabled(true);
		setBackground("var(--_lumo-button-background-color, var(--lumo-contrast-5pct))");
		addClassName(CLASS_NAME + "__panel");
		
		initLayout();
	}
	
	protected void initLayout() {
		container.removeAll();
		if(icon != null)
			container.add(icon);
		if(text != null)
			container.add(text);
	}
	
	public void setBorder(String style, int width) {
		getContent().getStyle()
			.set("border-color", "var(--lumo-primary-color)")
			.set("border-style", style)
			.set("border-width", width + "px");
	}

}
