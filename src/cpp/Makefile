ifeq ($(origin JAVA_HOME),undefined)
ifneq ($(wildcard /usr/lib/jvm/java-11-openjdk-amd64/include/.*),)
JAVA_HOME:=/usr/lib/jvm/java-11-openjdk-amd64/include
else
ifneq ($(wildcard /usr/lib/jvm/java-8-openjdk-amd64/include/.*),)
JAVA_HOME:=/usr/lib/jvm/java-8-openjdk-amd64/include
else
JAVA_HOME:=/usr/lib/jvm/java-14-openjdk-amd64/include
endif
endif
endif

GCC:=g++
FLAGS:=-shared -fPIC  -Wall -Wextra -pedantic -g -O2 -fopenmp -std=c++14

armadillo.h: ../maths/Armadillo.java
	cd ../; javah -o cpp/armadillo.h maths.Armadillo

util.o: util.cpp util.h
	$(GCC) $(FLAGS) -I"${JAVA_HOME}" -I"${JAVA_HOME}/linux" util.cpp -o util.o

armadillo.o: armadillo.cpp armadillo.h util.h
	$(GCC) $(FLAGS) -I"${JAVA_HOME}" -I"${JAVA_HOME}/linux" armadillo.cpp -o armadillo.o

../resources/armadillo_java.so: armadillo.cpp armadillo.o util.o
	$(GCC) $(FLAGS) -I"${JAVA_HOME}" -I"${JAVA_HOME}/linux" armadillo.cpp armadillo.o util.o -larmadillo -o ../resources/armadillo_java.so

all: ../resources/armadillo_java.so

clean:
	-rm ../resources/armadillo_java.so
	-rm armadillo.o
	-rm util.o
