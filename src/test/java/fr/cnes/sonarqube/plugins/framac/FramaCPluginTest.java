/*
 * This file is part of sonar-frama-c-plugin.
 *
 * sonar-frama-c-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sonar-frama-c-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with sonar-frama-c-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cnes.sonarqube.plugins.framac;

import org.junit.Test;
import org.sonar.api.Plugin;

import fr.cnes.sonarqube.plugins.framac.rules.FramaCRulesDefinition;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FramaCPluginTest {

	@Test
	public void testExtension() {
		
		Plugin.Context context = mock(Plugin.Context.class);
		FramaCPlugin plugin = new FramaCPlugin();
		plugin.define(context);
		
		verify(context).addExtension(FramaCRulesDefinition.class);
	}

}
