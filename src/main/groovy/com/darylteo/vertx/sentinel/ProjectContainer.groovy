package com.darylteo.vertx.sentinel

import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.GradleProject

public class ProjectContainer implements AutoCloseable, Iterable<Project> {
  private ProjectConnection conn

  private List projects = []

  public ProjectContainer(String rootPath) {
    this.conn = GradleConnector.newConnector().forProjectDirectory(new File("")).connect()
    GradleProject project = conn.getModel(GradleProject.class)
    BuildLauncher launcher = conn.newBuild()

    addProject(project, launcher)
  }

  @Override
  public void close() {
    this.conn.close()
  }

  @Override
  public Iterator<Project> iterator() {
    return projects.iterator()
  }

  private def addProject(GradleProject project, BuildLauncher launcher) {
    projects << new Project(project, launcher)

    this.projects.addAll(project.children.collect({ child ->
      new Project(child, launcher)
    }))
  }
}
