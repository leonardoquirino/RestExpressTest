Backend Test - Leonardo Loures
===================================
After researching for a lightweight framework that would be useful for this test, I choose to go with RestExpress. 
I had already read about it some time ago, but when I got back to it, I saw that it was a atempt to replicate the Express framework from Node.js and the MEAN stack using Netty/Java NIO.

This framework is simple, but still rough, and the MongoDB stack is way behind schedule, wich led to having to resort to Two Phase Commit. 
Maybe I should've went with Vert.x.  

To run the project:

	Make sure MongoDB is running
	mvn clean package exec:java

To create a 'fat' runnable jar file:

	mvn clean package

To run the jar file created via package

	java -jar target/{project-name}.jar [environment]


Configuration
-------------

By default, the 'mvn package' goal will create a fat jar file including the configuration files in src/main/resources.
These are loaded from the classpath at runtime. However, to override the values embedded in the jar file, simply create
a new configuration file on the classpath for the desired environment. For example, './config/dev/environment.properties'
and any settings in that file will get added to, or override settings embedded in the jar file.