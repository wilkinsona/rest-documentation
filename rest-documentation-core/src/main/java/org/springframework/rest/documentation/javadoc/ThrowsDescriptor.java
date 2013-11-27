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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author awilkinson
 */
public class ThrowsDescriptor {

	private final String exception;

	private final String reason;

	@JsonCreator
	public ThrowsDescriptor(@JsonProperty("exception") String exception,
			@JsonProperty("reason") String reason) {
		this.exception = exception;
		this.reason = reason;
	}

	public String getException() {
		return this.exception;
	}

	public String getReason() {
		return this.reason;
	}
}
