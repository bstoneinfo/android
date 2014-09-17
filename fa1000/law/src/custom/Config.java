package custom;

import com.bstoneinfo.lib.ad.BSAdUtils;
import com.bstoneinfo.lib.mail.MailManager;
import com.bstoneinfo.opt.ad.BSAdBannerAdChina;
import com.bstoneinfo.opt.ad.BSAdBannerAdmob;
import com.bstoneinfo.opt.ad.BSAdScreenAdChina;
import com.bstoneinfo.opt.ad.BSAdScreenAdmob;

public class Config {

    public static final String remoteConfigURL = "http://www.fa1000.com/config/law/law-1.0.0.json";

    public static void init() {
        BSAdUtils.registerAdClass("adchina_banner", BSAdBannerAdChina.class);
        BSAdUtils.registerAdClass("adchina_screen", BSAdScreenAdChina.class);
        BSAdUtils.registerAdClass("admob_banner", BSAdBannerAdmob.class);
        BSAdUtils.registerAdClass("admob_screen", BSAdScreenAdmob.class);

        MailManager.mailServerPort = 25;
        MailManager.mailValidate = true;
        MailManager.mailServerHost = "smtp.qq.com";
        MailManager.mailUserName = "1461095806@qq.com";
        MailManager.mailPassword = "mhl810510mhl@qq";
        MailManager.mailFromAddress = "1461095806@qq.com";
        MailManager.mailToAddress = "mmmpic@126.com";
    }

}
