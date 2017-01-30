FROM ubuntu

RUN apt-get update && apt-get upgrade -yy && apt-get install git maven openjdk-8-jdk  -yy

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

WORKDIR /root

RUN git clone https://github.com/Vehnem/kgapi.git && cd kgapi/ && mvn package

# RUN git clone https://github.com/AKSW/RDFUnit.git && cd RDFUnit/ && mvn -pl rdfunit-validate -am clean install 

CMD java -jar kgapi/target/kgapi-0.0.1-SNAPSHOT.jar -Xmx4000m

