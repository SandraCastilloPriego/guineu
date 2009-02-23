@echo off
cls
java -Djava.library.path=lib -Djava.util.logging.config.file=conf/logging.properties -Xms512m -Xmx1384m -cp Guineu.jar guineu.main.GuineuClient
