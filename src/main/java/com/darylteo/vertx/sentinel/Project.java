package com.darylteo.vertx.sentinel;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;

import com.darylteo.nio.DirectoryWatchService;
import com.darylteo.nio.PollingDirectoryWatchService;

public class Project implements AutoCloseable {
  /* Instance Variables */
  private ProjectConnection conn = null;
  private PollingDirectoryWatchService factory = null;

  private List<Project> children = new LinkedList<>();

  /* Constructors */
  public Project() throws IOException {
    this("");
  }

  public Project(String path) throws IOException {
    this(path, null);
  }

  public Project(String path, DirectoryWatchService factory) throws IOException {
    if (factory == null) {
      this.factory = factory();
    }

    this.conn = evaluateProject(path);
  }

  /* Private Methods */
  private ProjectConnection evaluateProject(String path) {
    ProjectConnection conn = projectConnection(new File(path));

    // pull the model and evaluate any subprojects
    GradleProject model = (GradleProject) conn.getModel(GradleProject.class);
    System.out.println("Project created at " + path);
    
    for (GradleProject subproject : model.getChildren()) {
      System.out.println(subproject.getPath());
    }

    return conn;
  }

  private ProjectConnection projectConnection(File projectDir) {
    return GradleConnector.newConnector()
      .forProjectDirectory(projectDir)
      .connect();
  }

  private PollingDirectoryWatchService factory() throws IOException {
    // configure the default settings for the factory here
    return new PollingDirectoryWatchService();
  }

  /* AutoCloseable Interface */
  @Override
  public void close() throws Exception {
    if (this.conn != null) {
      this.conn.close();
    }
  }
}
