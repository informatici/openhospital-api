# Open Hospital API

[![Java CI with Maven](https://github.com/informatici/openhospital-api/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/informatici/openhospital-api/actions?query=workflow%3A%22Java+CI+with+Maven%22)

This is the API project of [Open Hospital][openhospital]: it exposes a REST API of the business logic implemented in the [openhospital-core project][core].  

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
 
        rsc/application.properties <- set a SHA-256 token
        rsc/database.properties
        rsc/log4j.properties
        rsc/...
 
 4. set target/rsc/database.properties
 
        DB can be created with `docker-compose up` from `openhospital-core` or using a dedicated MySQL server
        
 5. start openhospital-api (in `target` folder)
 
        # Windows
        java -cp "openhospital-api-0.0.2.jar;rsc/;static/" org.springframework.boot.loader.JarLauncher

        # Linux
        java -cp "openhospital-api-0.0.2.jar:rsc/:static/" org.springframework.boot.loader.JarLauncher
        
 6. call services
    - URL base: http://localhost:8080
    - URL login: http://localhost:8080/auth/login
    - URL patients: http://localhost:8080/patients
    - URL swagger: http://localhost:8080/swagger-ui/

You can see Swagger Api Documentation at: http://localhost:8080/swagger-ui/

![image](https://user-images.githubusercontent.com/2938553/215335720-73d59075-f0df-44c4-93ed-eae79945bb71.png)
   
 

## How to deploy backend in docker environment

Make sure you have docker with docker-compose installed, then run the following commands:

    - DOCKER_BUILDKIT=0 docker-compose build [--no-cache]
    - docker-compose up
    - docker-compose  exec database /bin/bash
    - cd sql/
    - mysql -u isf -p
    - source create_all_demo.sql;

When done successfully, head over at http://localhost:[API_PORT]/swagger-ui/

You can change the deployment branch using an .env file.

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
