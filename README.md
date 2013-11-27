gradle-sqlrelease-plugin
===========================

SqlPlugin - a simple plugin to release SQL scripts to targetted environment.


Usage:
======

    buildscript {
        ext.buildDeps = [
                'mysql:mysql-connector-java:5.1.13',
                'com.github.skhatri:gradle-sqlrelease-plugin:1.0-SNAPSHOT'
        ]
        repositories.addAll(project.repositories)
        dependencies {
            project.ext.buildDeps.each {
                classpath it
            }
        }
    }


    apply plugin: 'sqlrelease'
    sql {
        username = project.ext['database.username']
        password = project.ext['database.password']
        url = project.ext['database.url']
        driverClassName = project.ext['database.driverClassName']
        dialect = 'mysql'

        app = 'myapp'
        testDataDir = 'src/main/database/testdata'
        includeTestData = true

        drop.dir = 'src/main/database/init'
        drop.filePattern = '**/*.*'

        release.dir = 'src/main/database/release'
        release.fileExt = '.sql'

        run.autocommit = true
        run.delimiter = ';'
    }


Available Tasks
---------------
    reset       ->  drops then runs release scripts
    drop        ->  drops the database objects using drop scripts
    release     ->  runs the release scripts
    showVersion ->  shows the latest version of script that is released
    initialize  ->  point where sql connection is established


Works With
-----------
    Sybase, MySQL, Oracle
    
I am in the process of making version tracking script configurable for various source systems.

