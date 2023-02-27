##########################
# Build checkmime
# by Edoardo Sabatini
# @2023
##########################

FROM maven:3.8.2-jdk-8 as buildcheckmime
WORKDIR /checkmime

# Copy only pom.xml of checkmime project and download dependencies
#
COPY pom.xml .
RUN mvn -B -f pom.xml dependency:go-offline

# Copy all other checkmime project files and build final project
#
COPY . .
RUN mvn -B clean install -DskipTests
CMD mvn spring-boot:run

