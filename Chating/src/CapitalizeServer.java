import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CapitalizeServer 
{
	static ArrayList<String> members = new ArrayList<>();

	public static void main(String[] args) throws IOException 
	{
		System.out.println("The capitalization server is running.");
        ExecutorService pool = Executors.newFixedThreadPool(20);
        int clientNumber = 0;
        try (ServerSocket listener = new ServerSocket(9898))
        {
            while (true) 
            {
            	Socket socket = listener.accept();
            	if(joinResponse(socket)) {
                    pool.execute(new Capitalizer(socket, clientNumber++));
            	}
            	else {
                    PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
	                outToClient.println("Name used");
            	}
            }
        }
	}
	
	private static boolean joinResponse (Socket socket) throws IOException {
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
    	String id = inFromClient.readLine();
    	if(members.contains(id)) {
    		outToClient.println("false");
    		return false;
    	}
    	else {
    		outToClient.println("true");
    		return true;
    	}
	}
	
	public void memeberListResponse(Socket socket) throws IOException {
		String s="";
        PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
        for(int i=0;i<members.size();i++) {
        	s+=members.get(i)+"\n";
        }
        outToClient.println(s);
	}
	
	private static class Capitalizer implements Runnable 
	{
        private Socket socket;
        private int clientNumber;
        

        public Capitalizer(Socket socket, int clientNumber) 
        {
            this.socket = socket;
            this.clientNumber = clientNumber;
            System.out.println("New client #" + clientNumber + " connected at " + socket);
        }

        public void run() 
        {
            try 
            {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
            	while(true) 
            	{
	                String sentence = inFromClient.readLine();
	                if (sentence.toLowerCase().equals("bye") || sentence.toLowerCase().equals("quit") || sentence.toLowerCase().equals("exit")) 
	                {
	                	break;
	    			}
	                outToClient.println(sentence.toUpperCase());
            	}
            } 
            catch (Exception e) 
            {
                System.out.println("Error handling client #" + clientNumber);
            } 
            finally
            {
                try 
                { 
                	socket.close(); 
                } 
                catch (IOException e) {
                	
                }
                System.out.println("Connection with client # " + clientNumber + " closed");
            }
        }
    }
}