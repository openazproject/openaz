
This directory contains three configuration files. They can be used with the
SimplePDP application or any other code, though only one of the three
includes PolicyFinderModules. The three files are

  sample1.xml - a simple configuration, taken from the configuration guide

  sample2.xml - same as sample1, except that a PolicyFinderModule loads the
                example policies (note that you must be running from the
                sample directory to load the policies correctly)

  standard.xml - a verbose configuration that shows you how to load all the
                 standard factory configurations by hand (you should never need
                 to run with this file, since the useStandard* attributes give
                 you the same functionality in a better way)

Between these three files, all of the current configuration features are
shown, so these are a good place to start if you're writing your own config
files.

Also included in this directory is the XML Schema for the config format. This
is included here just for convenience, and is available off the SunXACML
web site as well.
