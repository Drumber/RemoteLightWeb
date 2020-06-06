package de.lars.remotelightweb.ui;

import org.tinylog.Logger;

import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.github.appreciated.app.layout.entity.Section;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.components.UpdateDialog;
import de.lars.remotelightweb.ui.components.custom.PaperSlider;
import de.lars.remotelightweb.ui.views.*;
import de.lars.updater.sites.GitHubParser;

@CssImport("./styles/main-layout-style.css")
@Route
@PWA(name = "RemoteLightWeb Control Software", shortName = "RemoteLightWeb")
public class MainLayout extends AppLayoutRouterLayout<LeftLayouts.LeftResponsive> {
	private final String CLASS_NAME = "main-layout";
	
	private static MainLayout instance;
	private IconButton btnDarkmode;
	private IconButton btnPopupControl;
	private Div popupControl;
	
	public MainLayout() {
		instance = this;
		VaadinSession.getCurrent()
		.setErrorHandler((ErrorHandler) errorEvent -> {
			Notification.show("We are sorry, but an internal error occurred");
			Logger.error(errorEvent.getThrowable());
		});
		
		initMenu();
		initPopupMenu();
		
		// toggle dark mode
		String style = ((SettingSelection) RemoteLightWeb.getInstance().getAPI().getSettingsManager().getSettingFromId("ui.style")).getSelected();
		setDarkMode(style.equalsIgnoreCase("Dark"));
		
		// show update notification
		checkAndShowUpdateDialog();
		
		// add event listener to content area
		getAppLayout().getElement().getChildren().forEach(element -> {
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
	
	/**
	 * Get a instance of this class
	 * @return MainLayout instance
	 */
	public static MainLayout getInstance() {
		return instance;
	}
	
	/**
	 * Build AppMenu
	 */
	private void initMenu() {
		btnDarkmode = new IconButton(isDarkModeEnabled() ? VaadinIcon.MOON.create() : VaadinIcon.MOON_O.create(), e -> toggleDarkMode());
		btnPopupControl = new IconButton(VaadinIcon.SUN_O.create(), e -> togglePopupControl());
		
        init(AppLayoutBuilder.get(LeftLayouts.LeftResponsive.class)
                .withTitle("RemoteLightWeb")
                .withSwipeOpen(true)
                .withAppBar(AppBarBuilder.get()
                		.add(btnPopupControl)
                		.add(btnDarkmode)
                		.build())
                .withAppMenu(LeftAppMenuBuilder.get()
                        // \/ add new menu items here \/
                        .add(new LeftNavigationItem("Outputs", VaadinIcon.CONNECT.create(), MainView.class),
                                new LeftNavigationItem("Colors", VaadinIcon.PALETE.create(), ColorsView.class),
                                new LeftNavigationItem("Scenes", VaadinIcon.MAGIC.create(), ScenesView.class),
                                new LeftNavigationItem("Animations", VaadinIcon.PLAY_CIRCLE_O.create(), AnimationsView.class),
                                new LeftNavigationItem("Scripts", VaadinIcon.FILE_CODE.create(), ScriptsView.class),
                                new LeftNavigationItem("MusicSync", VaadinIcon.MUSIC.create(), MusicSyncView.class),
                                new LeftNavigationItem("Settings", VaadinIcon.COGS.create(), SettingsView.class)
                        )
                        // Footer
                        .withStickyFooter()
                        .addToSection(Section.FOOTER,
                        		new LeftNavigationItem("About", VaadinIcon.INFO_CIRCLE.create(), AboutView.class)
                        )
                        .build())
                .build());
	}
	
	private void initPopupMenu() {
		HorizontalLayout popupContent = new HorizontalLayout();
		popupContent.setAlignItems(Alignment.CENTER);
		popupContent.setJustifyContentMode(JustifyContentMode.CENTER);
		
		PaperSlider brightness = new PaperSlider();
		brightness.setMax(100);
		brightness.setPin(true);
		brightness.setValue((int) Main.getInstance().getSettingsManager().getSettingObject("out.brightness").getValue());
		brightness.addValueChangeListener(e -> {
			Main.getInstance().getOutputManager().setBrightness(brightness.getValue());
			Main.getInstance().getSettingsManager().getSettingObject("out.brightness").setValue(brightness.getValue());
		});
		popupContent.add(new Label("Brightness"), brightness);
		
		Div wrapper = new Div(popupContent);
		wrapper.setClassName(CLASS_NAME + "__popupcontrol-wrapper");
		
		popupControl = new Div(wrapper);
		popupControl.setClassName(CLASS_NAME + "__popupcontrol");
		popupControl.getStyle()
			.set("visibility", "hidden")
			.set("opacity", "0");
		
		// add it to the main layout
		getContent().add(popupControl);
	}
	
	public void togglePopupControl() {
		setPopupControl(!isPopupControlVisible());
	}
	
	public void setPopupControl(boolean visible) {
		popupControl.getStyle().set("visibility", visible ? "visible" : "hidden");
		popupControl.getStyle().set("opacity", visible ? "1" : "0");
	}
	
	public boolean isPopupControlVisible() {
		return !popupControl.getStyle().get("visibility").equals("hidden");
	}
	
	/**
	 * Toggle dark Lumo theme
	 */
	public void toggleDarkMode() {
		setDarkMode(!isDarkModeEnabled());
	}
	
	/**
	 * Enable or disable dark Lumo theme
	 * @param enable True: dark theme, False: light theme
	 */
	public void setDarkMode(boolean enable) {
		ThemeList themeList = UI.getCurrent().getElement().getThemeList();
		SettingSelection setting = ((SettingSelection) RemoteLightWeb.getInstance().getAPI().getSettingsManager().getSettingFromId("ui.style"));
		if (!enable) {
			themeList.remove(Lumo.DARK);
			btnDarkmode.setIcon(VaadinIcon.MOON_O.create());
			setting.setSelected("Light");
		} else {
			themeList.add(Lumo.DARK);
			btnDarkmode.setIcon(VaadinIcon.MOON.create());
			setting.setSelected("Dark");
		}
	}
	
	public boolean isDarkModeEnabled() {
		ThemeList themeList = UI.getCurrent().getElement().getThemeList();
		return themeList.contains(Lumo.DARK);
	}
	
	
	public void checkAndShowUpdateDialog() {
		GitHubParser parser = RemoteLightWeb.getInstance().getUpdateUtil().getParser();
		if(parser.isNewVersionAvailable() && RemoteLightWeb.getInstance().isUpdateNotifcCooldownOver()) {
			new UpdateDialog(parser).open();
		}
	}
	
}
