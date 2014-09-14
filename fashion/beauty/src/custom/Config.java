package custom;

import com.bstoneinfo.lib.ad.BSAdUtils;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.opt.ad.BSAdBannerAdChina;
import com.bstoneinfo.opt.ad.BSAdBannerAdmob;
import com.bstoneinfo.opt.ad.BSAdScreenAdChina;
import com.bstoneinfo.opt.ad.BSAdScreenAdmob;

public class Config {

    public static final boolean isPro = false;

    public static final String remoteConfigURL = "http://www.bstoneinfo.com/config/beauty/beauty-2.1.0-" + BSUtils.getManifestMetaData("UMENG_CHANNEL") + ".json";

    public static void init() {
        BSAdUtils.registerAdClass("adchina_banner", BSAdBannerAdChina.class);
        BSAdUtils.registerAdClass("adchina_screen", BSAdScreenAdChina.class);
        BSAdUtils.registerAdClass("admob_banner", BSAdBannerAdmob.class);
        BSAdUtils.registerAdClass("admob_screen", BSAdScreenAdmob.class);
    }

}
