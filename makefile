all:			SuperNode.class Peer.class App.class

SuperNode.class:		SuperNode.java
			@javac SuperNode.java

Peer.class:	Peer.java
			@javac Peer.java

App.class:	App.java
			@javac App.java

clean:
			@rm -rf *.class *~
