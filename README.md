# Vert.x-Proxy Example For RestFul API (Java)
I was tired using primitive vert.x with so many random resources.

## Getting Started
Inspired from laravel framework. ~~This will save your time while developing an apps.~~

> Minimum requirement: 
1. Understand Java Language (Beginner)
2. Understand Asynchronous Programming
3. [Know what vert.x is](https://vertx.io)
4. [Vert.x Proxy](https://vertx.io/docs/vertx-service-proxy/java/)

> Tech stack
1. Java 11..0.6
2. Postgresql 12.2
3. Maven 10.14.6

### Installing
> Production - Normal Installing / Linux env - NEED TO TEST
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

> Development - With Intellij IDE - TESTED ON MAC MOJAVE
1. Set the Project Structure
    - Root : Directory of this project
    - Source : 
        1. `src/main/java`
        2. `target/generated-sources/annotations`
    - Resources : `src/main/resources`
    - Exclude : 
        1. `target/`
        2. `.idea/`
2. Set Runner
    - Use `Template Application`
    - Main class : `io.vertx.core.Launcher`
    - Program Argument : `run com.andrechristikan.http.MainVerticle`
    - Working Directory : `projctDir/src/main/`
    - Before Lunch
        1. Add `Run Maven Goal`
        2. Fill `clean install`
        3. Complete
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
   
> Development - With Netbeans - TESTED ON WINDOWS 10
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

## Development
Still on development, i will finish this project as soon as possible.
~~Because I still have a life man.~~

> Build With
* [Vert.x Proxy](https://vertx.io/docs/vertx-service-proxy/java/)
* [Vert.x Auth JWT](https://vertx.io/docs/vertx-auth-jwt/java/)
* [Vert.x Asynchronous Programming](https://vertx.io/docs/guide-for-java-devs/)


> Features :
- Auth with JWT
- Dynamic config from database
- New Controller Class
- New Exception Class
- New Model Class
- Request Middleware 
- Promise and Future (Java Version)
- New Route Class
- Service and Implement Vert.x
- Env control app from JSON file
- Vert.x Proxy


> Todo :
- [x] Created Http Server (env from vertx.json)
- [x] Created Default Exception
- [x] Created Not Found Exception
- [x] Created Login Exception
- [x] Created Database Helper (env from vertx.json)
- [x] Created Parser Helper
- [x] Created Jwt Helper (env from vertx.json)
- [x] Created Auth For User Login
- [x] Created Auth For User Admin
- [x] Folder Structure
- [x] Route (Very simple route class)
- [x] Setting App From vertx.json File
- [x] All messages from files
- [x] Created Login controller
- [x] Model Class
- [x] User model
- [ ] User Service Implementation (Ongoing)
- [ ] Password Helper
- [ ] Flow login (Ongoing)
- [ ] Create Middleware Request

### Authors
> [@andrechrisikan](https://github.com/andrechristikan) | [Instagram](https://instagram.com/andrechristikan) | andrechrisikan@gmail.com

## Other
> Users Access App
1. username : admin, password : 123456
2. username : user, password : 123456

## License
This is totaly free ! :+1: :+1:

# ~~VERT.X IS NOT A FRAMEWORK, THIS IS JUST A TOOL. MAYBE MY PROJECT CAN BE A FRAMEWORK :XD~~
