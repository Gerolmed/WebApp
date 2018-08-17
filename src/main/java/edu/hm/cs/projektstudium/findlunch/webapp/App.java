package edu.hm.cs.projektstudium.findlunch.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This is the start up class for the WebApp.
 */
@SpringBootApplication
@EnableScheduling
public class App extends SpringBootServletInitializer
{
	/**
	 * This method is needed when deployed to a standalone tomcat.
	 * It sets up the application and necessary components.
	 */
	 @Override
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	    return application.sources(App.class);
	 }
    
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
    	
    }
}
