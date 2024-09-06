include .env
SHELL=/bin/bash

# The name of the file to be modified
OH_APPLICATION_PROPERTIES = ./rsc/application.properties
OH_DATABASE_PROPERTIES = ./rsc/database.properties
OH_LOG4J_PROPERTIES = ./rsc/log4j2-spring.properties
OH_SETTINGS = ./rsc/settings.properties

.DEFAULT_GOAL = modify

.PHONY: clean deps copy modify

# Clean configuration
clean-all:
	@echo "Clean configuration files and dependencies (deps)..."
	rm -rf deps $(OH_APPLICATION_PROPERTIES) $(OH_DATABASE_PROPERTIES) $(OH_LOG4J_PROPERTIES) $(OH_SETTINGS)

clean:
	@echo "Clean configuration files..."
	rm -rf $(OH_APPLICATION_PROPERTIES) $(OH_DATABASE_PROPERTIES) $(OH_LOG4J_PROPERTIES) $(OH_SETTINGS)

# create or update deps
deps:
	@echo "Create or update dependencies (deps)..."
	@if [ -d "deps" ]; then \
		cd deps; cd openhospital-core; git checkout $(OH_CORE_BRANCH); git pull; \
	else \
		mkdir deps && pushd deps && git clone --depth=1 -b ${OH_CORE_BRANCH} https://github.com/informatici/openhospital-core.git && popd; \
	fi

# Copy .dist
copy:
	@echo "Copy configuration files..."
	cp $(OH_APPLICATION_PROPERTIES).dist $(OH_APPLICATION_PROPERTIES)
	cp $(OH_DATABASE_PROPERTIES).dist $(OH_DATABASE_PROPERTIES)
	cp $(OH_LOG4J_PROPERTIES).dist $(OH_LOG4J_PROPERTIES)
	cp $(OH_SETTINGS).dist $(OH_SETTINGS)

# Modify copies
modify: clean deps copy
	@echo "Set configuration files..."
	sed -i "s/JWT_TOKEN_SECRET/$(JWT_TOKEN_SECRET)/g" $(OH_APPLICATION_PROPERTIES)
	sed -i "s/API_PORT/${API_PORT}/g" $(OH_APPLICATION_PROPERTIES)
	sed -i "s/localhost/backend/g" $(OH_APPLICATION_PROPERTIES)
	sed -i "s/localhost/database/g" $(OH_DATABASE_PROPERTIES)
	sed -i "s/LOG_DEST/logs/g" $(OH_LOG4J_PROPERTIES)

