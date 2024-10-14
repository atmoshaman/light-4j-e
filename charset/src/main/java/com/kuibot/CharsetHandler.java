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
import com.networknt.handler.Handler;
import com.networknt.handler.MiddlewareHandler;
import com.networknt.utility.ModuleRegistry;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ResponseCommitListener;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a component used to extend the Light-4j core library, which is used to control
 * the charset of response content.
 * <p>
 * Created by shaman on 13/10/24.
 *
 * @author Shaman Du
 * @since 2.1.37
 */
public class CharsetHandler implements MiddlewareHandler {
    static final Logger logger = LoggerFactory.getLogger(CharsetHandler.class);

    public static final String CONFIG_NAME = "charset";
    static CharsetConfig config = (CharsetConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, CharsetConfig.class);

    private volatile HttpHandler next;

    public CharsetHandler() {
    }

    private boolean isTextContentType(String contentType) {
        if (config.getContentTypeList() == null) return false;

        if (contentType == null) {
            return false;
        } else {
            return config.getContentTypeList().contains(contentType);
        }
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        if (logger.isDebugEnabled()) logger.debug("CharsetHandler.handleRequest starts.");

        exchange.addResponseCommitListener(new ResponseCommitListener() {
            @Override
            public void beforeCommit(HttpServerExchange exchange) {
                try {
                    String contentType = exchange.getResponseHeaders().getFirst(Headers.CONTENT_TYPE);
                    if (contentType != null) {
                        String baseContentType = contentType.split(";")[0].trim().toLowerCase();

                        if (isTextContentType(baseContentType)) {
                            // Check whether the charset parameter is already included
                            if (!contentType.toLowerCase().contains("charset")) {
                                String newContentType = baseContentType + "; charset=" + config.getCharset();
                                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, newContentType);

                                if (logger.isDebugEnabled()) {
                                    logger.debug("CharsetHandler: change content type from {} to {}", baseContentType, newContentType);
                                }
                            }
                        }
                    } else {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain; charset=UTF-8");
                    }
                } catch (Exception e) {
                    logger.error("Exception in ResponseCommitListener:", e);
                }
            }
        });

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        if (logger.isDebugEnabled()) logger.debug("CharsetHandler.handleRequest ends.");
        Handler.next(exchange, next);
    }

    @Override
    public HttpHandler getNext() {
        return next;
    }

    @Override
    public MiddlewareHandler setNext(final HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void register() {
        ModuleRegistry.registerModule(CharsetConfig.CONFIG_NAME, CharsetHandler.class.getName(), Config.getNoneDecryptedInstance().getJsonMapConfigNoCache(CharsetConfig.CONFIG_NAME), null);
    }

    @Override
    public void reload() {
        config = (CharsetConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, CharsetConfig.class);
        ModuleRegistry.registerModule(CharsetConfig.CONFIG_NAME, CharsetHandler.class.getName(), Config.getNoneDecryptedInstance().getJsonMapConfigNoCache(CharsetConfig.CONFIG_NAME), null);
        if (logger.isInfoEnabled()) logger.info("CharsetHandler is reloaded.");
    }
}
