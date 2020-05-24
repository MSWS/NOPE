title NOPE Build
call mvn clean install
@copy C:\Users\imodm\eclipse-workspace\NOPE\target\NOPE.jar C:\Users\imodm\Documents\Servers\TestServer\plugins /y 
echo Successfully built and copied
timeout 5
