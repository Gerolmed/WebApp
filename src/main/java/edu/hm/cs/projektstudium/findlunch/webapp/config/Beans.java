package edu.hm.cs.projektstudium.findlunch.webapp.config;

import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.offer.DateValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.offer.ImageValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.offer.OfferValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordReset.PasswordResetPasswordEqualValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordReset.PasswordResetValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile.ProfilePasswordEqualValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile.ProfilePasswordSetValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile.ProfileValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.restaurant.KitchenTypeValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.restaurant.OfferTimesValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.restaurant.OpeningTimesValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.restaurant.RestaurantValidator;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


/*
AppConfig File
BEANS

The @Configuration annotation indicates that the class declares one or more @Bean methods. 
These methods are invoked at runtime by Spring to manage lifecycle of the beans. 
In our case we have defined @Bean for view resolver for JSP view.
 */


/**
 * This class is responsible for defining necessary beans that are handled by Spring application context.
 * These beans can then be injected to classes which need their functionality.
 *
 * The @Configuration annotation indicates that the class declares one or more @Bean methods.
 * These methods are invoked at runtime by Spring to manage lifecycle of the beans.
 */
@Configuration
@EnableSwagger2
public class Beans extends WebMvcConfigurerAdapter{
	
	/**
	 * Password encoder bean, needed for encrypting user passwords.
	 *
	 * @return the b crypt password encoder
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	/**
	 * Bean für Validierung
	 *
	 * @return Validation-Prozessor
	 */
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	/**
	 * Validator bean that is used during Hibernate annotation validation.
	 * This validator bean makes it possible to resolve the error messages defined within the message source files.
	 *
	 * @return the local validator factory bean
	 */
	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
		validatorFactoryBean.setValidationMessageSource(messageSource());
		return validatorFactoryBean;
	}
	/**
	 * Message source which defines the basenames of the files in which messages/content for the homepage are stored.
	 *
	 * @return the resource bundle message source
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setBasenames("messages/content", "messages/errors", "messages/info", "messages/success",
				"messages/bill","messages/email","messages/termsAndConditions","messages/privacy","messages/faq",
				"messages/messages");
		return messageSource;	
	}

	/**
	 * Container customizer. Configures the embedded tomcat (e.g. post size)
	 *
	 * @return the embedded servlet container customizer
	 * @throws Exception the exception
	 */
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() throws Exception {

		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {

				if (container instanceof TomcatEmbeddedServletContainerFactory) {

					TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
					tomcat.addConnectorCustomizers(
							new TomcatConnectorCustomizer() {
								@Override
								public void customize(Connector connector) {
									connector.setMaxPostSize(20000000);//20MB
								}
							}
					);
				}
			}
		};
	}

	/**
	 * Definiert ein Docket für Swaggger
	 * @return  Docket für Swagger
	 */
	@Bean
	public Docket api() {                
	    return new Docket(DocumentationType.SWAGGER_2)
                //Nur die in @ApiResponses angegebenen Codes anzeigen.
                .useDefaultResponseMessages(false)
                .select()
                //Pfad festlegen, in dem nach REST-Controllern gesucht wird.
                .apis(RequestHandlerSelectors.basePackage("edu.hm.cs.projektstudium.findlunch.webapp.controller.rest"))
                //Keine Pfade (z.B. /api/) ausschließen.
                .paths(PathSelectors.any())
                .build()
	            //Metadaten (Überschrift, etc.) hinzufügen.
                .apiInfo(apiInfo());
	}

	/**
	 * Gives information regarding the api.
	 * @return Returns title, description, version, license, licenseUrl, contact and build of the api.
	 */
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("FindLunch REST-Schnittstelle")
                .description("Dokumentation der REST-Schnittstelle von FindLunch")
                .version("1.0.0")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .contact(new Contact("Prof. Dr. Peter Mandl", "www.wirtschaftsinformatik-muenchen.de", "mandl@cs.hm.edu"))
                .build();
	}

	/**
	 * Adds resource handlers to swagger and webjars.
	 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(new Locale("de"));
		return  resolver;
	}

	@Override
	public Validator getValidator() {
		return validator();
	}

	@Bean
	public PasswordResetValidator passwordResetValidator() {
		Set<Validator> springValidators = new HashSet<>();
		springValidators.add(new PasswordResetPasswordEqualValidator());
		PasswordResetValidator productValidator = new PasswordResetValidator();
		productValidator.setSpringValidators(springValidators);
		return productValidator;
	}

	@Bean
	public ProfileValidator profileValidator() {
		Set<Validator> springValidators = new HashSet<>();
		springValidators.add(new ProfilePasswordEqualValidator());
		springValidators.add(new ProfilePasswordSetValidator());
		ProfileValidator profileValidator = new ProfileValidator();
		profileValidator.setSpringValidators(springValidators);
		return profileValidator;
	}

	@Bean
	public RestaurantValidator restaurantValidator() {
		Set<Validator> springValidators = new HashSet<>();
		springValidators.add(new OpeningTimesValidator());
		springValidators.add(new OfferTimesValidator());
		springValidators.add(new KitchenTypeValidator());
		RestaurantValidator restaurantValidator = new RestaurantValidator();
		restaurantValidator.setSpringValidators(springValidators);
		return restaurantValidator;
	}

	@Bean
	public OfferValidator offerValidator() {
		Set<Validator> springValidators = new HashSet<>();
		springValidators.add(new DateValidator());
		springValidators.add(new ImageValidator());
		OfferValidator offerValidator = new OfferValidator();
		offerValidator.setSpringValidators(springValidators);
		return offerValidator;
	}

}
