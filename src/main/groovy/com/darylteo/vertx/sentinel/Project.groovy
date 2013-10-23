package com.darylteo.vertx.sentinel

import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.ResultHandler
import org.gradle.tooling.model.GradleProject

public class Project {
  public GradleProject gProject
  public BuildLauncher gBuilder

  public String getPath() {
    def path = gProject.path
    path = path.length() > 1 ? path[1..-1] : ''

    return path.replace(':', File.separator)
  }

  public Project(GradleProject project, BuildLauncher builder) {
    println "Project Added: ${project.name}"
    this.gProject = project
    this.gBuilder = builder
  }

  public void run(String ... taskNames) {
    run(taskNames)
  }

  public void run(Collection taskNames) {
    def tasks = taskNames.collect { name ->
      this.gProject.tasks.find { task ->
        task.name == name
      }
    }
    
    tasks = tasks.findAll { task ->
      task // not null
    }
    
    this.gBuilder.forTasks(tasks).run({} as ResultHandler)
  }
}