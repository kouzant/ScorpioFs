Instructions for building and running scorpioFS

Directory structure
1. src ----->contains the source code 
2. lib ---->contains 3rd party libraries for compiling the source
3. config ---> contains configuration files
3.1 config/log4j.properties---> contains configuration details for 
    the Log4j logging system used in scorpioFS. (see http://logging.apache.org/log4j/1.2/manual.html)
3.2 config/chord.properties----> contains properties such as the IP address of the 
    chord server, the directory to store chord.hashtable, and directory where the 
    chunks are stored (the local store)
3.3 config/build.conf--->defines two properties, the JDK_HOME and the FUSE_HOME
3.4 config/jvm_ldpath.def-->defines the LDPATH property for the JVM

Prerequisites 
1. sun-java6-jdk or (open-jdk)
2. ant
3. fuse-utils and libfuse2 and libfuse-dev
4. The user running the scorpiofs.sh must belong to the group fuse

Building (Compiling and packaging)
1. Edit the build.conf and set the JDK_HOME and FUSE_HOME properties
(e.g JDK_HOME=/usr/lib/jvm/java-6-sun, FUSE_HOME=/usr/lib)
2. Edit (if neccessary) the jvm_ldpath.def and correct the path to the jdk_home
3. Run ant dist. In the case of success the  structure of "dist" directory must be
   dist--| 
	 |---scorpioFS.jar
	 |---jni
	      |--------libjavafs.so
              |--------javafs_bindings.c
	      |--------javafs_bindings.h
              |--------javafs.c
	      |--------javafs.h
              |--------build.xml
              |--------MakeFile


Running
1. Edit the config/chord.properties and set the external_ip property to the ip address of your host. In case where 
   this chord node joins to an existing network provide the ip address and port of the bootstrap node by setting 
   the bootstrap property (e.g.bootstrap=195.251.230.112:6789) 
2. Run './chord.sh -config config/chord.properties
3. Create the directory where the scorpioFs file system should be mounted (e.g. mkdir /tmp/tmp) 
4. Run './scorpio.sh -port 6789 -fstree /tmp/fstree /tmp/tmp
5. Enjoy or die
6. Run 'fusermount -u /tmp/tmp' to unmount the scorpioFS.  