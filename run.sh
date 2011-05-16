#!/bin/bash

cd $(dirname $(readlink $0))/DrumsEmulation/jars
java -jar DrumsEmulation-0.0.0.0a.jar

