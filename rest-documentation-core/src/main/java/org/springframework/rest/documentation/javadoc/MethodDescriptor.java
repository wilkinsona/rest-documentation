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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author awilkinson
 */
public final class MethodDescriptor {

	private final String name;

	private final String returnType;

	private final String summary;

	private final String description;

	private final List<ParameterDescriptor> parameterDescriptors;

	private final List<ThrowsDescriptor> throwsDescriptors;

	@JsonCreator
	public MethodDescriptor(
			@JsonProperty("name") String name,
			@JsonProperty("returnType") String returnType,
			@JsonProperty("summary") String summary,
			@JsonProperty("description") String description,
			@JsonProperty("parameterDescriptors") List<ParameterDescriptor> parameterDescriptors,
			@JsonProperty("throwsDescriptors") List<ThrowsDescriptor> throwsDescriptors) {
		this.name = name;
		this.returnType = returnType;
		this.summary = summary;
		this.description = description;
		this.parameterDescriptors = parameterDescriptors;
		this.throwsDescriptors = throwsDescriptors;
	}

	public String getName() {
		return this.name;
	}

	public String getReturnType() {
		return this.returnType;
	}

	public String getSummary() {
		return this.summary;
	}

	public String getDescription() {
		return this.description;
	}

	public List<ParameterDescriptor> getParameterDescriptors() {
		return this.parameterDescriptors;
	}

	public ParameterDescriptor getParameterDescriptor(String name) {
		for (ParameterDescriptor parameterDescriptor : this.parameterDescriptors) {
			if (name.equals(parameterDescriptor.getName())) {
				return parameterDescriptor;
			}
		}
		return null;
	}

	public List<ThrowsDescriptor> getThrowsDescriptors() {
		return this.throwsDescriptors;
	}

	public ThrowsDescriptor getThrowsDescriptor(Class<? extends Throwable> throwable) {
		for (ThrowsDescriptor throwsDescriptor : this.throwsDescriptors) {
			if (throwable.getName().equals(throwsDescriptor.getException())) {
				return throwsDescriptor;
			}
		}
		return null;
	}
}
