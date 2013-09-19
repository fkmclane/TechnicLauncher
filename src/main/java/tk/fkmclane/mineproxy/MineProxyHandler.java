/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package tk.fkmclane.mineproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MineProxyHandler extends Thread {
	public static final Pattern login = Pattern.compile("http(s)?://login\\.minecraft\\.net/(.*)");
	public static final Pattern joinserver = Pattern.compile("http://session\\.minecraft\\.net/game/joinserver\\.jsp(.*)");
	public static final Pattern checkserver = Pattern.compile("http://session\\.minecraft\\.net/game/checkserver\\.jsp(.*)");
	public static final Pattern skin = Pattern.compile("http://skins\\.minecraft\\.net/MinecraftSkins/(.*)\\.png");
	public static final Pattern cape = Pattern.compile("http://skins\\.minecraft\\.net/MinecraftCloaks/(.*)\\.png");

	private Socket client, remote;
	private String auth_server;

	public MineProxyHandler(Socket client, String auth_server) {
		this.client = client;
		this.auth_server = auth_server;
		start();
	}

	public void run() {
		try {
			BufferedReader client_in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter client_out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			String[] request = getRequest(client_in);
			HashMap<String, String> headers = extractHeaders(client_in);

			URL url = parseRequest(request[1], auth_server);
			request[1] = url.toString();

			remote = new Socket(url.getHost(), url.getPort() < 0 ? url.getProtocol().equals("https") ? 443 : 80 : url.getPort());
			BufferedReader remote_in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
			BufferedWriter remote_out = new BufferedWriter(new OutputStreamWriter(remote.getOutputStream()));

			if(!request[0].equals("CONNECT"))
				sendHeaders(remote_out, request, headers);

			new Pipe(client_in, remote_out);
			new Pipe(remote_in, client_out);
		}
		catch(Exception e) {
			System.err.println("Exception caught while proxying request: " + e);
			//Don't leave the client and server hanging
			try {
				client.close();
				remote.close();
			}
			catch(Exception ex) {}
		}
	}

	private static String[] getRequest(BufferedReader in) throws IOException {
		return in.readLine().split(" ");
	}

	private static HashMap<String, String> extractHeaders(BufferedReader in) throws IOException {
		HashMap<String, String> headers = new HashMap<String, String>();

		String header;
		while((header = in.readLine()) != null) {
			int delimeter = header.indexOf(": ");
			if(delimeter != -1)
				headers.put(header.substring(0, delimeter), header.substring(delimeter + 1));
			else
				break;
		}

		return headers;
	}

	private static URL parseRequest(String request, String auth_server) throws MalformedURLException {
		if(!request.startsWith("http"))
			request = "http://" + request;

		Matcher login_matcher = login.matcher(request);
		if(login_matcher.matches())
			return new URL("http://" + auth_server + "/" + login_matcher.group(2));

		Matcher joinserver_matcher = joinserver.matcher(request);
		if(joinserver_matcher.matches())
			return new URL("http://" + auth_server + "/joinserver.php" + joinserver_matcher.group(1));

		Matcher checkserver_matcher = checkserver.matcher(request);
		if(checkserver_matcher.matches())
			return new URL("http://" + auth_server + "/checkserver.php" + checkserver_matcher.group(1));

		Matcher skin_matcher = skin.matcher(request);
		if(skin_matcher.matches())
			return new URL("http://" + auth_server + "/getskin.php?user=" + skin_matcher.group(1));

		Matcher cape_matcher = cape.matcher(request);
		if(cape_matcher.matches())
			return new URL("http://" + auth_server + "/getcape.php?user=" + cape_matcher.group(1));

		return new URL(request);
	}

	private static void sendHeaders(BufferedWriter out, String[] request, HashMap<String, String> headers) throws IOException {
		out.write(request[0]);
		for(int i = 1; i < request.length; i++)
			out.write(" " + request[i]);
		out.write("\r\n");

		for(String header : headers.keySet())
			out.write(header + ": " + headers.get(header) + "\r\n");

		out.write("\r\n");

		out.flush();
	}
}
