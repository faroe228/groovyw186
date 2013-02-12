#!/usr/bin/env bash

# http://stackoverflow.com/questions/192319/in-the-bash-script-how-do-i-know-the-script-file-name
# which solution is the best???

cd "$(dirname "$0")"
java -jar "${0##*/}.jar" "$@"