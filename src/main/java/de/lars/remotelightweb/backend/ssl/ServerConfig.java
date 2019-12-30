package de.lars.remotelightweb.backend.ssl;

import java.util.Properties;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
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
    		}
    	};
    }
    
    private int getPropNumber(Properties p, String key, int defaultVal) {
		int val = 443;
		try {
			val = Integer.parseInt(p.getProperty(key, String.valueOf(defaultVal)));
		} catch(NumberFormatException e) {}
		return val;
    }
	
}
