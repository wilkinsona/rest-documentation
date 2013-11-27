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

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

public interface ResponseStatusResolver {
	
	/**
	 * Resolves the {@link ResponseStatus} that will be produced in the event of an
	 * exception of the given {@code exceptionType} being thrown by the given
	 * {@code handlerMethod}.
	 * 
	 * @param exceptionClass The type of the exception
	 * @param handlerMethod The handler method
	 * 
	 * @return The {@code ResponseStatus} or {@code null} if it could not be resolved
	 */
	ResponseStatus resolveResponseStatus(Class<? extends Exception> exceptionClass, HandlerMethod handlerMethod);

}
