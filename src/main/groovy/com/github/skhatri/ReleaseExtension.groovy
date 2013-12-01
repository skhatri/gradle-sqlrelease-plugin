package com.github.skhatri

/**
 * Release scripts location options
 */
class ReleaseExtension {
    /**
     * source scripts directory
     */
    def String dir = 'release'
    /**
     * extension of the file to be run
     */
    def String fileExt = '.sql'
    /**
     * location of the output file to be created. Optional.
     * If one is provided the effective SQL is saved as a file in this location.
     */
    def File outputFile
    /**
     * A last script number is provided as a tag to the build for further usage
     */
    def String tag = ''
}
