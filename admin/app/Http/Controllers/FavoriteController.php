<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Favorite;
use Illuminate\Support\Facades\DB;

class FavoriteController extends Controller
{
    public function index(Request $request)
    {
        // Lấy ID của khách hàng từ request
        $customerId = $request->input('customer_id');

        // Kiểm tra nếu ID khách hàng không được cung cấp hoặc không hợp lệ
        if (!$customerId) {
            return response()->json(['message' => 'Customer ID is required'], 400);
        }

        // Tìm các ID của sản phẩm yêu thích của khách hàng có ID tương ứng
        $favoriteProductIds = Favorite::where('customer_id', $customerId)->pluck('product_id');

        // Lấy thông tin sản phẩm tương ứng với các ID đã tìm thấy
        $favoriteProducts = DB::table('product')->whereIn('id', $favoriteProductIds)->get();

        // Lặp qua các sản phẩm để thêm thông tin đánh giá
        foreach ($favoriteProducts as $product) {
            $review = DB::table('review')
                ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                ->where('product_id', $product->id)
                ->first();
            $product->total_reviews = $review->total_reviews;
            $product->average_rating = $review->average_rating;
        }

        return response()->json($favoriteProducts, 200);
    }

    public function deleteFavorite(Request $request)
    {
        // Lấy ID của khách hàng và ID của sản phẩm từ request
        $customerId = $request->input('customer_id');
        $productId = $request->input('product_id');

        // Kiểm tra nếu ID của khách hàng không được cung cấp hoặc không hợp lệ
        if (!$customerId) {
            return response()->json(['message' => 'Customer ID is required'], 400);
        }

        // Nếu không có ID sản phẩm được cung cấp, xóa tất cả sản phẩm yêu thích của khách hàng
        if (!$productId) {
            Favorite::where('customer_id', $customerId)->delete();
            return response()->json(['message' => 'All favorites deleted successfully'], 200);
        }

        // Nếu có ID sản phẩm được cung cấp, xóa chỉ sản phẩm yêu thích có ID sản phẩm tương ứng
        $favorite = Favorite::where('customer_id', $customerId)
            ->where('product_id', $productId)
            ->first();

        // Kiểm tra xem sản phẩm yêu thích có tồn tại không
        if (!$favorite) {
            return response()->json(['message' => 'Favorite not found'], 404);
        }

        $favorite->delete();

        return response()->json(['message' => 'Favorite deleted successfully'], 200);
    }

    public function create(Request $request)
    {
        var_dump($request->all());
        // Lấy ID của khách hàng và ID của sản phẩm từ request
        $customerId = $request->input('customer_id');
        $productId = $request->input('product_id');

        // Kiểm tra nếu ID của khách hàng và ID của sản phẩm không được cung cấp hoặc không hợp lệ
        if (!$customerId || !$productId) {
            return response()->json(['message' => 'Customer ID and Product ID are required'], 400);
        }

        // Kiểm tra xem sản phẩm yêu thích đã tồn tại chưa
        $existingFavorite = Favorite::where('customer_id', $customerId)
            ->where('product_id', $productId)
            ->exists();

        // Nếu sản phẩm yêu thích đã tồn tại, trả về thông báo lỗi
        if ($existingFavorite) {
            return response()->json(['message' => 'Favorite already exists'], 400);
        }

        // Tạo một bản ghi mới cho sản phẩm yêu thích
        $favorite = new Favorite([
            'customer_id' => $customerId,
            'product_id' => $productId,
        ]);
        $favorite->save();

        return response()->json(['message' => 'Favorite added successfully'], 201);
    }

}
