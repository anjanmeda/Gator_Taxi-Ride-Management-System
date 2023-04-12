
JC = javac

JR = java

MAIN = gatorTaxi.java

INPUT = input.txt


compile:
	$(JC) $(MAIN)
clean:
	$(RM) *.class