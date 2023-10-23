find . -name "*.java" -exec javac -d ./bin/ {} \;
java -cp ./bin Simulator
