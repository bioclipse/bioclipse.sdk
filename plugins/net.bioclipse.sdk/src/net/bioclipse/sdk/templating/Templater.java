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
package net.bioclipse.sdk.templating;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Templater {
    String template;

    public Templater(String template) {
        this.template = template;
    }

    public Templater(InputStream stream) {
        this(readInputStreamIntoString(stream));
    }

    private static String readInputStreamIntoString(InputStream stream) {
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        Reader in;
        try {
            in = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        int read;
        try {
            do {
                    read = in.read(buffer, 0, buffer.length);
                if (read>0) {
                    out.append(buffer, 0, read);
                }
            }
            while (read>=0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    public String generate(String... parameters) {
        if (parameters.length % 2 == 1)
            throw new IllegalArgumentException("Odd number of parameters: "
                                               + parameters.length);

        List<String> uniqKeys = new ArrayList<String>();
        for (int i = 0; i < parameters.length; i += 2) {
            String key = parameters[i];

            if (!keyFoundInTemplate(key))
                throw new IllegalArgumentException("No such key in template: "
                                                   + key);

            if (uniqKeys.contains(key))
                throw new IllegalArgumentException(
                    "Duplicate key in template: " + key
                );
            uniqKeys.add(key);
        }

        String result = template;
        for (int i = 0; i < parameters.length; i += 2) {
            String key   = parameters[i],
                   value = parameters[i+1];
            result = result.replace("${" + key + "}", value);
        }

        return result;
    }

    private boolean keyFoundInTemplate(String key) {
        return template.contains("${" + key + "}");
    }
}
