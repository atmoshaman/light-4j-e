/*
 * Copyright (c) 2024 Kuibot.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kuibot;

import com.networknt.config.Config;

import java.util.List;
import java.util.Map;

/**
 * CharsetConfig is singleton, and it is loaded from charset.yml in the config folder.
 * Created by shaman on 13/10/24.
 *
 * @author Shaman Du
 * @since 2.1.37
 */
public class CharsetConfig {
    private static final String ENABLED = "enabled";
    private static final String CHARSET = "charset";
    private static final String CONTENT_TYPE_LIST = "contentTypeList";

    private Map<String, Object> mappedConfig;
    public static final String CONFIG_NAME = "charset";

    private final Config config;
    // In order to maintain consistency with the Light-4j core library, the default charset is set here to ISO-8859-1.
    private String charset = "ISO-8859-1";
    private boolean enabled;
    private List<String> contentTypeList;

    private CharsetConfig() {
        this(CONFIG_NAME);
    }

    private CharsetConfig(String configName) {
        config = Config.getInstance();
        mappedConfig = config.getJsonMapConfigNoCache(configName);

        setConfigData();
    }

    public static CharsetConfig load() {
        return new CharsetConfig();
    }

    public static CharsetConfig load(String configName) {
        return new CharsetConfig(configName);
    }

    public void reload() {
        mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);

        setConfigData();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCharset() {
        return charset;
    }

    public List<String> getContentTypeList() {
        return contentTypeList;
    }

    public Map<String, Object> getMappedConfig() {
        return mappedConfig;
    }

    private void setConfigData() {
        Object object = getMappedConfig().get(ENABLED);
        if (object != null) enabled = Config.loadBooleanValue(ENABLED, object);
        object = getMappedConfig().get(CHARSET);
        if (object != null) charset = (String) object;
        object = getMappedConfig().get(CONTENT_TYPE_LIST);
        if (object != null) contentTypeList = (List<String>) object;
    }
}
