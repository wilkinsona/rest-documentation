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

import java.util.List;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author awilkinson
 */
public class Endpoint {

	private final RequestMethod requestMethod;

	private final String uriPattern;

	private final String returnType;

	private final String summary;

	private final String description;

	private final List<Outcome> outcomes;

	private final List<Parameter> parameters;

	public Endpoint(RequestMethod requestMethod, String uriPattern, String returnType,
			String summary, String description, List<Outcome> outcomes, List<Parameter> parameters) {
		this.requestMethod = requestMethod;
		this.uriPattern = uriPattern;
		this.returnType = returnType;
		this.summary = summary;
		this.description = description;
		this.outcomes = outcomes;
		this.parameters = parameters;
	}

	public RequestMethod getRequestMethod() {
		return this.requestMethod;
	}

	public String getUriPattern() {
		return this.uriPattern;
	}

	public String getReturnType() {
		return this.returnType;
	}

	public String getSummary() {
		return summary;
	}

	public String getDescription() {
		return this.description;
	}

	public List<Outcome> getOutcomes() {
		return this.outcomes;
	}

	public List<Parameter> getParameters() {
		return this.parameters;
	}
}
