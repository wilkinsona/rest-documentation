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

package org.springframework.rest.documentation;

import org.springframework.context.ApplicationContext;
import org.springframework.rest.documentation.javadoc.Javadoc;
import org.springframework.rest.documentation.model.Documentation;

public class DocumentationGenerator {
	
	private final ApplicationContext applicationContext;
	
	private final Javadoc javadoc;

	public DocumentationGenerator(ApplicationContext applicationContext, Javadoc javadoc) {
		this.applicationContext = applicationContext;
		this.javadoc = javadoc;
	}
	
	public Documentation generate() {		
		return new Documentation(new EndpointDiscoverer(this.javadoc, this.applicationContext).discoverEndpoints());
	}
}
