/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hdzitao.editstarters.version;

import lombok.Getter;

/**
 * 拷贝自 spring-io/initializr,删除不必要功能
 * <p>
 * Define a {@link Version} range. A square bracket "[" or "]" denotes an inclusive end of
 * the range and a round bracket "(" or ")" denotes an exclusive end of the range. A range
 * can also be unbounded by defining a a single {@link Version}. The examples below make
 * this clear.
 *
 * <ul>
 * <li>"[1.2.0.RELEASE,1.3.0.RELEASE)" version 1.2.0 and any version after this, up to,
 * but not including, version 1.3.0.</li>
 * <li>"(2.0.0,3.2.0]" any version after 2.0.0 up to and including version 3.2.0.</li>
 * <li>"2.5.0-M1", the first milestone of 2.5.0 and any version after that.</li>
 * </ul>
 *
 * @author Stephane Nicoll
 */
public class VersionRange {

    @Getter
    private final Version lowerVersion;

    @Getter
    private final boolean lowerInclusive;

    @Getter
    private final Version higherVersion;

    @Getter
    private final boolean higherInclusive;

    @Getter
    private final String originalText;

    protected VersionRange(Version lowerVersion, boolean lowerInclusive,
                           Version higherVersion, boolean higherInclusive,
                           String originalText) {
        this.lowerVersion = lowerVersion;
        this.lowerInclusive = lowerInclusive;
        this.higherVersion = higherVersion;
        this.higherInclusive = higherInclusive;
        this.originalText = originalText;
    }

    public VersionRange(Version startingVersion) {
        this(startingVersion, true, null, false, startingVersion.getOriginalText());
    }

    /**
     * Specify if the {@link Version} matches this range. Returns {@code true} if the
     * version is contained within this range, {@code false} otherwise.
     *
     * @param version the version to check
     * @return {@code true} if the version matches
     */
    public boolean match(Version version) {
        int lower = this.lowerVersion.compareTo(version);
        if (lower > 0) {
            return false;
        } else if (!this.lowerInclusive && lower == 0) {
            return false;
        }
        if (this.higherVersion != null) {
            int higher = this.higherVersion.compareTo(version);
            if (higher < 0) {
                return false;
            } else if (!this.higherInclusive && higher == 0) {
                return false;
            }
        }
        return true;
    }

    public String toRangeString() {
        StringBuilder sb = new StringBuilder();
        if (this.lowerVersion == null && this.higherVersion == null) {
            return "";
        }
        if (this.higherVersion != null) {
            sb.append(this.lowerInclusive ? "[" : "(").append(this.lowerVersion).append(",").append(this.higherVersion)
                    .append(this.higherInclusive ? "]" : ")");
        } else {
            sb.append(this.lowerVersion);
        }
        return sb.toString();
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
        VersionRange other = (VersionRange) obj;
        if (this.higherInclusive != other.higherInclusive) {
            return false;
        }
        if (this.higherVersion == null) {
            if (other.higherVersion != null) {
                return false;
            }
        } else if (!this.higherVersion.equals(other.higherVersion)) {
            return false;
        }
        if (this.lowerInclusive != other.lowerInclusive) {
            return false;
        }
        if (this.lowerVersion == null) {
            if (other.lowerVersion != null) {
                return false;
            }
        } else if (!this.lowerVersion.equals(other.lowerVersion)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.higherInclusive ? 1231 : 1237);
        result = prime * result + ((this.higherVersion == null) ? 0 : this.higherVersion.hashCode());
        result = prime * result + (this.lowerInclusive ? 1231 : 1237);
        result = prime * result + ((this.lowerVersion == null) ? 0 : this.lowerVersion.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.lowerVersion != null) {
            sb.append(this.lowerInclusive ? ">=" : ">").append(this.lowerVersion);
        }
        if (this.higherVersion != null) {
            sb.append(" and ").append(this.higherInclusive ? "<=" : "<").append(this.higherVersion);
        }
        return sb.toString();
    }

}
