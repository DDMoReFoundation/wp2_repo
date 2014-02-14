/*******************************************************************************
 * Copyright (C) 2013 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.convertertoolbox.domain;

import eu.ddmore.convertertoolbox.api.domain.Version;

/**
 * Bean representing a version
 */
public class VersionImpl implements Version {

    private int major;
    private int minor;
    private int patch;
    private String qualifier;

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public void setMajor(int major) {
        this.major = major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public void setMinor(int minor) {
        this.minor = minor;
    }

    @Override
    public int getPatch() {
        return patch;
    }

    @Override
    public void setPatch(int patch) {
        this.patch = patch;
    }

    @Override
    public String getQualifier() {
        return qualifier;
    }

    @Override
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public int compareTo(Version o) {
        int majorDiff = this.major - o.getMajor();
        if (majorDiff != 0) {
            return majorDiff;
        }

        int minorDiff = this.minor - o.getMinor();
        if (minorDiff != 0) {
            return minorDiff;
        }

        return this.patch - o.getPatch();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + patch;
        result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VersionImpl other = (VersionImpl) obj;
        if (major != other.major) {
            return false;
        }
        if (minor != other.minor) {
            return false;
        }
        if (patch != other.patch) {
            return false;
        }
        if (qualifier == null) {
            if (other.qualifier != null) {
                return false;
            }
        } else if (!qualifier.equals(other.qualifier)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String base = String.format("%d.%d.%d", major, minor, patch);
        if (qualifier!=null) {
            base = String.format("%s-%s", base, qualifier);
        }
        return base;
    }

}