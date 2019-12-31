package de.lars.remotelightweb;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import de.lars.remotelightclient.api.RemoteLightAPI;
import de.lars.remotelightweb.backend.ConfigFile;

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
	private RemoteLightAPI rlApi;
	private boolean closing;
	
    public static void main(String[] args) {
		config = new ConfigFile();
        // run web server
        context = SpringApplication.run(RemoteLightWeb.class, args);
    }
    
    public RemoteLightWeb() {
		instance = this;
		// set up RemoteLightAPI
		RemoteLightAPI.setRootDirectory(Paths.get(".").toAbsolutePath().normalize().toString());	// directory where the jar was executed
		RemoteLightAPI.setRootName(ROOT_FOLDER_NAME);
		rlApi = new RemoteLightAPI();
	}
    
    
    /**
     * Exit Spring Boot and the whole application
     */
    public static void exitApplication() {
    	SpringApplication.exit(context, () -> 0);
    }
    
    @PreDestroy
    public void onExit() {	// shutdown hook
    	if(closing)
    		return;
    	closing = true;
		System.out.println("###################################\n# Shutting down...\n###################################");
		getInstance().getAPI().close(false);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
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
    public RemoteLightAPI getAPI() {
    	return rlApi;
    }
    
    public static ConfigFile getConfig() {
    	return config;
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

}
