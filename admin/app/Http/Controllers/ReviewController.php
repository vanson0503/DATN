<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Review;

class ReviewController extends Controller
{
    function index($product_id) {
        // Lấy tất cả các đánh giá của sản phẩm có ID là $product_id
        $reviews = Review::where('product_id', $product_id)
        ->orderBy('created_time', 'desc')
        ->get();
        if($reviews->isEmpty()) {
            return response(json_encode(['message' => "This product has no reviews!"]), 404);
        }
        if(!$reviews) {
            return response(json_encode(['message' => "Product not found!"]), 404);
        }
        return response()->json($reviews,200);
    }
    function create(Request $request){
        $validatedData = $request->validate([
            'customer_id' => 'required|exists:customer,id',
            'product_id' => 'required|exists:product,id',
            'rate' => 'required|integer|min:1|max:5',
            'content' => 'nullable|string|max:255',
        ]);
    
        // Tạo mới đối tượng Review và gán dữ liệu từ request
        $review = new Review;
        $review->customer_id = $validatedData['customer_id'];
        $review->product_id = $validatedData['product_id'];
        $review->rate = $validatedData['rate'];
    
        if (isset($validatedData['content'])) {
            $review->content = $validatedData['content'];
        }
        // Lưu đánh giá vào cơ sở dữ liệu
        $review->save();
    
        // Trả về thông báo thành công
        return response()->json(['message' => 'Review created successfully'], 201);
    }
}
