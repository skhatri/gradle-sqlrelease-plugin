package com.github.skhatri

/**
 * Runtime SQL preferences
 */
class SqlPluginPropertiesExtension {
    /**
     * db user
     */
    def String username
    /**
     * db password
     */
    def String password
    /**
     * db url
     */
    def String url
    /**
     * driver class name
     */
    def String driverClassName
    /**
     * application name that can be used as discriminator if using the same table
     * for multiple projects
     */
    def String app
    /**
     * dialect - a value of sybase, oracle, mysql, h2 are supported right now
     */
    def String dialect

    /**
     * whether any additional folder is to be considered when batching scripts for this run
     */
    def String testDataDir
    /**
     * flag to include the test folder scripts. for lower environment this can be used
     * to include test data files and test configuration options
     */
    def boolean includeTestData
    /**
     * execution properties
     */
    def ExecutionExtension run = new ExecutionExtension()
    /**
     * drop script properties
     */
    def DropExtension drop = new DropExtension()
    /**
     * release script properties
     */
    def ReleaseExtension release = new ReleaseExtension()
}
