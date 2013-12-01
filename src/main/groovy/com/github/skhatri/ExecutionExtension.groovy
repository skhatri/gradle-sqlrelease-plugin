package com.github.skhatri

/**
 * SQL script files execution options
 */
class ExecutionExtension {
    /**
     * The SQL script separator be to be used. Default is ;
     */
    def String delimiter = ';'
    /**
     * When executing whether the format of the script is to be kept.
     */
    def String keepformat = 'yes'
    /**
     * Autocommit option
     */
    def boolean autocommit = true
}
