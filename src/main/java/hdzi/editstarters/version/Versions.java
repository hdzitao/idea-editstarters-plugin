package hdzi.editstarters.version;

import hdzi.editstarters.ui.ShowErrorException;
import hdzi.editstarters.version.Version.Qualifier;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Versions {
    private Versions() {
    }

    private static final Pattern VERSION_REGEX = Pattern.compile("^(\\d+)\\.(\\d+|x)\\.(\\d+|x)(?:([.|-])([^0-9]+)(\\d+)?)?$");

    private static final Pattern RANGE_REGEX = Pattern.compile("([(\\[])(.*),(.*)([)\\]])");

    public static Version parse(String text) {
        Matcher matcher = VERSION_REGEX.matcher(text.trim());
        if (!matcher.find()) {
            throw new ShowErrorException("Unsupported version format: " + text);
        }
        Integer major = Integer.valueOf(matcher.group(1));
        String minorStr = matcher.group(2);
        Integer minor = ("x".equals(minorStr) ? 999 : Integer.parseInt(minorStr));
        String patchStr = matcher.group(3);
        Integer patch = ("x".equals(patchStr) ? 999 : Integer.parseInt(patchStr));
        Qualifier qualifier = parseQualifier(matcher);
        return new Version(major, minor, patch, qualifier, text);
    }

    public static VersionRange parseRange(String text) {
        Matcher matcher = RANGE_REGEX.matcher(text.trim());
        if (!matcher.matches()) {
            // 尝试单版本匹配
            Version version = parse(text);
            return new VersionRange(version, true, null, true, text);
        }
        boolean lowerInclusive = matcher.group(1).equals("[");
        Version lowerVersion = parse(matcher.group(2));
        Version higherVersion = parse(matcher.group(3));
        boolean higherInclusive = matcher.group(4).equals("]");
        return new VersionRange(lowerVersion, lowerInclusive, higherVersion, higherInclusive, text);
    }

    private static Qualifier parseQualifier(Matcher matcher) {
        String qualifierSeparator = matcher.group(4);
        String qualifierId = matcher.group(5);
        if (StringUtils.isNoneBlank(qualifierSeparator) && StringUtils.isNoneBlank(qualifierId)) {
            String versionString = matcher.group(6);
            return new Qualifier(qualifierId, (versionString != null) ? Integer.valueOf(versionString) : null,
                    qualifierSeparator);
        }
        return null;
    }
}
