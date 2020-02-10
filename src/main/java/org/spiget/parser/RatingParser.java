package org.spiget.parser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.spiget.data.resource.Rating;

import static org.spiget.parser.ParserUtil.stringToInt;

public class RatingParser {

	@NotNull
	public Rating parse(@NotNull Element ratingElement) {
		Element resourceRatings = ratingElement.select("span.ratings").first();// <span class="ratings" title="4.90">
		Element resourceRatingsHint = ratingElement.select("span.Hint").first();// <span class="Hint">10 ratings</span>
		return new Rating(Integer.parseInt(stringToInt(resourceRatingsHint.text().split(" ")[0])), Float.parseFloat(resourceRatings.attr("title")));
	}

	@NotNull
	public Rating parseSingle(@NotNull Element ratingElement) {
		Element resourceRatings = ratingElement.select("span.ratings").first();// <span class="ratings" title="4.90">
		return new Rating(1, Float.parseFloat(resourceRatings.attr("title")));
	}

}
