package me.legrange.service;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

public final class YamlConfigurationLoader implements ConfigurationLoader {
    private final Yaml yaml = new Yaml();
    @Override
    public <C extends ServiceConfiguration> C load(InputStream in, Class<C> type) {
        Map map = yaml.loadAs(in, Map.class);
        List<Method> methods = gatherMethods(type);
        Map<String, String> varMap = new HashMap<>();
        for (Method method : methods) {
            String varName = toVarName(method);
            map.put(method.getName(), varName);
        }
        return (C) Proxy.newProxyInstance(YamlConfigurationLoader.class.getClassLoader(),
                new Class[]{type},
                new ConfigurationHandler(yaml.loadAs(in, Map.class)));
    }

    private String toVarName(Method method) {
        String name = method.getName();
        if (name.startsWith("get")) {
            return name.substring(3,4).toLowerCase() + name.substring(4);
        }
        if (name.startsWith("is")) {
            return name.substring(2,3).toLowerCase() + name.substring(3);
        }
        return name;
    }

    private <C extends ServiceConfiguration> List<Method> gatherMethods(Class<C> type) {
        List<Method> res = new ArrayList<>();
        res.addAll(Arrays.asList(type.getDeclaredMethods()).stream()
                .filter(meth -> !Modifier.isStatic(meth.getModifiers()))
                .collect(Collectors.toList()));
        Class<?> sup = type.getSuperclass();
        if ((sup!= null) && ServiceConfiguration.class.isAssignableFrom(sup)) {
            res.addAll(gatherMethods((Class<C>) type.getSuperclass()));
        }
        return res;
    }

    private class ConfigurationHandler implements InvocationHandler {

        private final Map config;

        public ConfigurationHandler(Map config) {
            this.config = config;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            String lookup;
            if (name.startsWith("get")) {
                lookup = name.substring(3).toLowerCase();
            }
            else if (name.startsWith("is")) {
                lookup = name.substring(2).toLowerCase();
            }
            else {
                throw new ConfigurationExeption(format("Don't know how to resolve data for '%s'", name));
            }
            Object value = config.get(lookup);
            if (value != null) {
                Class<?> returnType = method.getReturnType();
                if (returnType.isAssignableFrom(value.getClass())) {
                    return value;
                }
                throw new ConfigurationExeption(format("Cannot convert value of type %s to type  %s for method %s",
                        value.getClass().getSimpleName(), returnType.getSimpleName(), method.getName()));
            }
            return null;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, ConfigurationExeption {
        YamlConfigurationLoader loader = new YamlConfigurationLoader();
        TestConfig conf = loader.load(new FileInputStream(args[0]), TestConfig.class);
        System.out.println(conf.getName());
        System.out.println(conf.getSize());
        System.out.println(conf.isAllowed());
    }

}
