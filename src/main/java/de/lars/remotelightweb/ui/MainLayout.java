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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.views.AboutView;
import de.lars.remotelightweb.ui.views.AnimationsView;
import de.lars.remotelightweb.ui.views.ColorsView;
import de.lars.remotelightweb.ui.views.MainView;
import de.lars.remotelightweb.ui.views.MusicSyncView;
import de.lars.remotelightweb.ui.views.ScenesView;
import de.lars.remotelightweb.ui.views.ScriptsView;
import de.lars.remotelightweb.ui.views.SettingsView;

@Route
@PWA(name = "RemoteLightWeb Control Software", shortName = "RemoteLightWeb")
public class MainLayout extends AppLayoutRouterLayout<LeftLayouts.LeftResponsive> {
	
	private static MainLayout instance;
	private IconButton btnDarkmode;
	
	public MainLayout() {
		instance = this;
		VaadinSession.getCurrent()
		.setErrorHandler((ErrorHandler) errorEvent -> {
			Notification.show("We are sorry, but an internal error occurred");
			Logger.error(errorEvent.getThrowable());
		});
		
		//initContainer();
		initMenu();
		
		// toggle dark mode
		String style = ((SettingSelection) RemoteLightWeb.getInstance().getAPI().getSettingsManager().getSettingFromId("ui.style")).getSelected();
		setDarkMode(style.equalsIgnoreCase("Dark"));
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
		btnDarkmode = new IconButton(isDarkModeEnabled() ? VaadinIcon.SUN_O.create() : VaadinIcon.MOON_O.create(), e -> toggleDarkMode());
		
        init(AppLayoutBuilder.get(LeftLayouts.LeftResponsive.class)
                .withTitle("RemoteLightWeb")
                .withSwipeOpen(true)
                .withAppBar(AppBarBuilder.get()
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
			btnDarkmode.setIcon(VaadinIcon.SUN_O.create());
			setting.setSelected("Dark");
		}
	}
	
	public boolean isDarkModeEnabled() {
		ThemeList themeList = UI.getCurrent().getElement().getThemeList();
		return themeList.contains(Lumo.DARK);
	}
	
}
