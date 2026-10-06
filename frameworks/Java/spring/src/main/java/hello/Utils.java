package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.*;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebHandler;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.ServerResponse.ok;

abstract public class Utils {

	private static final int MIN_WORLD_NUMBER = 1;
	private static final int MAX_WORLD_NUMBER_PLUS_ONE = 10_001;

	public static int randomWorldNumber() {
		return ThreadLocalRandom.current().nextInt(MIN_WORLD_NUMBER, MAX_WORLD_NUMBER_PLUS_ONE);
	}

	public static IntStream randomWorldNumbers() {
		return ThreadLocalRandom.current().ints(MIN_WORLD_NUMBER, MAX_WORLD_NUMBER_PLUS_ONE).distinct();
	}



	@SpringBootApplication
	@RestController
	public class DemoApplication {

		@GetMapping("/")
		public String home() {
			return "Hello";
		}

		public static void main(String[] args) {
			SpringApplication.run(DemoApplication.class, args);
		}
	}

	@SpringBootConfiguration
	@Import({ HttpMessageConvertersAutoConfiguration.class, JacksonAutoConfiguration.class,
			JmxAutoConfiguration.class, ValidationAutoConfiguration.class,
			RestTemplateAutoConfiguration.class, DispatcherServletAutoConfiguration.class,
			HttpEncodingAutoConfiguration.class, MultipartAutoConfiguration.class,
			ServletWebServerFactoryAutoConfiguration.class, WebMvcAutoConfiguration.class,
			ErrorMvcAutoConfiguration.class, WebSocketServletAutoConfiguration.class,
			ConfigurationPropertiesAutoConfiguration.class,
			PropertyPlaceholderAutoConfiguration.class, ProjectInfoAutoConfiguration.class })
	@RestController
	public class SlimApplication {

		@GetMapping("/")
		public String home() {
			return "Hello";
		}

		public static void main(String[] args) {
			SpringApplication.run(DemoApplication.class, args);
		}
	}

	public class MicroApplication {

		public static void main(String[] args) throws Exception {
			GenericApplicationContext context = new GenericApplicationContext();
			context.registerBean(RouterFunction.class, () -> RouterFunctions.route(GET("/"),
					request -> ok().body(Mono.just("Hello"), String.class)));
			context.registerBean("webHandler", WebHandler.class, () -> RouterFunctions.toWebHandler(context.getBean(RouterFunction.class)));
			context.refresh();
			new SpringApplicationBuilder(context).run();
		}
	}
}
