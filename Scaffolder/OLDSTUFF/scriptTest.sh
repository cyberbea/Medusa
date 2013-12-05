#!/bin/bash
FILE1=METODO3/NetworkSyntHom/TAC125_May_01_ST.gexf
FILE2=METODO3/NetworkSyntHom/TAC125_May_10_ST.gexf
FILE3=METODO3/NetworkSyntHom/TAC125May_0505_ST.gexf
FILE4=METODO3/NetworkSyntHom/TAC125May_0703_ST.gexf
java -jar Scaffolder.jar -cover METODO3/NetworkSyntHom/TAC125_May_01_ST.gexf 1 -output TAC125_May_01_cover1.txt
java -jar Scaffolder.jar -cover $FILE1 2 -output $FILE1+_cover2.txt
java -jar Scaffolder.jar -cover $FILE2 1 -output $FILE2+_cover1.txt
java -jar Scaffolder.jar -cover $FILE2 2 -output $FILE2+_cover2.txt
java -jar Scaffolder.jar -cover $FILE3 1 -output $FILE3+_cover1.txt
java -jar Scaffolder.jar -cover $FILE3 2 -output $FILE3+_cover2.txt
java -jar Scaffolder.jar -cover $FILE4 1 -output $FILE4+_cover1.txt
java -jar Scaffolder.jar -cover $FILE4 2 -output $FILE4+_cover2.txt
