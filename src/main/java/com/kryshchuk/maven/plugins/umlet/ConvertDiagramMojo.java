package com.kryshchuk.maven.plugins.umlet;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.baselet.diagram.DiagramHandler;

/**
 * Goal which converts the UMLet uxf diagram into the graphics format.
 * 
 */
@Mojo(name = "convert", defaultPhase = LifecyclePhase.PRE_SITE)
public class ConvertDiagramMojo extends AbstractMojo {

  private static final FileFilter DIAGRAM_FILEFILTER = new FileFilter() {

    public boolean accept(final File f) {
      if (f.isDirectory()) {
        return true;
      }
      if (f.getName().toLowerCase().endsWith(".uxf")) {
        return true;
      }
      return false;
    }

  };

  /**
   * Target directory to store the converted images.
   */
  @Parameter(defaultValue = "${project.reporting.outputDirectory}/uml", property = "umlet.targetDir", required = true)
  private File outputDirectory;

  /**
   * Target directory to store the converted images.
   */
  @Parameter(defaultValue = "${basedir}/src/site/resources/uml", property = "umlet.sourceDir", required = true)
  private File diagramDirectory;

  /**
   * Convert diagram to this image format.
   */
  @Parameter(defaultValue = "png", property = "umlet.format", required = true)
  private String format;

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().debug("Diagram directory: " + diagramDirectory);
    getLog().debug("Output directory: " + outputDirectory);
    getLog().debug("Format: " + format);
    if (diagramDirectory.isDirectory()) {
      processDirectory(diagramDirectory);
    } else {
      getLog().warn("Diagram directory does not exist: " + diagramDirectory);
    }
  }

  private void processDirectory(final File dir) throws MojoExecutionException, MojoFailureException {
    final File[] files = diagramDirectory.listFiles(DIAGRAM_FILEFILTER);
    for (final File f : files) {
      if (f.isDirectory()) {
        processDirectory(f);
      } else {
        processDiagram(f);
      }
    }
  }

  private void processDiagram(final File diagram) throws MojoExecutionException, MojoFailureException {
    final String diagramFilepath = diagram.getAbsolutePath();
    final String sourceDirpath = diagramDirectory.getAbsolutePath();
    if (diagramFilepath.startsWith(sourceDirpath)) {
      final String relPath = diagramFilepath.substring(sourceDirpath.length());
      final int extIndex = relPath.lastIndexOf('.');
      final String targetRel = relPath.substring(0, extIndex) + "." + format;
      getLog().debug("Found source diagram " + targetRel);
      final File imageFile = new File(outputDirectory, targetRel);
      if (!imageFile.isFile() || diagram.lastModified() > imageFile.lastModified()) {
        checkOutputDirectory(imageFile.getParentFile());
        final DiagramHandler diagramHandler = new DiagramHandler(diagram);
        try {
          getLog().info("Converting " + diagram);
          diagramHandler.getFileHandler().doExportAs(format, imageFile);
        } catch (final IOException e) {
          getLog().error("Failed to convert diagram", e);
          throw new MojoFailureException(this, "Cannot convert diagram " + diagram.getAbsolutePath(), e.getMessage());
        }
      } else {
        getLog().debug("Diagram image is up to date: " + imageFile.getAbsolutePath());
      }
    } else {
      throw new MojoExecutionException("Could not determine target image path");
    }
  }

  private void checkOutputDirectory(final File dir) throws MojoFailureException {
    if (dir.isDirectory()) {
      getLog().debug("Output directory exists: " + dir.getAbsolutePath());
    } else {
      if (dir.exists()) {
        throw new MojoFailureException("Output destination exists, but is not a directory");
      } else {
        if (dir.mkdirs()) {
          getLog().debug("Created output directory: " + dir.getAbsolutePath());
        } else {
          throw new MojoFailureException("Could not create output directory");
        }
      }
    }
  }

}
