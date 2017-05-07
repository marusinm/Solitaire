# classpath="ija/ija2016/homework1/TestMe"
# classpath="TestMe"
classpath="TestMe"
libspath="../libs/"

javac -cp ${libspath}junit-4.12.jar: ${classpath}.java ija/ija2016/homework1/cardpack/*.java ija/ija2016/homework2/model/*.java ija/ija2016/homework2/model/board/*.java ija/ija2016/homework2/model/cards/*.java
java -ea -cp ${libspath}junit-4.12.jar: ${classpath}

#hw1
# javac ${path}.java
# java -ea -Xmx3m ${path} 

#diff checker
# javac -cp jexamxml.jar: *.java
# java -cp jexamxml.jar: Main
