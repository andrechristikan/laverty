# Laverty For RestFul API with Postgres (Vert-x Proxy | Java Version) - STILL ON DEVELOPING
Inspired from laravel framework. 
This project wanna be like mini-laravel framework. 
~~This will save your time while developing apps.~~

Tbh, i have no idea with my code. 
I just put everything that i know about Vert-x (Java) based 3 years experience be Backend Engineer. Hope you gonna like it. Enjoy~

## Getting Started
Let start from the bottom
> Minimum requirement: 
1. Understand Java Language (Beginner - Intermediate)
2. Understand Asynchronous Programming (Intermediate)
3. [Know what vert.x is](https://vertx.io)
4. [Vert.x Proxy](https://vertx.io/docs/vertx-service-proxy/java/)

> Tech stack
1. Java 11.0.6
2. Postgres 12.2
3. Maven 10.14.6

## Installation
Guide for install this project
### Production - Linux env (NEED TO TEST)
1. Open terminal, and pull this project
2. Goto into project
3. Set your config directory in Project MainClass `com.andrechristikan.http.MainVerticle` to `classes/configs/vertx.json`
4. Set config, config in `src/main/resources/configs`
   - Rename `vertx-example.json` to `vertx.json`
   - Open `vertx.json`
   - Adjust your setting to `production`
   - Don't forget to change the `environment` value to `production`
   - Resource directory for production is `classes`
5. Set the messages `src/main/resources/messages`
   - You can change the language with create the new package in `src/main/resources/messages` and copy all messages from default language files to the new language folder (ex:en to id). 
   - Then open the json file and change **the value** not **the key**.
6. Run `initial.sql` in project folder to inject data into Postgres
7. Build your project `mvn clean install`, **if error --....**
8. Run project `java -jar target/FileName.jar`

### Development - With Intellij IDE (TESTED ON MAC MOJAVE)
1. Set the Project Structure
    - Root : Directory of this project
    - Source : 
        1. `src/main/java`
        2. `target/generated-sources/annotations`
    - Resources : `src/main/resources`
    - Exclude : 
        1. `target/`
        2. `.idea/`
2. Set IDE Runner
    - Use `Template Application`
    - Main class : `io.vertx.core.Launcher`
    - Program Argument : `run com.andrechristikan.http.MainVerticle`
    - Working Directory : `projctDir/src/main/resources`
    - Before Lunch
        1. Add `Run Maven Goal`
        2. Fill `clean install`
        3. Done, save all configuration
3. Set your config directory in Project MainClass `com.andrechristikan.http.MainVerticle` to `configs/vertx.json`
4. Set config, config in `src/main/resources/configs`
   - Rename `vertx-example.json` to `vertx.json`
   - Open `vertx.json`
   - Adjust your setting to `local`
   - Don't forget to change the `environment` value to `local`
   - Resource directory for production is `resources`
5. Set the messages `src/main/resources/messages`
   - You can change the language with create the new package in `src/main/resources/messages` and copy all messages from default language files to the new language folder (ex:en to id). 
   - Then open the json file and change **the value** not **the key**.
6. Run `initial.sql` in project folder to inject data into Postgres
7. Run project, if you using MAC press `Control + R` on the keyboard
   
### Development - With Netbeans (TESTED ON WINDOWS 10)
1. Open project with Netbeans
2. Setting the project
    - Right click -> properties at the project name in side bar
    - Goto the `run` tap
    - Make sure if main class is `com.andrechristikan.http.MainVerticle` and working directory is `directory this project`
3. Set your config directory in Project MainClass `com.andrechristikan.http.MainVerticle` to `configs/vertx.json`
4. Set config, config in `src/main/resources/configs`
   - Rename `vertx-example.json` to `vertx.json`
   - Open `vertx.json`
   - Adjust your setting to `local`
   - Don't forget to change the `environment` value to `local`
   - Resource directory for production is `resources`
5. Set the messages `src/main/resources/messages`
   - You can change the language with create the new package in `src/main/resources/messages` and copy all messages from default language files to the new language folder (ex:en to id). 
   - Then open the json file and change **the value** not **the key**.
6. Run `initial.sql` in project folder to inject data into Postgres
7. Build this project, press `shift + f11`
8. Run project, press `f6` or `ctrl + f5`

## Example
Some example from this project

### Register New Verticle
```java
    public class LoginVerticle extends CoreVerticle implements  VerticleInterface{

        @Override
        public void start(Promise<Void> promise) throws Exception {
    
            messages = GeneralHelper.setMessages(vertx);
            configs = GeneralHelper.setConfigs(vertx);
    
            ServiceBinder binder = new ServiceBinder(this.vertx);
            binder.setAddress("event-bus-address").register(LoginService.class, new LoginServiceImplement(this.vertx));
            
            promise.complete();
        }
    
    }
```

### Controller
```java
    public class LoginController extends CoreController implements ControllerInterface {
    
        protected static LoginService service;
        
        public LoginController(Vertx vertx){
            super(vertx);
        }
    
        @Override
        public void setService(){
            service = LoginService.createProxy(coreVertx,"event-bus-address");
        }
    
        @Override
        public void login(RoutingContext ctx) {

            // this will hit the event bus service
            service.login(funct -> {
                if(funct.succeeded()){
                    // If success
                }else{
                    // If failed
                }
            });
        }
        
    }
```


### Model
```java
public class UserModel extends CoreModel {
    
    public UserModel(Vertx vertx, Transaction trans){
        super(vertx, trans);

        tableName = "users";
        service = "users";
        
        // you can change the primary key name from this
        primaryKeyName = "id";
    }
    
    
    /* 
        This is mandatory
        Set column from this function
    */ 
    @Override
    protected ArrayList<String> setColumns(){
        
        ArrayList<String> columns = new ArrayList<>();
        columns.add("id");
        columns.add("role_id");
        columns.add("username");
        columns.add("password");
        columns.add("email");
        columns.add("created_at");
        columns.add("updated_at");
        columns.add("last_login");
        
        return columns;
    }
    
    /* 
        This is optional
        This count type must same with count of column
    */ 
    
    @Override
    protected Map<String, String> setColumnsName(){
        
        Map<String, String> columnsName = new HashMap<>();
        columnsName.put("id","user_id");
        columnsName.put("role_id","role_id");
        columnsName.put("username","user_name");
        columnsName.put("password","password");
        columnsName.put("email","email_user");
        columnsName.put("created_at","created_at");
        columnsName.put("updated_at","updated_at");
        columnsName.put("last_login","last_login");
        
        return columnsName;
    }
    
    
    /* 
        This is mandatory
        This count type must same with count of column
        Support for Type
        - UUID
        - Timestamptz
        - Integer
        - Date
        - Datetime
        - Timestamp
        - Double
        - Float
        - Number
        - Boolean
    */ 
    @Override
    protected Map<String, String> setColumnsType(){
        
        Map<String, String> columnsType = new HashMap<>();
        columnsType.put("id","uuid");
        columnsType.put("role_id","string");
        columnsType.put("username","string");
        columnsType.put("password","string");
        columnsType.put("email","string");
        columnsType.put("created_at","timestamptz");
        columnsType.put("updated_at","timestamptz");
        columnsType.put("last_login","timestamptz");
        
        return columnsType;
    }
```

#### Init model class
```java
    // vertx from "io.vertx.core.Vertx" class
    // trans from "io.vertx.sqlclient.Transaction" class
    UserModel user = new UserModel(vertx, trans);

```

####  Use model to select
```java
    // Set the columns
    ArrayList <String> columns = new ArrayList<>();
    columns.add("role_id");
    columns.add("username");
    columns.add("email");

    // Select columns that you want
    user.select("id")
        .select(columns)
        .select("password")
        .findOne("primary-key")
    .setHandler(select -> {

        // Get the result
        JsonObject data = user.first();
        String data = user.first().asString();
        ...
    });




    // This will get all columns base on the model that you created
    user.findOne("primary-key").setHandler(select -> {
        ...
   });

    


    // If you to select more than one, do this
    // This will get all columns base on the model that you created
    user.findAll().setHandler(select -> {

        // Get the result
        JsonObject data = user.get();
        String data = user.get().asString();
        ...
     });




    // Also you can use where, limit, and order by in the select statement
    user.select(columns)
        .where("username","=","user")
        .where("role_id","like","%user%")
        .limit("10")
        .orderBy("role_id","asc")
        .findAll()
    .setHandler(select -> {
        ...
    });
```

####  Use model to insert
```java
    // Set value of columns
    user.columnsValue.put("role_id", "user");
    user.columnsValue.put("username", "user");
    user.columnsValue.put("password", "12345");
    user.columnsValue.put("email", "user@mail.com");
    
    // Save or insert new data
    user.save().setHandler(insert -> {

        // You also can get the new data
        JsonObject data = user.first();
        String data = user.first().asString();
        ...
    });
    



    // More example
    Map<String, String> columnsValue = new HashMap<>();
    columnsValue.put("role_id", "user");
    columnsValue.put("username", "user");
    columnsValue.put("password", "12345");
    columnsValue.put("email", "user@mail.com");

    // Insert with other method
    user.insert(columnsValue).setHandler(insert -> {
        ...
    });
```

####  Use the model to update
```java
    // Select before update
    user.findOne("primary-key").setHandler(select -> {
        
        // Set new value
        user.columnsValue.replace("role_id", "user");
        user.columnsValue.replace("username", "andreck");
        user.columnsValue.replace("password", "123456");
        user.columnsValue.replace("email", "andreck@gmail.com");
        
        // Update the data
        user.saveUpdate().setHandler(update -> {

            // You also can get the updated data
            JsonObject data = user.first();
            String data = user.first().asString();
           ... 
        });
    });




    // More example for update
    Map<String, String> columnsValue = new HashMap<>();
    columnsValue.put("role_id", "user");
    columnsValue.put("username", "user");
    columnsValue.put("password", "12345");
    columnsValue.put("email", "user@mail.com");

    // Update the data
    user.where("username","=","user")
        .where("role_id","=","user")
        .update(columnsValue, "primary-key")
    .setHandler(update -> {
        ...
    });
```

####  Use the model to delete
```java
    // Delete user with primary key
    user.findOne("primary-key").setHandler(select -> {

        // Delete the data
        user.where("username","=","user")
            .delete()
        .setHandler(delete -> {
           ... 
        });
    });




    // More example for delete
    user.where("username","=","user")
        .delete("primary-key")
    .setHandler(delete -> {
        ...
    });
```

####  Model count
```java
    // Count of findOne
    user.findOne("primary-key").setHandler(select -> {
        user.count().setHandler(count -> {
            String result = count.result();
            ...
        });
    });
    



    // Count of findAll
    user.findAll().setHandler(select -> {
        user.count().setHandler(count -> {
            String result = count.result();
            ...
        });
    });




    // Count with where statement
    user.where("username","=","user")
        .count()
    .setHandler(count -> {
         String result = count.result();
         ...
     });
```

#### How to get the result of model
```java
    user.first(); // this will return as JsonObject
    user.get(); // this will return as JsonArray

    // this will return as String
    user.first().asString();
    user.get().asString();
```

### Route
```java

```

### Exception
```java
    public class DefaultException extends CoreException implements ExceptionInterface{
        
        public DefaultException(Vertx vertx){
            super(vertx);
        }
    
        @Override
        public final void handler(RoutingContext ctx){
            // put your code in here
        }
    
    }

    // Use the middleware in route
    router.route("/api/v1/*").failureHandler(DefaultException::handler);
```
### Middleware
```java
    public class NewMiddleware extends CoreMiddleware implements MiddlewareInterface{
        
        public NewMiddleware(Vertx vertx){
            super(vertx);
        }
    
        @Override
        public void handler(RoutingContext ctx){
            // put your code in here
        }
    }

    // Use the middleware in route
    router.route("/api/v1/*").handler(NewMiddleware::handler);
```

### Authorization
```java
    // Admin authorization
    String role = "admin"; // change this if you want to other authorization
    String authorization = JwtHelper.getTokenFromHeader(RoutingContext);
    JWTAuth jwtAuthConfig = this.jwtHelper.getSettingJwtAuth();

    if (authorization != null) {
        String[] parts = authorization.split(" ");
        String token = parts[1];

        jwtAuthConfig.authenticate(new JsonObject().put("jwt", token), checked -> {
            if (checked.succeeded()) {
                User user = checked.result();

                user.isAuthorized(role, hndlr -> {
                    if(hndlr.succeeded()){
                        boolean hasAuthority = hndlr.result();
                        if (hasAuthority) {
                            // If Admin
                            ...
                        } else {
                            // If Not Admin
                            ...
                        }
                    }else{
                        // If authorization admin failed
                        ...
                    }
                });
            }else{
                // If token invalid
                ...
            }
        });
    }else{
        // If dont have token in request header
        ...
    }
```

### Env
```json
    {
        "language":"en",
        "environment":"local",
        "local":{
            "http-server": {
                "port": "8181",
                "address": "localhost"
            },
            "database":{
                "core":{
                    "driverName": "org.postgresql.Driver",
                    "host": "localhost",
                    "port": "5432",
                    "name": "andrechristikan_vertx",
                    "user": "andrechristikan",
                    "password": "",
                    "poolMinSize": 1,
                    "poolMaxSize": 10
                }
            },
            "resources-directory": "resources",
            "upload-files":{
                "delete-on-end":"true",
                "folder":{
                    "default":"files-uploaded",
                    "images":{
                        "default":"files-uploaded/defaults/general.jpg",
                        "vehicle":"vehicles",
                        "workshop":"workshops",
                        "user-customer":"users-customer",
                        "driver":"drivers",
                        "invoice":"invoices",
                        "service-detail":"service-details"
                    },
                    "pdf":{
                        "contract":""
                    }
                }
            }
        },
        "production":{
            "http-server": {
                "port": "8181",
                "address": "localhost"
            },
            "database":{
                "core":{
                    "driverName": "org.postgresql.Driver",
                    "host": "localhost",
                    "port": "5432",
                    "name": "andrechristikan_vertx",
                    "user": "postgres",
                    "password": "123456",
                    "poolMinSize": 1,
                    "poolMaxSize": 10
                }
            },
            "resources-directory": "resources",
            "upload-files":{
                "delete-on-end":"true",
                "folder":{
                    "default":"files-uploaded",
                    "images":{
                        "default":"files-uploaded/defaults/general.jpg",
                        "vehicle":"vehicles",
                        "workshop":"workshops",
                        "user-customer":"users-customer",
                        "driver":"drivers",
                        "invoice":"invoices",
                        "service-detail":"service-details"
                    },
                    "pdf":{
                        "contract":""
                    }
                }
            }
        },
        "jwt": {
            "type": "symmetric",
            "rsaPublicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyN/Iq2rDoDZuE9FSIyIAfGOabwdgA0ryxLdOfr1947Q68uPExG8XM/m9mdu4H+uIVI+GEEA+V9LJQQyKB+4O2XwGvrFOpEW/amA9VpnylURSpDiTq9dfLRUf4nKpiib+1u0o117CFKnXz45C70bYBlR7jiGEMPvSFk/Zv95+Gd40ExHNVhWz3+/zXgQcPH6o3t/iKbmeJlPwB/WeEOJtNdpc9rCTRXD0pEqtRr4IE4artfEdo3L78Z6CZ9hij+6CGbY4XZYP55nL2q3QL8htSxyl+qW7XxhaW3miIvxz7id8yQWPM/2jaWoerq03Pmw0+AxHWiBKyPgmbWPiNY+biwIDAQAB",
            "rsaPrivateKey": "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDI38irasOgNm4T0VIjIgB8Y5pvB2ADSvLEt05+vX3jtDry48TEbxcz+b2Z27gf64hUj4YQQD5X0slBDIoH7g7ZfAa+sU6kRb9qYD1WmfKVRFKkOJOr118tFR/icqmKJv7W7SjXXsIUqdfPjkLvRtgGVHuOIYQw+9IWT9m/3n4Z3jQTEc1WFbPf7/NeBBw8fqje3+IpuZ4mU/AH9Z4Q4m012lz2sJNFcPSkSq1GvggThqu18R2jcvvxnoJn2GKP7oIZtjhdlg/nmcvardAvyG1LHKX6pbtfGFpbeaIi/HPuJ3zJBY8z/aNpah6urTc+bDT4DEdaIErI+CZtY+I1j5uLAgMBAAECggEBALUs0it5wjPwo9wQActaUaHfnFzkA/80TD6NLqk1dCQ6XKjSMpkRsZXZfN8Ww71WjMHoKXzi1NgVcBvRmXWJi68xJEw+U7XqirkBUJGZjKiA1SD1PovRVzCsSvPNg4jtnxBvG1UVTGuivNmhcFGsTg93h3EsXbRojJY/MyDRJl9g5/H7wEK1BM3kqTN0gULtNBzTXwNVUD9RgdXd673PyFZEj3PH9a1P0cn1ZIcPJbeKybmO+MgpgWhvRUPOI0l6F+fQLMt5cQ4/uOhfXufY9r1/4K0nBL79Uf27NFUGmt9PEkzbNQc+/kRJYk9McJVSk6VMI/dQz9elg4mAq6oTKeECgYEA5aBph26tgc0XGCWO+dI7sdln7c7Q5auyvAsZmQRoYsSjodDCT9iuOMvWyQE8GnggOmmkCmtt+9Zm8yjojs0Pnb2BUl+PtQZzT4cx6PR6f9N6QrZn+FsGN/lAAwrIAy0qOm50mcnTbELu6u8s6CXZxN3s4yibxN/Jlz+T5visNNMCgYEA3/H6irzG0/wqA0frkHl+5nOAenr01OeEugg1HesAOGkIfKAtb7ULtG6VNirC4Te3wRSesaN31zSaFJKRuqkzIYEdQfcIoXmCIZ6AtrPwJvibKb0tyjJOrRkfcfcqJFktezFj3aCc7Q01JI1hdMIzNKgSyqgK5/pcbVmj7B6nq2kCgYEAzjxFq5K3GQaHgHNzN2MfpDt8AXUn369XJ1bc1bbEvjiYMqRZcPdOespxDTUjsy5C29O3tngIXxCQhEIwpNj99pGWjdIwMskpVbs6E11IjHf2reY/+SpfqnQdjt3nCBtPew1rghhn4Cs5hB3uek5MiWk88kStqqPH3iUHBIJBwGsCgYArCOMumgxTXFfufXmlL9PVEUkVQF+gMkVtQnqCn1BRhs2ctWNF3jK7n6yI1jrQ8NYdAotkc5dqAa/CWFAs4Kr0u00WChTcgu8g6satVXLG6mtW4AXWn0hDN1q1mdu9VG3LCLi6NE0zuKAHwCuENnWEnu/NhfFiltgAI4O+sj1iUQKBgGJ+Sn6egSuv7m7b9aO2PXDB5+FYWaAPBl27lDRkyIW7LPAQJIwlXbcQTEZEN0+Z3c0C5IFOrRmTjA6sTJu5NSJQf1hHm8+pwUfazWeyopH4wl4TwbLsl2xRpB3WgfcvjJXCBSemjtwfP3KFOV43lBsTOcgPTbOK+OsNEp32fqKo",
            "symmetricPublicKey": "TbfLSIUopL6dr50nLemDNE4wXxFleN1O",
            "keyStore": "/working/java/synectics.cirrus/cert/cirrus.jceks",
            "secret": "1234567890",
            "tokenTimeout": 1000
        },
        "role":[
            "user",
            "super_admin"
        ],
        "cors":{
            "allow-origin":"*",
            "header":[
                "Content-Type",
                "X-Token",
                "X-User",
                "Authorization"
            ],
            "method":[
                "GET", 
                "POST", 
                "PUT", 
                "DELETE",
                "CONNECT",
                "HEAD",
                "OPTIONS",
                "OTHER",
                "PATCH",
                "TRACE"
            ]
        },
        "response":{
            "Access-Control-Allow-Origin":"*",
            "Access-Control-Allow-Methods":"GET, POST, PUT, DELETE, HEAD, OPTIONS",
            "Access-Control-Allow-Headers":"Origin, Accept, Authorization, Content-Type, X-Token, X-User",
            "Cache-Control":"no-cache",
            "Content-Type":"application/json"
        }
    }
```
## Development
Still on development. I will finish this project as soon as possible.

> Build With
* [Vert.x Proxy](https://vertx.io/docs/vertx-service-proxy/java/)
* [Vert.x Auth JWT](https://vertx.io/docs/vertx-auth-jwt/java/)
* [Vert.x Asynchronous Programming](https://vertx.io/docs/guide-for-java-devs/)


> Features & Todo :
- [x] New Http Server Class
- [x] New Helper Class
- [x] Friendly Folder Structure
- [x] Apps settings from File ( Vertx.json )
- [x] Support multi languages
- [x] New Exception Class
- [x] Basic Query Builder
- [x] New Exception Class
- [x] New Response Class
- [x] New Middleware Class
- [x] Jwt Authorization
- [x] Auth Class
- [x] New Controller Class
- [x] Dynamic Column Name
- [ ] Controllable from database (ongoing)
- [x] New Route Class
- [x] Vert-x Service Implementation
- [x] User Login Pattern
- [ ] Optimization Message Response and System
- [ ] Query Builder With Full Features (Join, Having, Group, Distinct, Raw Query, etc)
- [ ] Database Migration


### Authors
> [@andrechrisikan](https://github.com/andrechristikan) | [Instagram](https://instagram.com/andrechristikan) | [andrechrisikan@gmail.com](mailto:@andrechrisikan@gmial.com)

Skills
1. Program Languages
    - PHP (Laravel Framework and CI Framework)
    - Python (Flask Framework)
    - Java (Vert-x)
2. DevOps skill
    - Containers and configuration management tools (Docker)
    - Web server (Nginx, Apache)
    - Cloud service providers (AWS Cloud)
    - Scripting( Bash and Python)
    - Application Building
    - Linux Fundamentals (Ubuntu)
    - Source Code management (Git)
3. Database Skill
    1. Relational Database
        - Mysql
        - Postgres
        - MariaDB
    2. NoSQL Database
        - MongoDB

## Other Information
> Users Access App
1. username : admin, password : 123456
2. username : user, password : 123456

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
