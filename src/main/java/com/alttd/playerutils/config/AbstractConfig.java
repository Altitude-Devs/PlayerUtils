package com.alttd.playerutils.config;

import com.alttd.playerutils.PlayerUtils;
import com.alttd.playerutils.util.Logger;
import com.google.common.collect.ImmutableMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "SameParameterValue"})
abstract class AbstractConfig {
    File file;
    YamlConfiguration yaml;
    private static Logger logger = null;

    AbstractConfig(PlayerUtils playerUtils, String filename, Logger logger) {
        AbstractConfig.logger = logger;
        init(new File(playerUtils.getDataFolder(), filename), filename);
    }

    AbstractConfig(File file, String filename, Logger logger) {
        AbstractConfig.logger = logger;
        init(new File(file.getPath() + File.separator + filename), filename);
    }

    private void init(File file, String filename) {
        this.file = file;
        this.yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            if (logger != null)
                logger.severe(String.format("Could not load %s, please correct your syntax errors", filename));
            throw new RuntimeException(ex);
        }
        yaml.options().copyDefaults(true);
    }

    void readConfig(Class<?> clazz, Object instance) {
        for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
            for (Method method : declaredClass.getDeclaredMethods()) {
                if (Modifier.isPrivate(method.getModifiers())) {
                    if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                        try {
                            method.setAccessible(true);
                            method.invoke(instance);
                        } catch (InvocationTargetException ex) {
                            throw new RuntimeException(ex.getCause());
                        } catch (Exception ex) {
                            if (logger != null)
                                logger.severe("Error invoking %.", method.toString());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        save();
    }

    private void save() {
        try {
            yaml.save(file);
        } catch (IOException ex) {
            if (logger != null)
                logger.severe("Could not save %.", file.toString());
            ex.printStackTrace();
        }
    }

    void set(String prefix, String path, Object val) {
        path = prefix + path;
        yaml.addDefault(path, val);
        yaml.set(path, val);
        save();
    }

    String getString(String prefix, String path, String def) {
        path = prefix + path;
        yaml.addDefault(path, def);
        return yaml.getString(path, yaml.getString(path));
    }

    boolean getBoolean(String prefix, String path, boolean def) {
        path = prefix + path;
        yaml.addDefault(path, def);
        return yaml.getBoolean(path, yaml.getBoolean(path));
    }

    int getInt(String prefix, String path, int def) {
        path = prefix + path;
        yaml.addDefault(path, def);
        return yaml.getInt(path, yaml.getInt(path));
    }

    double getDouble(String prefix, String path, double def) {
        path = prefix + path;
        yaml.addDefault(path, def);
        return yaml.getDouble(path, yaml.getDouble(path));
    }

    <T> List<String> getList(String prefix, String path, T def) {
        path = prefix + path;
        yaml.addDefault(path, def);
        List<?> list = yaml.getList(path, yaml.getList(path));
        return list == null ? null : list.stream().map(Object::toString).collect(Collectors.toList());
    }

    List<String> getStringList(String prefix, String path, List<String> def) {
        path = prefix + path;
        yaml.addDefault(path, def);
        return yaml.getStringList(path);
    }

    @NonNull
    <T> Map<String, T> getMap(String prefix, @NonNull String path, final @Nullable Map<String, T> def) {
        path = prefix + path;
        final ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
        if (def != null && yaml.getConfigurationSection(path) == null) {
            yaml.addDefault(path, def.isEmpty() ? new HashMap<>() : def);
            return def;
        }
        final ConfigurationSection section = yaml.getConfigurationSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                @SuppressWarnings("unchecked")
                final T val = (T) section.get(key);
                if (val != null) {
                    builder.put(key, val);
                }
            }
        }
        return builder.build();
    }

    ConfigurationSection getConfigurationSection(String path) {
        return yaml.getConfigurationSection(path);
    }
}