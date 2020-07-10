package de.lars.remotelightweb.backend.ssl;

import java.util.Properties;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.lars.remotelightweb.RemoteLightWeb;

@Configuration
public class ServerConfig {

    @Bean
    public ServletWebServerFactory servletContainer(@Value("${server.http.port}") int httpPort) {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        if(RemoteLightWeb.getConfig().isLoaded()) {
        	Properties prop = RemoteLightWeb.getConfig().getProperties();
        	httpPort = getPropNumber(prop, "http-port", httpPort);
        }
    	connector.setPort(httpPort);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }
    
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
    	return factory -> {
    		if(RemoteLightWeb.getConfig().isLoaded()) {
    			Properties prop = RemoteLightWeb.getConfig().getProperties();
    			
    			factory.setContextPath(prop.getProperty("context-path", ""));
    			factory.setPort(getPropNumber(prop, "https-port", 443));
    			
    			// configure custom user certificate (if enabled in config.properties)
    			Ssl ssl = new Ssl();
    			boolean enableCustomSsl = configureCustomCertificate(ssl, prop);
    			if(enableCustomSsl) {
    				factory.setSsl(ssl);
    			}
    		}
    	};
    }
    
    /**
     * Configure {@link org.springframework.boot.web.server.Ssl} instance to use
     * user specified custom certificate
     * @return true on successful configuration, false on failure
     */
    private boolean configureCustomCertificate(Ssl ssl, Properties prop) {
    	try {
    		if(!Boolean.parseBoolean(prop.getProperty("enable-custom-ssl", "false")))
    			return false;
    		
    		final String keystorePath	= prop.getProperty("ssl-keystore-path");
    		final String keystorePassw	= prop.getProperty("ssl-keystore-password");
    		final String keystoreType	= prop.getProperty("ssl-keystore-type");
    		final String keystoreAlias	= prop.getProperty("ssl-keystore-alias");
    		
    		ssl.setEnabled(true);
    		ssl.setKeyStore(keystorePath);
    		ssl.setKeyPassword(keystorePassw);
    		ssl.setKeyStoreType(keystoreType);
    		ssl.setKeyAlias(keystoreAlias);
    		return true;
    		
    	} catch(NumberFormatException e) {
    		System.err.println("Could not configure custom SSL certificate. Invalid properties value(s). " + e.getMessage());
    	}
    	return false;
    }
    
    private int getPropNumber(Properties p, String key, int defaultVal) {
		int val = 443;
		try {
			val = Integer.parseInt(p.getProperty(key, String.valueOf(defaultVal)));
		} catch(NumberFormatException e) {
			System.err.println("Invalid properties value: " + e.getMessage());
		}
		return val;
    }
	
}
