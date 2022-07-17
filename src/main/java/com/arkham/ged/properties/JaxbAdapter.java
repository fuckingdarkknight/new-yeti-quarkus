/*
 * Licensed to the Arkham asylum Software Foundation under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkham.ged.properties;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * The JAXB adapter used to read the properties
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class JaxbAdapter {
	private JaxbAdapter() {
		// Private because it's an utility class, so we should't get an instance of this class
	}

	/**
	 * @param is
	 * @param contextPackage
	 * @return The unmarshalled object
	 * @throws JAXBException
	 */
	public static Object unmarshall(InputStream is, String contextPackage) throws JAXBException {
		final Unmarshaller unmarshaller = createUnmarshaller(contextPackage);

		return unmarshaller.unmarshal(is);
	}

	private static Unmarshaller createUnmarshaller(String contextPackage) throws JAXBException {
		final JAXBContext jc = createJaxbContext(contextPackage);

		return jc.createUnmarshaller();
	}

	private static JAXBContext createJaxbContext(String contextPackage) throws JAXBException {
		return JAXBContext.newInstance(contextPackage);
	}
}
