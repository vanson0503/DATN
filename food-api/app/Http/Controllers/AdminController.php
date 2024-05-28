<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Admin;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Auth;

class AdminController extends Controller
{
    public function getAllAdmins()
    {
        $admins = Admin::all('id', 'username', 'image_url', 'status', 'role');
        return response()->json($admins);
    }

    public function getAdmin($id)
    {
        // Find the admin by ID
        $admin = Admin::find($id);

        // Check if admin was found
        if (!$admin) {
            return response()->json(['message' => 'Admin not found'], 404);
        }

        // Return the found admin, excluding sensitive data
        return response()->json($admin->makeHidden(['password']));
    }


    public function login(Request $request)
    {
        // Validate the incoming request data
        $request->validate([
            'username' => 'required|string',
            'password' => 'required|string'
        ]);

        // Find the admin by username
        $admin = Admin::where('username', $request->username)->first();

        // Check if admin exists and password is correct
        if ($admin && Hash::check($request->password, $admin->password)) {
            // Return a successful login response
            return response()->json(['message' => 'Đăng nhập thành công!', 'admin' => $admin->makeHidden(['password'])], 200);
        }

        // If authentication fails, return an error response
        return response()->json(['message' => 'Sai tài khoản hoặc mật khẩu'], 401);
    }

    public function store(Request $request)
    {
        // Validate the incoming request
        $request->validate([
            'username' => 'required|string|max:100|unique:admin,username',
            'password' => 'required|string|min:6|max:100',
            'image_url' => 'nullable|string|max:100',
            'status' => 'required|in:active,blocked',
            'role' => 'required|in:admin,manager,staff'
        ]);

        // Create a new admin instance
        $admin = new Admin;
        $admin->username = $request->username;
        $admin->password = Hash::make($request->password); // Hash the password for security
        $admin->image_url = $request->image_url;
        $admin->status = $request->status;
        $admin->role = $request->role;
        $admin->save(); // Save the admin to the database

        // Return a response indicating success
        return response()->json([
            'message' => 'Admin created successfully',
            'admin' => $admin
        ], 201);
    }
    // Method to update the role of an admin
    public function updateAdminRole(Request $request, $id)
    {
        $request->validate([
            'role' => 'required|in:admin,manager,staff',
        ]);

        $admin = Admin::find($id);
        if (!$admin) {
            return response()->json(['message' => 'Admin not found'], 404);
        }

        $admin->role = $request->role;
        $admin->save();

        return response()->json([
            'message' => 'Admin role updated successfully',
            'admin' => $admin
        ]);
    }

    public function update(Request $request, $id)
    {
        // Validate the incoming request
        $request->validate([
            'username' => 'sometimes|required|string|max:100|unique:admin,username,' . $id,
            'image_url' => 'nullable|string|max:100',
            'status' => 'required|in:active,blocked',
            'role' => 'required|in:admin,manager,staff'
        ]);

        // Find the admin by ID
        $admin = Admin::findOrFail($id);

        // Update admin details
        if ($request->has('username')) {
            $admin->username = $request->username;
        }
        // if ($request->has('password')) {
        //     $admin->password = Hash::make($request->password);
        // }
        if ($request->has('image_url')) {
            $admin->image_url = $request->image_url;
        }
        $admin->status = $request->status;
        $admin->role = $request->role;

        // Save the changes
        $admin->save();

        // Return a response
        return response()->json([
            'message' => 'Admin updated successfully',
            'admin' => $admin
        ]);
    }
    public function updatePassword($id,Request $request)
    {
        // Validate the incoming request
        try{
            $admin = Admin::findOrFail($id);
    
            // Update the password
            $admin->password = Hash::make($request->password);
    
            // Save the changes
            $admin->save();
    
            // Return a response
            return response()->json([
                'message' => 'Password updated successfully',
            ]);
        }
        catch(e){
            return response(
                ["message"=> "Lỗi",400]
            );
        }
    }

    // Method to update the status of an admin
    public function updateAdminStatus(Request $request, $id)
    {
        $request->validate([
            'status' => 'required|in:active,blocked',
        ]);

        $admin = Admin::find($id);
        if (!$admin) {
            return response()->json(['message' => 'Admin not found'], 404);
        }

        $admin->status = $request->status;
        $admin->save();

        return response()->json([
            'message' => 'Admin status updated successfully',
            'admin' => $admin
        ]);
    }
}
