package org.spiget.parser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.spiget.data.resource.ListedResource;
import org.spiget.data.resource.Rating;
import org.spiget.data.resource.version.ResourceVersion;

import static org.spiget.parser.ParserUtil.*;

public class PremiumResourceVersionItemParser extends ResourceVersionItemParser {

	/**
	 * Parses premium resource version items This generates a version ID from the resource id and version release date, since we can't get the real version ID from the HTML
	 *
	 * @param versionItem &lt;tr class="dataRow "&gt;
	 * @return the parsed version
	 */
	@NotNull
	@Override
	public ResourceVersion parse(@NotNull Element versionItem, @NotNull ListedResource resource) {
		Element version = versionItem.select("td.version").first();// <td class="version">1.5</td>
		Element releaseDate = versionItem.select("td.releaseDate").first();// <td class="releaseDate"><abbr class="DateTime" data-time="1466633628" data-diff="4835" data-datestring="Jun 22, 2016" data-timestring="11:13 PM" title="Jun 22, 2016 at 11:13 PM">Yesterday at 11:13 PM</abbr></td>
		Element downloads = versionItem.select("td.downloads").first();// <td class="downloads">2</td>
		Element rating = versionItem.select("td.rating").first();

		Element releaseDateTime = abbrOrSpan(releaseDate, ".DateTime");

		// Generate a unique ID for the version
		int versionId = Math.abs(stringToNumber(releaseDateTime.attr("title"), resource.getId()) + stringToNumber(version.text(), resource.getId()));
		String versionIdString = "999"+ String.valueOf(resource.getId() + versionId);

		ResourceVersion resourceVersion;
		{
			resourceVersion = new ResourceVersion(Integer.parseInt(versionIdString), version.text());

			resourceVersion.setDownloads(Integer.parseInt(stringToInt(downloads.text())));
		}
		{
			resourceVersion.setReleaseDate(parseTimeOrTitle(releaseDateTime));
		}
		{
			Rating rating1 = new RatingParser().parse(rating);
			resourceVersion.setRating(rating1);
		}

		return resourceVersion;
	}

}
