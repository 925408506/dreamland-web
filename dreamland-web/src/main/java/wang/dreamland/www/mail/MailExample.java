package wang.dreamland.www.mail;

/**
 * Created by wly on 2018/3/7.
 */

public class MailExample {

    public static void main (String args[]) throws Exception {
        String email = "925408506@qq.com";
        String validateCode = "1211";
        SendEmail.sendEmailMessage(email,validateCode);

    }
}
