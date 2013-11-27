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
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.rest.documentation.javadoc.Javadoc;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javadoc.RootDoc;

/**
 * @author awilkinson
 */
public class RestDoclet {

	public static boolean start(RootDoc rootDoc) throws IOException {

		Javadoc api = new JavadocProcessor().process(rootDoc);

		File outputDirectory = getOutputDirectory(rootDoc.options());

		if (!outputDirectory.isDirectory() && !outputDirectory.mkdirs()) {
			throw new IllegalStateException("Failed to create output directory "
					+ outputDirectory);
		}
		
		File file = new File(outputDirectory, "javadoc.json");
		
		FileWriter writer = new FileWriter(file);

		JsonGenerator generator = new JsonFactory(new ObjectMapper()).createGenerator(
				writer).useDefaultPrettyPrinter();

		generator.writeObject(api);

		return true;
	}

	public static int optionLength(String option) {
		if ("-d".equals(option)) {
			return 2;
		}
		else {
			return 0;
		}
	}

	private static File getOutputDirectory(String[][] options) {
		for (String[] option : options) {
			if ("-d".equals(option[0])) {
				return new File(option[1]);
			}
		}
		throw new IllegalArgumentException(
				"Specified options do not include an output directory");
	}
}
