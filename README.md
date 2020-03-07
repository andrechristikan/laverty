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
    - Working Directory : `projctDir/src/main/`
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
### Model
```java
    
public class UserModel extends AbstractModel {
    
    public UserModel(Vertx vertx, Transaction trans){
        super(vertx, trans);
        // logger = LoggerFactory.getLogger(UserModel.class); // Just in case if you want to put log with specific model
        
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

### Controller
```java

```

### Exception
```java

```
### Middleware
```java

```

### JWT Authorization
```java

```

### Env
```java

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
- [x] Auth Class
- [ ] New Controller Class (Restructuring)
- [ ] New Route Class (Restructuring)
- [ ] Vert-x Service Implementation
- [ ] User Login Pattern
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
