package com.cv.demo;

import com.cv.demo.Services.ProjectService;
import com.cv.demo.db.Repository.ProjectsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping(value = "" , produces = {"application/json", "text/xml"})
public class PortfolioApplication {

	@Autowired
	private ProjectsRep projectsRep;
	@Autowired
	private ProjectService projectService;

	public static void main(String[] args) {
		SpringApplication.run(PortfolioApplication.class, args);
	}

	/*@RequestMapping("/create-project")
	public Projects createProjects() {
		return projectsRep.save(new Projects());
	}

	@PostMapping (value = "/create-project/{name}")
	public Projects getAlertDetails(@PathVariable(value = "name") String name) {
		return projectsRep.save(new Projects(name));
	}

	@PostMapping (value = "/create-project/{name}/{comment}")
	public Projects getAlertDetails(@PathVariable(value = "name") String name , @PathVariable(value = "comment") String comment) {
		return projectsRep.save(new Projects(name , comment.replaceAll("-" , " ")));
	}*/

}
