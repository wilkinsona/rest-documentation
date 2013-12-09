/*
 * Copyright 2013 the original author or authors.
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

package org.springframework.rest.documentation.javadoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author awilkinson
 */
public class Javadoc {

	private final List<ClassDescriptor> classes;

	private final Map<String, ClassDescriptor> classesByName = new HashMap<String, ClassDescriptor>();

	@JsonCreator
	public Javadoc(@JsonProperty("classes") List<ClassDescriptor> classes) {
		this.classes = classes;
		for (ClassDescriptor classDescriptor : classes) {
			this.classesByName.put(classDescriptor.getName(), classDescriptor);
		}
	}

	public List<ClassDescriptor> getClasses() {
		return this.classes;
	}

	public ClassDescriptor getClassDescriptor(String className) {
		return this.classesByName.get(className);
	}

	public ClassDescriptor getClassDescriptor(Class<?> clazz) {
		return getClassDescriptor(clazz.getName());
	}
}
