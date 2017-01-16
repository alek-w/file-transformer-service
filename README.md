# kafka-consumer-file transformer service

#### Steps to Install and Run zookeeper and kafka on your system : 

Step 1: Download kafka from official web site

Step2: Extract it

Step3: Start zookeeper server

Step4: Start  Kafka server


#### Steps to start the service :

    $ sbt run

#
Also see at readme.md of file-uploader-service.
#
yo have to update `fileUploaderService.endpoint`  value in `{project_folder}/src/main/resources/application.conf` if you are going to use downloaded package.box for Vagrant 

#
* How to run tests:
~~~
sbt test
~~~