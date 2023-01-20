package com.cv.demo;

import com.cv.demo.db.Project;
import com.cv.demo.db.Repository.ProjectRepository;
import com.cv.demo.db.Repository.SubjectRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping(value = "" , produces = {"application/json", "text/xml"})
public class PortfolioApplication {

	public static void main(String[] args) {
		/*ConfigurableApplicationContext configurableApplicationContext =*/
		SpringApplication.run(PortfolioApplication.class, args);
		/*ProjectRepository projectRepository = configurableApplicationContext.getBean(ProjectRepository.class);
		SubjectRepository subjectRepository = configurableApplicationContext.getBean(SubjectRepository.class);*/
	}

}
