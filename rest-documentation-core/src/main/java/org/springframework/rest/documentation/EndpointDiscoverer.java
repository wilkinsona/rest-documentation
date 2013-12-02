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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.rest.documentation.javadoc.ClassDescriptor;
import org.springframework.rest.documentation.javadoc.Javadoc;
import org.springframework.rest.documentation.javadoc.MethodDescriptor;
import org.springframework.rest.documentation.javadoc.ThrowsDescriptor;
import org.springframework.rest.documentation.model.Endpoint;
import org.springframework.rest.documentation.model.Outcome;
import org.springframework.rest.documentation.model.Parameter;
import org.springframework.rest.documentation.model.ParameterType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author awilkinson
 */
public class EndpointDiscoverer {

	private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	private final Javadoc javadoc;

	private final ApplicationContext applicationContext;

	private final ResponseStatusResolver responseStatusResolver;

	public EndpointDiscoverer(Javadoc javadoc, ApplicationContext applicationContext) {
		this.javadoc = javadoc;
		this.applicationContext = applicationContext;
		this.responseStatusResolver = new CompositeResponseStatusResolver(applicationContext);
	}

	public List<Endpoint> discoverEndpoints() {
		RequestMappingHandlerMapping mapping = this.applicationContext
				.getBean(RequestMappingHandlerMapping.class);

		Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping
				.getHandlerMethods();

		List<Endpoint> endpoints = new ArrayList<Endpoint>(handlerMethods.size());

		for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods
				.entrySet()) {
			HandlerMethod handlerMethod = entry.getValue();
			if (shouldBeDocumented(handlerMethod)) {
				RequestMappingInfo requestMappingInfo = entry.getKey();
				endpoints.add(createEndpoint(handlerMethod, requestMappingInfo, this.javadoc));
			}
		}

