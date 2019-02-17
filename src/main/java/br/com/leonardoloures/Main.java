package br.com.leonardoloures;

import org.restexpress.RestExpress;
import org.restexpress.util.Environment;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    private static final String SERVICE_NAME = "Banco de artistas";
    private static Configuration config;
    private static RestExpress server;

	public static void main(String[] args) throws Exception
	{
		config = loadEnvironment(args);
		Server server = new Server(config);
		server.start().awaitShutdown();
	}

	private static Configuration loadEnvironment(String[] args) throws FileNotFoundException, IOException {
		if (args.length > 0) {
			return Environment.from(args[0], Configuration.class);
		}

		return Environment.fromDefault(Configuration.class);
	}
}
