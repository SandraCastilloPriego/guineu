@echo off
cls
java -Djava.library.path=lib -Djava.util.logging.config.file=conf/logging.properties -Xms1024m -Xmx1024m -cp Guineu.jar guineu.main.GuineuClient
