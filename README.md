# DbExport

Simple tool in order to export automatically DDL / Data from Databases, with options such as native DDL (Oracle / MySQL only) or JDBC standard exports.

## Project build

Build this project DbExport into an Executable JAR artifact, named "dbexport.jar"

Create these folders & copy-paste the following files :

- DbExport/
	- lib/
		- dbe.properties
		- dbexport.jar (this project DbExport to export as )
	- backup-<PROJECT>-<ENV>.cmd (one file per PROJECT/ENV, use the model "backup-PROJECT-ENV.cmd")
	- <one folder per PROJECT will be created during the execution of the .cmd files>
		- <one folder per ENV will be created during the execution of the .cmd files>

