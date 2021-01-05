package org.spiget.parser;

import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.spiget.data.resource.SpigetIcon;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.spiget.parser.ParserUtil.iconToBase64;

@Log4j2
public class IconParser {

    static final Pattern HASH_PATTERN = Pattern.compile("secure\\.gravatar\\.com\\/avatar\\/([0-9a-z]+)");
    static final Pattern INFO_PATTERN = Pattern.compile("\\?([0-9]+)");

    @NotNull
    public SpigetIcon parse(@NotNull Element iconElement) {
        Element resourceAvatarImage = iconElement.select("img").first();// <img src="data/avatars/s/54/54321.jpg?54321" width="48" height="48" alt="example">

        String iconSource = resourceAvatarImage.attr("src");
        String iconData = "";
        String info = "";
        String hash = "";
        if (iconSource.startsWith("//static.spigotmc.org/")) {
            iconSource = "";
        } else {
            if (iconSource.contains("secure.gravatar.com")) {
                Matcher matcher = HASH_PATTERN.matcher(iconSource);
                if (matcher.matches()) {
                    hash = matcher.group(1);
                }
            } else if (iconSource.startsWith("data")) {
                Matcher matcher = INFO_PATTERN.matcher(iconSource);
                if (matcher.matches()) {
                    info = matcher.group(1);
                }
            }
            try {
                iconData = iconToBase64(iconSource);
            } catch (IOException | InterruptedException e) {
                Sentry.captureException(e);
                log.warn("Failed to download icon data for " + iconSource, e);
            }
        }
        return new SpigetIcon(iconSource, iconData, info, hash);
    }

}
