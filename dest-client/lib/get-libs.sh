#!/bin/bash

#download images from git-hub account

#URL_BASE="https://github.com/marusinm/Solitaire/tree/master/lib"
URL_BASE="https://raw.githubusercontent.com/marusinm/Solitaire/master/lib"
colors=('s' 'c' 'h' 'd')

for var in "${colors[@]}"
do
	for i in {1..13}
	do
		if [ $i = 1 ]; then
			adapter="a"
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
wget "${URL_BASE}/back.gif"
wget "${URL_BASE}/card_background.gif"