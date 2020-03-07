package com.andrechristikan.verticle;

import io.vertx.core.Promise;

public interface  VerticleInterface {

    void start(Promise<Void> promise) throws Exception;
}
