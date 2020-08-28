package me.legrange.service;

import java.io.InputStream;

public interface ConfigurationLoader {

    <C extends ServiceConfiguration> C load(InputStream in, Class<C> type);
}
