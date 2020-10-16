CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: MainClient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/MainClient.o $(LDFLAGS)

MainClient: bin/connectionHandler.o bin/MainClient.o

	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/MainClient.o: src/MainClient.cpp
	g++ $(CFLAGS) -o bin/MainClient.o src/MainClient.cpp
	
clean:
	rm -f bin/*