/*
 * This file is part of sonarframac.
 *
 * sonarframac is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sonarframac is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with sonarframac.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cnes.sonarqube.plugins.framac.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;
import com.thoughtworks.xstream.annotations.XStreamInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to unmarshal i-Code xml file.
 *
 * It contains meta data about definedRules definition.
 */
@XStreamAlias("framac-rules")
@XStreamImplicitCollection("definedRules")
@XStreamInclude(Rule.class)
public class RulesDefinition {

    @XStreamAlias("rule")
    public List<Rule> definedRules;

    /**
     * Getter for accessing definedRules (definition).
     * @return A list of Rule.
     */
    public List<Rule> getDefinedRules() {
        // Retrieve issues (called definedRules)
        List<Rule> rules;
        if(this.definedRules !=null) {
            rules = this.definedRules;
        } else {
            rules = new ArrayList<>();
        }
        return rules;
    }

}
