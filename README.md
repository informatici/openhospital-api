# Open Hospital API [![Build Status](https://travis-ci.org/pviotti/openhospital-api.svg?branch=master)](https://travis-ci.org/pviotti/openhospital-api)

This is the API project of [Open Hospital][openhospital]: it exposes a REST API of the business logic implemented in the [openhospital-core project][core].  

## How to build [WIP]

For the moment, to build this project you should 

 1. fetch and build the `OP-102_master-refactoring-for-api` branch of the [core] project
    
        git clone https://github.com/informatici/openhospital-core.git --branch OP-102_master-refactoring-for-api
        cd openhospital-core
        mvn clean install -DskipTests=true
        
 2. clone and build this project
 
        git clone https://github.com/informatici/openhospital-api
        cd openhospital-api
        mvn clean install -DskipTests=true


 [openhospital]: https://www.open-hospital.org/
 [core]: https://github.com/informatici/openhospital/openhospital-core

