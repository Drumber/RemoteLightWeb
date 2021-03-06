package de.lars.remotelightweb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.lang.LangUtil;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.settings.types.SettingString;
import de.lars.remotelightcore.utils.DirectoryUtil;
import de.lars.remotelightweb.backend.ConfigFile;
import de.lars.remotelightweb.backend.utils.UpdateUtil;
import de.lars.remotelightweb.ui.utils.NotificationHandler;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class RemoteLightWeb extends SpringBootServletInitializer {

	public static final String VERSION = getVersion();
	public final static String ROOT_FOLDER_NAME = "RemoteLightWeb";
	
	private static ConfigFile config;
	private static ConfigurableApplicationContext context;
	private static RemoteLightWeb instance;
	private RemoteLightCore remoteLightCore;
	private UpdateUtil updateUtil;
	private NotificationHandler notificationHandler;
	
	private static long lastUpdateNotification;
	private boolean closing;
	
    public static void main(String[] args) {
		config = new ConfigFile();
        // run web server
        context = SpringApplication.run(RemoteLightWeb.class, args);
    }
    
    public RemoteLightWeb() {
		instance = this;
	}
    
    @EventListener(ApplicationReadyEvent.class)
    public void afterStartUp() {
		// set up RemoteLightCore
    	DirectoryUtil.setRootPath(Paths.get(".").toAbsolutePath().normalize().toString()); // directory where the jar was executed
		DirectoryUtil.DATA_DIR_NAME = ROOT_FOLDER_NAME;
		DirectoryUtil.RESOURCES_CLASSPATH = "/BOOT-INF/classes/resources/";
		MusicSyncManager.initNativeSound = false;
		
		// initialize RemoteLightCore
		remoteLightCore = new RemoteLightCore(new String[0], true);
		updateUtil = new UpdateUtil(VERSION);
		setup();	// initial some settings and check for updates
		
		// init and register notification handler
		notificationHandler = new NotificationHandler(remoteLightCore.getNotificationManager());
    }
    
    
    /**
     * Exit Spring Boot and the whole application
     */
    public static void exitApplication() {
    	int exitcode = SpringApplication.exit(context, () -> 0);
    	System.exit(exitcode);
    }
    
    @PreDestroy
    public void onExit() {	// shutdown hook
    	if(closing)
    		return;
    	closing = true;
		System.out.println("###################################\n# Shutting down...\n###################################");
		getInstance().getCore().close(false);
    }
    
    
    /**
     * Get the Main (this) class
     * @return RemoteLightWeb
     */
    public static RemoteLightWeb getInstance() {
    	return instance;
    }
    /**
     * 
     * @return RemoteLightAPI
     */
    public RemoteLightCore getCore() {
    	return remoteLightCore;
    }
    
    public static ConfigFile getConfig() {
    	return config;
    }
    
    public NotificationHandler getNotificationHandler() {
    	return notificationHandler;
    }
    
    
    // adapted from https://stackoverflow.com/a/1273432
    private static String getVersion() {
    	InputStream resStream = RemoteLightWeb.class.getClass().getResourceAsStream("/META-INF/maven/de.lars.remotelightweb/remotelightweb/pom.properties");
    	Properties prop = new Properties();
    	try {
			prop.load(resStream);
			return prop.getProperty("version", "?");
		} catch (Exception e) {
		}
		return "?";
    }
    
    public UpdateUtil getUpdateUtil() {
    	return updateUtil;
    }
    
    public boolean isUpdateNotifcCooldownOver() {
    	long differenz = System.currentTimeMillis() - lastUpdateNotification;
    	int hours = (int) ((differenz / (1000*60*60)) % 24);
    	if(hours > 4 || lastUpdateNotification == -1) {	// show notification every 4 hours or if not set (-1)
    		lastUpdateNotification = System.currentTimeMillis();
    		return true;
    	}
    	return false;
    }
    
    private void setup() {
    	SettingsManager s = getCore().getSettingsManager();
    	// add RemoteLightWeb updater setting
    	s.addSetting(new SettingBoolean("rlweb.updater", "Updater", SettingCategory.General, "Check for updates at startup", true));
    	
		File jarDir = new ApplicationHome(RemoteLightWeb.class).getSource();
    	String runCommand = "java -jar " + jarDir.getAbsolutePath();
    	String shutdownCommand = "shutdown.exe -s -t 0";
    	if(System.getProperty("os.name").toLowerCase().contains("linux")) {
    		runCommand = "nohup " + runCommand + " &";
    		shutdownCommand = "shutdown -h now";
    	}
    	
    	// register settings
    	s.addSetting(new SettingSelection("ui.language", "Language", SettingCategory.General, "UI Language", LangUtil.langCodeToName(LangUtil.LANGUAGES), "English", Model.ComboBox));
    	s.addSetting(new SettingSelection("ui.style", "Style", SettingCategory.General, "UI Color Theme", new String[] {"Light", "Dark"}, "Light", Model.ComboBox));
    	s.addSetting(new SettingString("rlweb.runcmd", "Run command after update", SettingCategory.Others, "This command is executed after an update", runCommand));
    	s.addSetting(new SettingString("rlweb.shutdowncmd", "Custom shutdown command", SettingCategory.Others, "Custom shutdown command for shutting down system", shutdownCommand));
    	s.addSetting(new SettingBoolean("rlweb.updater.screen", "Start updater in new screen (only Linux)", SettingCategory.Others, "Start updater in a new screen (needs screen installed)", false));
    	
    	// set default locale
		String langCode = ((SettingSelection) s.getSettingFromId("ui.language")).getSelected();
		i18n.setLocale(langCode);
    	
    	// check for updates
    	if(((SettingBoolean) s.getSettingFromId("rlweb.updater")).getValue() && !VERSION.equals("?")) {
    		updateUtil.check();
    	}
    	lastUpdateNotification = -1;
    	
    	// copy lua example files
    	File luaDestination = new File(DirectoryUtil.getLuaPath());
    	if(!luaDestination.exists())
    		luaDestination.mkdirs();
    	try {
			DirectoryUtil.copyZipFromJar("/resources/lua_examples/lua_examples.zip", luaDestination);
		} catch (IOException e) {
			Logger.error(e, "Could not copy Lua example files from jar file.");
		}
    }

}
