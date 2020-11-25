package com.rarchives.ripme.ripper.rippers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.rarchives.ripme.ripper.AbstractHTMLRipper;
import com.rarchives.ripme.utils.Http;

public class BenjaminVingriefRipper extends AbstractHTMLRipper {

    public BenjaminVingriefRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return "benjamin-vingrief";
    }
    @Override
    public String getDomain() {
        return "benjamin-vingrief.com";
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("^https?://www.benjamin-vingrief.com/([a-zA-Z0-9_\\-]+).*$");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            // Return the text contained between () in the regex
            return m.group(1);
        }
        throw new MalformedURLException("Expected benjamin-vingrief.com URL format: " +
                        "benjamin-vingrief.com/index - got " + url + "instead");
    }

    @Override
    public Document getFirstPage() throws IOException {
        return Http.url(url).get();
    }
    
    @Override
    public Document getNextPage(Document doc) throws IOException {
        // Find next page
        Elements hrefs = doc.select("a.pagination_current + a.pagination_link");
        if (hrefs.isEmpty()) {
            throw new IOException("No more pages");
        }
        String nextUrl = "http://www.benjamin-vingrief.com" + hrefs.first().attr("href");
        sleep(500);
        return Http.url(nextUrl).get();
    }
    /*
    @Override
    public List<String> getURLsFromPage(Document doc) {
        List<String> imageURLs = new ArrayList<>();
        for (Element thumb : doc.select("div.boxed-content > a > img")) {
            String image = thumb.attr("src").replaceAll("thumbs", "images");
            image = image.replace("_b", "_o");
            image = image.replaceAll("\\d-s", "i");
            imageURLs.add(image);
        }
        return imageURLs;
    }
    */
    
    @Override
    public List<String> getURLsFromPage(Document doc) {
        List<String> result = new ArrayList<String>();
        for (Element el : doc.select("img")) {
            result.add(el.attr("srcset"));
        }
        return result;
    }
    
    @Override
    public void downloadURL(URL url, int index) {
        addURLToDownload(url, getPrefix(index));
    }
}
