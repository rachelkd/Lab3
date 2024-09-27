package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, Map<String, String>> c = new HashMap<>();
    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */

    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Map<String, String> lan = new HashMap<>();
                for (String key : obj.keySet()) {
                    if ("alpha2".equals(key) || "alpha3".equals(key) || "id".equals(key)) {
                        continue;
                    }
                    lan.put(key, obj.getString(key));
                }
                c.put(obj.getString("alpha3"), lan);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        if (!c.containsKey(country)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(c.get(country).keySet());
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(c.keySet());
    }

    @Override
    public String translate(String country, String language) {
        if (c.containsKey(country) && !c.get(country).containsKey(language)) {
            return c.get(country).get(language);
        }
        return null;
    }
}
