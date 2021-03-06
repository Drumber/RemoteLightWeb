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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.components.ControlPanel;
import de.lars.remotelightweb.ui.components.dialogs.UpdateDialog;
import de.lars.remotelightweb.ui.components.icons.MenuIcons;
import de.lars.remotelightweb.ui.views.*;
import de.lars.updater.sites.GitHubParser;

@Route
@PWA(name = "RemoteLightWeb Control Software", shortName = "RemoteLight",
	description = "LED Control Software for addressable LED strips.",
	themeColor = "#2C3A4C")
public class MainLayout extends AppLayoutRouterLayout<LeftLayouts.LeftResponsive> {
	
	private RemoteLightCore core;
	private static MainLayout instance;
	private IconButton btnPopupControl;
	private ControlPanel controlPanel;
	
	public MainLayout() {
		instance = this;
		core = RemoteLightWeb.getInstance().getCore();
		
		VaadinSession.getCurrent()
		.setErrorHandler((ErrorHandler) errorEvent -> {
			Notification.show("We are sorry, but an internal error occurred");
			Logger.error(errorEvent.getThrowable());
		});
		
		// toggle dark mode
		String style = ((SettingSelection) core.getSettingsManager().getSettingFromId("ui.style")).getSelected();
		setDarkMode(style.equalsIgnoreCase("Dark"));
		
		initMenu();
		initPopupMenu();
		
		// show update notification
		checkAndShowUpdateDialog();
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
		btnPopupControl = new IconButton(MenuIcons.CONTROL.create(24), e -> controlPanel.togglePopupControl());
		btnPopupControl.getElement().setProperty("title", "Control Panel");
		
        init(AppLayoutBuilder.get(LeftLayouts.LeftResponsive.class)
                .withTitle("RemoteLightWeb")
                .withSwipeOpen(true)
                .withAppBar(AppBarBuilder.get()
                		.add(btnPopupControl)
                		.build())
                .withAppMenu(LeftAppMenuBuilder.get()
                        // \/ add new menu items here \/
                        .add(new LeftNavigationItem("Outputs", MenuIcons.OUTPUTS.create(), MainView.class),
                                new LeftNavigationItem("Colors", MenuIcons.COLOR_PALETTE.create(), ColorsView.class),
                                new LeftNavigationItem("Scenes", MenuIcons.SCENE.create(), ScenesView.class),
                                new LeftNavigationItem("Animations", MenuIcons.ANIMATION.create(), AnimationsView.class),
                                new LeftNavigationItem("Scripts", MenuIcons.SCRIPTS.create(), ScriptsView.class),
                                new LeftNavigationItem("MusicSync", MenuIcons.MUSICSYMC.create(), MusicSyncView.class),
                                new LeftNavigationItem("Settings", MenuIcons.SETTINGS.create(), SettingsView.class)
                        )
                        // Footer
                        .withStickyFooter()
                        .addToSection(Section.FOOTER,
                        		new LeftNavigationItem("About", MenuIcons.ABOUT.create(), AboutView.class)
                        )
                        .build())
                .build());
        // fix title styling issue
        ((Span) getAppLayout().getTitleComponent()).getStyle().set("color", "var(--lumo-header-text-color)");
	}
	
	private void initPopupMenu() {
		controlPanel = new ControlPanel(getAppLayout());
		// add it to the main layout
		getContent().add(controlPanel);
	}
	
	public ControlPanel getControlPanel() {
		return controlPanel;
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
		SettingSelection setting = ((SettingSelection) RemoteLightWeb.getInstance().getCore().getSettingsManager().getSettingFromId("ui.style"));
		if (!enable) {
			themeList.remove(Lumo.DARK);
			setting.setSelected("Light");
		} else {
			themeList.add(Lumo.DARK);
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
