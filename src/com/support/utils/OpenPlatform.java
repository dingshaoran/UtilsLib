package com.support.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMEvernoteHandler;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.controller.media.EvernoteShareContent;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.facebook.controller.UMFacebookHandler;
import com.umeng.socialize.facebook.controller.UMFacebookHandler.PostType;
import com.umeng.socialize.facebook.media.FaceBookShareContent;
import com.umeng.socialize.instagram.controller.UMInstagramHandler;
import com.umeng.socialize.instagram.media.InstagramShareContent;
import com.umeng.socialize.laiwang.controller.UMLWHandler;
import com.umeng.socialize.laiwang.media.LWDynamicShareContent;
import com.umeng.socialize.laiwang.media.LWShareContent;
import com.umeng.socialize.linkedin.controller.UMLinkedInHandler;
import com.umeng.socialize.linkedin.media.LinkedInShareContent;
import com.umeng.socialize.media.DoubanShareContent;
import com.umeng.socialize.media.GooglePlusShareContent;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.TwitterShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.pinterest.controller.UMPinterestHandler;
import com.umeng.socialize.pinterest.media.PinterestShareContent;
import com.umeng.socialize.pocket.controller.UMPocketHandler;
import com.umeng.socialize.pocket.media.PocketShareContent;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.umeng.socialize.yixin.controller.UMYXHandler;
import com.umeng.socialize.yixin.media.YiXinCircleShareContent;
import com.umeng.socialize.yixin.media.YiXinShareContent;
import com.umeng.socialize.ynote.controller.UMYNoteHandler;
import com.umeng.socialize.ynote.media.YNoteShareContent;

/**
 * 代码混淆请添加
 -dontshrink
 -dontoptimize
 -dontwarn com.google.android.maps.**
 -dontwarn android.webkit.WebView
 -dontwarn com.umeng.**
 -dontwarn com.tencent.weibo.sdk.**
 -dontwarn com.facebook.**

 -libraryjars libs/SocialSDK_QQZone_2.jar

 -keep enum com.facebook.**
 -keepattributes Exceptions,InnerClasses,Signature
 -keepattributes *Annotation*
 -keepattributes SourceFile,LineNumberTable

 -keep public interface com.facebook.**
 -keep public interface com.tencent.**
 -keep public interface com.umeng.socialize.**
 -keep public interface com.umeng.socialize.sensor.**
 -keep public interface com.umeng.scrshot.**

 -keep public class com.umeng.socialize.* {*;}
 -keep public class javax.**
 -keep public class android.webkit.**

 -keep class com.facebook.**
 -keep class com.umeng.scrshot.**
 -keep public class com.tencent.** {*;}
 -keep class com.umeng.socialize.sensor.**

 -keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}

 -keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

 -keep class im.yixin.sdk.api.YXMessage {*;}
 -keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

 -keep public class [your_pkg].R$*{
 public static final int *;
 */
/**
 * 根据运行时报错情况继承res：string、drawable，或者使用友盟自动集成工具集成（会产生其他用不到的东西）
 * 注意！！！如果提示：没有在sdk中配置，是因为友盟没有提供相应jar，版本升级后配置UMSsoHandler即可解决 使用SSO授权必须添加如下代码：
 * protected void onActivityResult(int requestCode, int resultCode, Intent data)
 * { super.onActivityResult(requestCode, resultCode, data); UMSsoHandler
 * ssoHandler = mController.getConfig().getSsoHandler(requestCode);
 * if(ssoHandler != null){ ssoHandler.authorizeCallBack(requestCode, resultCode,
 * data); } }
 * 
 * @author DSR
 * 
 */
public class OpenPlatform {
	private OpenPlatform() {
	}

	private static OpenPlatform op;
	private static UMSocialService mController;
	private static Activity context;
	private SnsPostListener spl;

