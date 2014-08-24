package custom;

import com.bstoneinfo.lib.ad.BSAdUtils;
import com.bstoneinfo.opt.ad.BSAdBannerAdChina;
import com.bstoneinfo.opt.ad.BSAdBannerGoogle;
import com.bstoneinfo.opt.ad.BSAdScreenAdChina;
import com.bstoneinfo.opt.ad.BSAdScreenGoogle;

public class Config {

    public static final boolean isPro = true;

    public static final String remoteConfigURL = "http://www.bstoneinfo.com/config/beauty/beauty-play-2.0.3.json";

    public static void init() {
        BSAdUtils.registerAdBanner("AdChina", BSAdBannerAdChina.class);
        BSAdUtils.registerAdScreen("AdChina", BSAdScreenAdChina.class);
        BSAdUtils.registerAdBanner("Admob", BSAdBannerGoogle.class);
        BSAdUtils.registerAdScreen("Admob", BSAdScreenGoogle.class);
    }

}
