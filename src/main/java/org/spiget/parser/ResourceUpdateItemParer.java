package org.spiget.parser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.spiget.data.resource.update.ResourceUpdate;

public class ResourceUpdateItemParer {

	@NotNull
	public ResourceUpdate parse(@NotNull Element resourceUpdateItem) {
		ResourceUpdate resourceUpdate = new ResourceUpdate(Integer.parseInt(resourceUpdateItem.id().split("-")[1]));
		Element textHeading = resourceUpdateItem.select("h2.textHeading").first();
		resourceUpdate.setTitle(textHeading.select("a").first().text());
		return resourceUpdate;
	}

}
