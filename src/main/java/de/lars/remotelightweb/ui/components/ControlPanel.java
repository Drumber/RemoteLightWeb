package de.lars.remotelightweb.ui.components;

import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.github.appreciated.app.layout.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.backend.utils.UpdateUtil;
import de.lars.remotelightweb.ui.MainLayout;
import de.lars.remotelightweb.ui.components.custom.PaperSlider;
import de.lars.remotelightweb.ui.components.dialogs.ShutdownDialog;
import de.lars.remotelightweb.ui.components.dialogs.UpdateDialog;

@CssImport("./styles/popup-control-panel-style.css")
public class ControlPanel extends Div {
	private static final long serialVersionUID = 1L;
	
	/** CSS class name prefix */
	private final String CLASS_NAME = "popup-control-panel";
	
	private final RemoteLightCore core;
	MainLayout main;
	private AppLayout appLayout;
	
	protected VerticalLayout popupContent;
	protected IconButton btnBrightness;
	protected IconButton btnDarkMode;
	protected IconButton btnUpdate;
	protected IconButton btnShutdown;
	protected PaperSlider sliderBrightness;
	
	public ControlPanel(AppLayout appLayout) {
		core = RemoteLightWeb.getInstance().getCore();
		main = MainLayout.getInstance();
		this.appLayout = appLayout;
		
		initComponents();
		initLayout();
		addEventClickListener();
		addButtonListeners();
		updateValues();
	}
	
	protected void initComponents() {
		popupContent = new VerticalLayout();
		popupContent.setAlignItems(Alignment.CENTER);
		popupContent.setJustifyContentMode(JustifyContentMode.CENTER);
		popupContent.setPadding(false);
		
		btnBrightness	= new IconButton(VaadinIcon.SUN_O.create());
		btnDarkMode		= new IconButton(VaadinIcon.MOON_O.create());
		btnUpdate		= new IconButton(VaadinIcon.REFRESH.create());
		btnShutdown		= new IconButton(VaadinIcon.POWER_OFF.create());
		
		btnBrightness.getElement().setProperty("title", "Change Brightness");
		btnDarkMode.getElement().setProperty("title", "Toggle DarkMode");
		btnUpdate.getElement().setProperty("title", "Check for Updates");
		btnShutdown.getElement().setProperty("title", "Shutdown");
		
		sliderBrightness = new PaperSlider();
		sliderBrightness.setMax(100);
		sliderBrightness.setPin(true);
		sliderBrightness.addValueChangeListener(e -> {
			core.getOutputManager().setBrightness(sliderBrightness.getValue());
			core.getSettingsManager().getSettingObject("out.brightness").setValue(sliderBrightness.getValue());
		});
		
		setClassName(CLASS_NAME + "__root");
		getStyle()
			.set("visibility", "hidden")
			.set("opacity", "0");
	}
	
	protected void initLayout() {
		Div divButtons = new Div(btnBrightness, btnDarkMode, btnUpdate, btnShutdown);
		divButtons.setClassName(CLASS_NAME + "__button-container");
		
		Div divBrighness = new Div(new Label("Brightness"), sliderBrightness);
		divBrighness.setClassName(CLASS_NAME + "__brightness-container");
		
		popupContent.add(divButtons, divBrighness);
		
		Div wrapper = new Div(popupContent);
		wrapper.setClassName(CLASS_NAME + "__wrapper");
		
		add(wrapper);
	}
	
	public void updateValues() {
		btnDarkMode.setIcon(main.isDarkModeEnabled() ? VaadinIcon.ADJUST.create() : VaadinIcon.MOON_O.create());
		sliderBrightness.setValue((int) core.getSettingsManager().getSettingObject("out.brightness").getValue());
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
	
	protected void addButtonListeners() {
		btnBrightness.addClickListener(e -> {
			changeBrightness();
			updateValues();
		});
		btnDarkMode.addClickListener(e -> {
			main.toggleDarkMode();
			updateValues();
		});
		btnUpdate.addClickListener(e -> {
			UpdateUtil updater = RemoteLightWeb.getInstance().getUpdateUtil();
			updater.check();
			new UpdateDialog(updater.getParser()).open();
		});
		btnShutdown.addClickListener(e -> {
			new ShutdownDialog().open();
		});
	}
	
	public void togglePopupControl() {
		setPopupControl(!isPopupControlVisible());
	}
	
	public void setPopupControl(boolean visible) {
		String transClassVisible = CLASS_NAME + "__root-visible";
		String transClassHidden = CLASS_NAME + "__root-hidden";
		removeClassNames(transClassVisible, transClassHidden);
		addClassName(visible ? transClassVisible : transClassHidden);
		getStyle().set("visibility", visible ? "visible" : "hidden");
		getStyle().set("opacity", visible ? "1" : "0");
		getStyle().set("max-height", visible ? "500px" : "0px");
	}
	
	public boolean isPopupControlVisible() {
		return !getStyle().get("visibility").equals("hidden");
	}
	
	
	protected void changeBrightness() {
		SettingObject bsetting = core.getSettingsManager().getSettingObject("out.brightness");
		int brightness = (int) bsetting.getValue();
		int newBright;
		if(brightness % 25 == 0) {
			int multiplier = brightness / 25 + 1;
			if(multiplier > 4)
				newBright = 0;
			else
				newBright = 25 * multiplier;
		} else {
			newBright = 100;
		}
		bsetting.setValue(newBright);
		core.getOutputManager().setBrightness(newBright);
	}

}
