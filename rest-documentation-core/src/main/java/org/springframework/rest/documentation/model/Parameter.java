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

package org.springframework.rest.documentation.model;


/**
 * @author awilkinson
 */
public class Parameter {

	private final String name;

	private final String type;

	private final boolean required;

	private final ParameterType parameterType;

	private final String description;

	public Parameter(String name, String type, boolean required, ParameterType parameterType,
			String description) {
		this.name = name;
		this.type = type;
		this.required = required;
		this.parameterType = parameterType;
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public boolean isRequired() {
		return this.required;
	}

	public ParameterType getParameterType() {
		return this.parameterType;
	}

	public String getDescription() {
		return this.description;
	}
}
