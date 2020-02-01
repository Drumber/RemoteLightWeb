package de.lars.remotelightweb.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

import org.tinylog.Logger;

import de.lars.remotelightweb.RemoteLightWeb;

public class ConfigFile {
	
	public final static String CONFIG_FILE_NAME = "config.properties";
	public final static String CONFIG_CLASSPATH = "config/config.properties";
	private Properties prop;
	
	public ConfigFile() {
		// copy config file from classpath if not exists
		File config = new File(RemoteLightWeb.ROOT_FOLDER_NAME + File.separator + CONFIG_FILE_NAME);
		if(!config.exists()) {
			try {
				config.getParentFile().mkdirs();
				InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_CLASSPATH);
				Files.copy(input, new File(RemoteLightWeb.ROOT_FOLDER_NAME + File.separator + CONFIG_FILE_NAME).toPath());
			} catch (IOException e) {
				Logger.error(e, "Could not copy config from classpath.");
			}
		}
		// load properties
		if(new File(RemoteLightWeb.ROOT_FOLDER_NAME + File.separator + CONFIG_FILE_NAME).exists()) {
			try (InputStream input = new FileInputStream(RemoteLightWeb.ROOT_FOLDER_NAME + File.separator + CONFIG_FILE_NAME)) {
				
				prop = new Properties();
				prop.load(input);
				
			} catch (IOException e) {
				Logger.error(e, "Could not load properties.");
			}
		}
	}
	
	public boolean isLoaded() {
		return prop != null;
	}
	
	public Properties getProperties() {
		return prop;
	}

}
