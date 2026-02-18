[![Dueuno on X](https://img.shields.io/twitter/follow/dueunoframework?style=social)](https://x.com/dueunoframework)

# Dueuno Application Template

With [Dueuno](https://dueuno.com) you can build Back-Office Web Applications with a single programming language: [Apache Groovy](https://groovy-lang.org).

## Getting Started
Make sure you have [Git](https://git-scm.com) and a [JDK](https://www.oracle.com/java/technologies/downloads/) installed. The minimum requirement is Java 25.
```
$ git clone https://github.com/dueuno-projects/dueuno-app-template
$ cd dueuno-app-template
$ ./gradlew bootRun
```

> The first run may take a while, as all required dependencies will be downloaded.

You should see something like this:

```
>                                                                            <
>                         _                                                  <
>                        | |   https://dueuno.com                            <
>                      __| |_   _  ___ _   _ _ __   ___                      <
>                     / _` | | | |/ _ \ | | | '_ \ / _ \                     <
>                    | (_| | |_| |  __/ |_| | | | | (_) |                    <
>                     \__,_|\__,_|\___|\__,_|_| |_|\___/                     <
>                               E L E M E N T S                              <
>                                                                            <

Grails application running at http://localhost:8080 in environment: development
```

Open your browser at [https://localhost:8080](https://localhost:8080) then:
- Login with `super`/`super` to manage the whole application
- Login with `admin`/`admin` to manage the `DEFAULT` tenant

![Dueuno Application Login Screen](./README.png)

## New Application
You can create a new application from this template project with an AI agent or manually.

### AI Agent
With the `JetBrains AI Assistant` plugin for `IntelliJ IDEA` + the `OpenAI Codex` plugin (or any other AI Agent setup in your preferred IDE) execute the following prompts:

1. `Rename the application and the corresponding packages to "myappname"`
2. `Create a new CRUD main feature in package "myappname" for the entity “person” with columns: firstname, lastname, age, gender. Respect the naming conventions for GORM entities, services and controllers.`

### Manually 
1. Find and replace the string `dueunoapp` across all project files with your chosen name
2. Rename the `dueunoapp` package accordingly
3. You can find feature implementation examples in the `template` package
4. Customize the application banner `/src/main/resources/banner.txt`

## Create the `.jar` Executable
1. Run `./gradlew bootJar`
2. The application `.jar` file will be generated under `/biuld/libs`
3. Run the application with `java -jar dueunoapp-1.0-SNAPSHOT.jar`

## Project Documentation
Documentation sources are under `/src/docs`.
1. Write the content using [Asciidoc](https://asciidoctor.org/docs/asciidoc-syntax-quick-reference/)
2. Generate the documentation with `./gradlew asciidoctor`
3. The generated HTML files will be available in the `/docs` directory.

Edit this README:
- Write the content using [Markdown](https://www.markdownguide.org/cheat-sheet/)

## Learn More
- Documentation: https://dueuno.com/docs
- Community: https://discord.gg/6yWnmT2hBj

## License
Dueuno Elements is Open Source software released under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).

---

```
It is not what you do,
it is how you do it.

Happy coding :)
```