package org.openapps.sql;

class SqlPluginPropertiesExtension {
    def String username
    def String password
    def String url
    def String driverClassName
    def String app
    def String testDataDir
    def boolean includeTestData
    def ExecutionExtension run = new ExecutionExtension()
    def DropExtension drop = new DropExtension()
    def ReleaseExtension release = new ReleaseExtension()
}
