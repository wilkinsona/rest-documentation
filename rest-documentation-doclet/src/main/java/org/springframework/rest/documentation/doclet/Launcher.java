/*
 * Copyright 2012-2013 the original author or authors.
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

package org.springframework.rest.documentation.doclet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.util.StringUtils;

public final class Launcher {
	
	private final File sourcePath;
	
	private final List<String> packages;
	
	private final List<File> classpath;
	
	private final List<File> docletPath;
	
	private final File outputDirectory;
	
	public Launcher(File sourcePath, List<String> packages,
			List<File> classpath, List<File> docletPath, File outputDirectory) {
		this.sourcePath = sourcePath;
		this.packages = packages;
		this.classpath = classpath;
		this.docletPath = docletPath;
		this.outputDirectory = outputDirectory;
	}

	public File launch() throws IOException,
			InterruptedException {

		String classpathString = StringUtils.collectionToDelimitedString(this.classpath, File.pathSeparator);

		String command = "javadoc -classpath " + classpathString + " -doclet "
				+ RestDoclet.class.getName() + " -private"
				+ " -d " + outputDirectory + " -docletpath " + StringUtils.collectionToDelimitedString(this.docletPath, File.pathSeparator)
				+ " -sourcepath " + sourcePath + " -subpackages "
				+ StringUtils.collectionToDelimitedString(packages, ":");
		
		Process process = Runtime.getRuntime().exec(
				command);

		StreamReadingRunnable outputReader = new StreamReadingRunnable(
				process.getInputStream());
		StreamReadingRunnable errorReader = new StreamReadingRunnable(
				process.getErrorStream());
		new Thread(outputReader).start();
		new Thread(errorReader).start();

		if (process.waitFor() != 0) {
			System.out.println(outputReader.getOutput());			
			System.err.println(errorReader.getOutput());
			
			throw new IllegalStateException("Doclet execution failed");
		} else {
			return new File(outputDirectory, "javadoc.json");
		}
	}
}
