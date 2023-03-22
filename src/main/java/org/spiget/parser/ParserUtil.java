package org.spiget.parser;

import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.spiget.client.SpigetClient;
import org.spiget.client.SpigetDownload;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class ParserUtil {

	static final Pattern DOT_URL_ID = Pattern.compile("\\.(.*?)/");
	static final Pattern PARAM_URL_ID = Pattern.compile("\\?.+=([0-9]+)");

	static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");

	public static boolean FOLLOW_EXTERNAL_URLS = true;

	public static String extractIdFromUrl(String url, Pattern pattern) {
		Matcher matcher = pattern.matcher(url);
		while (matcher.find()) {
			return matcher.group(1);
		}
		log.warn("Failed to extract id from " + url);
		return "-1";
	}

	public static Element abbrOrSpan(Element element, String selector) {
		Element element1 = element.select("abbr" + selector).first();
		if (element1 == null) { element1 = element.select("span" + selector).first(); }
		return element1;
	}

	public static long parseDateTimeToLong(String dateTime) {
		try {
			return DATE_TIME_FORMAT.parse(dateTime).getTime() / 1000;// divide by 1000 for UNIX
		} catch (ParseException e) {
			Sentry.captureException(e);
			log.warn("Unable to parse " + dateTime + " to timestamp");
			return 0;
		}
	}

	/**
	 * Downloads the icon from the specified source and converts it to Base64
	 *
	 * @param source source of the image
	 * @return the Base64 data
	 * @throws IOException          if the download fails
	 * @throws InterruptedException if the download fails
	 */
	@NotNull
	public static String iconToBase64(@NotNull String source) throws IOException, InterruptedException {
		SpigetDownload iconDownload = SpigetClient.download((!source.startsWith("http") ? SpigetClient.BASE_URL:"") + source);
		byte[] iconBytes = IOUtils.toByteArray(iconDownload.getInputStream());
		return Base64.getEncoder().encodeToString(iconBytes);
	}

	/**
	 * Strips dots and commas from a string to make it parsable to an Integer
	 *
	 * @param original number string
	 * @return the stripped integer string
	 */
	@NotNull
	public static String stringToInt(@NotNull String original) {
		return original.replace(",", "").replace(".", "");
	}

	@NotNull
	public static long parseTimeOrTitle(@NotNull Element element) {
		if (element.hasAttr("data-time")) {
			return Long.parseLong(element.attr("data-time"));
		} else if (element.hasAttr("title")) {
			return parseDateTimeToLong(element.attr("title") + " " + TimeZone.getDefault().getDisplayName());
		} else {
			log.warn("No data-time or title attribute found");
			return 0;
		}
	}

	public static int stringToNumber(@NotNull String string) {
		return stringToNumber(string, 1337);
	}

	public static int stringToNumber(@NotNull String string, int num) {
		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			int pos = ch - 'a' + 1;
			num -= pos;
		}
		return num;
	}

	public static String followExternalUrl(String url) {
		if (!FOLLOW_EXTERNAL_URLS) {
			return url;
		}
		try {
			if (!url.startsWith("http")) {
				url = SpigetClient.BASE_URL + url;
			}
			Connection.Response response = Jsoup.connect(url)
					.userAgent(SpigetClient.userAgent)
					.followRedirects(false)
					.timeout(5000)
					.method(Connection.Method.GET)
					.execute();
			String locationHeader = response.header("Location");
			if (locationHeader != null) {
				return locationHeader;
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			log.warn("Failed to follow external url", e);
		}
		return url;
	}

}
