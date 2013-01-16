package org.spoutcraft.launcher.technic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.spoutcraft.launcher.exceptions.DownloadException;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.DownloadUtils;

public class TechnicRestAPI {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static final String REST_URL = "http://www.sctgaming.com/Technic/API/";
	public static final String MODPACKS_URL = REST_URL + "modpacks/";
	public static final String CACHE_URL = REST_URL + "cache/";
	public static final String MOD_URL = CACHE_URL + "mod/";

	public static String getModDownloadURL(String mod, String build) {
		return CACHE_URL + mod + "/" + build;
	}

	public static String getModMD5URL(String mod, String build) {
		return getModDownloadURL(mod, build) + "/MD5";
	}

	public static String getModpackURL(String modpack, String build) {
		return MODPACKS_URL + modpack + "/build/" + build;
	}

	public static String getModpackBuildsURL(String modpack) {
		return MODPACKS_URL + modpack;
	}

	public static Modpacks getModpacks() throws RestfulAPIException {
		InputStream stream = null;
		String url = MODPACKS_URL;
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			Modpacks result = mapper.readValue(stream, Modpacks.class);
			return result;
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public static void downloadMod(File location, String mod, String build, DownloadListener listener) throws DownloadException {
		try {
			DownloadUtils.downloadFile(getModDownloadURL(mod, build), location.getPath(), location.getName(), getModMD5(mod, build), listener);
		} catch (IOException e) {
			throw new DownloadException(e);
		}
	}

	public static String getModMD5(String mod, String build) throws RestfulAPIException {
		InputStream stream = null;
		String url = getModMD5URL(mod, build);
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			TechnicMD5 md5Result = mapper.readValue(stream, TechnicMD5.class);
			return md5Result.getMD5();
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public static Modpack getModpack(String modpack, String build) throws RestfulAPIException {
		InputStream stream = null;
		String url = getModpackURL(modpack, build);
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			Modpack result = mapper.readValue(stream, Modpack.class);
			return result;
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public static ModpackBuilds getModpackBuilds(String modpack) throws RestfulAPIException {
		InputStream stream = null;
		String url = getModpackBuildsURL(modpack);
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			ModpackBuilds result = mapper.readValue(stream, ModpackBuilds.class);
			return result;
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	private class TechnicMD5 {
		@JsonProperty("MD5")
		String md5;

		public String getMD5() {
			return md5;
		}
	}
}