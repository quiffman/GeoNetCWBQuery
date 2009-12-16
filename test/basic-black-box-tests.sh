#!/bin/bash
#
# Some basic black-box tests for CWB Query
#

tmpdir="/tmp/cwb-test.$$"

while getopts "hfqt:" opt; do
	case "$opt" in
		q) silence='2>&1 >/dev/null';;
		t) tmpdir="$OPTARG";;
		f) force="true";;
		h|*)
			echo "Usage: $0 [-q] [-t tmpdir]"
			exit 1
		;;
	esac
done

echo "s$silence t$tmpdir f$force"

test -d "$tmpdir" && test -z "$force" && read -p "Removing $tmpdir [y/N]: " confirm
test -z "$force" && test "$confirm" = "y" && rm -rf $tmpdir

test ! -e "$tmpdir" || exit 1

mkdir -p $tmpdir

# Test a command line query
java -jar GeoNetCWBQuery-2.0.0-RC1.jar -b "2009/01/01 00:00:00" -s "NZWLGT.LTZ40" -d 400 -t sac -o $tmpdir/cwb-test-1.out $silence

# Test running a channel listing
java -jar GeoNetCWBQuery-2.0.0-RC1.jar -lsc -b "2009/01/01 00:00:00" -d 100 2>$tmpdir/cwb-test-2.out

cat > $tmpdir/CWB.batch <<EOF
-b "2009/01/01 00:00:00" -s "NZWLGT.LTZ40" -t sac -o $tmpdir/cwb-batch-test-1.out
-b "2009/01/01 00:00:00" -s "NZWLGT.LTZ40" -d 600 -t sac -o "$tmpdir/%N-%n-%s-%c-%l-%Y-%y-%j-%J-%M-%D-%h-%m-%S%z-cwb-batch-test-2.out"
-b "2009/01/01 00:00:00" -s "NZWEL..HH[ENZ]..|NZBFZ..HHZ..|.*WAZ.*20" -d 300 -t sac -o "$tmpdir/%N-cwb-batch-test-3.out"
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t ms -o $tmpdir/cwb-batch-test-4.out
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t msz -o $tmpdir/cwb-batch-test-5.out
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t dcc -o $tmpdir/cwb-batch-test-6.out
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t dcc512 -o $tmpdir/cwb-batch-test-7.out
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t text -o $tmpdir/cwb-batch-test-8.out
EOF

# Test the rest via a batch file
java -jar GeoNetCWBQuery-2.*.jar -f $tmpdir/CWB.batch $silence

cat > $tmpdir/CWB.md5 <<EOF
9f7555de4d8f89a6f1c9fc8d05e19c51  $tmpdir/cwb-test-1.out
f942a5a2d868b9f1fd9394be12ca0d91  $tmpdir/cwb-test-2.out
cb20eb719832db4910a4d830f69c7a38  $tmpdir/cwb-batch-test-1.out
47a7f0f4f157710697c8430f9b1eae29  $tmpdir/NZWLGTLTZ40-NZ-WLGT-LTZ-40-09-2009-001-2454833-01-01-00-00-00-cwb-batch-test-2.out
72d35048a4ee43e91cf99857342478b1  $tmpdir/NZBFZ__HHZ10-cwb-batch-test-3.out
9408af0c4071ad8cda3366df2245b6b2  $tmpdir/NZWAZ__LNE20-cwb-batch-test-3.out
2557642ab3ab099c2c94de239f608025  $tmpdir/NZWAZ__LNZ20-cwb-batch-test-3.out
20ed04531f27198dc6c99401c1dea2f6  $tmpdir/NZWEL__HHE10-cwb-batch-test-3.out
a074d2af7a574f226e87789ab39eadaf  $tmpdir/NZWEL__HHN10-cwb-batch-test-3.out
20e616630aca460a026d5d3ad7722a44  $tmpdir/NZWEL__HHZ10-cwb-batch-test-3.out
3b103d7bcbb9ac99d6de26026fc93cf9  $tmpdir/cwb-batch-test-4.out
2c5ae4cc1bf1de7208ff62e65fa5b468  $tmpdir/cwb-batch-test-5.out
757476816a0da1ec5922385a4b18987e  $tmpdir/cwb-batch-test-6.out
3b103d7bcbb9ac99d6de26026fc93cf9  $tmpdir/cwb-batch-test-7.out
a85e23dcf59c9426e58bf57e4022cc86  $tmpdir/cwb-batch-test-8.out
EOF

md5sum -c $tmpdir/CWB.md5

