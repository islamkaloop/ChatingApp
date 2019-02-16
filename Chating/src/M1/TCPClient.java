package M1;
import java.io.*;
import java.net.*;

class TCPClient {
	public static void main(String argv[]) throws Exception {
		System.out.println(" \" Welcome To Capitalization Center :) \" ");
		String sentence;
		String modifiedSentence;
		String sent2 = "";
		Socket clientSocket = new Socket("J6578", 2019);
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		while (true) {
			System.out.print("Your Message: ");
			sentence = inFromUser.readLine();
			sent2 = sentence.toLowerCase();
			if (sent2.equals("bye") | sent2.equals("quit") | sent2.equals("exit")) {
				outToServer.writeBytes( "bye"+ '\n');
				clientSocket.close();
				System.out.println("See You again");
				break;
			}
			outToServer.writeBytes(sentence + '\n');
			modifiedSentence = inFromServer.readLine();
			System.out.println("FROM SERVER: " + modifiedSentence);
		}
	}
}