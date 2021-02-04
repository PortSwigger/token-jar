# TokenJar v2.1
Burp Suite extension. Useful for managing tokens like anti-CSRF, CSurf, Session values. Can be used to set params that require random numbers or params that are computed based on application response.

Here is just brief information. More details are on plugin GitHub [page](https://dannegrea.github.io/TokenJar/)

Please report issues using [GitHub Issues](https://github.com/DanNegrea/TokenJar/issues)

## Requirements for usage
* Burp Suite Free or Pro
* JRE or JDK 1.8+
 
### Notice
* Tested with JDK 11 (End of Premier Support [September 2023](https://www.oracle.com/java/technologies/java-se-support-roadmap.html))
* JS Engine (NashornScriptEngine) is set to be deprecated (still available in [JRE 13](https://docs.oracle.com/en/java/javase/13/docs/api/jdk.scripting.nashorn/jdk/nashorn/api/scripting/NashornScriptEngine.html))

## Requirements for building
* Google Guava 28.2
* Google Gson 2.8.6
