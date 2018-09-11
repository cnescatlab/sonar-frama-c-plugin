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
package fr.cnes.sonarqube.plugins.framac.report;

/**
 * Error definition provided by the Frama-C report analysis
 */
public class FramaCError implements ErrorInterface {
    private final String type;
    private final String description;
    private final String filePath;
    private final String line;

    public FramaCError(final String type, final String description, final String filePath, final String line) {
      this.type = type;
      this.description = description;
      this.filePath = filePath;
      this.line = line;
    }

    public String getType() {
      return type;
    }

    @Override
	public String getDescription() {
      return description;
    }

    public String getFilePath() {
      return filePath;
    }

    public String getLine() {
      return line;
    }

    @Override
    public String toString() {
      StringBuilder s = new StringBuilder();
      s.append(type);
      s.append("|");
      s.append(description);
      s.append("|");
      s.append(filePath);
      s.append("(");
      s.append(line);
      s.append(")");
      return s.toString();
    }
    
    @Override
	public String getRuleKey(){
    	return this.type;
    }
    @Override
	public String getLineDescriptor(){
    	return this.line;
    }
}
