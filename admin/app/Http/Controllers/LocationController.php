<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Location;

class LocationController extends Controller
{
    public function index(Request $request)
    {
        $customerId = $request->input('customer_id');

        if (!$customerId) {
            return response()->json(['message' => 'Customer ID is required'], 400);
        }

        $locations = Location::where('customer_id', $customerId)->get();

        return response()->json($locations, 200);
    }

    public function create(Request $request)
    {
        // Validate request data
        $validatedData = $request->validate([
            'customer_id' => 'required|exists:customers,id',
            'name' => 'required|max:100',
            'phone_number' => 'required|max:20',
            'address' => 'required|max:255',
        ]);

        // Check if it's the first location
        $isFirstLocation = Location::where('customer_id', $validatedData['customer_id'])->count() == 0;

        // Set is_default to 1 if it's the first location
        $validatedData['is_default'] = $isFirstLocation ? 1 : 0;

        // Create new location
        $location = Location::create($validatedData);

        return response()->json(['message' => 'Location created successfully', 'location' => $location], 201);
    }

    public function update(Request $request, $id)
    {
        // Validate request data
        $validatedData = $request->validate([
            'name' => 'required|max:100',
            'phone_number' => 'required|max:20',
            'address' => 'required|max:255',
            'is_default' => 'boolean', // Kiểm tra nếu trường này là boolean
        ]);

        // Find the location by ID
        $location = Location::findOrFail($id);

        // Update the location with validated data
        $location->update($validatedData);

        // Nếu trường is_default đã được cung cấp trong dữ liệu yêu cầu
        if ($request->has('is_default')) {
            // Đặt is_default của tất cả các địa điểm của khách hàng này thành false
            $location->customer->locations()->update(['is_default' => false]);

            // Đặt is_default của địa điểm đã được cập nhật thành true
            $location->update(['is_default' => true]);
        }

        return response()->json(['message' => 'Location updated successfully', 'location' => $location], 200);
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
