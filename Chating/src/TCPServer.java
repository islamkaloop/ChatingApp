import java.io.*;
import java.net.*;

class TCPServer {
	public static void main(String argv[]) throws Exception {
		String clientSentence;
		String capitalizedSentence;
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(2019);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			while(!connectionSocket.isClosed()){
			clientSentence = inFromClient.readLine();
			if (clientSentence != null) {
				capitalizedSentence = clientSentence.toUpperCase() + '\n';
				outToClient.writeBytes(capitalizedSentence);
				if(clientSentence.equals("bye"))
					connectionSocket.close();
			}
			}
		}
	}
}
