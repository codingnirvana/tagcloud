package se.yesbabyyes.tagcloud;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class Brand {

    private Map<String,List<String>> wordMap = new HashMap<>();
    private Map<String, List<String>> categoriesMap = new HashMap<>();

    public Brand() {
        try (Stream<String> stream = Files.lines(Paths.get("/data/kg/brand-output.tsv"))) {
            stream.forEach( (String s) -> {
                String[] parts = s.split("\t");
                String brand = parts[0].toLowerCase().replaceAll("[+-]", " ");
                List<String> allStrings = parts.length > 2 ? new ArrayList<>(asList(parts[2].split(";"))) : new ArrayList<String>();
                List<String>  allCategories = parts.length > 3 ? new ArrayList<>(asList(parts[3].split(";"))): new ArrayList<String>();
                List<String> newStrings = allStrings
                        .stream().map(s1 -> s1.replaceAll(brand, "")).collect(Collectors.toList());
                List<String> categoryStrings = allCategories
                        .stream().map(s1 -> s1.split("\\(")[0]).collect(Collectors.toList());

                if (wordMap.containsKey(brand)) {
                    List<String> existing = wordMap.get(brand);
                    existing.addAll(newStrings);
                    wordMap.put(brand, new ArrayList<String>(new HashSet<>(existing)));
                } else {
                    wordMap.put(brand, newStrings);
                }
                if (categoriesMap.containsKey(brand)) {
                    List<String> existing = categoriesMap.get(brand);
                    existing.addAll(categoryStrings);
                    categoriesMap.put(brand, existing);
                } else {
                    categoriesMap.put(brand, categoryStrings);
                }
                }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Stream<String> getWordTagCloud(String brandName) {
        String sanitizedBrandName = "";
        try {
            sanitizedBrandName = URLDecoder.decode(brandName.toLowerCase(), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sanitizedBrandName = sanitizedBrandName.replaceAll("#","");
        return wordMap.getOrDefault(sanitizedBrandName, new ArrayList<>(Collections.singletonList("BRAND NA"))).stream();
    }

    public Stream<String> getCategoryTagCloud(String brandName) {
        String sanitizedBrandName = "";
        try {
            sanitizedBrandName = URLDecoder.decode(brandName.toLowerCase(), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sanitizedBrandName = sanitizedBrandName.replaceAll("cat:","");
        return categoriesMap.getOrDefault(sanitizedBrandName, new ArrayList<>(Collections.singletonList("BRAND CATEGORY NA"))).stream();
    }
}
