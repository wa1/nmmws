package se.kth.erlundin.ninemenmorris;



import java.io.IOException;

public class SendMessageToDevice {

	public static void main(String[] args) throws IOException {
                //Send a message to the app
		int responseCode = MessageUtil.sendMessage(
				/*ServerConfiguration.AUTHENTICATION_TOKEN,
				ServerConfiguration.REGISTRATION_ID,*/
                        ServerConfiguration.getAuthToken(), 
                        "APA91bH0eAR3xY0dCja5RR-LXw1GvkYLyXg5011EL74Yn-uqbupHw_PRImLzxata40QorFbTmBgzqwlA_75q_6HKdKyTl1t4B5RFyLm6_lKcp9ExOSzNNjFbkV5GaEx8XkjnXScgIN9MOa4E7cWCbAt3ZXhBN5qgDTa5LlnP7UZWeXpqS_AlbMs",	
                        "HIGHSCORES");
		System.out.println(ServerConfiguration.getAuthToken() + ". " + (responseCode));
	}
}
