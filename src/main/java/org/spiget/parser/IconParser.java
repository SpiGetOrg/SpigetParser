package org.spiget.parser;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.spiget.data.resource.SpigetIcon;

import java.io.IOException;

import static org.spiget.parser.ParserUtil.iconToBase64;

@Log4j2
public class IconParser {

	@NotNull
	public SpigetIcon parse(@NotNull Element iconElement) {
		Element resourceAvatarImage = iconElement.select("img").first();// <img src="data/avatars/s/54/54321.jpg?54321" width="48" height="48" alt="example">

		String iconSource = resourceAvatarImage.attr("src");
		String iconData = "";
		if (iconSource.contains("static.spigotmc.org/")) {
			iconSource = "";
		} else {
			try {
				iconData = iconToBase64(iconSource);
			} catch (IOException | InterruptedException e) {
				log.warn("Failed to download icon data for " + iconSource, e);
			}
		}
		return new SpigetIcon(iconSource, iconData);
	}

}
