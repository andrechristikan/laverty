/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sama.carrental;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 *
 * @author skyfy
 */
public class Runner {

    private static final String CORE_JAVA_DIR = "/src/main";

    public static void runClusteredExample(Class clazz) {
        runExample(CORE_JAVA_DIR, clazz, new VertxOptions().setClustered(true), null);
    }

    public static void runClusteredExample(Class clazz, VertxOptions options) {
        runExample(CORE_JAVA_DIR, clazz, options.setClustered(true), null);
    }
      
    public static void runExample(Class clazz) {
        runExample(CORE_JAVA_DIR, clazz, new VertxOptions().setClustered(false), null);
    }

    public static void runExample(Class clazz, DeploymentOptions options) {
        runExample(CORE_JAVA_DIR, clazz, new VertxOptions().setClustered(false), options);
    }

    public static void runExample(String exampleDir, Class clazz, VertxOptions options, DeploymentOptions deploymentOptions) {
        runExample(exampleDir + clazz.getPackage().getName().replace(".", "/"), clazz.getName(), options, deploymentOptions);
    }

    public static void runScriptExample(String prefix, String scriptName, VertxOptions options) {
        File file = new File(scriptName);
        String dirPart = file.getParent();
        String scriptDir = prefix + dirPart;
        runExample(scriptDir, scriptDir + "/" + file.getName(), options, null);
    }

    public static void runExample(String exampleDir, String verticleID, VertxOptions options, DeploymentOptions deploymentOptions) {
        if (options == null) {
            // Default parameter
            options = new VertxOptions();
        }
        // Smart cwd detection

        // Based on the current directory (.) and the desired directory (exampleDir), we try to compute the vertx.cwd
        // directory:
        try {
            // We need to use the canonical file. Without the file name is .
            File current = new File(".").getCanonicalFile();
            if (exampleDir.startsWith(current.getName()) && !exampleDir.equals(current.getName())) {
                exampleDir = exampleDir.substring(current.getName().length() + 1);
            } else {
                exampleDir = current.getPath() + CORE_JAVA_DIR;
            }
        } catch (IOException e) {
            // Ignore it.
        }

        System.setProperty("vertx.cwd", exampleDir); //  + "webroot"
        Consumer<Vertx> runner = vertx -> {
            try {
                if (deploymentOptions != null) {
                    vertx.deployVerticle(verticleID, deploymentOptions);
                } else {
                    vertx.deployVerticle(verticleID);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (options.isClustered()) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
            Vertx vertx = Vertx.vertx(options);
            runner.accept(vertx);
        }
    }
    
}
