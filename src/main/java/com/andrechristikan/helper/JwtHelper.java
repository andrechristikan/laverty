/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.helper;

import com.andrechristikan.core.CoreHelper;
import com.andrechristikan.http.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.web.RoutingContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Syn-User
 */
public class JwtHelper extends CoreHelper {


    protected JWTAuth jwt;

    public JwtHelper(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(DatabaseHelper.class);

        this.getSettingJwtAuth();
    }
    
    private void getSettingJwtAuth(){

        String jwtType = conf("main.jwt.type");
        if (jwtType.equalsIgnoreCase("rsa")) {
            // openssl genrsa -subj -out jwt.pem 2048
            // openssl pkcs8 -topk8 -inform PEM -in jwt.pem -out jwt_private_key.pem -nocrypt
            // openssl rsa -in jwt.pem -outform PEM -pubout -out jwt_public_key.pem
            this.jwt = JWTAuth.create(coreVertx, new JWTAuthOptions()
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm("RS256")
                            .setPublicKey(conf("main.jwt.rsaPublicKey"))
                            .setSecretKey(conf("main.jwt.rsaPrivateKey"))
                    ));
        } else if (jwtType.equalsIgnoreCase("jceks")) {
            // keytool -genkeypair -keystore cirrus.jceks -storetype jceks -storepass 1234567890
            //    -keyalg EC -keysize 256 -alias ES256 -keypass 1234567890 -sigalg SHA256withECDSA
            //    -dname "CN=Sudito Lie,OU=Synectics,O=Gtech Digital Asia,L=Jakarta,ST=DKI,C=ID" -validity 360
            this.jwt = JWTAuth.create(coreVertx, new JWTAuthOptions()
                    .setKeyStore(new KeyStoreOptions()
                            .setType("jceks")
                            .setPath(conf("main.jwt.keyStore"))
                            .setPassword(conf("main.jwt.keyStore"))));
        } else {
            this.jwt = JWTAuth.create(coreVertx, new JWTAuthOptions()
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm("HS256")
                            .setPublicKey(conf("main.jwt.symmetricPublicKey"))
                            .setSymmetric(true)));
        }
    }
    
    
    public JsonObject getTokenJwt(String role, JsonObject jUser){
        int tokenTimeout = parser.parseInt(conf("main.jwt.tokenTimeout"),1000);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        List<String> authorities = new ArrayList<>();
        authorities.add(role.trim());
        
        String tokenJwt = this.jwt.generateToken(
                            jUser,
                            new JWTOptions()
                                .setExpiresInMinutes(tokenTimeout)
                                .setPermissions(authorities)
        );
        
        JsonObject token = new JsonObject();
        long expired_time = ts.getTime()+tokenTimeout;
        token.put("token", tokenJwt);
        token.put("expired_time", Long.toString(expired_time));
        
        return token;
    }

    public JWTAuth getJwtAuth(){
        return this.jwt;
    }


    
    public static String getTokenFromHeader(RoutingContext ctx){
        return ctx.request().headers().get(HttpHeaders.AUTHORIZATION);
    }
    
}
