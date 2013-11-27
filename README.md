REST API documentation generator
================================

A tool for generating REST API documentation for a Spring application

Usage
-----

The documentation generator's artifacts are not yet available in any repository. To use it, build it and install it into your local repository. It's built using Maven:

    mvn clean install

The easiest way to use the documentation generator is with Spring Boot.

Firstly, add the Maven plugin to the `<plugins>` section of your Boot application's POM:

    <plugin>
        <groupId>org.springframework.rest.documentation</groupId>
        <artifactId>rest-documentation-maven-plugin</artifactId>
        <version>0.1.0.BUILD-SNAPSHOT</version>
        <executions>
            <execution>
                <goals>
                    <goal>generate</goal>
                </goals>
            </execution>
        </executions>
    </plugin>

This plugin analyzes your application's Javadoc and captures the documentation of thrown exceptions, method parameters, etc.

Secondly, add the documentation generator's Spring Boot integration: to the `<dependencies>` section of your Boot application's POM:

    <dependency>
        <groupId>org.springframework.rest.documentation</groupId>
        <artifactId>rest-documentation-boot</artifactId>
        <version>0.1.0.BUILD-SNAPSHOT</version>
    </dependency>

You can now start your Boot application:

    mvn spring-boot:run

The documentation will now be available to view at http://localhost/swagger/index.html