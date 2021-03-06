/*******************************************************************************
 * Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>.
 *******************************************************************************/
package eu.ddmore.convertertoolbox.domain;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds information available to the service clients
 */
public class ServiceDescriptor {
    private final String name;
    private final String version;
    private final Collection<ConversionCapability> capabilities;
    /**
     * @param name
     * @param version
     * @param capabilities
     */
    @JsonCreator
    public ServiceDescriptor(@JsonProperty("name") String name, @JsonProperty("version") String version, @JsonProperty("capabilities") Collection<ConversionCapability> capabilities) {
        super();
        this.name = name;
        this.version = version;
        this.capabilities = capabilities;
    }

    public String getVersion() {
        return version;
    }
    
    public String getName() {
        return name;
    }
    
    public Collection<ConversionCapability> getCapabilities() {
        return capabilities;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((capabilities == null) ? 0 : capabilities.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceDescriptor other = (ServiceDescriptor) obj;
        if (capabilities == null) {
            if (other.capabilities != null)
                return false;
        } else if (!capabilities.equals(other.capabilities))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("ServiceDescriptor [name=%s, version=%s, capabilities=%s]", name, version, capabilities);
    }
    
}
