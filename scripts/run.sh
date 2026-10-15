#!/usr/bin/env bash
set -e

LD_LIBRARY_PATH=/lib/jvm/openjdk25/lib /lib/jvm/openjdk25/bin/java -cp /tmp/puke-out puke.Puke
