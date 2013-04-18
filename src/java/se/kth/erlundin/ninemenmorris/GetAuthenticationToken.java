package se.kth.erlundin.ninemenmorris;



import java.io.IOException;

public class GetAuthenticationToken {

	public static void main(String[] args) throws IOException {
		String token = AuthenticationUtil.getToken("ninemenmorris@gmail.com","nn215NN6");
		System.out.println(token);
	}
}
