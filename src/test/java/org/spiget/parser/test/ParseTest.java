package org.spiget.parser.test;

import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.spiget.client.SpigetClient;
import org.spiget.data.author.ListedAuthor;
import org.spiget.data.category.ListedCategory;
import org.spiget.data.resource.ListedResource;
import org.spiget.data.resource.Resource;
import org.spiget.data.resource.version.ListedResourceVersion;
import org.spiget.parser.ParserUtil;
import org.spiget.parser.ResourceListItemParser;
import org.spiget.parser.ResourcePageParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class ParseTest {

	public ParseTest() {
		SpigetClient.config = new JsonObject();
		SpigetClient.config.addProperty("debug.connections", false);
	}

	@Test
	public void dateTimeParseTest() {
		String dateTime = "May 27, 2016 at 5:20 PM " + TimeZone.getDefault().getDisplayName();
		long unix = ParserUtil.parseDateTimeToLong(dateTime);

		assertTrue(unix != 0);
	}

	@Test
	public void resourceItemParseTest() throws IOException {
		String html = org.apache.commons.io.IOUtils.toString(ParseTest.class.getResourceAsStream("/vendor/HologramAPI-ResourceItem.html"));
		Document document = Jsoup.parse(html);
		Element resourceItem = document.select("li.resourceListItem").first();

		ListedResource parsed = new ResourceListItemParser().parse(resourceItem);
		assertEquals(6766, parsed.getId());
		assertEquals("[API] HologramAPI [1.7 | 1.8 | 1.9]", parsed.getName());
		assertEquals("1.6.0", parsed.getVersion().getName());
		assertFalse(parsed.isPremium());
		assertEquals(6643, parsed.getAuthor().getId());
		assertEquals("inventivetalent", parsed.getAuthor().getName());
	}

	@Test
	public void premiumResourceItemParseTest() throws IOException {
		String html = org.apache.commons.io.IOUtils.toString(ParseTest.class.getResourceAsStream("/vendor/DeluxeChat-ResourceItem.html"));
		Document document = Jsoup.parse(html);
		Element resourceItem = document.select("li.resourceListItem").first();

		ListedResource parsed = new ResourceListItemParser().parse(resourceItem);
		System.out.println(parsed);
		assertEquals(1277, parsed.getId());
		assertEquals("DeluxeChat", parsed.getName());
		assertEquals("1.12.3", parsed.getVersion().getName());
		assertTrue(parsed.isPremium());
		assertEquals(7.50, parsed.getPrice(), 0);
		assertEquals("USD", parsed.getCurrency());
		assertEquals(1001, parsed.getAuthor().getId());
		assertEquals("clip", parsed.getAuthor().getName());
	}

	@Test
	public void resourcePageParseTest() throws IOException {
		String html = org.apache.commons.io.IOUtils.toString(ParseTest.class.getResourceAsStream("/vendor/InventoryScroll-ResourcePage.html"));
		Document document = Jsoup.parse(html);

		ListedResource base = new ListedResource(21714, "InventoryScroll");//Would be provided by the resource list fetcher
		base.setCategory(new ListedCategory(22, "Mechanics"));
		base.setVersion(new ListedResourceVersion(0, "1.1.0"));
		base.setAuthor(new ListedAuthor(6643, "inventivetalent"));

		Resource parsed = new ResourcePageParser().parse(document, base);
		assertEquals(Arrays.asList("1.7", "1.8", "1.9"), parsed.getTestedVersions());
		assertEquals("inventivetalent", parsed.getAuthor().getName());
		assertNotNull(parsed.getDescription());
		assertTrue(new String(Base64.getDecoder().decode(parsed.getDescription())).startsWith("This plugin allows you to swap the items in your hotbar with other items in your inventory."));
	}

	@Test
	public void stringToNumberTest() {
		String string = "1.58.6.59.7";
		int number = ParserUtil.stringToNumber(string);

		System.out.println(string + " => " + number);
	}

	@Test
	public void stringToNumberTest2() {
		String string = "Jan 2, 2018 at 2:22 PM";
		int number = ParserUtil.stringToNumber(string);

		System.out.println(string + " => " + number);
	}


}
