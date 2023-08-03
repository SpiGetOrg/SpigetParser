package org.spiget.parser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.spiget.data.author.ListedAuthor;
import org.spiget.data.resource.Rating;
import org.spiget.data.resource.ResourceReview;
import org.spiget.data.resource.SpigetIcon;

import java.util.Base64;

import static org.spiget.parser.ParserUtil.*;

public class ResourceReviewItemParser {

    @NotNull
    public ResourceReview parse(@NotNull Element resourceReviewItem) {
        int reviewId;
        int reviewResource = 0;
        ListedAuthor reviewAuthor;
        Rating reviewRating;
        String reviewVersion;
        String reviewMessage;
        long reviewDate;
        String reviewResponse = null;

        {
            Element reviewResourceElement = resourceReviewItem.select("div.reviewResource").first();
            if (reviewResourceElement != null) {
                Element reviewResourceLink = reviewResourceElement.select("a").first();
                if (reviewResourceLink != null) {
                    String reviewResourceUrl = reviewResourceLink.attr("href");
                    reviewResource = Integer.parseInt(extractIdFromUrl(reviewResourceUrl, DOT_URL_ID));
                }
            }
        }
        {
            Element memberAvatar = resourceReviewItem.select("a.avatar").first();
            reviewAuthor = new ListedAuthor(Integer.parseInt(extractIdFromUrl(memberAvatar.attr("href"), DOT_URL_ID)));
            reviewAuthor.setName(resourceReviewItem.attr("data-author"));
            SpigetIcon avatar = new IconParser().parse(memberAvatar);
            reviewAuthor.setIcon(avatar);
        }
        {
            Element messageInfo = resourceReviewItem.select("div.messageInfo").first();
            {
                Element rating = messageInfo.select("div.rating").first();
                reviewRating = new RatingParser().parseSingle(rating);
            }
            {
                Element version = abbrOrSpan(messageInfo, ".muted");
                reviewVersion = version.text().substring("Version: ".length());
            }
            {
                Element baseHtml = messageInfo.select("blockquote.baseHtml").first();
                reviewMessage = Base64.getEncoder().encodeToString(baseHtml.html().getBytes());
            }

            {
                Element messageMeta = messageInfo.select("div.messageMeta").first();

                Element permalink = messageMeta.select("a.item").first();
                reviewId = Integer.parseInt(extractIdFromUrl(permalink.attr("href"), PARAM_URL_ID));

                reviewDate = parseTimeOrTitle(abbrOrSpan(messageMeta, ".DateTime"));
            }

            {
                Element messageResponse = messageInfo.select("ol.messageResponse").first();
                if (messageResponse != null) {
                    Element blockQuote = messageResponse.select("blockquote").first();
                    reviewResponse = Base64.getEncoder().encodeToString(blockQuote.html().getBytes());
                }
            }
        }

        ResourceReview review = new ResourceReview(reviewId);
        review.setResource(reviewResource);
        review.setAuthor(reviewAuthor);
        review.setRating(reviewRating);
        review.setVersion(reviewVersion);
        review.setMessage(reviewMessage);
        review.setDate(reviewDate);
        review.setResponseMessage(reviewResponse);

        return review;
    }

}