		return endpoints;
	}

	private boolean shouldBeDocumented(HandlerMethod handlerMethod) {
		return !handlerMethod.getMethod().getDeclaringClass().getName().startsWith("org.springframework");
	}

	private Endpoint createEndpoint(HandlerMethod handlerMethod,
			RequestMappingInfo requestMappingInfo, Javadoc api) {
		Class<?> clazz = handlerMethod.getMethod().getDeclaringClass();

		ClassDescriptor classDescriptor = api.getClassDescriptor(clazz);

		if (classDescriptor == null) {
			System.out.println(clazz);
		}

		MethodDescriptor methodDescriptor = classDescriptor
				.getMethodDescriptor(handlerMethod.getMethod());

		List<Outcome> outcomes = getOutcomes(handlerMethod, methodDescriptor);

		List<Parameter> parameters = getParameters(handlerMethod, methodDescriptor);

		return new Endpoint(getHttpMethod(requestMappingInfo),
				getUriPattern(requestMappingInfo), outcomes, parameters);
	}

	private List<Parameter> getParameters(HandlerMethod handlerMethod,
			MethodDescriptor methodDescriptor) {
		List<Parameter> parameters = new ArrayList<Parameter>();

		for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
			methodParameter
			.initParameterNameDiscovery(this.parameterNameDiscoverer);
			String name = methodParameter.getParameterName();

			Annotation[] annotations = methodParameter.getMethod().getParameterAnnotations()[methodParameter.getParameterIndex()];
			PathVariable pathVariable = getAnnotation(annotations, PathVariable.class);

			ParameterType parameterType = null;
			boolean required = true;

			if (pathVariable != null) {
				parameterType = ParameterType.PATH;
				required = true;
			} else {
				RequestParam requestParam = getAnnotation(annotations, RequestParam.class);
				if (requestParam != null) {
					parameterType = ParameterType.REQUEST_PARAMETER;
					required = requestParam.required();
				} else {
					RequestBody requestBody = getAnnotation(annotations, RequestBody.class);
					if (requestBody != null) {
						parameterType = ParameterType.BODY;
						required = requestBody.required();
					}
				}
			}

			if (parameterType != null) {
				String description = methodDescriptor.getParameterDescriptor(name)
						.getDescription();

				parameters.add(new Parameter(methodParameter.getParameterName(), required,
						parameterType, description));
			}
		}

		return parameters;
	}

	@SuppressWarnings("unchecked")
	private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> toFind) {
		for (Annotation annotation: annotations) {
			if (annotation.annotationType().equals(toFind)) {
				return (T) annotation;
			}
		}
		return null;
	}

	private List<Outcome> getOutcomes(HandlerMethod handlerMethod,
			MethodDescriptor methodDescriptor) {
		List<Outcome> outcomes = new ArrayList<Outcome>();

		ResponseStatus successResponseStatus = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ResponseStatus.class);
		if (successResponseStatus == null) {
			outcomes.add(new Outcome(HttpStatus.OK, "Success"));
		} else {
			outcomes.add(new Outcome(successResponseStatus.value(), "Success"));
		}

		Set<ErrorCause> errorCauses = getErrorCauses(handlerMethod.getMethod(),
				methodDescriptor);

		for (ErrorCause errorCause : errorCauses) {
			Outcome outcome = createOutcome(errorCause.getExceptionClass(),
					errorCause.getThrowsDescriptor(), handlerMethod);
			if (outcome != null) {
				outcomes.add(outcome);
			}
		}

		return outcomes;
	}

	private String getUriPattern(RequestMappingInfo requestMappingInfo) {
		Assert.state(requestMappingInfo.getPatternsCondition().getPatterns().size() == 1,
				"A single URI pattern is required");
		return requestMappingInfo.getPatternsCondition().getPatterns().iterator().next();
	}

	private RequestMethod getHttpMethod(RequestMappingInfo requestMappingInfo) {
		Assert.state(requestMappingInfo.getMethodsCondition().getMethods().size() == 1,
				"A single HTTP request method is required");
		return requestMappingInfo.getMethodsCondition().getMethods().iterator().next();
	}

	private Outcome createOutcome(Class<? extends Exception> exceptionClass,
			ThrowsDescriptor descriptor, HandlerMethod handler) {

		ResponseStatus annotation = this.responseStatusResolver.resolveResponseStatus(exceptionClass, handler);
		if (annotation != null) {
			HttpStatus status = annotation.value();
			if (descriptor != null) {
				return new Outcome(status, descriptor.getReason());
			}
			else {
				return new Outcome(status);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Set<ErrorCause> getErrorCauses(Method method,
			MethodDescriptor methodDescriptor) {

		Class<? extends Exception>[] exceptionTypes = (Class<? extends Exception>[]) method
				.getExceptionTypes();

		Set<ErrorCause> errorCauses = new HashSet<ErrorCause>();

		for (Class<? extends Exception> exceptionType : exceptionTypes) {
			ThrowsDescriptor throwsDescriptor = methodDescriptor
					.getThrowsDescriptor(exceptionType);
			errorCauses.add(new ErrorCause(exceptionType, throwsDescriptor));
		}

		for (ThrowsDescriptor throwsDescriptor: methodDescriptor.getThrowsDescriptors()) {
			try {
				errorCauses.add(new ErrorCause((Class<? extends Exception>)Class.forName(throwsDescriptor.getException()), throwsDescriptor));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return errorCauses;
	}

	private static final class ErrorCause {

		private final Class<? extends Exception> exceptionClass;

		private final ThrowsDescriptor throwsDescriptor;

		public ErrorCause(Class<? extends Exception> exceptionClass,
				ThrowsDescriptor throwsDescriptor) {
			this.exceptionClass = exceptionClass;
			this.throwsDescriptor = throwsDescriptor;
		}

		public Class<? extends Exception> getExceptionClass() {
			return this.exceptionClass;
		}

		public ThrowsDescriptor getThrowsDescriptor() {
			return this.throwsDescriptor;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((this.exceptionClass == null) ? 0 : this.exceptionClass.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ErrorCause other = (ErrorCause) obj;
			if (this.exceptionClass == null) {
				if (other.exceptionClass != null)
					return false;
			}
			else if (!this.exceptionClass.equals(other.exceptionClass))
				return false;
			return true;
		}

	}

}
