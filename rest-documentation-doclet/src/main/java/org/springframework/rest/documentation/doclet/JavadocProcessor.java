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

import org.springframework.rest.documentation.javadoc.ClassDescriptor;
import org.springframework.rest.documentation.javadoc.Javadoc;
import org.springframework.rest.documentation.javadoc.MethodDescriptor;
import org.springframework.rest.documentation.javadoc.ParameterDescriptor;
import org.springframework.rest.documentation.javadoc.ThrowsDescriptor;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;

public final class JavadocProcessor {

	Javadoc process(RootDoc rootDoc) {
		List<ClassDescriptor> classDescriptors = new ArrayList<ClassDescriptor>();

		for (ClassDoc classDoc : rootDoc.classes()) {
			classDescriptors.add(processClass(classDoc));
		}

		return new Javadoc(classDescriptors);
	}

	private ClassDescriptor processClass(ClassDoc classDoc) {
		List<MethodDescriptor> methodDescriptors = new ArrayList<MethodDescriptor>();

		for (MethodDoc methodDoc : classDoc.methods()) {
			methodDescriptors.add(processMethod(methodDoc));
		}

		return new ClassDescriptor(getClassName(classDoc), methodDescriptors);
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

	private MethodDescriptor processMethod(MethodDoc methodDoc) {
		List<ParameterDescriptor> parameterDescriptors = processParameters(methodDoc);
		List<ThrowsDescriptor> throwsDescriptors = processThrows(methodDoc);

		String commentText = methodDoc.commentText();

		int periodIndex = commentText.indexOf('.');

		String summary;
		String description;

		if (periodIndex >= 0) {
			summary = commentText.substring(0, periodIndex);
			description = commentText.substring(periodIndex + 1);
		} else {
			summary = commentText;
			description = "";
		}

		return new MethodDescriptor(methodDoc.name(), getClassName(methodDoc.returnType()), summary, description, parameterDescriptors,
				throwsDescriptors);
	}

	private String getClassName(Type type) {
		ClassDoc classDoc = type.asClassDoc();
		if (classDoc != null) {
			return getClassName(classDoc);
		} else {
			System.out.println(type.getClass() + ": " + type.qualifiedTypeName());
			return type.qualifiedTypeName();
		}
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
