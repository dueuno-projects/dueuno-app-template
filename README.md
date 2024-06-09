[![Apache 2.0 license](https://img.shields.io/badge/License-APACHE%202.0-green.svg?logo=APACHE&style=flat)](https://opensource.org/licenses/Apache-2.0)
[![Dueuno on X](https://img.shields.io/twitter/follow/dueunoframework?style=social)](https://x.com/dueunoframework)

# Dueuno Application Template

[Dueuno Elements](https://github.com/dueuno-projects/dueuno-elements) is a framework built on top of the [Grails Framework](https://grails.org) to build Backoffice web applications (aka Internal Tools) with a single programming language: the amazing [Apache Groovy](https://groovy-lang.org).

## Getting Started

You need Java Development Kit (JDK) 17 installed. We support only LTS Java releases.

1. Run the application with `gradlew bootRun`
2. Visit http://localhost:8080
3. Login with `admin/admin` to manage the default tenant or with `super/super` to manage the whole application

## Create a new app

To create a new app from this template:

1. Find/replace the string `dueunoapp` in all project files with a name of your choice
2. Rename the `dueunoapp` package with a name of your choice

## Create executable jar

1. Run `gradlew bootJar`
2. The application executable file will be generated under the `/biuld/libs` directory
3. Run the application with `java -jar dueunoapp-1.0-SNAPSHOT.jar`

## Guides

You can find the latest documentation here: https://dueuno.com/docs/

## License

Dueuno Elements is an Open Source software released under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).

## Please remember...

```
It's not what you do,
but how you do it.
That makes the difference.

Happy coding :)
```