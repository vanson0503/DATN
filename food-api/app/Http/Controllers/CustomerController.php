<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Customer;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class CustomerController extends Controller
{
    public function index()
    {
        $customers = Customer::select('id', 'full_name', 'phone_number', 'email', 'status', 'image_url', 'social_name', 'created_time', 'updated_time')->get();
        return response()->json($customers, 200);
    }

    public function updateStatus(Request $request, $id)
    {
        // Validate the request to ensure 'status' is provided and it's one of the allowed values
        $request->validate([
            'status' => 'required|in:active,inactive,blocked'
        ]);

        // Retrieve the customer by id
        $customer = Customer::find($id);

        // Check if the customer exists
        if (!$customer) {
            return response()->json(['message' => 'Customer not found'], 404);
        }

        // Update the status
        $customer->status = $request->status;
        $customer->save();

        // Return a success response
        return response()->json([
            'message' => 'Customer status updated successfully',
            'customer' => $customer
        ], 200);
    }

    public function getCustomerById($id)
    {
        $customer = Customer::select('id', 'full_name', 'phone_number', 'email', 'status', 'image_url', 'social_name', 'created_time', 'updated_time')
            ->where('id', $id)
            ->first();

        if (!$customer) {
            return response()->json(['message' => 'Customer not found'], 404);
        }

        return response()->json($customer, 200);
    }


    public function login(Request $request)
    {
        $credentials = $request->only('email_or_phone', 'password');

        if (filter_var($credentials['email_or_phone'], FILTER_VALIDATE_EMAIL)) {
            $credentials['email'] = $credentials['email_or_phone'];
            unset($credentials['email_or_phone']);
        } else {
            $credentials['phone_number'] = $credentials['email_or_phone'];
            unset($credentials['email_or_phone']);
        }

        // Lấy thông tin người dùng từ cơ sở dữ liệu với các trường cần thiết
        if (isset($credentials['email'])) {
            $customer = Customer::select('id', 'full_name', 'email', 'phone_number', 'password', 'status', 'image_url', 'social_name', 'created_time', 'updated_time')
                ->where('email', $credentials['email'])
                ->first();
        } else {
            $customer = Customer::select('id', 'full_name', 'email', 'phone_number', 'password', 'status', 'image_url', 'social_name', 'created_time', 'updated_time')
                ->where('phone_number', $credentials['phone_number'])
                ->first();
        }

        if ($customer && $customer->status === 'active' && Hash::check($credentials['password'], $customer->password)) {
            Auth::loginUsingId($customer->id);
            return response()->json(['message' => 'Login successful', 'customer' => $customer], 200);
        }

        // Đăng nhập thất bại
        return response()->json(['message' => 'Invalid credentials'], 401);
    }


    public function loginGoogle(Request $request)
    {
        $rules = [
            'full_name' => 'required|string|max:100',
            'email' => 'required|string|email|max:100',
            'image_url' => 'required|string',
        ];

        // Custom error messages
        $messages = [
            'required' => 'Trường :attribute không được để trống.',
            'string' => 'Trường :attribute phải là chuỗi.',
            'email' => 'Trường :attribute phải là địa chỉ email hợp lệ.',
            'max' => 'Trường :attribute không được vượt quá :max kí tự.',
        ];

        // Validate the request data
        $validator = Validator::make($request->all(), $rules, $messages);

        // Check validation result
        if ($validator->fails()) {
            return response()->json(['message' => $validator->errors()->first()], 400);
        }
        // Lấy dữ liệu từ yêu cầu
        $fullName = $request->input('full_name');
        $email = $request->input('email');
        $imageUrl = $request->input('image_url');

        // Kiểm tra xem email đã tồn tại trong cơ sở dữ liệu hay chưa
        $existingCustomer = Customer::where('email', $email)->first();

        if ($existingCustomer) {
            // Nếu email đã tồn tại, kiểm tra xem có social_name khác null không
            if ($existingCustomer->social_name !== null) {
                // Nếu có, cho phép đăng nhập
                return response()->json(['message' => 'Đăng nhập thành công', 'customer' => $existingCustomer], 200);
            } else {
                // Nếu không, báo lỗi email này đã được đăng ký
                return response()->json(['message' => 'Email này đã được đăng ký.'], 400);
            }
        } else {
            // Nếu email chưa tồn tại, tạo tài khoản mới
            $newCustomer = new Customer();
            $newCustomer->full_name = $fullName;
            $newCustomer->email = $email;
            $newCustomer->image_url = $imageUrl;
            $newCustomer->social_name = 'google'; // Đánh dấu là đăng nhập bằng Google
            $newCustomer->status = 'active';
            $newCustomer->save();

            return response()->json(['message' => 'Đăng ký tài khoản thành công', 'customer' => $newCustomer], 200);
        }
    }



    public function register(Request $request)
{
    $email = $request->input('email');
    $phone_number = $request->input('phone_number');

    // Kiểm tra xem email hoặc số điện thoại đã tồn tại trong cơ sở dữ liệu hay không
    $existingCustomer = Customer::where('email', $email)
        ->orWhere('phone_number', $phone_number)
        ->first();

    if ($existingCustomer) {
        return response()->json(['message' => 'Email hoặc số điện thoại đã tồn tại'], 400);
    }

    $data = $request->only('full_name', 'email', 'phone_number', 'password');
    $verifyCode = mt_rand(100000, 999999);
    DB::beginTransaction();

    try {
        // Thêm khách hàng mới vào cơ sở dữ liệu
        $customer = new Customer();
        $customer->full_name = $data['full_name'];
        $customer->email = $email;
        $customer->phone_number = $phone_number;
        $customer->password = Hash::make($data['password']);
        $customer->status = 'inactive';
        $customer->verify_code = $verifyCode;
        $customer->save();

        // Gửi email xác nhận
        $mailController = new MailController();
        $emailSent = $mailController->sendVerificationEmail($email, $verifyCode);

        if (!$emailSent) {
            DB::rollback(); // Rollback transaction if email sending fails
            return response()->json(['message' => 'Failed to send verification email'], 500);
        }

        DB::commit(); // Commit transaction if everything is successful
        return response()->json(['message' => 'Đăng ký thành công'], 200);
    } catch (\Exception $e) {
        DB::rollback(); // Rollback transaction on any exception
        return response()->json(['message' => 'Đã xảy ra lỗi' . $e->getMessage()], 500);
    }
}
public function sendResetPasswordRequest(Request $request)
    {
        $email = $request->input('email');

        // Validate the email
        $request->validate([
            'email' => 'required|email'
        ]);

        // Find the customer with the provided email
        $customer = Customer::where('email', $email)->first();

        if (!$customer) {
            return response()->json(['message' => 'Địa chỉ email không tồn tại!'], 404);
        }

        // Generate a reset code
        $resetCode = mt_rand(100000, 999999);

        // Update the customer record with the reset code
        $customer->verify_code = $resetCode;
        $customer->save();

        // Send the reset code via email
        $mailController = new MailController();
        $emailSent = $mailController->sendResetPasswordEmail($email, $resetCode);

        if (!$emailSent) {
            return response()->json(['message' => 'Không gửi được email!'], 500);
        }

        return response()->json(['message' => 'Gửi mã thành công!'], 200);
    }
    public function resendResetPasswordCode(Request $request)
    {
        $email = $request->input('email');

        // Validate the email
        $request->validate([
            'email' => 'required|email'
        ]);

        // Find the customer with the provided email
        $customer = Customer::where('email', $email)->first();

        if (!$customer) {
            return response()->json(['message' => 'Địa chỉ email không tồn tại!'], 404);
        }

        // Generate a new reset code
        $resetCode = mt_rand(100000, 999999);

        // Update the customer record with the new reset code
        $customer->verify_code = $resetCode;
        $customer->save();

        // Send the new reset code via email
        $mailController = new MailController();
        $emailSent = $mailController->sendResetPasswordEmail($email, $resetCode);

        if (!$emailSent) {
            return response()->json(['message' => 'Không gửi được email!'], 500);
        }

        return response()->json(['message' => 'Gửi mã thành công!'], 200);
    }

    public function confirmResetPasswordCode(Request $request)
    {
        $data = $request->only('email', 'verify_code');

        // Validate the request
        $validator = Validator::make($data, [
            'email' => 'required|email',
            'verify_code' => 'required|numeric',
        ]);

        if ($validator->fails()) {
            return response()->json(['message' => $validator->errors()->first()], 400);
        }

        // Find the customer with the provided email
        $customer = Customer::where('email', $data['email'])->first();

        if (!$customer) {
            return response()->json(['message' => 'Không tìm thấy khách hàng với địa chỉ email này'], 404);
        }

        // Check if the verify code matches
        if ($customer->verify_code != $data['verify_code']) {
            return response()->json(['message' => 'Mã xác nhận không chính xác'], 400);
        }

        // Check the expiration time of the verify code
        $verifyCreatedAt = $customer->updated_time;
        $now = now();
        $diffInMinutes = $now->diffInMinutes($verifyCreatedAt);

        if (abs($diffInMinutes) > 5) {
            return response()->json(['message' => 'Mã xác nhận đã hết hạn'], 400);
        }
        $customer->verify_code = null;
        $customer->save();

        return response()->json(['message' => 'Xác nhận mã thành công'], 200);
    }

    public function resendVerifyCode(Request $request)
    {
        // Lấy email từ request
        $email = $request->input('email');

        // Tìm khách hàng với địa chỉ email đã cung cấp
        $customer = Customer::where('email', $email)->first();

        // Kiểm tra xem khách hàng có tồn tại không
        if (!$customer) {
            return response()->json(['message' => 'Không tìm thấy khách hàng với địa chỉ email này'], 404);
        }

        // Tạo mã xác nhận mới
        $verifyCode = mt_rand(100000, 999999);

        // Cập nhật mã xác nhận mới vào cơ sở dữ liệu
        $customer->verify_code = $verifyCode;
        $customer->save();

        // Gửi email mới chứa mã xác nhận
        $mailController = new MailController();
        $emailSent = $mailController->sendVerificationEmail($customer->email, $verifyCode);

        // Kiểm tra xem email đã được gửi thành công hay không
        if (!$emailSent) {
            return response()->json(['message' => 'Không thể gửi lại mã xác nhận'], 500);
        }

        return response()->json(['message' => 'Mã xác nhận mới đã được gửi lại thành công'], 200);
    }

    public function confirmVerifyCode(Request $request)
    {
        // Lấy dữ liệu từ request
        $data = $request->only('email_or_phone', 'verify_code');

        // Kiểm tra xem dữ liệu đã được cung cấp đủ không
        if (!isset($data['email_or_phone']) || !isset($data['verify_code'])) {
            return response()->json(['message' => 'Dữ liệu không hợp lệ'], 400);
        }

        // Tìm khách hàng với email hoặc số điện thoại đã cung cấp
        $customer = Customer::where(function ($query) use ($data) {
            $query->where('email', $data['email_or_phone'])
                ->orWhere('phone_number', $data['email_or_phone']);
        })->first();

        // Kiểm tra xem khách hàng có tồn tại không
        if (!$customer) {
            return response()->json(['message' => 'Không tìm thấy khách hàng với thông tin này'], 404);
        }

        // Kiểm tra xem mã xác nhận có khớp không
        if ($customer->verify_code != $data['verify_code']) {
            return response()->json(['message' => 'Mã xác nhận không chính xác'], 400);
        }

        // Kiểm tra thời gian tạo mã xác nhận
        $verifyCreatedAt = $customer->updated_time;

        $now = now();
        // $now = $now->addHours(7);
        $diffInMinutes = $now->diffInMinutes($verifyCreatedAt);
        // Kiểm tra xem mã xác nhận có hợp lệ trong vòng 5 phút không
        if (abs($diffInMinutes) > 5) {
            return response()->json(['message' => 'Mã xác nhận đã hết hạn'], 400);
        }

        // Cập nhật trạng thái của khách hàng và xóa mã xác nhận
        $customer->status = 'active';
        $customer->verify_code = null;
        $customer->save();

        return response()->json(['message' => 'Xác nhận mã thành công'], 200);
    }
    public function resetPassword(Request $request)
    {
        // Validate the request to ensure email and password are provided and valid
        $request->validate([
            'email' => 'required|email',
            'password' => 'required|string|min:6'
        ]);

        // Retrieve the customer by email
        $customer = Customer::where('email', $request->input('email'))->first();

        // Check if the customer exists
        if (!$customer) {
            return response()->json(['message' => 'Customer not found'], 404);
        }

        // Update the password
        $customer->password = Hash::make($request->input('password'));
        $customer->save();

        // Return a success response
        return response()->json(['message' => 'Đặt lại mật khẩu thành công'], 200);
    }
    
    public function updatePasswordById(Request $request, $id)
    {
        // Validate the request to ensure old_password and new_password are provided and valid
        $request->validate([
            'old_password' => 'required|string|min:6',
            'new_password' => 'required|string|min:6'
        ]);

        // Retrieve the customer by id
        $customer = Customer::find($id);

        // Check if the customer exists
        if (!$customer) {
            return response()->json(['message' => 'Customer not found'], 404);
        }

        // Check if the old password is correct
        if (!Hash::check($request->input('old_password'), $customer->password)) {
            return response()->json(['message' => 'Old password is incorrect'], 400);
        }

        // Update the password
        $customer->password = Hash::make($request->input('new_password'));
        $customer->save();

        // Return a success response
        return response()->json(['message' => 'Password updated successfully'], 200);
    }
    
    

    public function update(Request $request, $id)
    {
        $customer = Customer::findOrFail($id);

        // Kiểm tra nếu request có chứa ảnh
        if ($request->hasFile('image')) {
            $file = $request->file('image');
            $filename = $file->hashName(); // Generates a unique name based on file contents
            $file->storeAs('public/avatars', $filename);
            $customer->image_url = $filename;
        }

        // Cập nhật các trường thông tin khác
        $customer->full_name = $request->input('full_name');
        $customer->phone_number = $request->input('phone_number');
        $customer->email = $request->input('email');

        $customer->save();

        return response()->json(['message' => 'Customer updated successfully']);
    }
    public function updateAvatar(Request $request, $id)
    {
        $customer = Customer::findOrFail($id);

        // Kiểm tra nếu request có chứa ảnh
        if ($request->hasFile('image')) {
            $file = $request->file('image');
            $filename = $file->hashName(); // Generates a unique name based on file contents
            $file->storeAs('public/avatars', $filename);
            $customer->image_url = $filename;
        }

        $customer->save();

        return response()->json(['message' => 'Customer updated successfully']);
    }
}
