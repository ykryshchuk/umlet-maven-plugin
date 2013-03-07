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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which converts the UMLet uxf diagram into the graphics format.
 * 
 */
@Mojo(name = "convert", defaultPhase = LifecyclePhase.PRE_SITE)
public class ConvertDiagramMojo extends AbstractMojo {

  /**
   * Target directory to store the converted images.
   */
  @Parameter(defaultValue = " ${project.reporting.outputDirectory}", property = "outputDirectory", required = true)
  private File outputDirectory;

  public void execute() throws MojoExecutionException {
    checkOutputDirectory();

  }

  private void checkOutputDirectory() throws MojoExecutionException {
    if (outputDirectory.isDirectory()) {
      getLog().debug("Output directory exists: " + outputDirectory.getAbsolutePath());
    } else {
      if (outputDirectory.exists()) {
        throw new MojoExecutionException("Output destination exists, but is not a directory");
      } else {
        if (outputDirectory.mkdirs()) {
          getLog().debug("Created output directory: " + outputDirectory.getAbsolutePath());
        } else {
          throw new MojoExecutionException("Could not create output directory");
        }
      }
    }
  }

}
