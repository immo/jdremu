#!/bin/bash
echo $(readlink $0)
if [ -h "$0" ];
then
cd $(dirname $(readlink $0))/DrumsEmulation/jars
else
cd $(dirname $0)/DrumsEmulation/jars
fi
java -jar DrumsEmulation-0.0.0.0a.jar

