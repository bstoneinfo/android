package custom;

import com.bstoneinfo.lib.ad.BSAdUtils;
import com.bstoneinfo.opt.ad.BSAdBannerAdChina;
import com.bstoneinfo.opt.ad.BSAdBannerAdmob;
import com.bstoneinfo.opt.ad.BSAdScreenAdChina;
import com.bstoneinfo.opt.ad.BSAdScreenAdmob;

public class Config {

    public static final boolean isPro = true;

    public static final String remoteConfigURL = "http://www.bstoneinfo.com/config/beauty/beauty-pro-2.0.3.json";

    public static void init() {
        BSAdUtils.registerAdBanner("AdChina", BSAdBannerAdChina.class);
        BSAdUtils.registerAdScreen("AdChina", BSAdScreenAdChina.class);
        BSAdUtils.registerAdBanner("Admob", BSAdBannerAdmob.class);
        BSAdUtils.registerAdScreen("Admob", BSAdScreenAdmob.class);
    }

}
