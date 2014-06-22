package com.github.skhatri

import org.gradle.api.Plugin
import org.gradle.api.Project;

/**
 * Gradle SQL Plugin
 */
class SqlPlugin implements Plugin<Project> {

    /**
     * Helper method to run sql file
     */
    def runSql(project, runConfig, dir, filePattern) {

        project.ant.sql(
                'driver': project.sql.driverClassName,
                'url': project.sql.url,
                'password': project.sql.password,
                'userid': project.sql.username,
                'classpath': project.buildscript.configurations.classpath.asPath,
                'delimiter': runConfig.delimiter,
                'keepformat': runConfig.keepformat,
                'autocommit': runConfig.autocommit
        ) {
            fileset(dir: dir) {
                include(name: filePattern)
            }
        }
    }

    /**
     * Exposes tasks - initialize, drop, release, reset
     */
    void apply(Project project) {
        def sqlExtension = project.extensions.create('sql', SqlPluginPropertiesExtension)
        def dropConfig = sqlExtension.extensions.create('drop', DropExtension)
        def releaseConfig = sqlExtension.extensions.create('release', ReleaseExtension)
        def runConfig = sqlExtension.extensions.create('run', ExecutionExtension)

        def sql

        project.task('initialize') {
            doLast {
                def ds = new org.apache.commons.dbcp.BasicDataSource()
                ds.with {
                    url = project.sql.url
                    driverClassName = project.sql.driverClassName
                    username = project.sql.username
                    password = project.sql.password
                }
                sql = new groovy.sql.Sql(ds)
            }
        }

        project.task('showVersion', dependsOn: 'initialize') {
            description = 'shows the latest version of script that is released'
            doLast {
                def row = sql.firstRow "select max(version) as version from version_info"
                def maxVersion = row.version
                println "current db version ${maxVersion}"
            }
        }

        project.task('release', dependsOn: ['initialize']) {
            description = 'runs the release scripts'

            doLast {
                println 'Performing Release...'

                def releaseDir = releaseConfig.dir
                def fileExt = releaseConfig.fileExt
                def output = releaseConfig.outputFile

                def app = project.sql.app

                def dialectValue = project.sql.dialect
                def version = QueryProvider.valueOf(dialectValue.toUpperCase())

                def maxVersion = 0
                def row = sql.firstRow version.getVersionSQL(), [app]
                maxVersion = row.version
                println "Current DB version: ${maxVersion ?: 0}"

                def fileList = []
                def allFiles = []


                allFiles = project.file(releaseDir).listFiles().findAll({ !it.directory })
                if (project.sql.includeTestData) {
                    allFiles += project.file(project.sql.testDataDir).listFiles().findAll({ !it.directory })
                }
                allFiles = allFiles.findAll { it.name.endsWith(fileExt) }
                allFiles.each { theFile ->
                    def fileName = theFile.name
                    def matcher = fileName =~ /^([0-9]+).*/
                    if (!matcher.matches()) {
                        throw new RuntimeException("file ${fileName} should start with number.")
                    }
                    def fNumber = matcher[0][1]
                    def versionNumber = (fNumber =~ /^0*/).replaceAll('')
                    if (versionNumber != '') {
                        if (new Long(versionNumber) > maxVersion) {
                            def versionExpando = new groovy.util.Expando(versionNumber: versionNumber as int, fileName: fileName, dirName: theFile.parent)
                            fileList.add(versionExpando)
                        }
                    }
                }
                fileList.sort(new Comparator() {
                    public int compare(Object thisObject, Object thatObject) {
                        return thisObject.versionNumber.compareTo(thatObject.versionNumber)
                    }
                })
                println "Number of Scripts to run: ${fileList.size()}"
                if (output) {
                    output.withOutputStream { out -> out.write(''.bytes) }
                }
                def tag = ''
                fileList.each {
                    ext.execFile = it.fileName
                    ext.dirName = it.dirName
                    sql.execute(version.getStartSQL(), [it.versionNumber, it.fileName, app])

                    println "executing ${ext.execFile}"
                    runSql(project, runConfig, ext.dirName, ext.execFile)
                    def updateList = [app, it.versionNumber]
                    if (version != QueryProvider.MYSQL) {
                        updateList << it.versionNumber
                    }
                    sql.execute(version.getUpdateSQL(), updateList)
                    tag = it.versionNumber as String
                    if (output) {
                        output.append(new File(ext.dirName, ext.execFile).getText('utf-8').bytes)
                    }
                }
                def sqlExt = project.extensions.getByName('sql')
                def releaseExtension = sqlExt.extensions.getByName('release')
                releaseExtension.tag = tag
            }
        }

        project.task('drop', dependsOn: ['initialize']) {
            description = 'drops the database objects using drop scripts'

            doLast {
                println 'Droping Database...'
                runSql(project, runConfig, dropConfig.dir, dropConfig.filePattern)
            }
        }
        project.task('reset', dependsOn: ['drop', 'release']) {
            description = 'drops then runs release scripts. Be Careful!!!'
        }

    }
}



