package M3;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerA
{
	static ArrayList<String> members = new ArrayList<>();
	static ArrayList<Socket> membersSocket = new ArrayList<>();
	static String clientName;
	static Socket clientSocket;
	static DataInputStream inFromClient;
    static DataOutputStream outToClient;
	static ArrayList<DataInputStream> inFromClientList = new ArrayList<>();
	static ArrayList<DataOutputStream> outToClientList =  new ArrayList<>();
	
	public static void main(String[] args) throws IOException 
	{
		System.out.println("Server A is running.");
        ExecutorService pool = Executors.newFixedThreadPool(20);
        int clientNumber = 0;
        try (ServerSocket listener = new ServerSocket(2018))
        {
            while (true) 
            {
            	clientSocket = listener.accept();
            	if(joinResponse()) 
            	{
                    pool.execute(new Thread(clientSocket, clientNumber++,clientName,inFromClient,outToClient));
            	}
            }
        }
	}
	
	private static boolean joinResponse () throws IOException 
	{
		inFromClient = new DataInputStream(clientSocket.getInputStream());
		outToClient = new DataOutputStream(clientSocket.getOutputStream());
		String id = inFromClient.readUTF();
		if(id.equals("server")){
			String mess = inFromClient.readUTF();
			if(mess.equals("#")){
				String s="";
		          for(int i=0;i<members.size();i++) {
		          	s+=members.get(i)+"\n";
		          }
		        outToClient.writeUTF(s);
		        outToClient.flush();
			}
			else{
				String x[]=new String[3];
        		x=mess.split(" ", 3);
        		DataOutputStream destination = null;
				for(int i=0;i<members.size();i++) 
	        	{
	        		if(members.get(i).equals(x[1])) 
	        		{
	        			destination=outToClientList.get(i);
	        			break;
	        		}
	        	}
				destination.writeUTF("*From "+x[0]+" to You: "+x[2]);
	            destination.flush();
	            outToClient.writeUTF("true");
	            outToClient.flush();
			}
    		clientSocket.close();
    		inFromClient.close();
    		outToClient.close();
    		clientName=null;
    		clientSocket=null;
    		inFromClient=null;
    		outToClient=null;
    		return false;
		}
    	clientName=id;
    	String[] membersinserver2 =getMembersfromotherserver().split("\n"); 
    	int i =0;
    	for(int k=0;k<membersinserver2.length;k++){
    		if(id.equals(membersinserver2[k]))
    			i++;
    	}
    	if(members.contains(id)||i!=0) 
    	{
    		outToClient.writeUTF("false");
    		outToClient.flush();
    		clientSocket.close();
    		inFromClient.close();
    		outToClient.close();
    		clientName=null;
    		clientSocket=null;
    		inFromClient=null;
    		outToClient=null;
    		return false;
    	}
    	else 
    	{
    		members.add(id);
    		membersSocket.add(clientSocket);
    		inFromClientList.add(inFromClient);
    		outToClientList.add(outToClient);
    		outToClient.writeUTF("true");
    		outToClient.flush();
    		return true;
    	}
	}
	
	public static String getMembersfromotherserver() throws IOException{
		Socket sock =new Socket("j6578", 2019);
		DataInputStream inFromClient = new DataInputStream(sock.getInputStream());
		DataOutputStream outToClient = new DataOutputStream(sock.getOutputStream());
		outToClient.writeUTF("server");
		outToClient.flush();
		outToClient.writeUTF("#");
		outToClient.flush();
		String s = inFromClient.readUTF();
		outToClient.close();
		inFromClient.close();
		sock.close();
		return s;
	}
	
	public static void Route (DataOutputStream outToClient,String clientName,String Message,String Destination) throws IOException{
		boolean founded=false;
		String[] membersinserver2 =getMembersfromotherserver().split("\n"); 
    	int j =0;
    	for(int k=0;k<membersinserver2.length;k++){
    		System.out.println(membersinserver2[k]);
    		if(Destination.equals(membersinserver2[k]))
    			j++;
    	}
    	if(members.contains(Destination)||j!=0) 
    	{
    		founded=true;
    	}
		if(!founded) 
		{
			outToClient.writeUTF("false");
			outToClient.flush();
		}
		else 
		{
			if(j==0){
				DataOutputStream destination = null;
				for(int i=0;i<members.size();i++) 
	        	{
	        		if(members.get(i).equals(Destination)) 
	        		{
	        			destination=outToClientList.get(i);
	        			break;
	        		}
	        	}
				destination.writeUTF("*From "+clientName+" to You: "+Message);
	            destination.flush();
	            outToClient.writeUTF("true");
	            outToClient.flush();
			}
			else{
				System.out.println(1);
				Socket sock =new Socket("j6578", 2019);
				DataInputStream inFromClient1 = new DataInputStream(sock.getInputStream());
				DataOutputStream outToClient1 = new DataOutputStream(sock.getOutputStream());
				outToClient1.writeUTF("server");
				outToClient1.flush();
				outToClient1.writeUTF(clientName+" "+Destination+" "+Message);
				outToClient1.flush();
				if(inFromClient1.readUTF().equals("true")){
		            outToClient.writeUTF("true");
		            outToClient.flush();
				}
				else{
					outToClient.writeUTF("false");
		            outToClient.flush();
				}
				outToClient1.close();
				inFromClient1.close();
				sock.close();
			}
		}
	}
	
	private static class Thread implements Runnable 
	{
        private Socket socket;
        private int clientNumber;
        private String clientName;
        private DataInputStream inFromClient;
        private DataOutputStream outToClient;

        public Thread(Socket socket, int clientNumber, String clientName, DataInputStream inFromClient, DataOutputStream outToClient) 
        {
            this.socket = socket;
            this.clientNumber = clientNumber;
            this.clientName=clientName;
            this.inFromClient=inFromClient;
            this.outToClient=outToClient;
            System.out.println("New client #" + clientNumber + " connected at " + socket);
        }
        
        public void memberListResponse() throws IOException {
    		String s="";
            for(int i=0;i<members.size();i++) {
            	s+=members.get(i)+"\n";
            }
            s+=getMembersfromotherserver();
            outToClient.writeUTF(s);
            outToClient.flush();
    	}

        public void run() 
        {
            try 
            {
            	while(true) 
            	{
        			String sentence = inFromClient.readUTF();
	                if(sentence !=null)
	                {
	                	if (sentence.equals("quit")) 
		                {
		                	break;
		    			}
		                else if(sentence.equals("getMemberList")) 
		                {
		                	memberListResponse();
		                }
		                else
		                {
		                	String x[]=new String[2];
		            		x=sentence.split(" ", 2);
		            		if(x[0].equals(clientName))
		            		{
		            			outToClient.writeUTF("false");
		            			outToClient.flush();
		            			continue;
		            		}
		            		Route(outToClient, clientName, x[1], x[0]);
		                }
	                }
        		}
            } 
            catch (Exception e) 
            {
            	System.out.println(e.getMessage());
            	e.printStackTrace();
                System.out.println("Error handling client #" + clientNumber);
            } 
            finally
            {
                try 
                {
                	for(int i=0;i<members.size();i++) 
                	{
                		if(members.get(i).equals(clientName)) 
                		{
                			outToClient.writeUTF("#");
                			outToClient.flush();
                        	members.remove(i);
                        	membersSocket.remove(i);
                        	outToClientList.remove(i);
                        	inFromClientList.remove(i);
                        	inFromClient.close();
                        	outToClient.close();
                			break;
                		}
                	}
                	socket.close(); 
                } 
                catch (IOException e) {
                	
                }
                System.out.println("Connection with client # " + clientNumber + " closed");
            }
        }
    }
}