package com.darylteo.vertx.sentinel

import java.nio.file.Path
import java.nio.file.Paths

import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.Future

import com.darylteo.nio.DirectoryChangedSubscriber
import com.darylteo.nio.DirectoryWatcher
import com.darylteo.nio.PollingDirectoryWatchService
import com.sun.org.apache.xpath.internal.operations.String

public class MainVerticle extends Verticle {
  private Long timerId = null

  private ProjectContainer projects
  private PollingDirectoryWatchService watchService

  @Override
  public def start() {
    def tasks = container.config.tasks ?: ['copyMod']
    long delay = container.config.delay ?: 3000l

    try {
      this.projects = new ProjectContainer("")
      this.watchService = setupWatchService(this.projects, tasks)
      this.timerId = setupTimer(delay)

      System.out.println("Vertx Gradle Sentinel Started")
    } catch (Exception e) {
      e.printStackTrace()
      throw new RuntimeException(e)
    }
  }

  @Override
  public def stop() {
    if (this.timerId != null) {
      vertx.cancelTimer(this.timerId)
    }

    if (this.projects != null) {
      this.projects.close()
    }

    super.stop()
  }

  private PollingDirectoryWatchService setupWatchService(Iterable<Project> projects, def tasks) throws IOException {
    PollingDirectoryWatchService watchService = new PollingDirectoryWatchService()

    projects.each { Project p ->
      Path path = Paths.get(p.path)

      def watcher = watchService.newWatcher(path)
      watcher.include 'src/**'

      watcher.subscribe(new DirectoryChangedSubscriber() {
          @Override
          public void directoryChanged(DirectoryWatcher w, Path entry) {
            p.run(tasks)
          }
        })
    }

    return watchService
  }


  private Long setupTimer(long duration) {
    return vertx.setPeriodic(duration, {
      watchService.poll()
    })
  }
}
