REST API documentation generator
================================

A tool for generating REST API documentation for a Spring application

Usage
-----

The documentation generator's artifacts are not yet available in any repository. To use it, build it and install it into your local repository. It's built using Maven:

    mvn clean install

The easiest way to use the documentation generator is with Spring Boot.

Firstly, add Maven's javadoc plugin, configured with a custom Doclet, to the `<plugins>` section of your Boot application's POM:

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-javadoc-plugin</artifactId>
        <version>2.9.1</version>
        <executions>
            <execution>
                <id>generate-rest-docs</id>
                <phase>process-classes</phase>
                <configuration>
                    <doclet>org.springframework.rest.documentation.doclet.RestDoclet</doclet>
                    <docletArtifacts>
                        <docletArtifact>
                            <groupId>org.springframework.rest.documentation</groupId>
                            <artifactId>rest-documentation-doclet</artifactId>
                            <version>0.1.0.BUILD-SNAPSHOT</version>
                        </docletArtifact>
                        <docletArtifact>
                            <groupId>${project.groupId}</groupId>
                            <artifactId>${project.artifactId}</artifactId>
                            <version>${project.version}</version>
                        </docletArtifact>
                    </docletArtifacts>
                    <additionalparam>-d ${project.build.outputDirectory}</additionalparam>
                    <useStandardDocletOptions>false</useStandardDocletOptions>
                    <show>package</show>
                </configuration>
                <goals>
                    <goal>javadoc</goal>
                </goals>
            </execution>
        </executions>
    </plugin>

This plugin analyzes your application's Javadoc and captures the documentation of methods, method parameters, thrown exceptions, etc.

Secondly, add the documentation generator's Spring Boot integration: to the `<dependencies>` section of your Boot application's POM:

    <dependency>
        <groupId>org.springframework.rest.documentation</groupId>
        <artifactId>rest-documentation-boot</artifactId>
        <version>0.1.0.BUILD-SNAPSHOT</version>
    </dependency>

You can now start your Boot application:

    mvn spring-boot:run

The documentation will be available to view at http://localhost:8080/swagger/index.html