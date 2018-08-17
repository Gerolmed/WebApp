# Use the default MariaDB Container as base. It is based on Debian Jessie
FROM mariadb:latest

# Set the environment variables for users and passwords according to the pre-existing
# project configuration.
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_USER=db_findlunch_service
ENV MYSQL_PASSWORD=LT>r71VGWS

# Update and upgrade installed packages
RUN apt-get update
RUN apt-get -y upgrade
RUN apt-get dist-upgrade -y

# Installiere Locale, setze Locale auf English und Kodierung auf UTF-8.
RUN apt-get install -y locales
RUN sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && \
    locale-gen
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# Setzt die Zeitzone auf Berlin
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Install the default JRE (1.7 for Jessie)
RUN apt-get install -y default-jre --force-yes

# Install supervisor, which allows us to run two processes from a single container
RUN apt-get install -y supervisor --force-yes

# Install JRE 1.8 using the jessie-backports repository and make the container use it.
RUN echo 'deb http://http.debian.net/debian jessie-backports main' >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -t jessie-backports -y ca-certificates-java --force-yes
RUN apt-get install -t jessie-backports -y openjdk-8-jre-headless --force-yes
RUN update-java-alternatives --set java-1.8.0-openjdk-amd64

# Expose 8080 for Tomcat, 3306 for MariaDB
EXPOSE 8080 3306

# Mount the tmp volume, in case the container needs to write to the filesystem.
VOLUME /tmp

# All ARGs are defined in the pom.xml.
# Copy the database initialization scripts in the container to be run by the MariaDB
# container start-up script.
ARG DB_INIT
ADD ${DB_INIT} /docker-entrypoint-initdb.d/

# Copy the application JAR in the container
ARG JAR_FILE
ADD ${JAR_FILE} app.jar

# Copy the supervisor configuration file in the container.
ARG SUP_CONF
ADD ${SUP_CONF} /etc/supervisor/conf.d/supervisord.conf

# Copy the application start-up script in the container and make it executable.
ARG STARTUP
ADD ${STARTUP} /findlunch.sh
RUN chmod +x /findlunch.sh

# Call the supervisor daemon on container start-up.
ENTRYPOINT ["/usr/bin/supervisord"]