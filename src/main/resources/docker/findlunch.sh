#!/bin/bash

# Wait 10 seconds, then launch the JAR.
sleep 10
java -Djava.security.egd=file:/dev/./urandom -jar /app.jar -n