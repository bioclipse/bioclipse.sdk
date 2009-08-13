package net.bioclipse.sdk.templating;

import java.util.ArrayList;
import java.util.List;

public class Templater {
    String template;

    public Templater(String template) {
        this.template = template;
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