package com.bstoneinfo.lib.mail;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.os.Handler;

import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSTimer;

/**
 * 简单邮件（不带附件的邮件）发送器
 */
public class SimpleMailSender {

    public enum MailSenderStatus {
        INIT,
        SENDING,
        FINISHED,
        FAILED,
        CANCEL
    }

    public interface MailSenderListener {
        public void finished();

        public void failed(Exception e);
    }

    private int timeoutSeconds = 60;
    private boolean canceled = false;
    private BSTimer timer;
    private MailSenderStatus status = MailSenderStatus.INIT;

    public void setTimeout(int seconds) {
        timeoutSeconds = seconds;
    }

    public void cancel() {
        status = MailSenderStatus.CANCEL;
        canceled = true;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void createTimer(final MailSenderListener listener) {
        status = MailSenderStatus.SENDING;
        if (timeoutSeconds > 0) {
            timer = BSTimer.asyncRun(new Runnable() {
                @Override
                public void run() {
                    status = MailSenderStatus.FAILED;
                    canceled = true;
                    timer = null;
                    if (listener != null) {
                        listener.failed(new TimeoutException());
                    }
                    BSLog.d("Timeout");
                }
            }, timeoutSeconds * 1000);
        }
    }

    private void notifyFinished(Handler handler, final MailSenderListener listener) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                status = MailSenderStatus.FINISHED;
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (!canceled && listener != null) {
                    listener.finished();
                }
                BSLog.d("Success");
            }
        });
    }

    private void notifyFailed(Handler handler, final MailSenderListener listener, final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                status = MailSenderStatus.FAILED;
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (!canceled && listener != null) {
                    listener.failed(e);
                }
                BSLog.d("Failed: " + e.toString());
            }
        });
    }

    /**
     * 以文本格式发送邮件
     * 
     * @param mailInfo 待发送的邮件的信息
     */
    public void sendTextMail(final MailSenderInfo mailInfo, final MailSenderListener listener) {
        if (status != MailSenderStatus.INIT) {
            return;
        }
        createTimer(listener);
        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                // 判断是否需要身份认证    
                MyAuthenticator authenticator = null;
                Properties pro = mailInfo.getProperties();
                if (mailInfo.isValidate()) {
                    // 如果需要身份认证，则创建一个密码验证器    
                    authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
                }
                // 根据邮件会话属性和密码验证器构造一个发送邮件的session    
                Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
                try {
                    // 根据session创建一个邮件消息    
                    Message mailMessage = new MimeMessage(sendMailSession);
                    // 创建邮件发送者地址    
                    Address from = new InternetAddress(mailInfo.getFromAddress());
                    // 设置邮件消息的发送者    
                    mailMessage.setFrom(from);
                    // 创建邮件的接收者地址，并设置到邮件消息中    
                    Address to = new InternetAddress(mailInfo.getToAddress());
                    mailMessage.setRecipient(Message.RecipientType.TO, to);
                    // 设置邮件消息的主题    
                    mailMessage.setSubject(mailInfo.getSubject());
                    // 设置邮件消息发送的时间    
                    mailMessage.setSentDate(new Date());
                    // 设置邮件消息的主要内容    
                    String mailContent = mailInfo.getContent();
                    mailMessage.setText(mailContent);
                    // 发送邮件    
                    if (canceled) {
                        return;
                    }
                    Transport.send(mailMessage);
                    notifyFinished(handler, listener);
                } catch (final MessagingException ex) {
                    notifyFailed(handler, listener, ex);
                }
            }
        }.start();
    }

    /**
     * 以HTML格式发送邮件
     * 
     * @param mailInfo 待发送的邮件信息
     */
    public void sendHtmlMail(final MailSenderInfo mailInfo, final MailSenderListener listener) {
        if (status != MailSenderStatus.INIT) {
            return;
        }
        createTimer(listener);
        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                // 判断是否需要身份认证    
                MyAuthenticator authenticator = null;
                Properties pro = mailInfo.getProperties();
                //如果需要身份认证，则创建一个密码验证器     
                if (mailInfo.isValidate()) {
                    authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
                }
                // 根据邮件会话属性和密码验证器构造一个发送邮件的session    
                Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
                try {
                    // 根据session创建一个邮件消息    
                    Message mailMessage = new MimeMessage(sendMailSession);
                    // 创建邮件发送者地址    
                    Address from = new InternetAddress(mailInfo.getFromAddress());
                    // 设置邮件消息的发送者    
                    mailMessage.setFrom(from);
                    // 创建邮件的接收者地址，并设置到邮件消息中    
                    Address to = new InternetAddress(mailInfo.getToAddress());
                    // Message.RecipientType.TO属性表示接收者的类型为TO    
                    mailMessage.setRecipient(Message.RecipientType.TO, to);
                    // 设置邮件消息的主题    
                    mailMessage.setSubject(mailInfo.getSubject());
                    // 设置邮件消息发送的时间    
                    mailMessage.setSentDate(new Date());
                    // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象    
                    Multipart mainPart = new MimeMultipart();
                    // 创建一个包含HTML内容的MimeBodyPart    
                    BodyPart html = new MimeBodyPart();
                    // 设置HTML内容    
                    html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
                    mainPart.addBodyPart(html);
                    // 将MiniMultipart对象设置为邮件内容    
                    mailMessage.setContent(mainPart);
                    // 发送邮件    
                    if (canceled) {
                        return;
                    }
                    Transport.send(mailMessage);
                    notifyFinished(handler, listener);
                } catch (MessagingException ex) {
                    BSLog.d(ex.toString());
                    notifyFailed(handler, listener, ex);
                }
            }
        }.start();
    }

}
