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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

final class StreamReadingRunnable implements Runnable {

	private final byte[] buffer = new byte[4096];

	private final InputStream stream;

	private volatile String output;

	public StreamReadingRunnable(InputStream stream) {
		this.stream = stream;
	}

	@Override
	public void run() {
		StringWriter stringWriter = new StringWriter();
		int length;
		try {
			while ((length = this.stream.read(this.buffer)) >= 0) {
				stringWriter.append(new String(this.buffer, 0, length));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			this.output = stringWriter.toString();
		}
	}

	public String getOutput() {
		return this.output;
	}
}