<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
use PHPMailer\PHPMailer\SMTP;

class MailController extends Controller
{
    public function sendVerificationEmail($email, $verifyCode)
    {
        $mail = new PHPMailer(true);

        try {
            $mail->SMTPDebug = SMTP::DEBUG_OFF; // Disable verbose debug output
            $mail->IsSMTP(); // Enable SMTP
            $mail->SMTPAuth = true; // Enable authentication
            $mail->SMTPSecure = 'ssl'; // Enable secure transfer (REQUIRED for Gmail)
            $mail->Host = "smtp.gmail.com";
            $mail->Port = 465; // or 587
            $mail->IsHTML(true);
            $mail->Username = "vanson050320023@gmail.com";
            $mail->Password = "iareshxasrwmzibu";
            $mail->SetFrom("vanson050320023@gmail.com");
            $mail->Subject = "Food shop";
            $logo_url = 'https://th.bing.com/th/id/OIG4.TDYZyYkJCba3ZlUJ228N?pid=ImgGn'; // Path to your logo image
            $mail->Body = '
                <div style="text-align: center;">
                    <img src="' . $logo_url . '" alt="Logo" style="max-width: 300px; height: auto;">
                    <h2 style="font-size: 18px;">Vui lòng nhập mã: <strong>' . $verifyCode . '</strong> để xác nhận tài khoản!</h2>
                    <p>Link có hiệu lực trong vòng 5 phút!</p>
                </div>';
            $mail->AddAddress($email);
            $mail->send();
            return true;
        } catch (Exception $e) {
            return false;
        }
    }

    public function sendResetPasswordEmail($email, $resetCode)
    {
        $mail = new PHPMailer(true);

        try {
            $mail->SMTPDebug = SMTP::DEBUG_OFF; // Disable verbose debug output
            $mail->IsSMTP(); // Enable SMTP
            $mail->SMTPAuth = true; // Enable authentication
            $mail->SMTPSecure = 'ssl'; // Enable secure transfer (REQUIRED for Gmail)
            $mail->Host = "smtp.gmail.com";
            $mail->Port = 465; // or 587
            $mail->IsHTML(true);
            $mail->Username = "vanson050320023@gmail.com";
            $mail->Password = "iareshxasrwmzibu";
            $mail->SetFrom("vanson050320023@gmail.com");
            $mail->Subject = "Reset Password";
            $logo_url = 'https://th.bing.com/th/id/OIG4.TDYZyYkJCba3ZlUJ228N?pid=ImgGn'; // Path to your logo image
            $mail->Body = '
                <div style="text-align: center;">
                    <img src="' . $logo_url . '" alt="Logo" style="max-width: 300px; height: auto;">
                    <h2 style="font-size: 18px;">Vui lòng nhập mã: <strong>' . $resetCode . '</strong> để đặt lại mật khẩu của bạn!</h2>
                    <p>Link có hiệu lực trong vòng 5 phút!</p>
                </div>';
            $mail->AddAddress($email);
            $mail->send();
            return true;
        } catch (Exception $e) {
            return false;
        }
    }
}
