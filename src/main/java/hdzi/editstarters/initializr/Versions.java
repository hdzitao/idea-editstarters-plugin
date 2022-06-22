package hdzi.editstarters.initializr;

import hdzi.editstarters.ui.ShowErrorException;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Versions {
    private Versions() {
    }

    private static final Pattern VERSION_REGEX = Pattern.compile("^(\\d+)\\.(\\d+|x)\\.(\\d+|x).*$");

    private static final Pattern RANGE_REGEX = Pattern.compile("([(\\[])(.*),(.*)([)\\]])");

    public static Version parse(String text) {
        Matcher matcher = VERSION_REGEX.matcher(text);
        if (!matcher.find()) {
            throw new ShowErrorException("Unsupported spring version!");
        }
        Integer major = Integer.valueOf(matcher.group(1));
        String minorStr = matcher.group(2);
        Integer minor = ("x".equals(minorStr) ? 999 : Integer.parseInt(minorStr));
        String patchStr = matcher.group(3);
        Integer patch = ("x".equals(patchStr) ? 999 : Integer.parseInt(patchStr));
        return new Version(major, minor, patch);
    }

    public static VersionRange parseRange(String text) {
        Matcher matcher = RANGE_REGEX.matcher(text.trim());
        if (!matcher.matches()) {
            // 尝试单版本匹配
            Version version = parse(text);
            return new VersionRange(version, true, null, true);
        }
        boolean lowerInclusive = matcher.group(1).equals("[");
        Version lowerVersion = parse(matcher.group(2));
        Version higherVersion = parse(matcher.group(3));
        boolean higherInclusive = matcher.group(4).equals("]");
        return new VersionRange(lowerVersion, lowerInclusive, higherVersion, higherInclusive);
    }

    @Data
    public static class Version implements Comparable<Version> {
        private final Integer major;

        private final Integer minor;

        private final Integer patch;

        private Version(Integer major, Integer minor, Integer patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        public String toVersionID() {
            return this.major + "." + this.minor + "." + this.patch;
        }

        public boolean inRange(VersionRange range) {
            int lower = range.lowerVersion.compareTo(this);
            if (lower > 0) {
                return false;
            } else if (!range.lowerInclusive && lower == 0) {
                return false;
            }
            if (range.higherVersion != null) {
                int higher = range.higherVersion.compareTo(this);
                if (higher < 0) {
                    return false;
                } else if (!range.higherInclusive && higher == 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int compareTo(Version other) {
            if (other == null) {
                return 1;
            }
            int majorDiff = safeCompare(this.major, other.major);
            if (majorDiff != 0) {
                return majorDiff;
            }
            int minorDiff = safeCompare(this.minor, other.minor);
            if (minorDiff != 0) {
                return minorDiff;
            }
            int patch = safeCompare(this.patch, other.patch);
            if (patch != 0) {
                return patch;
            }
            return 0;
        }

        private static int safeCompare(Integer first, Integer second) {
            Integer firstIndex = (first != null) ? first : 0;
            Integer secondIndex = (second != null) ? second : 0;
            return firstIndex.compareTo(secondIndex);
        }
    }

    @Data
    public static class VersionRange {
        private final Version lowerVersion;

        private final boolean lowerInclusive;

        private final Version higherVersion;

        private final boolean higherInclusive;

        private VersionRange(Version lowerVersion, boolean lowerInclusive, Version higherVersion, boolean higherInclusive) {
            this.lowerVersion = lowerVersion;
            this.lowerInclusive = lowerInclusive;
            this.higherVersion = higherVersion;
            this.higherInclusive = higherInclusive;
        }

    }
}
