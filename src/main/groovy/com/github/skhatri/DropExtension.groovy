package com.github.skhatri

/**
 * Configuration of the directory and file pattern to be applied when running the drop all
 * or create new scripts
 */
class DropExtension {
    /**
     * directory where drop scripts are located
     */
    def String dir = '.'
    /**
     * ANT like file pattern selecting files to be run
     */
    def String filePattern = '**/*.sql'
}
