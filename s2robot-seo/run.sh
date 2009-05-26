#!/bin/sh

cd `dirname $`
BASE_DIR=`pwd`
LIB_DIR=$BASE_DIR/lib
S2ROBOT_EXAMPLE_JAR_FILE=`ls $BASE_DIR/target/s2robot-seo-*.jar`
CP_PATH=".:$S2ROBOT_EXAMPLE_JAR_FILE"

for file in `ls $LIB_DIR/*.jar` ; do
    CP_PATH="$CP_PATH:$file"
done

java -cp $CP_PATH org.seasar.robot.seo.SeoCommand $@

