# Mobile Application

## Technologies Used
* Application - Android
* Test Server - PHP
* Test DB - MySQL

## Test Environment Resources
* PHP files to host on server - in folder php_files/
* SQL files to initialize the test DB - in folder sqls/

## Steps to run app in Test Mode
* Host the PHP files either 1) using Docker or 2) on an actual server (already hosted)
* Run a MySQL server either 1) using Docker or 2) on an actual server (already hosted)
* Run app on the same network as the servers so that the URLs are resolvable

## Steps to run app in Production Mode
* Update the API endpoints to point to the production server (TODO)
* Run app on any network with internet