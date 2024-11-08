

Compile: 
for /r src %f in (*.java) do javac -d bin %f


Run: 
java -cp bin src.client.ChatClient