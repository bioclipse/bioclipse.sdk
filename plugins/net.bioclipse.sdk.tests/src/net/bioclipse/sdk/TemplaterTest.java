package net.bioclipse.sdk;

import net.bioclipse.sdk.templating.Templater;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TemplaterTest {
    @Test
    public void templateWithoutSubstitutions() {
        Templater t = new Templater("foo");
        assertEquals("foo", t.generate());
    }

    @Test(expected=IllegalArgumentException.class)
    public void generateWithOddNumberOfKeys() {
        Templater t = new Templater("foo");
        t.generate("bar");
    }

    @Test()
    public void generateWithNonexistentKey() {
        Templater t = new Templater("foo");
        t.generate("noSuchKey", "value");
    }

    @Test(expected=IllegalArgumentException.class)
    public void generateWithDuplicateKeys() {
        Templater t = new Templater("foo");
        t.generate("key1", "foo", "key1", "bar");
    }

    @Test()
    public void generateWithOneSubstitution() {
        Templater t = new Templater("foo${key1}bar");
        assertEquals("foo!bar", t.generate("key1", "!"));
    }

    @Test()
    public void generateWithTwoSubstitutions() {
        Templater t = new Templater("foo${key1}${key1}bar");
        assertEquals("foo!!bar", t.generate("key1", "!"));
    }
}
