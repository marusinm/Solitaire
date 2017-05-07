#!/bin/bash

URL_BASE="https://github.com/kgupta99/Solitaire-In-Java/raw/master/src/cards"
colors=('s' 'c' 'h' 'd')

for var in "${colors[@]}"
do
	for i in {1..13}
	do
		if [ $i = 1 ]; then
			adapter="a"
		elif [ $i = 10 ]; then #na nasom gite to odstranit tento if
			adapter="t"
		elif [ $i = 11 ]; then
			adapter="j"
		elif [ $i = 12 ]; then
			adapter="q"
		elif [ $i = 13 ]; then
			adapter="k"
		else
			adapter=$i
		fi
		# echo "Welcome $i times ${var}"
		# echo "Card $adapter${var}"
		wget "${URL_BASE}/${adapter}${var}.gif"
	done
done