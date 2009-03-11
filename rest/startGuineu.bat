@echo off
cls
java -Djava.library.path=lib -Djava.util.logging.config.file=conf/logging.properties -Xms1024m -Xmx2048m -cp Guineu.jar guineu.main.GuineuClient
