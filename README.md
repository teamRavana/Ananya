# Ananya
This repository contains the Sinhala Named Entity Recognition System and other utility tools built by Team Raavana 


Steps to build

1. Install Sinmin dependency

* Build Sinmin repository following instructions in README.md in https://github.com/madurangasiriwardena/corpus.sinhala.tools

or 

* Run the below command from the root of this repo

```
mvn install:install-file -Dfile=lib/corpus/sinhala/corpus.sinhala.tools/1.0-SNAPSHOT/corpus.sinhala.tools-1.0-SNAPSHOT.jar -DgroupId=corpus.sinhala -DartifactId=corpus.sinhala.tools -Dversion=1.0-SNAPSHOT -Dpackaging=jar
```

2. Run ```mvn clean install``` from the root of this repo.