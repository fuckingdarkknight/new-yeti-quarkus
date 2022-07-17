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
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.blob.DocumentLinkBean;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.InputScanFileDef;
import com.arkham.ged.properties.LikeAntPropertiesTranslator;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.solver.Translator;
import com.arkham.ged.util.GedUtil;

/**
 * Copy the input file to another target specified by ActionType properties
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 déc. 2015
 */
public class CopyFileAction extends AbstractAction<InputScanFileDef, File> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CopyFileAction.class);

	private static final String TARGET = "target";

	@Override
	public List<DocumentLinkBean> execute(File file, FileKey fk, Connection con, PropertiesAdapter pa, List<DocumentLinkBean> bean, InputScanFileDef sfd) throws ActionException {
		final Translator translator = new LikeAntPropertiesTranslator();
		final String targetDir = translator.translate(getParamStringValue(pa, TARGET, null));

		if (targetDir == null) {
			throw new ActionException(GedMessages.Action.actionParameter, TARGET);
		}

		final File dir = new File(targetDir);
		if (dir.isDirectory()) {
			// Nothing to do, everything is OK
		} else if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new ActionException(GedMessages.Action.cannotCreateDirectory, dir.getAbsolutePath());
			}
		} else {
			throw new ActionException(GedMessages.Action.actionTargetFile, dir.getAbsolutePath());
		}

		final File target = new File(targetDir, fk.getFilename());

		try {
			LOGGER.info("execute() : copy file {} to path {}", file.getAbsolutePath(), target.getAbsolutePath());

			GedUtil.copyFile(file, target);
		} catch (final IOException e) {
			throw new ActionException(e);
		}

		return bean;
	}
}
