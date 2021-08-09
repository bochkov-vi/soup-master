package ru.itain.soup;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableJpaRepositories(basePackages = {
		"ru.itain.soup.common.repository",
		"ru.itain.soup.tool.simulator_editor.repository",
		"ru.itain.soup.tool.umm_editor.repository",
		"ru.itain.soup.tool.im_editor.repository",
		"ru.itain.soup.syllabus.dto.repository",
})
@EnableScheduling
public class Application {
	static {
		System.setProperty(AvailableSettings.HBM2DLL_CREATE_SCHEMAS, "true");
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
