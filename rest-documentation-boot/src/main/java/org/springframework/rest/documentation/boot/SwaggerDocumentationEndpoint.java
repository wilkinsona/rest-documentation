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

package org.springframework.rest.documentation.boot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.rest.documentation.model.Endpoint;
import org.springframework.rest.documentation.model.Outcome;
import org.springframework.rest.documentation.model.Parameter;

import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationAllowableValues;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

@ConfigurationProperties(name = "endpoints.swagger-documentation", ignoreUnknownFields = false)
public class SwaggerDocumentationEndpoint extends AbstractEndpoint<Documentation> implements
		ApplicationContextAware {

	private RestDocumentationView restDocumentationView = new RestDocumentationView();

	public SwaggerDocumentationEndpoint() {
		super("/swagger-documentation");
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.restDocumentationView.setApplicationContext(context);
	}

	@Override
	public MediaType[] getProduces() {
		return new MediaType[] { MediaType.APPLICATION_JSON };
	}

	@Override
	public Documentation invoke() {
		org.springframework.rest.documentation.model.Documentation documentation = this.restDocumentationView.getSnapshot();
		Documentation swaggerDocumentation = new Documentation("Unknown", "1.2", "/", "/");
		Map<String, List<Endpoint>> endpointsByPath = new HashMap<String, List<Endpoint>>();		
		for (Endpoint endpoint: documentation.getEndpoints()) {
			List<Endpoint> endpoints = endpointsByPath.get(endpoint.getUriPattern());
			if (endpoints == null) {
				endpoints = new ArrayList<Endpoint>();
				endpointsByPath.put(endpoint.getUriPattern(), endpoints);
			}
			endpoints.add(endpoint);
		}
		
		int nicknameCounter = 0;
		int operationsCounter = 0;
		
		for (Map.Entry<String, List<Endpoint>> entry: endpointsByPath.entrySet()) {			
			DocumentationEndPoint documentationEndPoint = new DocumentationEndPoint(entry.getKey(), "Description " + operationsCounter++);
			for (Endpoint endpoint: entry.getValue()) {
				DocumentationOperation documentationOperation = new DocumentationOperation(endpoint.getRequestMethod().name(), "", "");
				documentationOperation.setNickname(Integer.toString(nicknameCounter++));
				for (Outcome outcome: endpoint.getOutcomes()) {
					if (outcome.getStatus().value() >= 400) {
						documentationOperation.addErrorResponse(new DocumentationError(outcome.getStatus().value(), outcome.getDescription()));
					}
				}
				for (Parameter parameter: endpoint.getParameters()) {
					DocumentationAllowableValues allowableValues = null;
					DocumentationParameter documentationParameter = new DocumentationParameter(parameter.getName(), parameter.getDescription(), "", "path", null, allowableValues, true, false);
					documentationParameter.setDataType("integer");
					documentationOperation.addParameter(documentationParameter);
				}
				documentationEndPoint.addOperation(documentationOperation);
			}
			swaggerDocumentation.addApi(documentationEndPoint);
		}
		return swaggerDocumentation;
	}
}
