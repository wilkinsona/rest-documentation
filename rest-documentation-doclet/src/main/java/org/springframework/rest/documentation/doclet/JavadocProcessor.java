/*
 * Copyright 2012-2013 the original author or authors.
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

package org.springframework.rest.documentation.doclet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.rest.documentation.javadoc.ClassDescriptor;
import org.springframework.rest.documentation.javadoc.Javadoc;
import org.springframework.rest.documentation.javadoc.MethodDescriptor;
import org.springframework.rest.documentation.javadoc.ParameterDescriptor;
import org.springframework.rest.documentation.javadoc.ThrowsDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.ThrowsTag;

public final class JavadocProcessor {

	Javadoc process(RootDoc rootDoc) {
		List<ClassDescriptor> classDescriptors = new ArrayList<ClassDescriptor>();

		for (ClassDoc classDoc : rootDoc.classes()) {					
			if (isController(classDoc)) {
				classDescriptors.add(processControllerClass(classDoc));
			}
		}

		return new Javadoc(classDescriptors);
	}

	private boolean isController(ClassDoc classDoc) {
		try {			
			String className = getClassName(classDoc);
			Class<?> candidateClass = Class.forName(className);
			return AnnotationUtils.findAnnotation(candidateClass, Controller.class) != null || AnnotationUtils.findAnnotation(candidateClass, RestController.class) != null;
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getClassName(ClassDoc classDoc) {
		String className = classDoc.qualifiedName();
		ClassDoc containingClass = classDoc.containingClass();
		while (containingClass != null) {
			className = className.substring(0, className.lastIndexOf('.')) + "$" + className.substring(className.lastIndexOf('.') + 1);
			containingClass = containingClass.containingClass();
		}
		return className;
	}

	private ClassDescriptor processControllerClass(ClassDoc classDoc) {
		List<MethodDescriptor> methodDescriptors = new ArrayList<MethodDescriptor>();

		for (MethodDoc methodDoc : classDoc.methods()) {
			if (isRequestMappingMethod(methodDoc)) {
				methodDescriptors.add(processMethod(methodDoc));
			}
		}

		return new ClassDescriptor(classDoc.qualifiedTypeName(), methodDescriptors);
	}

	private boolean isRequestMappingMethod(MethodDoc methodDoc) {
		for (AnnotationDesc annotation : methodDoc.annotations()) {
			if (RequestMapping.class.getName().equals(
					annotation.annotationType().qualifiedTypeName())) {
				return true;
			}
		}
		return false;
	}

	private MethodDescriptor processMethod(MethodDoc methodDoc) {
		List<ParameterDescriptor> parameterDescriptors = processParameters(methodDoc);
		List<ThrowsDescriptor> throwsDescriptors = processThrows(methodDoc);

		return new MethodDescriptor(methodDoc.name(), parameterDescriptors,
				throwsDescriptors);
	}

	private List<ParameterDescriptor> processParameters(MethodDoc methodDoc) {
		List<ParameterDescriptor> parameterDescriptors = new ArrayList<ParameterDescriptor>();

		for (Parameter parameter : methodDoc.parameters()) {
			String description = null;

			for (ParamTag paramTag : methodDoc.paramTags()) {
				if (paramTag.parameterName().equals(parameter.name())) {
					description = paramTag.parameterComment();
				}
			}
			parameterDescriptors.add(new ParameterDescriptor(parameter.name(), parameter
					.type().qualifiedTypeName(), getDimension(parameter), description));
		}
		return parameterDescriptors;
	}

	private int getDimension(Parameter parameter) {
		String dimensionString = parameter.type().dimension();
		return dimensionString.length() / 2;
	}

	private List<ThrowsDescriptor> processThrows(MethodDoc methodDoc) {
		List<ThrowsDescriptor> throwsDescriptors = new ArrayList<ThrowsDescriptor>();

		for (ThrowsTag throwsTag : methodDoc.throwsTags()) {
			throwsDescriptors.add(new ThrowsDescriptor(throwsTag.exceptionType()
					.qualifiedTypeName(), throwsTag.exceptionComment()));
		}

		return throwsDescriptors;
	}
}
