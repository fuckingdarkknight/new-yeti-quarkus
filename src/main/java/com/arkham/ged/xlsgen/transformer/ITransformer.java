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
package com.arkham.ged.xlsgen.transformer;

/**
 * Interface reprise du projet cotation, mais l'objectif n'est pas tout à fait le même : transformations uniquement spool YML vers POI.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 7 déc. 2016
 * @param <T> Type string forcément depuis un flux YML
 */
public interface ITransformer<T> {
	/**
	 * Réalise la transformation : spool -> Excel
	 *
	 * @param value La valeur d'entrée
	 * @return La valeur transformée
	 */
	Object transform(T value);
}
