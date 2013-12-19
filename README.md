# About

This was my code for a [Hearts](http://en.wikipedia.org/wiki/Hearts) game written for a high school computer science class I took as a junior in 2011.

The assignment specs are [here](http://cs.leanderisd.org/current/a/project-hearts.html) as of this writing, but this link may break. My implementation goes considerably beyond what was asked, and supports among other things:

* Network play with other human or computer players
* Graphical user interface
* Card animations
* Chat with other players
* Multiple card passing schemes (cycled through each trick)

To run:

    javac *.java
	java Launcher

To create an executable JAR file:

    javac *.java
	jar cvfM hearts.jar *.class images META-INF
	javaw -jar hearts.jar

# Acknowledgement

The card images came from [here](http://www.jfitz.com/cards/).

# Disclaimer

As mentioned above, this code was written in 2011 when I was a junior in high school and at the time still fairly new to programming. I make no assurances of any kind as to the quality or correctness or safety of any of the code here, and this code should under no circumstances be used to judge my current competence as a software engineer. This repo exists solely to share the project with some people who were interested.