	public static OpenPlatform getInstance(Activity act) {
		if (act == null)// act不能为空
			return null;
		if (mController == null) {// 初始化友盟
			mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		}
		context = act;
		if (op == null) {
			op = new OpenPlatform();
		}
		return op;
	}

	public UMSocialService share2QQ(String appId, String appKey, String content, String title, UMImage umImage) {
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, appId, appKey); // 添加QQ支持,
																					// 并且设置QQ分享内容的target
																					// url
		qqSsoHandler.setTargetUrl("http://www.40000km.com.cn/");
		qqSsoHandler.addToSocialSDK();
		QQShareContent qqShareContent = new QQShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.QQ);
		return mController;
	}

	public UMSocialService share2QQWeiBo(String content, String title, UMImage umImage) {
		TencentWBSsoHandler qqSsoHandler = new TencentWBSsoHandler(); // 添加腾讯微博
		qqSsoHandler.setTargetUrl("http://www.40000km.com.cn/");
		qqSsoHandler.addToSocialSDK();
		TencentWbShareContent qqShareContent = new TencentWbShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.TENCENT);
		return mController;
	}

	public UMSocialService share2QZone(String appId, String appKey, String content, String title, UMImage umImage) {
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, appId, appKey);
		qZoneSsoHandler.setTargetUrl("http://www.40000km.com.cn/");
		qZoneSsoHandler.addToSocialSDK();
		QZoneShareContent qqShareContent = new QZoneShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.QZONE);
		return mController;
	}

	public UMSocialService share2WeiXin(String appId, String appKey, String content, String title, UMImage umImage) {
		UMWXHandler wxHandler = new UMWXHandler(context, appId, appKey);
		wxHandler.setTargetUrl("http://www.40000km.com.cn/");
		wxHandler.addToSocialSDK();
		WeiXinShareContent qqShareContent = new WeiXinShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.WEIXIN);
		return mController;
	}

	public UMSocialService share2FriendWeiXin(String appId, String appKey, String content, String title, UMImage umImage) {
		UMWXHandler wxHandler = new UMWXHandler(context, appId, appKey);
		wxHandler.setTargetUrl("http://www.40000km.com.cn/");
		wxHandler.setToCircle(true);
		wxHandler.addToSocialSDK();
		CircleShareContent qqShareContent = new CircleShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.WEIXIN_CIRCLE);
		return mController;
	}

	public UMSocialService share2Sina(String content, String title, UMImage umImage) {
		SinaSsoHandler wxHandler = new SinaSsoHandler();
		wxHandler.setTargetUrl("http://www.40000km.com.cn/");
		wxHandler.addToSocialSDK();

		SinaShareContent qqShareContent = new SinaShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.SINA);
		return mController;
	}

	public UMSocialService share2RenRen(String id, String appKey, String secretKey, String content, String title, UMImage umImage) {
		RenrenSsoHandler wxHandler = new RenrenSsoHandler(context, id, appKey, secretKey);
		wxHandler.addToSocialSDK();

		RenrenShareContent qqShareContent = new RenrenShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.RENREN);
		return mController;
	}

	public UMSocialService share2DouBan(String content, String title, UMImage umImage) {

		DoubanShareContent qqShareContent = new DoubanShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.DOUBAN);
		return mController;
	}

	public UMSocialService share2RenRen(String content, String title, UMImage umImage) {
		SinaSsoHandler wxHandler = new SinaSsoHandler();
		wxHandler.addToSocialSDK();

		SinaShareContent qqShareContent = new SinaShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.RENREN);
		return mController;
	}

	public UMSocialService share2Instagram(UMImage umImage) {
		UMInstagramHandler instagramHandler = new UMInstagramHandler(context);
		instagramHandler.addToSocialSDK();
		InstagramShareContent instagramShareContent = new InstagramShareContent(umImage);
		mController.setShareMedia(instagramShareContent);
		directShare(context, SHARE_MEDIA.INSTAGRAM);
		return mController;
	}

	public UMSocialService share2Facebook(String appId, String content, String title, UMImage umImage) {
		UMFacebookHandler mFacebookHandler = new UMFacebookHandler(context, appId, PostType.FEED);
		mFacebookHandler.addToSocialSDK();
		FaceBookShareContent fbContent = new FaceBookShareContent("分享");
		fbContent.setShareImage(umImage);
		fbContent.setShareContent(content);
		fbContent.setTitle(title);
		// fbContent.setTargetUrl("http://www.40000km.com.cn/");
		mController.setShareMedia(fbContent);
		directShare(context, SHARE_MEDIA.FACEBOOK);
		return mController;
	}

	public UMSocialService share2YiXin(String appId, String content, String title, UMImage umImage) {
		UMYXHandler yixinHandler = new UMYXHandler(context, appId);
		yixinHandler.enableLoadingDialog(false);// 关闭分享时的等待Dialog
		yixinHandler.setTargetUrl("http://www.40000km.com.cn/");// 设置target Url,
		yixinHandler.addToSocialSDK();
		YiXinShareContent qqShareContent = new YiXinShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.YIXIN);
		return mController;
	}

	public UMSocialService share2FriendYiXin(String appId, String content, String title, UMImage umImage) {
		UMYXHandler yixinHandler = new UMYXHandler(context, appId);
		yixinHandler.setToCircle(true);
		yixinHandler.enableLoadingDialog(false);// 关闭分享时的等待Dialog
		yixinHandler.setTargetUrl("http://www.40000km.com.cn/");// 设置target Url,
		yixinHandler.addToSocialSDK();
		YiXinCircleShareContent qqShareContent = new YiXinCircleShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.YIXIN_CIRCLE);
		return mController;
	}

	public UMSocialService share2LaiWang(String appId, String appKey, String content, String title, UMImage umImage) {
		UMLWHandler umlwHandler = new UMLWHandler(context, appId, appKey);
		umlwHandler.addToSocialSDK();
		LWShareContent qqShareContent = new LWShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.LAIWANG);
		return mController;
	}

	public UMSocialService share2LaiWangDongTai(String appId, String appKey, String content, String title, UMImage umImage) {
		UMLWHandler umlwHandler = new UMLWHandler(context, appId, appKey);
		umlwHandler.setToCircle(true);
		umlwHandler.addToSocialSDK();
		LWDynamicShareContent qqShareContent = new LWDynamicShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.LAIWANG_DYNAMIC);
		return mController;
	}

	public UMSocialService share2Pinterest(String appId, String content, String title, UMImage umImage) {
		UMPinterestHandler pinterestHandler = new UMPinterestHandler(OpenPlatform.context, appId);
		pinterestHandler.addToSocialSDK();

		if (content != null) {
			PinterestShareContent qqShareContent = new PinterestShareContent(content);

			if (title != null)
				qqShareContent.setTitle(title);
			if (umImage != null)
				qqShareContent.setShareImage(umImage);
			// qqShareContent.setShareMusic(uMusic);
			// qqShareContent.setShareVideo(video);
			mController.setShareMedia(qqShareContent);
		}
		directShare(context, SHARE_MEDIA.PINTEREST);
		return mController;

	}

	public UMSocialService share2YinXiangNote(String content, UMImage umImage) {
		UMEvernoteHandler evernoteHandler = new UMEvernoteHandler(context);
		evernoteHandler.addToSocialSDK();

		if (content != null) {
			EvernoteShareContent shareContent = new EvernoteShareContent(content);
			if (umImage != null)
				shareContent.setShareMedia(umImage);
			mController.setShareMedia(shareContent);
		}
		directShare(context, SHARE_MEDIA.EVERNOTE);
		return mController;
	}

	public UMSocialService share2YouDaoNote(String content, String title, UMImage umImage) {
		UMYNoteHandler yNoteHandler = new UMYNoteHandler(context);
		yNoteHandler.addToSocialSDK();
		YNoteShareContent qqShareContent = new YNoteShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (title != null)
			qqShareContent.setTitle(title);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.YNOTE);
		return mController;
	}

	public UMSocialService share2LinkedIn(String content, UMImage umImage) {
		UMLinkedInHandler linkedInHandler = new UMLinkedInHandler(context);
		linkedInHandler.addToSocialSDK();
		LinkedInShareContent linkedInShareContent = new LinkedInShareContent();

		if (content != null)
			linkedInShareContent.setShareContent(content);
		if (umImage != null)
			linkedInShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(linkedInShareContent);
		directShare(context, SHARE_MEDIA.LINKEDIN);
		return mController;
	}

	public UMSocialService share2Pocket(String content, UMImage umImage) {
		UMPocketHandler pocketHandler = new UMPocketHandler(context);
		pocketHandler.addToSocialSDK();
		PocketShareContent qqShareContent = new PocketShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.POCKET);
		return mController;
	}

	public UMSocialService share2Email(String content, UMImage umImage) {
		EmailHandler emailHandler = new EmailHandler();// 添加email
		emailHandler.addToSocialSDK();
		MailShareContent qqShareContent = new MailShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.EMAIL);
		return mController;
	}

	public UMSocialService share2Sms(String content, UMImage umImage) {
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();

		SmsShareContent qqShareContent = new SmsShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.SMS);
		return mController;
	}

	public UMSocialService share2Twitter(String content, UMImage umImage) {

		TwitterShareContent qqShareContent = new TwitterShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.TWITTER);
		return mController;
	}

	public UMSocialService share2GooglePlus(String content, UMImage umImage) {
		GooglePlusShareContent qqShareContent = new GooglePlusShareContent();
		if (content != null)
			qqShareContent.setShareContent(content);
		if (umImage != null)
			qqShareContent.setShareImage(umImage);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		mController.setShareMedia(qqShareContent);
		directShare(context, SHARE_MEDIA.GOOGLEPLUS);
		return mController;
	}

	/**
	 * 调用此方法进行分享，分享的信息分装到mController
	 * 
	 * @param context
	 * @param sm
	 */
	private void directShare(final Context context, SHARE_MEDIA sm) {
		mController.getConfig().closeToast();
		mController.directShare(context, sm, spl == null ? new SnsPostListener() {// 三维运算符，当没有设置SnsPostListener的时候使用默认的

			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				if (eCode != StatusCode.ST_CODE_SUCCESSED) {
					LogUtils.i("share", "分享成功");
				} else {
					LogUtils.i("share", "分享失败");
				}

			}
		}
				: spl);
	}

	public void setShareListener(SnsPostListener spl) {
		this.spl = spl;
	}

	/**
	 * 注销本次登录。注意：该接口不支持Facebook，twitter，G+，易信，来往授权
	 * 
	 * @param platform
	 *            平台如：SHARE_MEDIA.QQ
	 * @param scl如果onComplete方法中
	 *            status== StatusCode.ST_CODE_SUCCESSED成功
	 */
	public UMSocialService logout(final SHARE_MEDIA platform, SocializeClientListener scl) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		mController.getConfig().closeToast();
		mController.deleteOauth(context, platform, scl);
		return mController;

	}

	/**
	 * 
	 * 
	 * 注销本次登录。注意：该接口不支持Facebook，twitter，G+，易信，来往授权
	 * 
	 * @param platform
	 *            平台如：SHARE_MEDIA.QQ
	 * @param scl如果onComplete方法中
	 *            if(status == 200 && info != null){}得到用户信息
	 */
	public UMSocialService getUserInfo(final SHARE_MEDIA platform, UMDataListener scl) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		mController.getConfig().closeToast();
		mController.getPlatformInfo(context, platform, scl);
		return mController;
	}

	/**
	 * 登录QQ。
	 * 
	 * @param ual可以使用UMAuthListenerImpl方法oncomplete中
	 *            bundle.getString("uid");
	 */
	public UMSocialService loginQQ(String appId, String appKey, UMAuthListener ual) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, appId, appKey); // 添加QQ支持sso
		qqSsoHandler.setTargetUrl("http://www.40000km.com.cn/");
		qqSsoHandler.addToSocialSDK();
		mController.getConfig().closeToast();
		mController.doOauthVerify(context, SHARE_MEDIA.QQ, ual);
		return mController;
	}

	/**
	 * 登录QQ微博。
	 * 
	 * @param ual可以使用UMAuthListenerImpl方法oncomplete中
	 *            bundle.getString("uid");
	 */
	public UMSocialService loginQQWb(String appId, String appKey, UMAuthListener ual) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		TencentWBSsoHandler qqSsoHandler = new TencentWBSsoHandler(); // 添加QQ支持sso
		qqSsoHandler.addToSocialSDK();
		mController.getConfig().closeToast();
		mController.doOauthVerify(context, SHARE_MEDIA.TENCENT, ual);
		return mController;
	}

	/**
	 * 登录微信。
	 * 
	 * @param ual可以使用UMAuthListenerImpl
	 */
	public UMSocialService loginWeChat(String appId, String appKey, UMAuthListener ual) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		UMWXHandler wxHandler = new UMWXHandler(context, appId, appKey);
		wxHandler.setRefreshTokenAvailable(false);
		wxHandler.setTargetUrl("http://www.40000km.com.cn/");
		wxHandler.addToSocialSDK();
		mController.getConfig().closeToast();
		mController.doOauthVerify(context, SHARE_MEDIA.WEIXIN, ual);
		return mController;
	}

	/**
	 * 登录空间。
	 * 
	 * @param ual可以使用UMAuthListenerImpl
	 */
	public UMSocialService loginQZone(String appId, String appKey, UMAuthListener ual) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		QZoneSsoHandler wxHandler = new QZoneSsoHandler(context, appId, appKey);
		wxHandler.addToSocialSDK();
		mController.getConfig().closeToast();
		mController.doOauthVerify(context, SHARE_MEDIA.QZONE, ual);
		return mController;
	}

	/**
	 * 登录人人。
	 * 
	 * @param ual可以使用UMAuthListenerImpl
	 */
	public UMSocialService loginRenRen(String appId, String appKey, String secretKey, UMAuthListener ual) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		RenrenSsoHandler wxHandler = new RenrenSsoHandler(context, appId, appKey, secretKey);
		wxHandler.addToSocialSDK();
		mController.getConfig().closeToast();
		mController.doOauthVerify(context, SHARE_MEDIA.RENREN, ual);
		return mController;
	}

	/**
	 * 登录豆瓣。
	 * 
	 * @param ual可以使用UMAuthListenerImpl
	 */
	public UMSocialService loginDouban(UMAuthListener ual) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		mController.getConfig().closeToast();
		mController.doOauthVerify(context, SHARE_MEDIA.DOUBAN, ual);
		return mController;
	}

	/**
	 * 登录新浪。
	 * 
	 * @param ual可以使用UMAuthListenerImpl
	 * @return
	 */
	public UMSocialService loginSina(UMAuthListener ual) {
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		SinaSsoHandler wxHandler = new SinaSsoHandler();
		wxHandler.addToSocialSDK();
		mController.getConfig().closeToast();
		mController.doOauthVerify(context, SHARE_MEDIA.SINA, ual);
		return mController;

	}

	public static abstract class UMAuthListenerImpl implements UMAuthListener {

		@Override
		public void onCancel(SHARE_MEDIA platform) {
		}

		@Override
		public abstract void onComplete(Bundle bundle, SHARE_MEDIA platform);

		@Override
		public void onError(SocializeException e, SHARE_MEDIA platform) {
		}

		@Override
		public void onStart(SHARE_MEDIA platform) {
		}

	}
}
