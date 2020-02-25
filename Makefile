
SHELL=/bin/sh
CLASSPATH=jtools

JAVAC=javac
JAVADOC=javadoc
JAVA=java
TEMP=/usr/tmp

export CLASSPATH

default: check all_jtools all_ctools core_classes tinyvm_emul
	@echo ------ TinyVM installed successfully.
	@echo ------ Please check the README file for
	@echo ------ information about running an example.

all: default tinyvm_bin

tag_release:
	cvs commit
	cvs tag RELEASE_`cat VERSION`
	make release_no_tag

release: release_no_tag
	
release_no_tag:
	make clean
	make all
	make javadoc
	rm -rf ${TEMP}/tinyvm_*
	export TINYVM_VERSION=tinyvm_`cat VERSION`; make dir_and_zip

dir_and_zip:
	mkdir ${TEMP}/${TINYVM_VERSION}
	cp -r . ${TEMP}/${TINYVM_VERSION}
	cd ${TEMP}/${TINYVM_VERSION}; make remove_cvs_and_junk
	cd ${TEMP}; tar cvf ${TINYVM_VERSION}.tar ${TINYVM_VERSION}; gzip ${TINYVM_VERSION}.tar
	diff bin/tinyvm.srec ${TEMP}/${TINYVM_VERSION}/bin/tinyvm.srec
	#rm -rf ${TEMP}/${TINYVM_VERSION}

remove_cvs_and_junk:
	rm -rf `find . -name 'CVS'`
	rm -rf `find . -name '*.o'`
	rm -rf `find . -name '*.class'`
	rm -rf `find . -name '*.tvm'`
	rm -rf `find . -name '*.exe'`
	rm -rf `find . -name '*.core'`
	rm -rf `find . -name 'core'`
	
check:
	@if [ "${TINYVM_HOME}" != "" ]; then \
	  echo "Note: The TINYVM_HOME variable is no longer needed"; \
	  exit 1; \
	fi;

check_release:
	@echo TINYVM_HOME=${TINYVM_HOME}
	@echo Location of tvmc=`which tvmc`
	@echo Location of tvmld=`which tvmld`
	make
	cd regression; ./run.sh

all_jtools: java_tools generated_files java_loader
	cd jtools; jar cf ../lib/jtools.jar `find . -name '*.class'`

java_tools:
	@echo Making tools
	${JAVAC} jtools/js/tools/*.java

generated_files: common/classes.db common/signatures.db
	${JAVA} -Dtinyvm.home="." js.tools.GenerateConstants

java_loader:
	@echo Making loader
	${JAVAC} jtools/js/tinyvm/*.java

all_ctools:
	cd tools; make

tinyvm_bin:
	@echo Making TinyVM binary
	cd vmsrc; make

tinyvm_emul:
	@echo Making TinyVM binary for emulation
	cd vmtest; make

core_classes:
	${JAVAC} -classpath classes `find classes -name '*.java'`
	cd classes; jar cf ../lib/classes.jar `find . -name '*.class'`

javadoc:
	if [ ! -d apidocs ]; then mkdir apidocs; fi
	${JAVADOC} -d apidocs -sourcepath classes java.lang java.util tinyvm.rcx

clean:
	rm -rf `find . -name '*.class'`
	rm -rf `find . -name 'core'`
	rm -rf `find . -name '*.o'`
	rm -rf `find . -name '*~'`

cleaner: clean
	rm -rf `find . -name '*.srec'`






