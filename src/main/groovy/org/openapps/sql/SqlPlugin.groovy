package org.openapps.sql

import org.gradle.api.Plugin
import org.gradle.api.Project;

class SqlPlugin implements Plugin<Project> {

    def runSql(project, dir, filePattern) {
        project.ant.sql(
                'driver': project.sql.driverClassName,
                'url': project.sql.url,
                'password': project.sql.password,
                'userid': project.sql.username,
                'classpath': project.buildscript.configurations.classpath.asPath,
                'delimiter': project.sql.run.delimiter,
                'keepformat': project.sql.run.keepformat,
                'autocommit': project.sql.run.autocommit
        ) {
            fileset(dir: dir) {
                include(name: filePattern)
            }
        }
    }

    void apply(Project project) {
        def sqlExtension = project.extensions.create('sql', SqlPluginPropertiesExtension)
        sqlExtension.extensions.create('drop', DropExtension)
        sqlExtension.extensions.create('release', ReleaseExtension)
        sqlExtension.extensions.create('run', ExecutionExtension)

        def sql

        project.task('initialize') << {
            def ds = new org.apache.commons.dbcp.BasicDataSource()
            ds.with {
                url = project.sql.url
                driverClassName = project.sql.driverClassName
                username = project.sql.username
                password = project.sql.password
            }
            sql = new groovy.sql.Sql(ds)
        }

        project.task('showVersion', dependsOn: 'initialize') {
            doFirst {
                description = 'shows the latest version of script that is released'
            }
            doLast {
                def row = sql.firstRow "select max(version) as version from version_info"
                def maxVersion = row.version
                println "current db version ${maxVersion}"
            }
        }

        project.task('release', dependsOn: ['initialize']) {
            doFirst {
                description = 'runs the release scripts'
            }
            doLast {
                println 'Performing Release...'
                def releaseDir = project.sql.release.dir
                def fileExt = project.sql.release.fileExt
                def app = project.sql.app


                def maxVersion = 0
                def row = sql.firstRow 'select max(version) as version from version_info where app = ? and endtime is not null', [app]
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
                fileList.each {
                    ext.execFile = it.fileName
                    ext.dirName = it.dirName
                    sql.execute('BEGIN\n' +
                            '        declare @maxid int\n' +
                            '        select @maxid = (select coalesce(max(id), 0)+1 from version_info)\n' +
                            '        insert into version_info(id, version, filename, app, starttime) values(@maxid, ?, ?, ?, getdate())\n' +
                            '        END\n', [it.versionNumber, it.fileName, app])

                    println "executing ${ext.execFile}"
                    runSql(project, ext.dirName, ext.execFile)
                    sql.execute("update version_info set endtime = getdate() where app = ? and version = ? and id = (select max(id) from version_info where version=?)", [app, it.versionNumber, it.versionNumber])
                }
            }
        }

        project.task('drop', dependsOn: ['initialize']) {
            doFirst {
                description ='drops the database objects using drop scripts'
            }
            doLast {
                println 'Droping Database...'
                runSql(project, project.sql.drop.dir, project.sql.drop.filePattern)
            }
        }
        project.task('reset', dependsOn: ['drop', 'release']) {
            doFirst {
                description = 'drops then runs release scripts. Be Careful!!!'
            }
        }

    }
}



