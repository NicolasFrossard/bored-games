# BoredGames

How to start the BoredGames application
---

1. Run `mvn clean install` to build the server
2. Run `yarn build` to build the client
3. Start application with `java -jar target/com.boredgames.server-1.0-SNAPSHOT.jar server config.yml`
4. Go to `http://localhost:8080`

Development mode
---

1. Run `BoredGamesApplication` in debug mode within your IDE
2. Run `yarn dev` to run the client in dev mode. It will automatically update when the client code is being modified.

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
