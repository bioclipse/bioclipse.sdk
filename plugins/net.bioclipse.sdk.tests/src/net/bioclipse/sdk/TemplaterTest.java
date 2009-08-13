/*******************************************************************************
 * Copyright (c) 2008-2009 Carl Masak <carl.masak@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
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

    @Test(expected=IllegalArgumentException.class)
    public void substitutionWithoutCorrespondingKey() {
        Templater t = new Templater("foo${key1}bar");
        t.generate();
    }
}
