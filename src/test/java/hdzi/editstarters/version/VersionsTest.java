package hdzi.editstarters.version;

import com.github.hdzitao.editstarters.version.Version;
import com.github.hdzitao.editstarters.version.VersionRange;
import com.github.hdzitao.editstarters.version.Versions;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionsTest {

    @Test
    public void parse() {
        Version version = Versions.parse("2.3.12.M2");
        assertEquals(2, version.getMajor().intValue());
        assertEquals(3, version.getMinor().intValue());
        assertEquals(12, version.getPatch().intValue());
        assertEquals("M", version.getQualifier().getId());
        assertEquals(2, version.getQualifier().getVersion().intValue());
    }

    @Test
    public void parseRange() {
        VersionRange range = Versions.parseRange("[1.2.0,1.3.0.M2)");
        assertTrue(range.match(Versions.parse("1.2.0")));

        assertFalse(range.match(Versions.parse("1.2.0.M2")));
        assertTrue(range.match(Versions.parse("1.2.0.RELEASE")));

        assertTrue(range.match(Versions.parse("1.3.0.M1")));
        assertFalse(range.match(Versions.parse("1.3.0.M2")));
    }
}