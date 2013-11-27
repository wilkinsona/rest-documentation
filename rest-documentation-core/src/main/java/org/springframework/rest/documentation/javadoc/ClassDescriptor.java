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

import java.lang.reflect.Method;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author awilkinson
 */
public class ClassDescriptor {

	private final String name;

	private final List<MethodDescriptor> methodDescriptors;

	@JsonCreator
	public ClassDescriptor(@JsonProperty("name") String name,
			@JsonProperty("methods") List<MethodDescriptor> methods) {
		this.name = name;
		this.methodDescriptors = methods;
	}

	public String getName() {
		return this.name;
	}

	public List<MethodDescriptor> getMethodDescriptors() {
		return this.methodDescriptors;
	}

	public MethodDescriptor getMethodDescriptor(Method method) {
		for (MethodDescriptor methodDescriptor : this.methodDescriptors) {
			if (method.getName().equals(methodDescriptor.getName())) {
				if (method.getParameterTypes().length == methodDescriptor
						.getParameterDescriptors().size()) {
					if (isMatch(method.getParameterTypes(),
							methodDescriptor.getParameterDescriptors())) {
						return methodDescriptor;
					}
				}
			}
		}
		return null;
	}

	private boolean isMatch(Class<?>[] parameterTypes,
			List<ParameterDescriptor> parameterDescriptors) {
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> type = parameterTypes[i];
			ParameterDescriptor descriptor = parameterDescriptors.get(i);
			if (!isMatch(type, descriptor)) {
				return false;
			}
		}
		return true;
	}

	private boolean isMatch(Class<?> type, ParameterDescriptor descriptor) {
		if (type.isArray()) {
			return type.getComponentType().getName().equals(descriptor.getType());
		}
		else {
			return type.getName().equals(descriptor.getType());
		}
	}
}
