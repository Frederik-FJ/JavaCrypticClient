package connection;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.websocket.*;

@ClientEndpoint
public class WebSocketClient {
	
	Session userSession = null; 
	private MessageHandler messageHandler;
	boolean response = false;
	String message;
	WebSocketContainer container;

	public WebSocketClient(URI endpointURI) {
		try{
			this.container = ContainerProvider.getWebSocketContainer();
			this.container.connectToServer(this, endpointURI);
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@OnOpen
	public void onOpen(Session userSession){
		System.out.println("opening websocket");
		this.userSession = userSession;
	}



	@OnClose
	public void onClose(Session userSession, CloseReason reason){

		LocalDateTime dt = LocalDateTime.now();
		DateTimeFormatter dtformater= DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		String formatDateTime = dt.format(dtformater);


		System.out.print(formatDateTime);
		System.out.println("closing websocket");
	}

	@OnMessage
	public void onMessage(String message){
		if(this.messageHandler != null){
			this.messageHandler.handleMessage(message);
		}

		// System.out.println("Response: \t" + message);

		if(message.contains("notify-id")){
			return;
		}
		this.message = message;
		this.response = true;
	}


	public void addMessageHandler(MessageHandler msgHandler){
		this.messageHandler = msgHandler;
	}

	public void sendMessage(String message){
		this.userSession.getAsyncRemote().sendText(message);
		//System.out.println("Message: \t" + message);
	}

	public Map request(Map command, boolean noResponse){

		GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
		String tag = "";
		if(command.get("ms") != null)
			tag = command.get("tag").toString();
		Gson gson = gsonBuilder.create();
		Map result;
		this.response = false;
		this.sendMessage(gson.toJson(command));
		try{
			if(!noResponse){
				while(!response){
					Thread.sleep(3);
				}
				if (command.get("ms") != null){
					result = gson.fromJson(message, Map.class);
					try{
						while (!result.get("tag").toString()
								.equals(tag)){
							Thread.sleep(3);
							result = gson.fromJson(message, Map.class);
						}
					}catch (NullPointerException e){
						System.err.println(message);
					}

				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		response = false;
		return gson.fromJson(this.message, Map.class);
	}

	public void sendCommand(Map command){
		Gson gson = new Gson();
		this.sendMessage(gson.toJson(command));
		response = false;
	}

	public Map microservice(String ms, List<String> endpoint, Map data) throws InvalidServerResponseException, UnknownMicroserviceException {

		Gson gson = new Gson();

		Map<String, Object> req = new HashMap<>();
		req.put("ms", ms);
		req.put("endpoint", endpoint);
		req.put("data", data);
		req.put("tag", uuid());
		Map response = this.request(req, false);

		if(response.containsKey("error")){
			String error = response.get("error").toString();
			if(error.equals("unknown microservice")){
				throw new UnknownMicroserviceException(ms);
			}
			throw new InvalidServerResponseException(response);
		}

		/*if(!response.containsKey("response")){
			throw new InvalidServerResponseException(response);
		}*/
		//System.out.println(gson.toJson(req));
		//System.out.println(response + ms);
		Map dats = (Map) response.get("data");
		if(dats.containsKey("error")){
			//TODO richtigen Fehler suchen und diesen werfen
			throw new InvalidServerResponseException(response);
		}
		return dats;

	}

	public String uuid(){
		return UUID.randomUUID().toString();
	}






















	interface MessageHandler{
		void handleMessage(String message);
	}

}
