package com.cv.demo;

import com.cv.demo.db.Projects;
import com.cv.demo.db.Repository.ProjectsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping(value = "" , produces = {"application/json", "text/xml"})
public class PortfolioApplication {

	@Autowired
	private ProjectsRep projectsRep;

	public static void main(String[] args) {
		SpringApplication.run(PortfolioApplication.class, args);
	}

	@RequestMapping("create-project")
	public Projects createProjects() {
		return projectsRep.save(new Projects());
	}

}
