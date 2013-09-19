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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MineProxy extends Thread {
	private String auth_server;
	private ServerSocket server;
	private int port;

	public MineProxy(String auth_server) throws IOException {
		this.auth_server = auth_server;
		server = new ServerSocket(0);
		port = server.getLocalPort();
	}

	public void run() {
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", Integer.toString(port));
		System.setProperty("https.proxyHost", "127.0.0.1");
		System.setProperty("https.proxyPort", Integer.toString(port));

		while(true) {
			try {
				Socket connection = server.accept();
				new MineProxyHandler(connection, auth_server);
			}
			catch(IOException e) {
				//Ignore
			}
		}
	}
}
