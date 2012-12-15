This project is to play with HTTP clients on the JVM.

## To start up the local server
<pre>
mvn package
mvn exec:java
</pre>

## For Ecilpse support.
<pre>
mvn dependency:sources
mvn dependency:resolve -Dclassifier=javadoc
mvn eclipse:eclipse
</pre>