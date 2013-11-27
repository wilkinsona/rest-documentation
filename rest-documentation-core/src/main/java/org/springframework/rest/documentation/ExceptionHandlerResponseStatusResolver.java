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

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;

public class ExceptionHandlerResponseStatusResolver implements ResponseStatusResolver {
	
	private final ApplicationContext applicationContext;
	
	public ExceptionHandlerResponseStatusResolver(
			ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ResponseStatus resolveResponseStatus(
			Class<? extends Exception> exceptionClass,
			HandlerMethod handlerMethod) {
		ExceptionHandlerMethodResolver exceptionHandlerMethodResolver = new ExceptionHandlerMethodResolver(handlerMethod.getMethod().getDeclaringClass());
		ResponseStatus responseStatus = resolveResponseStatus(exceptionHandlerMethodResolver, exceptionClass);
		if (responseStatus == null) {
			List<ControllerAdviceBean> controllerAdviceBeans = ControllerAdviceBean.findAnnotatedBeans(applicationContext);
			for (int i = 0; i < controllerAdviceBeans.size() && responseStatus == null; i++) {
				ControllerAdviceBean bean = controllerAdviceBeans.get(i);
				if (bean.isApplicableToBeanType(handlerMethod.getMethod().getDeclaringClass())) {
					exceptionHandlerMethodResolver = new ExceptionHandlerMethodResolver(bean.getBeanType());
					responseStatus = resolveResponseStatus(exceptionHandlerMethodResolver, exceptionClass);
					if (responseStatus != null) {
						break;
					}
				}
			}
		}
		return responseStatus;
	}
	
	private ResponseStatus resolveResponseStatus(ExceptionHandlerMethodResolver exceptionHandlerMethodResolver, Class<? extends Exception> exceptionClass) {
		Method method;
		try {
			method = ExceptionHandlerMethodResolver.class.getDeclaredMethod("getMappedMethod", Class.class);
			method.setAccessible(true);
			Method exceptionHandler = (Method) ReflectionUtils.invokeMethod(method, exceptionHandlerMethodResolver, exceptionClass);
			if (exceptionHandler != null) {
				return AnnotationUtils.findAnnotation(exceptionHandler, ResponseStatus.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
