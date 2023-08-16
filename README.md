# Open Hospital API

[![Java CI with Maven](https://github.com/informatici/openhospital-api/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/informatici/openhospital-api/actions?query=workflow%3A%22Java+CI+with+Maven%22)

This is the API project of [Open Hospital][openhospital]: it exposes a REST API of the business logic implemented in the [openhospital-core project][core].

## Summary

  * [How to build [WIP]](#how-to-build-wip)
    + [Using Swagger-UI](#using-swagger-ui)
    + [Using Postman](#using-postman)
  * [How to deploy backend in docker environment](#how-to-deploy-backend-in-docker-environment)
  * [Cleaning](#cleaning)
  * [How to contribute](#how-to-contribute)
  * [Community](#community)
  * [Code style](#code-style)

<small>Table of contents generated with <i><a href='http://ecotrust-canada.github.io/markdown-toc/'>markdown-toc</a></i></small>


## How to build [WIP]

For the moment, to build this project you should 

 1. fetch and build the [core] project
    
        git clone https://github.com/informatici/openhospital-core.git
        cd openhospital-core
        mvn clean install -DskipTests=true
        
 2. clone and build this project
 
        git clone https://github.com/informatici/openhospital-api
        cd openhospital-api
        mvn clean install -DskipTests=true
        
 3. prepare settings from each rsc/*.dist file
 
        rsc/application.properties <- set a SHA-256 jwt token
        rsc/database.properties
        rsc/log4j.properties
        rsc/...
 
 4. set target/rsc/database.properties
 
        DB can be created with `docker-compose up` from `openhospital-core` or using a dedicated MySQL server
        
 5. start openhospital-api (in `target` folder)
 
        # Windows
        java -cp "openhospital-api-0.1.0.jar;rsc/;static/" org.springframework.boot.loader.JarLauncher

        # Linux
        java -cp "openhospital-api-0.1.0.jar:rsc/:static/" org.springframework.boot.loader.JarLauncher
        
 6. call services
    - URL base: http://localhost:8080
    - URL login: http://localhost:8080/auth/login
    - URL patients: http://localhost:8080/patients
    - URL swagger: http://localhost:8080/swagger-ui.html

You can see Swagger Api Documentation at: http://localhost:8080/swagger-ui/

![image](https://github.com/mwithi/openhospital-api/assets/2938553/b30eec4c-7f2a-465b-adfd-52b4f7442791)
   
### Using Swagger-UI

 1. use endpoint /auth/login to login and get the token
 
![image](https://github.com/mwithi/openhospital-api/assets/2938553/6c7ebd7e-aad7-4d9a-9bdc-62ad71c1cbdd)
![image](https://github.com/mwithi/openhospital-api/assets/2938553/d395317c-80f1-43d3-8c8b-3527f2a336cf)

 2. use the Authorize button at the top of the Swagger-UI, paste the token from step #1 and click Authorize

![image](https://github.com/mwithi/openhospital-api/assets/2938553/ae1f371d-d14b-4329-8221-696358b9b79b)
 
 3. close the dialog

![image](https://github.com/mwithi/openhospital-api/assets/2938553/4217fc43-fa19-4e22-8bd4-9be4aa162f73)

 4. now all the endpoints are automatically secured and the token will be added to the request

![image](https://github.com/mwithi/openhospital-api/assets/2938553/69075a2f-eff6-4739-a37f-2a259cfe8743)

### Using Postman

 1. import postman_collection.json in your Postman installation
 
## How to deploy backend in Docker environment

Make sure you have docker with docker-compose installed, then run the following commands:

- copy `dotenv` file into `.env` and set variables as needed (the SHA-256 jwt token is needed)
- run `make`
- run `docker compose up -d database` (wait for some seconds the very first time to build the DB)
- (optional - demo data after set the database container) run `docker compose run --rm oh-database-init`
- run `docker compose up backend`

When done successfully, head over at http://localhost:[API_PORT]/swagger-ui/

You can change the deployment branch using an .env file.

## Cleaning

	docker compose rm --stop --volumes --force
	make clean


## How to contribute

You can find the contribution guidelines in the [Open Hospital wiki][contribution-guide].  
A list of open issues is available on [Jira][jira].

## Community

You can reach out to the community of contributors by joining 
our [Slack workspace][slack] or by subscribing to our [mailing list][ml].


## Code style

This project uses a consistent code style and provides definitions for use in both IntelliJ and Eclipse IDEs.

<details><summary>IntelliJ IDEA instructions</summary>

For IntelliJ IDEA the process for importing the code style is:

* Select *Settings* in the *File* menu
* Select *Editor*
* Select *Code Style*
* Expand the menu item and select *Java*
* Go to *Scheme* at the top, click on the setting button by the side of the drop-down list
* Select *Import Scheme*
* Select *IntelliJ IDE code style XML*
* Navigate to the location of the file which relative to the project root is:  `.ide-settings/idea/OpenHospital-code-style-configuration.xml`
* Select *OK* 
* At this point the code style is stored as part of the IDE and is used for **all** projects opened in the editor.  To restrict the settings to just this project again select the setting button by the side of the *Scheme* list and select *Copy to Project...*. If successful a notice appears in the window that reads: *For current project*.

</details>

<details><summary>Eclipse instructions</summary>

For Eclipse the process requires loading the formatting style and the import order separately.

* Select *Preferences* in the *Window* menu
* Select *Java*
* Select *Code Style* and expand the menu
* Select *Formatter*
* Select the *Import...* button
* Navigate to the location of the file which relative to the project root is:  `.ide-settings/eclipse/OpenHospital-Java-CodeStyle-Formatter.xml`
* Select *Open*
* At this point the code style is stored and is applicable to all projects opened in the IDE.  To restrict the settings just to this project select *Configure Project Specific Settings...* in the upper right.  In the next dialog select the *openhospital* repository and select *OK*.  In the next dialog select the *Enable project specific settings* checkbox.  Finally select *Apply and Close*.
* Back in the *Code Style* menu area, select *Organize Imports*
* Select *Import...*
* Navigate to the location of the file which relative to the project root is:  `.ide-settings/eclipse/OpenHospital.importorder`
* Select *Open*
* As with the formatting styles the import order is applicable to all projects.  In order to change it just for this project repeat the same steps as above for *Configure Project Specific Settings...*
 
</details> 

[openhospital]: https://www.open-hospital.org/
[core]: https://github.com/informatici/openhospital-core
[contribution-guide]: https://openhospital.atlassian.net/wiki/display/OH/Contribution+Guidelines
[jira]: https://openhospital.atlassian.net/jira/software/c/projects/OP/issues/
[slack]: https://join.slack.com/t/openhospitalworkspace/shared_invite/enQtOTc1Nzc0MzE2NjQ0LWIyMzRlZTU5NmNlMjE2MDcwM2FhMjRkNmM4YzI0MTAzYTA0YTI3NjZiOTVhMDZlNWUwNWEzMjE5ZDgzNWQ1YzE
[ml]: https://sourceforge.net/projects/openhospital/lists/openhospital-devel
