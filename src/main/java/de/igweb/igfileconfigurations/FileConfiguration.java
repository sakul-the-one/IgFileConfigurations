package de.igweb.igfileconfigurations;

import lombok.Getter;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Getter
public class FileConfiguration {

    private final MetaData metaData;

    private final String path;

    private final Map<String, Object> values = new HashMap<>();

    public FileConfiguration() {
        this.metaData = getClass().getAnnotation(MetaData.class);
        this.path = metaData.value();
        load();
    }

    public void load() {
        try {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(path));
            for (Field field : getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    field.set(this, yamlConfiguration.get(field.getName()));
                } catch (Exception a) {
                    a.printStackTrace();
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load configuration from " + path, exception);
        }
    }

    public void save() {
        try {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(path));
            for (Field declaredField : getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                yamlConfiguration.set(declaredField.getName(), declaredField.get(this));
            }
            yamlConfiguration.save(new File(path));
        } catch (Exception exception) {
            throw new RuntimeException("Failed to save configuration to " + path, exception);
        }
    }

    public Object get(String key) {
        return values.get(key);
    }

    public void set(String key, Object value) {
        values.put(key, value);
    }

}
