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
package com.arkham.ged.action;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.pattern.listener.BasicEvent;
import com.arkham.ged.blob.DocumentLinkBean;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.properties.InputScanFileDef;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.properties.PropertiesAdapter.GLOBAL_EVENTS;

/**
 * Shutdown application
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 7 févr. 2020
 */
public class ShutdownAction extends AbstractAction<InputScanFileDef, File> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownAction.class);

	@Override
	public List<DocumentLinkBean> execute(File data, FileKey fk, Connection con, PropertiesAdapter pa, List<DocumentLinkBean> bean, InputScanFileDef asd) throws ActionException {
		LOGGER.info("execute() : Shutdown action, deleting file {}", data.getName());
		deleteFile(data);

		pa.fireEvent(new BasicEvent<>("yeti", GLOBAL_EVENTS.SHUTDOWN));

		return bean;
	}
}
