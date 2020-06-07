package de.lars.remotelightweb.ui.components;

import com.github.appreciated.app.layout.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.components.custom.PaperSlider;

@CssImport("./styles/popup-control-panel-style.css")
public class ControlPanel extends Div {
	private static final long serialVersionUID = 1L;
	
	/** CSS class name prefix */
	private final String CLASS_NAME = "popup-control-panel";
	
	private final RemoteLightCore core;
	private AppLayout appLayout;
	
	protected HorizontalLayout popupContent;
	protected PaperSlider brightness;
	
	public ControlPanel(AppLayout appLayout) {
		core = RemoteLightWeb.getInstance().getCore();
		this.appLayout = appLayout;
		
		popupContent = new HorizontalLayout();
		brightness = new PaperSlider();
		initLayout();
		addEventClickListener();
	}
	
	protected void initLayout() {
		popupContent.setAlignItems(Alignment.CENTER);
		popupContent.setJustifyContentMode(JustifyContentMode.CENTER);
		
		brightness.setMax(100);
		brightness.setPin(true);
		brightness.setValue((int) core.getSettingsManager().getSettingObject("out.brightness").getValue());
		brightness.addValueChangeListener(e -> {
			core.getOutputManager().setBrightness(brightness.getValue());
			core.getSettingsManager().getSettingObject("out.brightness").setValue(brightness.getValue());
		});
		popupContent.add(new Label("Brightness"), brightness);
		
		Div wrapper = new Div(popupContent);
		wrapper.setClassName(CLASS_NAME + "__wrapper");
		
		setClassName(CLASS_NAME + "__root");
		getStyle()
			.set("visibility", "hidden")
			.set("opacity", "0");
		add(wrapper);
	}
	
	protected void addEventClickListener() {
		// add event listener to content area
		appLayout.getElement().getChildren().forEach(element -> {
			// if element is the application content wrapper -> add event listener
			if(element.getAttribute("slot").equals("application-content")) {
				// click event listener
				element.addEventListener("click", event -> {
					// hide popup control window if visible and not clicked
					if(isPopupControlVisible() && !event.getSource().getClassList().contains(CLASS_NAME + "__popupcontrol")) {
						setPopupControl(false);
					}
				});
			}
		});
	}
	
	public void togglePopupControl() {
		setPopupControl(!isPopupControlVisible());
	}
	
	public void setPopupControl(boolean visible) {
		getStyle().set("visibility", visible ? "visible" : "hidden");
		getStyle().set("opacity", visible ? "1" : "0");
	}
	
	public boolean isPopupControlVisible() {
		return !getStyle().get("visibility").equals("hidden");
	}

}
