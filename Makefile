include .env
SHELL=/bin/bash

# The name of the file to be modified
OH_APPLICATION_PROPERTIES = ./rsc/application.properties
OH_DATABASE_PROPERTIES = ./rsc/database.properties
OH_LOG4J_PROPERTIES = ./rsc/log4j.properties
OH_SETTINGS = ./rsc/settings.properties

.DEFAULT_GOAL = modify

.PHONY: clean deps copy modify

# Clean configuration
clean:
	rm -rf deps $(OH_APPLICATION_PROPERTIES) $(OH_DATABASE_PROPERTIES) $(OH_LOG4J_PROPERTIES) $(OH_SETTINGS)

# create deps
deps: 
	mkdir deps && pushd deps && git clone --depth=1 -b ${OH_CORE_BRANCH} https://github.com/informatici/openhospital-core.git && popd

# Copy .dist
copy:
	cp $(OH_APPLICATION_PROPERTIES).dist $(OH_APPLICATION_PROPERTIES)
	cp $(OH_DATABASE_PROPERTIES).dist $(OH_DATABASE_PROPERTIES)
	cp $(OH_LOG4J_PROPERTIES).dist $(OH_LOG4J_PROPERTIES)
	cp $(OH_SETTINGS).dist $(OH_SETTINGS)

# Modify copies
modify: clean deps copy
	sed -i "s/JWT_TOKEN_SECRET/$(JWT_TOKEN_SECRET)/g" $(OH_APPLICATION_PROPERTIES)
	sed -i "s/localhost/backend/g" $(OH_APPLICATION_PROPERTIES)
	sed -i "s/localhost/database/g" $(OH_DATABASE_PROPERTIES)
	
