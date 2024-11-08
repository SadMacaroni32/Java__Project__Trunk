

Run: 
java -cp bin src.server.ChatServer
java -cp bin src.client.ChatClient


Compile: 
for /r src %f in (*.java) do javac -d bin %f



