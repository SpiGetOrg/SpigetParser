package org.spiget.parser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.spiget.data.resource.update.ResourceUpdate;

import java.util.Base64;

import static org.spiget.parser.ParserUtil.abbrOrSpan;
import static org.spiget.parser.ParserUtil.parseTimeOrTitle;

public class ResourceUpdateParser {

	@NotNull
	public ResourceUpdate parse(@NotNull Document document, @NotNull ResourceUpdate base) {
		Element resourceUpdate = document.select("li.resourceUpdate").first();
		Element messageText = resourceUpdate.select("blockquote.messageText").first();
		Element datePermalink = resourceUpdate.select("a.datePermalink").first();
		Element updateDateTime = abbrOrSpan(datePermalink, ".DateTime");

		base.setDescription(Base64.getEncoder().encodeToString(messageText.html().getBytes()));
		base.setDate(parseTimeOrTitle(updateDateTime));
		return base;
	}

}
