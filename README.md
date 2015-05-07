SAIKU
---------------
*BUILDING*

Updated: May 7, 2015
REQUIRES: Maven 3.X (previous versions will succeed for some parts and the build will fail on others).

mvn clean install -DskipTest=true


*Issue Tracker: http://jira.meteorite.bi*



mvn clean clover2:setup test clover2:aggregate clover2:clover

If you require Foodmart for a different database checkout the foodmart loader wrapper script: https://github.com/OSBI/foodmart-data

Help and Support
________________

http://community.meteorite.bi
(Work in progress)

Contributing
_____________

Please read CONTRIBUTING.md for contribution guidelines.

