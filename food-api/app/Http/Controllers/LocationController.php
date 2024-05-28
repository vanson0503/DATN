<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Location;

class LocationController extends Controller
{
    public function index(Request $request)
    {
        if ($request->input("id")) {
            $id = $request->input("id");
            $location = Location::find($id);
            return response()->json($location, 200);
        } else {
            $customerId = $request->input('customer_id');

            if (!$customerId) {
                return response()->json(['message' => 'Customer ID is required'], 400);
            }

            $locations = Location::where('customer_id', $customerId)->get();

            return response()->json($locations, 200);
        }
    }

    public function create(Request $request)
    {
        try {


            // Kiểm tra xem người dùng đã có location nào chưa
            $isFirstLocation = Location::where('customer_id', $request->customer_id)->doesntExist();

            // Nếu đây là location đầu tiên hoặc request có chỉ định đặt làm mặc định
            $isDefault = $isFirstLocation || $request->is_default == "true";

            if ($isDefault) {
                // Nếu có yêu cầu đặt làm mặc định, đặt tất cả location khác của người dùng này là không mặc định
                Location::where('customer_id', $request->customer_id)->update(['is_default' => 0]);
            }

            // Tạo location mới với trạng thái mặc định được xác định
            $location = new Location([
                'customer_id' => $request->customer_id,
                'name' => $request->name,
                'phone_number' => $request->phone_number,
                'address' => $request->address,
                'is_default' => $isDefault ? 1 : 0  // Nếu là location đầu tiên hoặc được yêu cầu làm mặc định thì đặt là 1
            ]);

            $location->save();

            return response()->json(['message' => 'Location added successfully'], 201);
        } catch (\Throwable $err) {
            return response()->json(['message' => 'Failed to add location: ' . $err->getMessage()], 500);
        }
    }



    public function update(Request $request, $id)
    {
        try {
            $location = Location::findOrFail($id);

            // Kiểm tra xem có yêu cầu thay đổi trạng thái mặc định hay không
            if ($request->has('is_default') && $request->is_default == "true") {
                // Đặt tất cả location khác của người dùng này là không mặc định
                Location::where('customer_id', $location->customer_id)->update(['is_default' => 0]);

                // Đặt location hiện tại là mặc định
                $location->is_default = 1;
            }

            // Cập nhật thông tin khác
            $location->name = $request->name ?? $location->name;
            $location->phone_number = $request->phone_number ?? $location->phone_number;
            $location->address = $request->address ?? $location->address;

            $location->save();

            return response()->json(['message' => 'Location updated successfully'], 200);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Location not found or failed to update', 'error' => $e->getMessage()], 404);
        }
    }

    public function setDefault($id)
    {
        // Find the location by ID
        $location = Location::findOrFail($id);

        // Get the customer ID of the location
        $customerId = $location->customer_id;

        // Set is_default to 0 for all locations of the customer
        Location::where('customer_id', $customerId)->update(['is_default' => 0]);

        // Set the selected location to be the default one
        $location->update(['is_default' => 1]);

        return response()->json(['message' => 'Default location set successfully', 'location' => $location], 200);
    }

    public function delete($id)
    {
        // Tìm địa điểm theo ID
        $location = Location::find($id);

        // Kiểm tra xem địa điểm có tồn tại không
        if (!$location) {
            return response()->json(['message' => 'Location not found'], 404);
        }

        // Xóa địa điểm
        $location->delete();

        return response()->json(['message' => 'Location deleted successfully'], 200);
    }
}
