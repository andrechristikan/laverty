/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.helper;

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
public class JwtHelper {
    
    private final Vertx vertx;
    private final JsonObject jwtConfig;
    
    protected JsonObject mainConfigs;
    
    public JwtHelper(Vertx vertx){
        this.vertx = vertx;
        
        // Message & Config
        this.setConfigs();
    
        this.jwtConfig = this.mainConfigs.getJsonObject("jwt");
    }
    
    private void setConfigs(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.mainConfigs = jMapData.get("configs.main");
    }

    
    public JWTAuth getSettingJwtAuth(){
        
        JWTAuth jwt;
        String jwtType = this.jwtConfig.getString("type");
        if (jwtType.equalsIgnoreCase("rsa")) {
            // openssl genrsa -subj -out jwt.pem 2048
            // openssl pkcs8 -topk8 -inform PEM -in jwt.pem -out jwt_private_key.pem -nocrypt
            // openssl rsa -in jwt.pem -outform PEM -pubout -out jwt_public_key.pem
            jwt = JWTAuth.create(this.vertx, new JWTAuthOptions()
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm("RS256")
                            .setPublicKey(this.jwtConfig.getString("rsaPublicKey"))
                            .setSecretKey(this.jwtConfig.getString("rsaPrivateKey"))
                    ));
        } else if (jwtType.equalsIgnoreCase("jceks")) {
            // keytool -genkeypair -keystore cirrus.jceks -storetype jceks -storepass 1234567890
            //    -keyalg EC -keysize 256 -alias ES256 -keypass 1234567890 -sigalg SHA256withECDSA
            //    -dname "CN=Sudito Lie,OU=Synectics,O=Gtech Digital Asia,L=Jakarta,ST=DKI,C=ID" -validity 360
            jwt = JWTAuth.create(this.vertx, new JWTAuthOptions()
                    .setKeyStore(new KeyStoreOptions()
                            .setType("jceks")
                            .setPath(this.jwtConfig.getString("keyStore"))
                            .setPassword(this.jwtConfig.getString("secret"))));
        } else {
            jwt = JWTAuth.create(this.vertx, new JWTAuthOptions()
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm("HS256")
                            .setPublicKey(this.jwtConfig.getString("symmetricPublicKey"))
                            .setSymmetric(true)));
        }
        
        return jwt;
    }
    
    
    public JsonObject GetTokenJwt(JWTAuth jwt, String role, JsonObject jUser){
        int tokenTimeout = this.jwtConfig.getInteger("tokenTimeout");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        List<String> authorities = new ArrayList<>();
        authorities.add(role.trim());

        
        String tokenJwt = jwt.generateToken(
                            jUser,
                            new JWTOptions()
                                .setExpiresInMinutes(tokenTimeout)
                                .setPermissions(authorities)
        );
        
        JsonObject token = new JsonObject();
        Long expired_time = ts.getTime()+tokenTimeout;
        token.put("token", tokenJwt);
        token.put("expired_time", expired_time.toString());
        
        return token;
    }
    
    private String getTokenFromHeader(RoutingContext ctx){
        
        String authorization = ctx.request().headers().get(HttpHeaders.AUTHORIZATION);
        String[] parts = authorization.split(" ");
        String token = parts[1];
        
        return token;
    }
    
}
