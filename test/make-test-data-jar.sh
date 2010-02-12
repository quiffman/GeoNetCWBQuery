#!/bin/bash

jarversion=1.0.0

tmpdir="/tmp/cwb-test.$$"
mkdir -p $tmpdir

cp make-test-data-jar.sh $tmpdir

mkdir -p $tmpdir/miniseed-data/test-one

echo 'java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0-RC1.jar -t msz -b "2009/01/01 00:00:00" -d 1800 -s "NZMRZ..HH.10" -o $tmpdir/miniseed-data/test-one/%N.ms' > $tmpdir/miniseed-data/test-one/command.txt

java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0-RC1.jar -t msz -b "2009/01/01 00:00:00" -d 1800 -s "NZMRZ..HH.10" -o $tmpdir/miniseed-data/test-one/%N.ms

mkdir -p $tmpdir/sac-data/test-one

echo 'java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0-RC1.jar -t sac -sacpz nm -b "2009/01/01 00:00:00" -d 1800 -s "NZMRZ..HH.10" -o $tmpdir/sac-data/test-one/%N.ms' > $tmpdir/sac-data/test-one/command.txt

java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0-RC1.jar -t sac -sacpz nm -b "2009/01/01 00:00:00" -d 1800 -s "NZMRZ..HH.10" -o $tmpdir/sac-data/test-one/%N.sac

cd $tmpdir

jar -0cvf CWBQueryTestData-${jarversion}.jar miniseed-data sac-data  make-test-data-jar.sh

cd -

mkdir -p $tmpdir/CWBQueryTestData/$jarversion

mv $tmpdir/CWBQueryTestData-${jarversion}.jar $tmpdir/CWBQueryTestData/$jarversion/

rsync -v --archive --no-perms --rsh=ssh $tmpdir/CWBQueryTestData repoadmin@repo.geonet.org.nz:/work/maven/public_html/ivy/repo/enterprise/nz/org/geonet/

rm -rf $tmpdir

