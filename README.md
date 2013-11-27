gradle-sqlrelease-plugin
===========================

SqlPlugin


Usage:
======

buildscript {
    ext.buildDeps = [
        'com.sybase.jdbc.driver:jconn3:6.1', 
        'org.openapps.sql:gradle-sqlrelease-plugin:1.0-SNAPSHOT'
    ]
    repositories.addAll(project.repositories)
    dependencies {
        project.ext.buildDeps.each {
            classpath it
        }
    }
}


apply plugin: 'sqlrelease'
sqlrelease {
    username = props['database.username']
    password = props['database.password']
    url = props['database.url']
    driverClassName = props['database.driverClassName']

    app = 'myapp'
    testDataDir = 'src/main/database/testdata'
    includeTestData = true

    drop.dir = 'src/main/database/init'
    drop.filePattern = '**/*.*'

    release.dir = 'src/main/database/release'
    release.fileExt = '.ddl'

    run.autocommit = true
    run.delimiter = 'GO'
}

Available Tasks
---------------
reset       ->  drops then runs release scripts
drop        ->  drops the database objects using drop scripts
release     ->  runs the release scripts
showVersion ->  shows the latest version of script that is released
initialize  ->  point where sql connection is established
